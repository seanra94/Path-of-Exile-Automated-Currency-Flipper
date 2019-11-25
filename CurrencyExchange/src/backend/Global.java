package backend;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import backend.dataStructures.Currency;
import backend.dataStructures.Exchange;
import backend.dataStructures.ExchangeData;
import gui.eventHandling.DataGeneratedEvent;
import gui.eventHandling.DataGeneratedListener;

public class Global {

//--------------------------------------------------------------Fields--------------------------------------------------------------//

	//Constants
	public static MathContext mc = new MathContext(8, RoundingMode.HALF_EVEN);
	private static final String LEAGUE = "00000";
	private static final String APIKEY = "00000";
	private static final String ACCOUNT_NAME = "00000";
	private static final String EMAIL_ADDRESS = "00000";
	private static final String EMAIL_PASSWORD = "00000";
	private static final String FORUM_THREAD_ID = "00000";

	private static final boolean DELETE_ONLY = false; // Program only deletes trades and then exits
	
	//For finding exchanges
	private static final int PARTS = 4; 
	private static final int COMMON_COUNT = 15; // (Default 35) The minimum number of online exchanges buying or selling this currency for chaos such that this currency is considered "common"

	//Writing to text
	private static final int MAX_ELEMENTS_PER_LINE = 4;
	private static final int MAX_SPACES = 22;
	
	//Statistical analysis
	private static final int MAX_ULR_EXCHANGE_COUNT = 12; // (Default 12) The first X exchanges that are used for statistical analysis
	private static final double STANDARD_DEVIATION_MULTIPLIER = 1; // (Default 1) An exchange is an outlier if its ratio is outside STANDARD_DEVIATION_MULTIPLIER * the mean  (Lower = tighter)
	private static final double MEDIAN_ERROR_MULTIPLIER = 1; // (Default 1) Higher = tighter

	//private static final double MAX_COMMON_EXCHANGE_VALUE = 50;
	//private static final double MAX_FREQ_EXCHANGE_VALUE = 75;
	//private static final double MAX_INFREQ_EXCHANGE_VALUE = 7;
	
	//Stock and price ranges
	private static final double MAX_EXCHANGE_VALUE = 55; // (Default 55) The maximum chaos value of an exchange 
	private static final double MINIMUM_COMMON_PROFIT = 5.5; // (Default 11) The minimum profit for common exchanges (e.g. selling alts, buying fuses)
	private static final double MINIMUM_FREQUENT_PROFIT = 10; // (Default 20) The minimum profit for frequent exchanges (e.g. selling alts, buying chaos)
	private static final double MINIMUM_INFREQUENT_PROFIT = 10; // (Default 20) The minimum profit for infrequent exchanges (e.g. selling fragments, buying chaos)
	private static final double MINIMUM_PROFIT_VALUE_RATIO = 0.075; // (Default 0.125)
	
	//Misc
	private static final double SECOND_CHANCE_PROFIT_MULTIPLIER = 1; // (Default 1)
	private static final double MINIMUM_RATIO_MULTIPLIER = 1.1; // (Default 1.1)
	private static final double MAXIMUM_RATIO_MULTIPLIER = 1.3; // (Default 1.3)
	
	//Maximum profit
	//The Maximum profit is Max(MAXIMUM_PROFIT_STATIC_VALUE, MAXIMUM_PROFIT_MULTIPLIER * MINIMUM_X_PROFIT)
	private static final double MAXIMUM_PROFIT_STATIC_VALUE = 15; // // (Default 10)
	private static final double MAXIMUM_PROFIT_MULTIPLIER = 1.25; // (Default 1.25)
	
	private static Map<Integer, Currency> _currMapNoC;
	private static List<Exchange> _missingSellChaosList;
	private static List<Exchange> _missingBuyChaosList;
	private static List<Integer> _commonIdList;
	private static List<ExchangeData> _finalOfferList;
	private static Currency _chaosCurrency;
	private static Map<Integer, String> _idToNameMap;
	private static Map<String, Currency> _nameToCurrencyMap;
	private static final EventListenerList _listenerList = new EventListenerList();
	
	private static final int DEFAULT_X_GAP = 10;
	private static final int DEFAULT_Y_GAP = 10;
	private static final int X_RES = 1920;
	private static final int Y_RES = 1080;
	
//--------------------------------------------------------------Methods--------------------------------------------------------------//

	public static void reset() {
		_currMapNoC = new HashMap<Integer, Currency>();
		_idToNameMap = new HashMap<Integer, String>();
		_missingSellChaosList = new ArrayList<Exchange>();
		_missingBuyChaosList = new ArrayList<Exchange>();
		_commonIdList = new ArrayList<Integer>();
		_finalOfferList = new ArrayList<ExchangeData>();
		_chaosCurrency = null;
	}
	
//--------------------------------------------------------------Event Handling--------------------------------------------------------------//

	//**********DataGenerated event handling**********//
	public static void addDataGeneratedListener(DataGeneratedListener listener) {
		_listenerList.add(DataGeneratedListener.class, listener);
	}

