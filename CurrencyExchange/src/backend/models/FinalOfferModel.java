package backend.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import backend.Global;
import backend.dataStructures.Currency;
import backend.dataStructures.Exchange;
import backend.dataStructures.ExchangeData;

public class FinalOfferModel {
	
//--------------------------------------------------------------Fields--------------------------------------------------------------//

	private static Map<Integer, Currency> _currMapWithC;
	private static Currency _chaosCurrency;
	private static final double MAX_EXCHANGE_VALUE = Global.getMaxExchangeValue();
	private static final double MAXIMUM_PROFIT_STATIC_VALUE = Global.getMaximumProfitStaticValue();
	private static final double MAXIMUM_PROFIT_MULTIPLIER = Global.getMaximumProfitMultiplier();
//--------------------------------------------------------------mMethods--------------------------------------------------------------//

	//--------------------
	public static void generateFinalOffers() {
		_currMapWithC = Global.get_currMapNoC();
		_chaosCurrency = Global.get_chaosCurrency();
		Global.set_finalOfferList(new ArrayList<ExchangeData>());
		FinalOfferModel finalOfferModel = new FinalOfferModel();
		
		Map<Integer, Exchange> chaosMap = _chaosCurrency.get_webOffersMap();
		ExecutorService es = Executors.newFixedThreadPool(_currMapWithC.size() + chaosMap.size());
		
		for (Entry<Integer, Currency> currencyEntry : _currMapWithC.entrySet()) {
			for (Entry<Integer, Exchange> exchangeEntry : currencyEntry.getValue().get_webOffersMap().entrySet()) {
				es.execute(finalOfferModel.new FinalOfferAnalyser(exchangeEntry.getValue()));
			}
		}
		for (Entry<Integer, Exchange> exchangeEntry : chaosMap.entrySet()) {
			es.execute(finalOfferModel.new FinalOfferAnalyser(exchangeEntry.getValue()));
		}
		
		es.shutdown();	
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	//--------------------
	private void generateFinalOffer(Exchange exchange) {

		List<ExchangeData> ratioSet = exchange.get_exchangeRatioSet();
		List<ExchangeData> potentialList = exchange.get_potentialExchangeDataList();
		ExchangeData potential;
		int setSize = ratioSet.size();
		int oldIndex = 0;
		int newIndex = 0;
		int profitId = -1; //The index in the potentialList for 
		double bestProfit = 0;
		double minimalProfit = Integer.MAX_VALUE;
		int minimalId = -1;
		double testProfit;
		double ratioProfit;
		double sellChaosValue = exchange.get_SellChaosValue();
		double buyChaosValue = exchange.get_BuyChaosValue();
		double minimumProfit = exchange.get_minimumProfit();
		double idealProfit = Math.max(MAXIMUM_PROFIT_MULTIPLIER * minimumProfit, MAXIMUM_PROFIT_STATIC_VALUE);

		RatioComparitor ratioComparitor = new RatioComparitor();
		//i.e. There is at least one web offer for this exchange
		if (setSize != 0) {
			for (ExchangeData ratio : ratioSet) {
				
				ratioProfit = ratio.get_buyValue() * buyChaosValue - ratio.get_sellValue() * sellChaosValue;
				newIndex = Collections.binarySearch(potentialList, ratio, ratioComparitor); //The index of the (sorted by ratio) potential list whereby all potentials in the list after this index are higher than the given ratio
				for (int i = oldIndex; i < newIndex; i++) {
					potential = potentialList.get(i);
					testProfit = buyChaosValue * potential.get_buyValue() - sellChaosValue * potential.get_sellValue();
					if (testProfit > bestProfit && testProfit <= idealProfit) {
						profitId = i;
						bestProfit = testProfit;
					} else if (testProfit < minimalProfit) {
						minimalId = i;
						minimalProfit = testProfit;
					}
				}

				if (profitId != -1) {
					exchange.setAndAddFinalOffer(potentialList.get(profitId), bestProfit);
					return;
				} else if (minimalId != -1) {
					exchange.setAndAddFinalOffer(potentialList.get(minimalId), minimalProfit);
					return;
				} else if (ratioProfit >= minimumProfit && ratio.get_buyValue() * buyChaosValue <= MAX_EXCHANGE_VALUE && ratio.get_sellValue() * sellChaosValue <= MAX_EXCHANGE_VALUE) {
					exchange.setAndAddFinalOffer(ratio, ratioProfit);
					return;
				}

				bestProfit = 0;
				minimalProfit = Integer.MAX_VALUE;
				newIndex = oldIndex;
			}

		//i.e. There are no web offers for this exchange
		} else {
			for (int i = 0; i < potentialList.size(); i++) {
				potential = potentialList.get(i);
				testProfit = buyChaosValue * potential.get_buyValue() - sellChaosValue * potential.get_sellValue();
				if (testProfit > bestProfit && testProfit <= idealProfit) {
					profitId = i;
					bestProfit = testProfit;
				} else if (testProfit < minimalProfit) {
					minimalId = i;
					minimalProfit = testProfit;
				}
			}
			
			if (profitId != -1) {
				exchange.setAndAddFinalOffer(potentialList.get(profitId), bestProfit);
				return;
			} else if (minimalId != -1) {
				exchange.setAndAddFinalOffer(potentialList.get(minimalId), minimalProfit);
				return;
			} 
		}
		
		if (potentialList.size() != 0) {
			ExchangeData lastResort = potentialList.get(0);
			double lastResortProfit = lastResort.get_buyValue() * buyChaosValue - lastResort.get_sellValue() * sellChaosValue;
			exchange.setAndAddFinalOffer(lastResort, lastResortProfit);
			return;
		} else {
			//System.out.println("(!5) No Final Offer generated! " + exchange.toString());
			exchange.setAndAddFinalOffer(null, 0);
		}
	}
	
//--------------------------------------------------------------Runnable--------------------------------------------------------------//
	
	//--------------------
	public class RatioComparitor implements Comparator<ExchangeData> {
		@Override
		public int compare(ExchangeData e1, ExchangeData e2) {
			Double v1 = e1.get_buySellRatio();
			return v1.compareTo(e2.get_buySellRatio());
		}
	}	
	
	//--------------------
	private class FinalOfferAnalyser implements Runnable {
		private final Exchange _exchange;

		//--------------------
		FinalOfferAnalyser(Exchange exchange){
			_exchange = exchange;
		}
		
		//--------------------
		@Override
		public void run() {
			generateFinalOffer(_exchange);
		}
	}
}
