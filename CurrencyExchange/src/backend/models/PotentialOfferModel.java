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


public class PotentialOfferModel {
	
//--------------------------------------------------------------Fields--------------------------------------------------------------//
	private static final double SECOND_CHANCE_PROFIT_MULTIPLIER = Global.getSecondChanceProfitMultiplier();
	private static final double MINIMUM_FREQUENT_PROFIT = Global.getMinimumFrequentProfit();
	private static final double MINIMUM_INFREQUENT_PROFIT = Global.getMinimumInfrequentProfit();
	private static final double MINIMUM_COMMON_PROFIT = Global.getMinimumCommonProfit();
	private static Map<Integer, Currency> _currMapWithC;
	private static Currency _chaosCurrency;
	private static List<Exchange> _emptySellChaos;
	private static List<Exchange> _emptySellOther;
	private static double _minimumProfValRatio;


//--------------------------------------------------------------Methods--------------------------------------------------------------//
	
	//--------------------
	public static void generatePotentialOffers() {
		_currMapWithC = Global.get_currMapNoC();
		_chaosCurrency = Global.get_chaosCurrency();
		_minimumProfValRatio = Global.getMinimumProfitValueRatio();
		_emptySellChaos = new ArrayList<Exchange>();
		_emptySellOther = new ArrayList<Exchange>();
		
		PotentialOfferModel potentialOffersModel = new PotentialOfferModel();
		Map<Integer, Exchange> chaosMap = _chaosCurrency.get_webOffersMap();
		ExecutorService es = Executors.newFixedThreadPool(_currMapWithC.size() + chaosMap.size());
		
		
		
		for (Entry<Integer, Currency> currencyEntry : _currMapWithC.entrySet()) {
			for (Entry<Integer, Exchange> exchangeEntry : currencyEntry.getValue().get_webOffersMap().entrySet()) {
				Exchange exchange = exchangeEntry.getValue();
				if (currencyEntry.getValue().is_Common()) {
					if (exchange.get_buyId() == 4) { //Sell Curr, buy Chaos
						System.out.println("(1a) " + exchange.get_sellId() + ", " + exchange.get_buyId() + " is frequent!");
						exchange.set_minimumProfit(MINIMUM_FREQUENT_PROFIT);
					} else {//Sell Curr, buy Curr
						exchange.set_minimumProfit(MINIMUM_COMMON_PROFIT);
						System.out.println("(2a) " + exchange.get_sellId() + ", " + exchange.get_buyId() + " is common!");
					}
				} else {
					exchange.set_minimumProfit(MINIMUM_INFREQUENT_PROFIT);
					System.out.println("(3a) " + exchange.get_sellId() + ", " + exchange.get_buyId() + " is infrequent!");
				}
				es.execute(potentialOffersModel.new OfferAnalyser(exchangeEntry.getValue()));
			}
		}
		for (Entry<Integer, Exchange> exchangeEntry : chaosMap.entrySet()) {
			Exchange exchange = exchangeEntry.getValue();
			if (_currMapWithC.get(exchange.get_buyId()).is_Common()) {
				System.out.println("(1b) " + exchange.get_sellId() + ", " + exchange.get_buyId() + " is frequent!");
				exchange.set_minimumProfit(MINIMUM_FREQUENT_PROFIT);
			} else {
				System.out.println("(2b) " + exchange.get_sellId() + ", " + exchange.get_buyId() + " is infrequent!");
				exchange.set_minimumProfit(MINIMUM_INFREQUENT_PROFIT);
			}
			es.execute(potentialOffersModel.new OfferAnalyser(exchangeEntry.getValue()));
		}
		
		es.shutdown();	
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		removeEmptyExchanges();
	}