	public static void fireDataGeneratedEvent() throws Exception {
		_currMapNoC.put(4, _chaosCurrency);
		
		DataGeneratedEvent event = new DataGeneratedEvent(Global.class, _currMapNoC, _finalOfferList);
		
		Object[] listeners = _listenerList.getListenerList();

		for(int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == DataGeneratedListener.class) {
				((DataGeneratedListener) listeners[i + 1]).DataGeneratedEventOccured(event);
			}
		}
	}
	
//--------------------------------------------------------------Getters and Setters--------------------------------------------------------------//

	public static double getMinimumFrequentProfit() {
		return MINIMUM_FREQUENT_PROFIT;
	}

	public static double getMinimumInfrequentProfit() {
		return MINIMUM_INFREQUENT_PROFIT;
	}
	
	public static MathContext getMc() {
		return mc;
	}

	public static void setMc(MathContext mc) {
		Global.mc = mc;
	}

	public static Currency get_chaosCurrency() {
		return _chaosCurrency;
	}

	public static void set_chaosCurrency(Currency _chaosCurrency) {
		Global._chaosCurrency = _chaosCurrency;
	}

	public static Map<Integer, Currency> get_currMapNoC() {
		return _currMapNoC;
	}
	
	public static void set_currMapNoC(Map<Integer, Currency> _webCurrenciesMap) {
		Global._currMapNoC = _webCurrenciesMap;
	}

	public static List<Integer> getCommonidlist() {
		return _commonIdList;
	}

	public static void set_commonIdList(List<Integer> _commonIdList) {
		Global._commonIdList = _commonIdList;
	}

	public static List<Exchange> get_missingSellChaosList() {
		return _missingSellChaosList;
	}

	public static void set_missingSellChaosList(List<Exchange> _missingSellChaosList) {
		Global._missingSellChaosList = _missingSellChaosList;
	}

	public static List<Exchange> get_missingBuyChaosList() {
		return _missingBuyChaosList;
	}

	public static void set_missingBuyChaosList(List<Exchange> _missingBuyChaosList) {
		Global._missingBuyChaosList = _missingBuyChaosList;
	}
	
	//Get constants
	public static String getLeague() {
		return LEAGUE;
	}

	public static int getParts() {
		return PARTS;
	}

	public static int getCommonCount() {
		return COMMON_COUNT;
	}
	
	public static int getMaxUlrExchangeCount() {
		return MAX_ULR_EXCHANGE_COUNT;
	}
	
	public static int getMaxElementsPerLine() {
		return MAX_ELEMENTS_PER_LINE;
	}

	public static int getMaxSpaces() {
		return MAX_SPACES;
	}

	public static double getMinimumCommonProfit() {
		return MINIMUM_COMMON_PROFIT;
	}

	public static double getSecondChanceProfitMultiplier() {
		return SECOND_CHANCE_PROFIT_MULTIPLIER;
	}

	public static double getMaxExchangeValue() {
		return MAX_EXCHANGE_VALUE;
	}

	public static double getMinimumRatioMultiplier() {
		return MINIMUM_RATIO_MULTIPLIER;
	}

	public static double getMaximumRatioMultiplier() {
		return MAXIMUM_RATIO_MULTIPLIER;
	}

	public static List<Integer> get_commonIdList() {
		return _commonIdList;
	}

	public static List<ExchangeData> get_finalOfferList() {
		return _finalOfferList;
	}

	public static void set_finalOfferList(List<ExchangeData> _finalOfferList) {
		Global._finalOfferList = _finalOfferList;
	}

	public static boolean isDeleteOnly() {
		return DELETE_ONLY;
	}

	public static double getMinimumProfitValueRatio() {
		return MINIMUM_PROFIT_VALUE_RATIO;
	}

	public static int getDefaultXGap() {
		return DEFAULT_X_GAP;
	}

	public static int getDefaultYGap() {
		return DEFAULT_Y_GAP;
	}

	public static int getxRes() {
		return X_RES;
	}

	public static int getyRes() {
		return Y_RES;
	}

	public static double getMaximumProfitStaticValue() {
		return MAXIMUM_PROFIT_STATIC_VALUE;
	}

	public static double getMaximumProfitMultiplier() {
		return MAXIMUM_PROFIT_MULTIPLIER;
	}

	public static String getApikey() {
		return APIKEY;
	}

	public static Map<Integer, String> get_currencyNameMap() {
		return _idToNameMap;
	}

	public static void set_currencyNameMap(Map<Integer, String> _currencyNameMap) {
		Global._idToNameMap = _currencyNameMap;
	}

	public static double getStandardDeviationMultiplier() {
		return STANDARD_DEVIATION_MULTIPLIER;
	}

	public static double getMedianErrorMultiplier() {
		return MEDIAN_ERROR_MULTIPLIER;
	}

	public static String getEmailAddress() {
		return EMAIL_ADDRESS;
	}

	public static String getEmailPassword() {
		return EMAIL_PASSWORD;
	}

	public static String getAccountName() {
		return ACCOUNT_NAME;
	}

	public static Map<String, Currency> get_nameToCurrencyMap() {
		return _nameToCurrencyMap;
	}

	public static void set_nameToCurrencyMap(Map<String, Currency> _nameToCurrencyMap) {
		Global._nameToCurrencyMap = _nameToCurrencyMap;
	}

	public static String getForumThreadId() {
		return FORUM_THREAD_ID;
	}





	
	
}
