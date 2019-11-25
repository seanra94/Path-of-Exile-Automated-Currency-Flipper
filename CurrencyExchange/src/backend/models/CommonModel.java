package backend.models;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import backend.Global;
import backend.dataStructures.Currency;
import backend.dataStructures.Exchange;
import backend.dataStructures.ExchangeData;

public class CommonModel {

//--------------------------------------------------------------Fields--------------------------------------------------------------//
	private static final double MAXIMUM_RATIO_MULTIPLIER = Global.getMaximumRatioMultiplier();
	private static final int MAX_ULR_EXCHANGE_COUNT = 10;
	private static Map<Integer, Currency> _webCurrenciesMap = Global.get_currMapNoC();
	private static List<Integer> _commonIdList = Global.getCommonidlist();
		
//--------------------------------------------------------------Methods--------------------------------------------------------------//

	//--------------------
	public static void generateCommonExchanges() {
		CommonModel commonModel = new CommonModel();
		
		if (_commonIdList.size() != 0) {
			ExecutorService es = Executors.newFixedThreadPool(_commonIdList.size());
			for (Integer sellId : _commonIdList) {
				es.execute(commonModel.new CommonAnalyser(_webCurrenciesMap.get(sellId)));
			}
			es.shutdown();	
			try {
				es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	//--------------------
	private static void generateCommonExchange(Currency sellCurrency) {
		
		int sellId = sellCurrency.get_id();
		Map<Integer, Exchange> sellCurrMap = sellCurrency.get_webOffersMap();
		double sellChaosValue = sellCurrency.get_chaosValue();
		Exchange sellCurrBuyChaos = _webCurrenciesMap.get(sellId).get_webOffersMap().get(4);
		int maxSell;
		
		Currency buyCurrency;
		double buyChaosValue;
		int maxBuy;
		
		Exchange exchange;
		List<ExchangeData> exDataList;
		double buySellRatio;
		double lowerBound = 0;
		double upperBound = 0;
		
		newExchangeLoop: for (Integer buyId : _commonIdList) {
			if (sellId == buyId) {
				continue;
			}
			buyCurrency = _webCurrenciesMap.get(buyId);
			buyChaosValue = buyCurrency.get_chaosValue();
			exchange = sellCurrMap.get(buyId);

			//Setup exchange
			if (exchange == null) {
				exchange = new Exchange(sellId, buyId, true, sellCurrency, buyCurrency);
				sellCurrency.get_webOffersMap().put(buyId, exchange);
			}
			
			exchange.set_SellChaosValue(sellChaosValue);
			exchange.set_BuyChaosValue(buyChaosValue);

			maxSell = (int) Math.round(sellCurrency.get_maxSell() * 1.25);
			maxBuy =  (int) Math.round(buyCurrency.get_maxSell() * 1.25);

			//Set bounds
			exchange.setMinMaxAmounts(0, 0, maxSell, maxBuy);
			exDataList = exchange.get_exchangeDataList();
			try {
				lowerBound = sellCurrBuyChaos.get_BuySellLowerBound() / buyChaosValue;
				upperBound = (sellCurrBuyChaos.get_BuySellUpperBound() / buyChaosValue) * MAXIMUM_RATIO_MULTIPLIER;
			} catch (NullPointerException e) {
				System.out.println("Selling: " + sellId + " Buying: " + buyId);
				e.printStackTrace();
			}
			
			if (exDataList.size() == MAX_ULR_EXCHANGE_COUNT) {
				upperBound = Math.min(upperBound, exDataList.get(exDataList.size() - 1).get_buySellRatio());
			}
			
			exchange.setBounds(lowerBound, upperBound);
			
			//Remove outliers and add good exchanges to ratio set
			if (exDataList.size() > 0) {
				for (ExchangeData exData : exchange.get_exchangeDataList()) {
					buySellRatio = exData.get_buySellRatio();
					if (buySellRatio < lowerBound || buySellRatio > upperBound) {
						exchange.addOutlier(exData);
					} else {
						exchange.addToRatioSet(exData);
					}
				}
				for (ExchangeData outlier : exchange.get_outlierList()) {
					exDataList.remove(outlier);
				}
			}
			continue newExchangeLoop;
		}
	}
//--------------------------------------------------------------Runnable--------------------------------------------------------------//
	
	//--------------------
	private class CommonAnalyser implements Runnable {
		private final Currency _sellCurrency;

		//--------------------
		CommonAnalyser(Currency sellCurrency){
			_sellCurrency = sellCurrency;
		}
		
		//--------------------
		@Override
		public void run() {
			generateCommonExchange(_sellCurrency);
		}
	}

}
