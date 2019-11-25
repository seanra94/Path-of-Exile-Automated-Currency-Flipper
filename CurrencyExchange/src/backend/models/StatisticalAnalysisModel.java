package backend.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import backend.Global;
import backend.dataStructures.Currency;
import backend.dataStructures.Exchange;
import backend.dataStructures.ExchangeData;


public class StatisticalAnalysisModel {

//--------------------------------------------------------------Fields--------------------------------------------------------------//
	private static final int MAX_ULR_EXCHANGE_COUNT = Global.getMaxUlrExchangeCount();
	private static final double MINIMUM_RATIO_MULTIPLIER = Global.getMinimumRatioMultiplier();
	private static final double MAXIMUM_RATIO_MULTIPLIER = Global.getMaximumRatioMultiplier();
	private static final double STANDARD_DEVIATION_MULTIPLIER = Global.getStandardDeviationMultiplier();
	private static final double MEDIAN_ERROR_MULTIPLIER = Global.getMedianErrorMultiplier();
	
	private static Map<Integer, Currency> _webCurrenciesMap;
	private static Currency _chaosCurrency;
	private static List<Exchange> _missingSellChaosList;
	private static List<Exchange> _missingBuyChaosList; 
	

	
//--------------------------------------------------------------Methods--------------------------------------------------------------//

	//--------------------
	public static void analyseAllExchanges() {
		_webCurrenciesMap = Global.get_currMapNoC();
		_chaosCurrency = Global.get_chaosCurrency();
		_missingSellChaosList = Global.get_missingSellChaosList();
		_missingBuyChaosList = Global.get_missingBuyChaosList();
		
		analyseChaosExchanges();
		setAllChaosValues();
		analyseMissingExchanges();
	}
	
	
	//--------------------
	public static void analyseChaosExchanges() {
		StatisticalAnalysisModel statisticalAnalysisModel = new StatisticalAnalysisModel();
		Map<Integer, Exchange> chaosMap = _chaosCurrency.get_webOffersMap();
		ExecutorService es = Executors.newFixedThreadPool(_webCurrenciesMap.size() + chaosMap.size());
		
		for (Entry<Integer, Currency> entry : _webCurrenciesMap.entrySet()) {
			es.execute(statisticalAnalysisModel.new StatAnalyser(entry.getValue().get_webOffersMap().get(4)));
		}

		for (Entry<Integer, Exchange> entry : chaosMap.entrySet()) {
			es.execute(statisticalAnalysisModel.new StatAnalyser(entry.getValue()));
		}
		
		es.shutdown();	
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
	}

	//--------------------
		private void analyseChaosExchange(Exchange exchange) {
			
			if (exchange == null) {
				return;
			}
			List<ExchangeData> dataList = exchange.get_exchangeDataList();
			List<ExchangeData> outliers = new ArrayList<ExchangeData>();
			int size = dataList.size();
			//Trim URL exchanges
			if (size > MAX_ULR_EXCHANGE_COUNT) {
				int i = MAX_ULR_EXCHANGE_COUNT;
				while (i < size) {
					dataList.remove(i);
					size--;
				}
			}
			double errorTolerance = 1 - ((MEDIAN_ERROR_MULTIPLIER * size)/MAX_ULR_EXCHANGE_COUNT); //Tighter when size is smaller the max url size
			double median;
			

			//Use median to remove ridiculous outliers that may skew mean
			if (dataList.size() % 2 == 0) {
				median = (dataList.get(size/2).get_buySellRatio() + dataList.get(size/2 - 1).get_buySellRatio())/2;
			} else {
				median = dataList.get(size/2).get_buySellRatio();
			}
			
			for (ExchangeData exchangeData : dataList) {
				
				if (exchangeData.get_buySellRatio() < (1 - errorTolerance) * median || exchangeData.get_buySellRatio() > (1 + errorTolerance) * median) {
					outliers.add(exchangeData);
				}
			}

			int outliersSize = outliers.size();
			if (outliersSize > 0) {
				if (outliersSize != size) {
					for (ExchangeData outlier : outliers) {
						dataList.remove(outlier);
					}
				} else {
					outliersSize = 0;
					outliers = new ArrayList<ExchangeData>();
				}
			}
			DescriptiveStatistics descDataTrimmed = new DescriptiveStatistics();
			for (ExchangeData exchangeData : dataList) {
				descDataTrimmed.addValue(exchangeData.get_buySellRatio());
			}
			double mean = descDataTrimmed.getMean();
			double standardDeviation = descDataTrimmed.getStandardDeviation();
			double zValue;
			//Use Z value to remove outliers
			for (ExchangeData exchangeData : dataList) {
				zValue = (exchangeData.get_buySellRatio() - mean)/standardDeviation;
				if (Math.abs(zValue) > STANDARD_DEVIATION_MULTIPLIER) {
					outliers.add(exchangeData);
				}
			}
			int outliersSize2 = outliers.size();
			if (outliersSize2 > outliersSize) {
				if (outliersSize2 != dataList.size()) {
					
					for (int i = outliersSize; i < outliers.size(); i++) {
						dataList.remove(outliers.get(i));
					}
				} else {
					outliers = new ArrayList<ExchangeData>();
				}
			}

			exchange.set_outlierList(outliers);	
		}
	
