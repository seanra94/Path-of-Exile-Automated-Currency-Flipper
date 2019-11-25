package backend.dataStructures;

import java.text.DecimalFormat;

public class ExchangeData {
	
//--------------------------------------------------------------Fields--------------------------------------------------------------//
	
	private final static DecimalFormat df = new DecimalFormat("#.####");
	private final static DecimalFormat df2 = new DecimalFormat("#.##");
	
	private final int _sellValue; 
	private final int _buyValue;
	private final double _sellBuyRatio;
	private final double _buySellRatio; //Bigger = more profitable
	
	private int _sellId;
	private int _buyId;
	private double _chaosProfit;
	
//--------------------------------------------------------------Constructor--------------------------------------------------------------//

	public ExchangeData(int sellValue, int buyValue) {
		_sellValue = sellValue;
		_buyValue = buyValue;
		_sellBuyRatio = (double) sellValue/buyValue;
		_buySellRatio = (double) buyValue/sellValue;
	}
	
//--------------------------------------------------------------Methods--------------------------------------------------------------//

	@Override
	public String toString() {
		return "[sellValue = " + _sellValue + " buyValue = " + _buyValue + " sellBuyRatio = " + df.format(_sellBuyRatio) + " buySellRatio = " + df.format(_buySellRatio) + "]";
	}

	public String toString(double sellChaosValue, double buyChaosValue) {
		return "[" + _buyValue + "/" + _sellValue + " (" + df.format(_buySellRatio) +
		") $" + df2.format(_buyValue * buyChaosValue - _sellValue * sellChaosValue) + "]";
 	}
	
	
	public String toString(int maxSpaces, double sellChaosValue, double buyChaosValue) {
		return 
		_sellValue 					+ addSpaces(String.valueOf(_sellValue), maxSpaces) 	+
		_buyValue 					+ addSpaces(String.valueOf(_buyValue), maxSpaces) 	+ 
		df.format(_sellBuyRatio) 	+ addSpaces(df.format(_sellBuyRatio), maxSpaces) 	+ 
		df.format(_buySellRatio) 	+ addSpaces(df.format(_buySellRatio), maxSpaces)	+
		"$" + df2.format(_buyValue * buyChaosValue - _sellValue * sellChaosValue);
 	}
	
	private String addSpaces(String pastString, int maxSpaces) {
		int spaceCount = maxSpaces - pastString.length();
		return new String(new char[spaceCount]).replace('\0', ' ');
	}
	
//--------------------------------------------------------------Getters and Setters--------------------------------------------------------------//

	public int get_sellValue() {
		return _sellValue;
	}

	public int get_buyValue() {
		return _buyValue;
	}

	public double get_sellBuyRatio() {
		return _sellBuyRatio;
	}

	public double get_buySellRatio() {
		return _buySellRatio;
	}

	public int get_sellId() {
		return _sellId;
	}

	public int get_buyId() {
		return _buyId;
	}

	public void set_finalOffer(int sellId, int buyId, double chaosProfit) {
		_sellId = sellId;
		_buyId = buyId;
		_chaosProfit = chaosProfit;
	}
	
	public void set_finalOffer(int sellId, int buyId, double sellChaosValue, double buyChaosValue) {
		set_finalOffer(sellId, buyId, buyChaosValue * _buyValue - sellChaosValue * _sellValue);
	}

	public double get_chaosProfit() {
		return _chaosProfit;
	}


	
}