	//--------------------
	private void generatePotentialOffer(Exchange exchange, boolean standardsLowered) {
		
		//Remove empty exchanges
		if (exchange.is_isEmpty()) {
			if (exchange.get_sellId() == 4) {
				_emptySellChaos.add(exchange);
				return;
			} else {
				_emptySellOther.add(exchange);
				return;
			}
			
		}
		List<ExchangeData> potentialList = new ArrayList<ExchangeData>();
		int numMin;		int numMax;
		int denMin;		int denMax;
		int numDist;	int denDist;
		int lb;			int ub;
		int min;		int max;

		double sellChaosValue = exchange.get_SellChaosValue();
		double buyChaosValue = exchange.get_BuyChaosValue();
		double minimumProfit = exchange.get_minimumProfit();
		int minProfitInt;

		numMin = exchange.get_minBuy();
		numMax = exchange.get_maxBuy();
		denMin = exchange.get_minSell();
		denMax = exchange.get_maxSell();

		numDist = numMax - numMin;
		denDist = denMax - denMin;
		potentialList = new ArrayList<ExchangeData>();

		if (numDist > denDist) {
			denLoop: for (int d = denMin; d <= denMax; d++) {
				
				lb = (int) Math.ceil(exchange.get_BuySellLowerBound() * d);
				ub = (int) Math.floor(exchange.get_BuySellUpperBound() * d);
				minProfitInt = (int) Math.ceil((minimumProfit + d * sellChaosValue)/buyChaosValue);
				max = Math.min(numMax, ub);

				//Make big
				if (lb > numMin) {
					if (minProfitInt > lb) {
						if (minProfitInt <= max) {
							min = minProfitInt;
						} else {
							continue denLoop;
						}
					} else {
						min = lb;
					}
				} else {
					if (minProfitInt > numMin) {
						if (minProfitInt <= max) {
							min = minProfitInt;
						} else {
							continue denLoop;
						}
					} else {
						min = numMin;
					}
				}
				
				max = Math.max(max, min);

				double minProfitValueRatioBound = _minimumProfValRatio * sellChaosValue * d; //THIS IS BAD CODE, REPLACE LATER
				double dValue = d * sellChaosValue; //THIS IS BAD CODE, REPLACE LATER
				for (int n = min; n <= max; n++) {
					if (n * buyChaosValue - dValue > minProfitValueRatioBound) { //THIS IS BAD CODE, REPLACE LATER
						potentialList.add(new ExchangeData(d, n));		
					}
												
				}
			}
			if (potentialList.size() != 0) {
				Collections.sort(potentialList, new RatioComparitor());
			} else {
				if (!standardsLowered && (exchange.get_buyId() == 4 || exchange.get_sellId() == 4)) {
					exchange.set_minimumProfit(minimumProfit * SECOND_CHANCE_PROFIT_MULTIPLIER);
					generatePotentialOffer(exchange, true);
					return;
				} else {
					System.out.println("WARNING: No potentials found. exchange: " + exchange.toString());
				}
			}
			exchange.set_potentialExchangeDataList(potentialList);
	
		} else {
			numLoop: for (int n = numMin; n <= numMax; n++) {

				lb = (int) Math.floor((exchange.get_SellBuyLowerBound()) * n);
				ub = (int) Math.ceil((exchange.get_SellBuyUpperBound()) * n);
				minProfitInt = (int) Math.floor((n * buyChaosValue - minimumProfit)/sellChaosValue);
				min = Math.max(denMin, lb);

				//Make small
				if (denMax > ub) {

					if (ub > minProfitInt) {
						if (minProfitInt >= min) {
							max = minProfitInt;
						} else {
							continue numLoop;
						}
					} else {
						max = ub;
					}
				} else {
					if (denMax > minProfitInt) {
						if (minProfitInt >= min) {
							max = minProfitInt;
						} else {
							continue numLoop;
						}
					} else {
						max = denMax;
					}
				}
				
				max = Math.max(max, min);

				for (int d = min; d <= max; d++) {
					
					if (n * buyChaosValue - d * sellChaosValue > _minimumProfValRatio * sellChaosValue * d) { //THIS IS BAD CODE, REPLACE LATER
						potentialList.add(new ExchangeData(d, n));		
					}
					
													
				}
			}
			if (potentialList.size() != 0) {
				Collections.sort(potentialList, new RatioComparitor());
			} else {
				
				if (!standardsLowered && (exchange.get_buyId() == 4 || exchange.get_sellId() == 4)) {
					exchange.set_minimumProfit(minimumProfit * SECOND_CHANCE_PROFIT_MULTIPLIER);
					generatePotentialOffer(exchange, true);
					return;
				} else {
					System.out.println("WARNING: No potentials found. exchange: " + exchange.toString());
				}
			}
	
			exchange.set_potentialExchangeDataList(potentialList);
		}	
	};
	
	private static void removeEmptyExchanges() {
		Map<Integer, Exchange> chaosMap = _chaosCurrency.get_webOffersMap();
		for (Exchange exchange : _emptySellChaos) {
			chaosMap.remove(exchange.get_buyId());
		}
		for (Exchange exchange : _emptySellOther) {
			_currMapWithC.get(exchange.get_sellId()).get_webOffersMap().remove(exchange.get_buyId());
		}
	}

//--------------------------------------------------------------Runnable--------------------------------------------------------------//
	public class RatioComparitor implements Comparator<ExchangeData> {
		@Override
		public int compare(ExchangeData e1, ExchangeData e2) {
			Double v1 = e1.get_buySellRatio();
			return v1.compareTo(e2.get_buySellRatio());
		}
	}	

	//--------------------
	private class OfferAnalyser implements Runnable {
		private final Exchange _exchange;

		//--------------------
		OfferAnalyser(Exchange exchange){
			_exchange = exchange;
		}

		//--------------------
		@Override
		public void run() {
			generatePotentialOffer(_exchange, false);
		}
	}


}
