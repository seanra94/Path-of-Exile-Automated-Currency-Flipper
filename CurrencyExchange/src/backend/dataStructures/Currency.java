package backend.dataStructures;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Currency {
	
//--------------------------------------------------------------Fields--------------------------------------------------------------//

	private final String _name;
	private final String _shortName;
	 
	private final int _id; //Selling this currency
	private final Map<Integer, Exchange> _webOffersMap; //Buying these currencies
	
	private String _currTag;
	private boolean _isCommon;
	private double _chaosValue;
	private int _maxSell;
	private int _minSell;
	private Long _stock;
	private String _stashType;
	private int _stashXPos;
	private int _stashYPos;
	
//--------------------------------------------------------------Constructor--------------------------------------------------------------//
	
	//--------------------
	public Currency(String name, String shortName, int id) {
		_name = name;
		shortName = shortName.substring(0, 1).toUpperCase() + shortName.substring(1);
		_shortName = shortName;
		_id = id;
		_webOffersMap = new HashMap<Integer, Exchange>();
		_isCommon = false;
		set_stock((long) 0);
	}
	
//--------------------------------------------------------------Methods--------------------------------------------------------------//
	
	//--------------------
	public void addWebOffer(int buyId, int sellValue, int buyValue, boolean isCommon, Currency buyCurrency) {

		if (_webOffersMap.containsKey(buyId)) {
			_webOffersMap.get(buyId).addExchangeData(sellValue, buyValue);
		} else { //Create new exchange and add exchangeData to it
			_webOffersMap.put(buyId, new Exchange(_id, buyId, sellValue, buyValue, isCommon, this, buyCurrency));
		}
	}
	
	//--------------------
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(
				"********************************************************************************************************\n" +
				_id + ": " + _name + " (" + _shortName + ")\n");
		for (Entry<Integer, Exchange> entry : _webOffersMap.entrySet()) {
			sb.append(entry.getValue().toString());
		}
		return sb.toString();
	}

//--------------------------------------------------------------Getters and Setters--------------------------------------------------------------//

	public void set_isCommon(boolean _isCommon) {
		this._isCommon = _isCommon;
	}
	public String get_name() {
		return _name;
	}
	public String get_shortName() {
		return _shortName;
	}
	public int get_id() {
		return _id;
	}
	public Map<Integer, Exchange> get_webOffersMap() {
		return _webOffersMap;
	}
	public double get_chaosValue() {
		return _chaosValue;
	}
	public void set_chaosValue(double chaosValue) {
		_chaosValue = chaosValue;
	}
	public void set_StockRange(int minSell, int maxSell) {
		_minSell = Math.min(_minSell, minSell);
		_maxSell = Math.max(_maxSell, maxSell);
	}
	public boolean is_Common() {
		return _isCommon;
	}
	public int get_maxSell() {
		return _maxSell;
	}
	public int get_minSell() {
		return _minSell;
	}

	public Long get_stock() {
		return _stock;
	}

	public void set_stock(Long _stock) {
		this._stock = _stock;
	}

	public String getStashType() {
		return _stashType;
	}

	public int getStashXPos() {
		return _stashXPos;
	}

	public int getStashYPos() {
		return _stashYPos;
	}

	public void setStashInfo(String currTag, String stashType, int stashXPos, int stashYPos) {
		_currTag = currTag;
		_stashType = stashType;
		_stashXPos = stashXPos;
		_stashYPos = stashYPos;
	}

	public String get_currTag() {
		return _currTag;
	}




}