	//--------------------
	private static void setStockRange(Currency buyCurrency, Exchange exchange) {
		int minSell = Integer.MAX_VALUE;
		int minBuy = Integer.MAX_VALUE;
		int maxSell = Integer.MIN_VALUE;
		int maxBuy = Integer.MIN_VALUE;
		int sellValue;
		int buyValue;
		for (ExchangeData exchangeData : exchange.get_exchangeDataList()) {
			sellValue = exchangeData.get_sellValue();
			buyValue = exchangeData.get_buyValue();
			
			if (sellValue < minSell) {
				minSell = sellValue;
			}

			if (buyValue < minBuy) {
				minBuy = buyValue;	
			}
			
			if (sellValue > maxSell) {
				maxSell = sellValue;
			}
			
			if (buyValue > maxBuy) {
				maxBuy = buyValue;	
			}
		}
		_chaosCurrency.set_StockRange(minSell, maxSell);
		buyCurrency.set_StockRange(minBuy, maxBuy);
		int finalMaxSell = maxSell * 2;
		int finalMinSell = (int) Math.round(minSell * 0.25);
		int finalMaxBuy = maxBuy * 2;
		int finalMinBuy = (int) Math.round(minBuy * 0.25);	
		exchange.setMinMaxAmounts(finalMinSell, finalMinBuy, finalMaxSell, finalMaxBuy);
	}
	//--------------------
	private static void setAllChaosValues() {
		Currency currency;
		Exchange sellChaosExchange;
		Exchange buyChaosExchange;
		List<ExchangeData> sellExDataList;
		List<ExchangeData> buyExDataList;
		double lowerBound;
		double upperBound;
		double estimatedValue;
		Map<Integer, Exchange> chaosMap = _chaosCurrency.get_webOffersMap();
		//DecimalFormat df = new DecimalFormat("#.####");
		//Determine chaos values of all currencies
		
		_chaosCurrency.set_chaosValue(1);
		
		for (Entry<Integer, Currency> sellCurrencyEntry : _webCurrenciesMap.entrySet()) {
			currency = sellCurrencyEntry.getValue();
			sellChaosExchange = chaosMap.get(currency.get_id());
			buyChaosExchange = currency.get_webOffersMap().get(4);

			if (sellChaosExchange != null && buyChaosExchange != null) {
				sellExDataList = sellChaosExchange.get_exchangeDataList();
				buyExDataList = buyChaosExchange.get_exchangeDataList();
				lowerBound = sellExDataList.get(0).get_sellBuyRatio();
				upperBound = buyExDataList.get(0).get_buySellRatio();
				estimatedValue = (1.0/3.0) * lowerBound + (2.0/3.0) * upperBound; //COME BACK LATER
				
				setChaosValue(sellChaosExchange, currency, 1, estimatedValue, sellExDataList);
				setChaosValue(buyChaosExchange, currency, estimatedValue, 1, buyExDataList);
				//System.out.println("Curr: " + currency.get_id() + " " + currency.get_name() + " lb: " + df.format(lowerBound) + " value: " + df.format(estimatedValue) + " ub: " + df.format(upperBound) +
				//		" <---> " + df.format(1.1 / estimatedValue) + " [" + df.format(1.3 / estimatedValue) + ", " + df.format(sellExDataList.get(sellExDataList.size() - 1).get_BuySellRatio()) + "]" +
				//		" <---> " + df.format(1.1 * estimatedValue) + " [" + df.format(1.3 * estimatedValue) + ", " + df.format(buyExDataList.get(buyExDataList.size() - 1).get_BuySellRatio()) + "]");
				
			} else if (sellChaosExchange == null) {
				buyExDataList = buyChaosExchange.get_exchangeDataList();
				estimatedValue = buyExDataList.get(0).get_buySellRatio();
				currency.set_chaosValue(estimatedValue);
				setChaosValue(buyChaosExchange, currency, estimatedValue, 1, buyExDataList);
				
			} else if (buyChaosExchange == null) {
				sellExDataList = sellChaosExchange.get_exchangeDataList();
				estimatedValue = sellExDataList.get(0).get_sellBuyRatio();
				currency.set_chaosValue(estimatedValue);
				setChaosValue(sellChaosExchange, currency, 1, estimatedValue, sellExDataList);
			} else {
				System.out.println("MAJOR ERROR: This shouldn't be possible");
			}

		}
		
		correctFragmentSetValues(48, 27, 28, 29, 30); //Sacrifice set (dusk, midnight, dawn, noon)
		correctFragmentSetValues(49, 31, 32, 33, 34); //Mortal set (grief, rage, hope, ignorance)
		correctFragmentSetValues(50, 36, 37, 38, 39); //Pale set (eber, yriel, inya, volkuur)
		correctFragmentSetValues(51, 41, 42, 43, 44); //Shaper set (hydra, phoenix, minotaur, chimera)
		correctSplinterValues(62, 52); //Xoph
		correctSplinterValues(63, 53); //Tul
		correctSplinterValues(64, 54); //Esh
		correctSplinterValues(65, 55); //Uul-Netol
		correctSplinterValues(66, 56); //Chayula
		
	
		
	}
	//--------------------
	private static void setChaosValue(Exchange exchange, Currency currency, double sellChaosValue, double buyChaosValue, List<ExchangeData> dataList) {
		//DecimalFormat df = new DecimalFormat("#.####");
		if (sellChaosValue == 1) {
			currency.set_chaosValue(buyChaosValue);
				exchange.setBounds(
						Math.min(MINIMUM_RATIO_MULTIPLIER / buyChaosValue, dataList.get(0).get_buySellRatio()), 
						Math.max(MAXIMUM_RATIO_MULTIPLIER / buyChaosValue, dataList.get(dataList.size() - 1).get_buySellRatio()));
		
				/*
				System.out.println("1a - " + exchange.get_sellId() + ", " + exchange.get_buyId() + "\t "
						+ "Min = " + df.format(MINIMUM_RATIO_MULTIPLIER / buyChaosValue) + " OR " + df.format(dataList.get(0).get_BuySellRatio())
						+ " Max = " + df.format(MAXIMUM_RATIO_MULTIPLIER / buyChaosValue) + " OR " + df.format(dataList.get(dataList.size() - 1).get_BuySellRatio()));
				 */
		} else {
			currency.set_chaosValue(sellChaosValue);
		
				
				exchange.setBounds(
						Math.min(MINIMUM_RATIO_MULTIPLIER * sellChaosValue, dataList.get(0).get_buySellRatio()),
						Math.max(MAXIMUM_RATIO_MULTIPLIER * sellChaosValue, dataList.get(dataList.size() - 1).get_buySellRatio()));
				/*
				System.out.println("2a - " + exchange.get_sellId() + ", " + exchange.get_buyId() + "\t "
						+ "Min = " + df.format(MINIMUM_RATIO_MULTIPLIER * sellChaosValue) + " OR " + df.format(dataList.get(0).get_BuySellRatio())
						+ " Max = " + df.format(MAXIMUM_RATIO_MULTIPLIER * sellChaosValue) + " OR " + df.format(dataList.get(dataList.size() - 1).get_BuySellRatio()));
				 */
		}
	
			exchange.set_SellChaosValue(sellChaosValue);
			exchange.set_BuyChaosValue(buyChaosValue);
			setStockRange(currency, exchange);
			for (ExchangeData exData : exchange.get_exchangeDataList()) {
				exchange.addToRatioSet(exData);
			}
		
	}
	//--------------------
	private static void correctSplinterValues(int stoneId, int splinterId) {
		try {
			Currency stone;
			Currency splinter;
			stone = _webCurrenciesMap.get(stoneId);
			splinter = _webCurrenciesMap.get(splinterId);
			stone.set_chaosValue(Math.min(splinter.get_chaosValue() * 100, stone.get_chaosValue()));
		} catch (NullPointerException e) {
			System.out.println("Error: Stone " + stoneId + " is null or splinter " + splinterId + " is null");
		}
	}
	//--------------------
	private static void correctFragmentSetValues(int setId, int fragId1, int fragId2, int fragId3, int fragId4) {
		try {
			Currency set;
			Currency frag1;
			Currency frag2;
			Currency frag3;
			Currency frag4;
			set = _webCurrenciesMap.get(setId);
			frag1 = _webCurrenciesMap.get(fragId1);
			frag2 = _webCurrenciesMap.get(fragId2);
			frag3 = _webCurrenciesMap.get(fragId3);
			frag4 = _webCurrenciesMap.get(fragId4);
			set.set_chaosValue(Math.min(frag1.get_chaosValue() + frag2.get_chaosValue() + frag3.get_chaosValue() + frag4.get_chaosValue(), set.get_chaosValue()));
		} catch (NullPointerException e) {
			System.out.println("Error: Set " + setId + " is null or one or more fragments are null");
		}
	}
	//--------------------
	private static void analyseMissingExchanges() {
		
		for (Exchange foundExchange : _missingBuyChaosList) {
			analyseMissingExchange(foundExchange, true);
		}
		
		for (Exchange foundExchange : _missingSellChaosList) {
			analyseMissingExchange(foundExchange, false);

		}
	}
	//--------------------
	private static void analyseMissingExchange(Exchange foundExchange, boolean sellingChaos) {
		Exchange missingExchange;
		int missingId;
		double sellChaosValue;
		double buyChaosValue;
		int maxSell = Integer.MIN_VALUE;
		int maxBuy = Integer.MIN_VALUE;
		double lowerBound;
		double upperBound;

		if (sellingChaos) {
			sellChaosValue = foundExchange.get_BuyChaosValue();
			missingId = foundExchange.get_buyId();
			buyChaosValue = 1;
			missingExchange = new Exchange(missingId, 4, false, _webCurrenciesMap.get(missingId), _chaosCurrency);
			_webCurrenciesMap.get(missingId).get_webOffersMap().put(4, missingExchange);
			maxSell = foundExchange.get_maxBuy();
			maxBuy =  foundExchange.get_maxSell();
			lowerBound = foundExchange.get_SellBuyLowerBound() * MINIMUM_RATIO_MULTIPLIER;
			upperBound = foundExchange.get_SellBuyUpperBound() * MAXIMUM_RATIO_MULTIPLIER;
		} else {
			missingId = foundExchange.get_sellId();
			sellChaosValue = 1;
			buyChaosValue = foundExchange.get_SellChaosValue();
			missingExchange = new Exchange(4, missingId, false, _chaosCurrency, _webCurrenciesMap.get(missingId));
			_chaosCurrency.get_webOffersMap().put(missingId, missingExchange);
			maxSell = foundExchange.get_maxBuy();
			maxBuy =  foundExchange.get_maxSell();
			lowerBound = foundExchange.get_SellBuyLowerBound() * MINIMUM_RATIO_MULTIPLIER;
			upperBound = foundExchange.get_SellBuyUpperBound() *  MAXIMUM_RATIO_MULTIPLIER;
		}
		missingExchange.set_SellChaosValue(sellChaosValue);
		missingExchange.set_BuyChaosValue(buyChaosValue);
		missingExchange.setMinMaxAmounts(0, 0, maxSell, maxBuy);
		missingExchange.setBounds(lowerBound, upperBound);
		System.out.println("Missing ex: " + missingExchange.toString() + '\n');
	}
	
	
//--------------------------------------------------------------Runnable--------------------------------------------------------------//

	//--------------------
	private class StatAnalyser implements Runnable {
		private final Exchange _exchange;

		//--------------------
		StatAnalyser(Exchange exchange){
			_exchange = exchange;
		}

		//--------------------
		@Override
		public void run() {
			analyseChaosExchange(_exchange);
		}
	}

}


