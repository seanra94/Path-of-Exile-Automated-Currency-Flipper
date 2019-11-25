package backend.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import backend.Global;
import backend.dataStructures.Currency;
import backend.dataStructures.Exchange;

public class CurrencyModel {

//--------------------------------------------------------------Fields--------------------------------------------------------------//

	private static String LEAGUE = Global.getLeague();
	private static int PARTS = Global.getParts();
	private static int COMMON_COUNT = Global.getCommonCount();

	private static Map<Integer, Currency> _currMapNoC;
	private static Map<Integer, String> _idToNameMap; //FIX LATER (REDUNDANT)
	private static Map<String, Currency> _nameToCurrencyMap;
	private static List<Integer> _commonIdList;
	private static List<Integer> _idList;
	private static Currency _chaosCurrency;
	
	private static ExecutorService _executorService;
	private static List<Exchange> _missingSellChaosList;
	private static List<Exchange> _missingBuyChaosList;
	private static String _idString;
	private static String _commonIdString;
	private static CountDownLatch latch;

	
//--------------------------------------------------------------Anonymous Class--------------------------------------------------------------//
	
	//--------------------
	public static void generateAllCurrencyData() {
		_executorService = Executors.newCachedThreadPool();
		_currMapNoC = new HashMap<Integer, Currency>();
		_idToNameMap = new HashMap<Integer, String>();
		_nameToCurrencyMap = new HashMap<String, Currency>();
		_commonIdList = new ArrayList<Integer>();
		_idList = new ArrayList<Integer>();
		_missingSellChaosList = new ArrayList<Exchange>();
		_missingBuyChaosList = new ArrayList<Exchange>();
		CurrencyModel currencyModel = new CurrencyModel();
		
		findCurrencies();
	
		String[] partsArray = splitIdString(_idString, PARTS);
		//Get chaos exchanges
		for (int i = 0; i < partsArray.length; i++) {
			_executorService.execute(currencyModel.new WebCrawler (partsArray[i], "4", true));
			_executorService.execute(currencyModel.new WebCrawler ("4", partsArray[i], true));
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Latch 1 Open");
		checkCurrencies();
		System.out.println("Id String: " + _idString);
		System.out.println("Common Id String: " + _commonIdString);
		latch = new CountDownLatch(_commonIdList.size());
		
		//Get common exchanges
		for (Integer id : _commonIdList) {
			_executorService.execute(currencyModel.new WebCrawler (id.toString(), removeIdFromString(id, _commonIdString), true));
		}	

		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Latch 2 Open");
		_executorService.shutdown();	
		try {
			_executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		
		Global.set_chaosCurrency(_chaosCurrency);
		Global.set_commonIdList(_commonIdList);
		Global.set_currMapNoC(_currMapNoC);
		Global.set_missingBuyChaosList(_missingBuyChaosList);
		Global.set_missingSellChaosList(_missingSellChaosList);
		Global.set_currencyNameMap(_idToNameMap);
		Global.set_nameToCurrencyMap(_nameToCurrencyMap);
	}

//--------------------------------------------------------------Methods--------------------------------------------------------------//

	//--------------------
	private static void findCurrencies() {
		try {
			Element element = Jsoup.connect("http://currency.poe.trade").get();
			Node primaryNode = element.getElementById("cat-want-0").childNode(1);
			Attributes attributes;
			Integer id;
			StringBuilder sb = new StringBuilder();
			String title;

			for (int i = 1; i < primaryNode.childNodeSize(); i+=2) {
				try {
					attributes = primaryNode.childNode(i).attributes();
					id = Integer.parseInt(attributes.get("data-id"));
					title = attributes.get("title");
					if (title != null && title != "") {
						_currMapNoC.put(id, new Currency(attributes.get("title"), attributes.get("data-title"), id));
						sb.append(id + "-");
						System.out.println("Id = " + id + " title = " + title + " data-title = " + attributes.get("data-title")); // DELETE LATER
					} else {
						System.out.println("Unable to find title for currency with id: " + id);
					}
					
					
				} catch (IndexOutOfBoundsException | NumberFormatException e) {
					System.out.println("ERROR (findCurrencies): i = " + i);
					e.printStackTrace();
					return;
				}
			}
			//-----Enable for essence trading-----
			
			/*
			Node essenceNode = element.getElementById("cat-want-1").childNode(3);
			for (int i = 1; i < essenceNode.childNodeSize(); i+=2) {
				try {
					attributes = essenceNode.childNode(i).attributes();
					id = Integer.parseInt(attributes.get("data-id"));
					title = attributes.get("title");
					if (returnMatch("(^essence.*|deafening.*)", title) != null) {
						_currMapNoC.put(id, new Currency(attributes.get("title"), attributes.get("data-title"), id));
						sb.append(id + "-");
					}
				} catch (IndexOutOfBoundsException | NumberFormatException e) {
					System.out.println("ERROR (findCurrencies): i = " + i);
					e.printStackTrace();
					return;
				}
			}
			*/
			sb.setLength(sb.length() - 1);
			_idString = sb.toString();
			latch = new CountDownLatch(PARTS * 2);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//--------------------
	private static void checkCurrencies() {
		_chaosCurrency = _currMapNoC.get(4);
		_chaosCurrency.get_webOffersMap().remove(4); //Remove chaos for chaos
		_currMapNoC.remove(4);
		_idToNameMap.put(4, "Chaos Orb");
		_nameToCurrencyMap.put("Chaos Orb", _chaosCurrency);
		
		Currency currency;
		int currencyID;
		Exchange buyChaosExchange;
		Exchange sellChaosExchange;
		List<Integer> emptyIdList = new ArrayList<Integer>();
		Map<Integer, Exchange> currencyOfferMap;
		Map<Integer, Exchange> chaosOfferMap = _chaosCurrency.get_webOffersMap();
		checkLoop: for (Entry<Integer, Currency> entry : _currMapNoC.entrySet()) {
			currency = entry.getValue();
			currencyID = currency.get_id();
			currencyOfferMap = currency.get_webOffersMap();
			buyChaosExchange = currencyOfferMap.get(4);
			sellChaosExchange = chaosOfferMap.get(currencyID);
			
			//Add Currency name to _currencyNameMap
			_idToNameMap.put(currencyID, currency.get_name());
			_nameToCurrencyMap.put(currency.get_name(), currency);
			
			//Check if empty
			if (buyChaosExchange == null && sellChaosExchange != null) {
				_missingBuyChaosList.add(sellChaosExchange);
				System.out.println("Sell " + currencyID  + " (" + currency.get_name() + ") Buy 4 "  + " is missing!");
				
			} else if (buyChaosExchange != null && sellChaosExchange == null) {	
				_missingSellChaosList.add(buyChaosExchange);
				System.out.println("Buy " + currencyID  + " (" + currency.get_name() + ") Sell 4 "  + " is missing!");
				
			} else if (buyChaosExchange == null && sellChaosExchange == null) {	
				emptyIdList.add(currencyID);
				System.out.println("(" + currencyID + ")"  + currency.get_name() + " is empty!");
				continue checkLoop;
			} else {
				//Check if common
				if (buyChaosExchange.get_exchangeDataList().size() >= COMMON_COUNT & sellChaosExchange.get_exchangeDataList().size() >= COMMON_COUNT) {
					currency.set_isCommon(true);
					_commonIdList.add(currencyID);
				}
			}
			_idList.add(currencyID);

		}
		//Remove empty currencies from map
		for (Integer emptyCurrencyId : emptyIdList) {
			_currMapNoC.remove(emptyCurrencyId);
			chaosOfferMap.remove(emptyCurrencyId);
		}		
		Collections.sort(_idList);
		Collections.sort(_commonIdList);
		StringBuilder sb = new StringBuilder();
		
		//Create common id list
		sb = new StringBuilder();
		for (Integer commonId : _commonIdList) {
			sb.append(commonId + "-");
		}
		if (sb.length() != 0) {
			sb.setLength(sb.length() - 1);
		}
		
		_commonIdString = sb.toString();
		_commonIdString = removeIdFromString(4, _commonIdString);
	}
	
	//--------------------
	/*
	private static String returnMatch(String regex, String searchString){
		Pattern p;
		try {
			p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		} catch (PatternSyntaxException e) {
			return null;
		}
		
		Matcher m = p.matcher(searchString);
		if (m.find()) {
			try {
				return m.group(1);
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
		} else {
			return null;
		}
	}
	*/
	//--------------------
	private static String[] splitIdString(String idString, int parts) {
		String[] partsArray = new String[parts];
		String[] splitArray = idString.split("-");

		for (int i = 0; i < parts; i++) {
			partsArray[i] = splitArray[i];
		}

		int counter = 0;
		for (int i = parts; i < splitArray.length; i++) {
			if (counter == parts) {
				counter = 0;
			}
			partsArray[counter] += "-" + splitArray[i];
			counter++;
		}

		return partsArray;
	}
	
	private static String removeIdFromString(int id, String commonString) {
		return commonString.replaceFirst("^" + id +"-|-" + id + "(-)|-" + id + "$", "$1");
	}

//--------------------------------------------------------------Runnable--------------------------------------------------------------//

	//--------------------
	private class WebCrawler implements Runnable {
		private final String _wantString;
		private final String _haveString;
		private final boolean _shouldCountdown;
		private boolean _isDone = false;

		//--------------------
		WebCrawler(String wantString, String haveString, boolean shouldCountdown){
			_wantString = wantString;
			_haveString = haveString;
			_shouldCountdown = shouldCountdown;
		}
		
		//--------------------
		@Override
		public void run() {
			findOffersForOneUrl();
			if (_shouldCountdown) {
				latch.countDown();
			}
			
			synchronized (this) {
			    _isDone = true;
			    notify();
			}
		}

		//--------------------
		private void findOffersForOneUrl() {
			String url = "http://currency.poe.trade/search?league=" + LEAGUE + "&online=x&want=" + _wantString + "&have=" + _haveString;
			try {
				Element content =  Jsoup.connect(url).get().getElementById("content");
				Elements relevantElements = content.select("div.displayoffer");

				splitIf : if (relevantElements.size() == 200) {//Indicates the search is missing one or more exchanges
					System.out.println("WARNING: Size == 200 for SELLING " + _wantString + " | BUYING " + _haveString); 
					if (!_wantString.contains("-") && !_haveString.contains("-")) { //Cannot split any further
						System.out.println("CRITICAL WARNING: Cannot split further. SELLING" + _wantString + " | BUYING: " + _haveString);
						break splitIf;
					}

					WebCrawler leftCrawler;
					WebCrawler rightCrawler;
					if (_wantString.length() > _haveString.length()) {
						String[] partsArray = splitIdString(_wantString, 2);
						leftCrawler = new WebCrawler (partsArray[0], _haveString, false);
						rightCrawler = new WebCrawler (partsArray[1], _haveString, false);
						_executorService.execute(leftCrawler);
						_executorService.execute(rightCrawler);

					} else {
						String[] partsArray = splitIdString(_haveString, 2);
						leftCrawler = new WebCrawler (_wantString, partsArray[0], false);
						rightCrawler = new WebCrawler (_wantString, partsArray[1], false);
						_executorService.execute(leftCrawler);
						_executorService.execute(rightCrawler);
					}

					synchronized (leftCrawler) {
						if (!leftCrawler._isDone) {
							try {
								leftCrawler.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}

					synchronized (rightCrawler) {
						if (!rightCrawler._isDone) {
							try {
								rightCrawler.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					return;
				}

				Attributes attributes;
				int sellId;
				int buyId;
				double sellValue;
				double buyValue;

				/*	Eg Selling 100 Alt and buying 10 Chaos
				 * 	data-sellcurrency = 1		data-buycurrency = 4
				 *  data-sellValue = 100		data-buyValue = 10
				 */

				for (Element element : relevantElements) {
					attributes = element.attributes();
					buyId = Integer.parseInt(attributes.get("data-buycurrency"));
					sellId = Integer.parseInt(attributes.get("data-sellcurrency"));
					sellValue = Double.parseDouble(attributes.get("data-sellvalue"));
					buyValue = Double.parseDouble(attributes.get("data-buyvalue"));

					if (sellValue % 1 == 0.5 || buyValue % 1 == 0.5) {
						sellValue *= 2;
						buyValue *= 2;
						if ((int) sellValue != sellValue || (int) buyValue != buyValue) {
							System.out.println("WARNING: Offer not an integer!" +
									" sellValue = (" + attributes.get("data-sellvalue") + ", " + sellValue + ")" +
									" buyValue = (" + attributes.get("data-buyvalue") + ", " + sellValue + ")");
							sellValue = Math.round(sellValue);
							buyValue = Math.round(buyValue);
						}
					}

					if (sellId == 4 || buyId == 4) {
						_currMapNoC.get(sellId).addWebOffer(buyId, (int) sellValue, (int) buyValue, false, _currMapNoC.get(buyId));
					} else {
						_currMapNoC.get(sellId).addWebOffer(buyId, (int) sellValue, (int) buyValue, true, _currMapNoC.get(buyId));
					}
				}
			} catch (IOException e) {
				System.out.println("MAJOR ERROR: URL = " + url);
				e.printStackTrace();
				findOffersForOneUrl();

			}
			System.out.println("Done! Selling: " + _wantString + " | Buying: " + _haveString);
		}
	}
}
