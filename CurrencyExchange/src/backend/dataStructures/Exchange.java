package backend.dataStructures;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import backend.Global;

public class Exchange {
	
//--------------------------------------------------------------Fields--------------------------------------------------------------//
	private static final DecimalFormat df = new DecimalFormat("#.####");
	private static final double MAX_EXCHANGE_VALUE = Global.getMaxExchangeValue();
	
	private final Currency _sellCurrency;
	private final Currency _buyCurrency;
	
	private final List<ExchangeData> _exchangeDataList;
	private final List<ExchangeData> _exchangeRatioSet;
	private List<ExchangeData> _outlierList;
	private List<ExchangeData> _potentialExchangeDataList;
	private ExchangeData _generatedExchange;
	private ExchangeData _finalOffer;
	private final int _sellId;
	private final int _buyId;
	private double _BuySellLowerBound;
	private double _BuySellUpperBound;
	private double _SellBuyLowerBound;
	private double _SellBuyUpperBound;
	private int _minSell;
	private int _minBuy;
	private int _maxSell;
	private int _maxBuy;
	private double _sellChaosValue;
	private double _buyChaosValue;
	private double _minimumProfit;
	private final boolean _isCommon;
	private boolean _isEmpty = false;
	private boolean _isFrequent;

//--------------------------------------------------------------Constructor--------------------------------------------------------------//

	public Exchange(int sellId, int buyId, boolean isCommon, Currency sellCurrency, Currency buyCurrency) {
		_sellId = sellId;
		_buyId = buyId;
		_isCommon = isCommon;
		_sellCurrency = sellCurrency;
		_buyCurrency = buyCurrency;
		
		_exchangeDataList = new ArrayList<ExchangeData>();
		_outlierList = new ArrayList<ExchangeData>();
		_potentialExchangeDataList = new ArrayList<ExchangeData>();
		_exchangeRatioSet = new ArrayList<ExchangeData>();
		
		System.out.println("New exchange created! sellID = " + sellId + " buyID = " + buyId 
				+ " sellCurrName = " + _sellCurrency.get_name() + " buyCurName = " + buyCurrency.get_name());
	}
	
	public Exchange(int sellId, int buyId, int sellValue, int buyValue, boolean isCommon, Currency sellCurrency, Currency buyCurrency) {
		this(sellId, buyId, isCommon, sellCurrency, buyCurrency);
		addExchangeData(sellValue, buyValue);
	}
	

//--------------------------------------------------------------Methods--------------------------------------------------------------//
	public void addExchangeData(int sellValue, int buyValue) {
		_exchangeDataList.add(new ExchangeData(sellValue, buyValue));
	}
	
	public void addToRatioSet(ExchangeData exData) {
		int size = _exchangeRatioSet.size();
		if (size != 0) {
			ExchangeData compExData = _exchangeRatioSet.get(_exchangeRatioSet.size() - 1);
			double compareBuySellRatio = compExData.get_buySellRatio();
			if (compareBuySellRatio == exData.get_buySellRatio()) {
				double exDataProf = exData.get_buyValue() * _buyChaosValue - exData.get_sellValue() * _sellChaosValue;
				double compExDataProf = compExData.get_buyValue() * _buyChaosValue - compExData.get_sellValue() * _sellChaosValue;
				if (exDataProf > compExDataProf) {
					return;
				} else {
					_exchangeRatioSet.remove(compExData);
				}
			}
		}
		_exchangeRatioSet.add(exData);
	}
	
	public void addOutlier(ExchangeData exData) {
		_outlierList.add(exData);
	}
	
	
//--------------------------------------------------------------Testing--------------------------------------------------------------//
	
	//--------------------
	private String addSpaces(String pastString, int maxSpaces) {
		int spaceCount = maxSpaces - pastString.length();
		if (spaceCount >= 0) {
			return new String(new char[spaceCount]).replace('\0', ' ');
		} else {
			System.out.println("Oddly large string: " + pastString + " Selling: " + _sellId + " Buying: " + _buyId);
			return new String(new char[pastString.length() + 1]).replace('\0', ' ');
		}
	}
	//--------------------
	private String createSpacedString(String[] stringArray, int maxSpaces) {
		StringBuilder sb = new StringBuilder(maxSpaces * stringArray.length );
		
		try {
			sb.append(stringArray[0]);
			for (int i = 1; i < stringArray.length; i++) {
				sb.append(addSpaces(stringArray[i - 1], maxSpaces) + stringArray[i]);
			}
			return sb.toString();
		} catch (IndexOutOfBoundsException e) {
			System.out.println("ERROR: Array too small! Array = " + stringArray.toString());
			return "----ERROR----";
		}
	}
	//--------------------
	public String toString() {
		try {
			return 
					"[sellId = " + _sellId + " buyId = " + _buyId + " BuySellLowerBound = " + df.format(_BuySellLowerBound) + " BuySellUpperBound = " + df.format(_BuySellUpperBound) +
					" SellBuyLowerBound = " + df.format(_SellBuyLowerBound) + " SellBuyUpperBound = " + df.format(_SellBuyUpperBound) + " minSell = " + _minSell + " minBuy = " + _minBuy +
					" maxSell = " + _maxSell + " maxBuy = " +_maxBuy + " sellChaosValue = " + df.format(_sellChaosValue) + " buyChaosValue = " + df.format(_buyChaosValue) + "]";
		} catch (Exception e) {
			return "[sellId = " + _sellId + " buyId = " + _buyId + "]";
		}
	}
	//--------------------
	public String toString(int maxElementsPerLine, int maxSpaces) {
		int biggerMaxSpaces = (int) Math.round(maxSpaces * 1.3);
		String filler = new String(new char[biggerMaxSpaces * 5]).replace('\0', '-');										
		String[] row1 = new String[5];
		row1[0] = "Selling (#" + _sellId + ")";
		row1[1] = "Buying (#" + _buyId + ")";
		row1[2] = "Sell/Buy";
		row1[3] = "Buy/Sell";
		row1[4] = "Profit";
		String[] row2 = new String[5];
		row2[0] = "$" + df.format(_sellChaosValue);
		row2[1] = "$" + df.format(_buyChaosValue);
		row2[2] = "#" + _sellId + " = "+ df.format(_sellChaosValue/_buyChaosValue) + " * #" + _buyId;
		row2[3] = "#" + _buyId + " = "+ df.format(_buyChaosValue/_sellChaosValue) + " * #" + _sellId;
		row2[4] = "Min($" + _minimumProfit + ")";
		String[] row3 = new String[4];
		row3[0] = "[" + _minSell + ", " + _maxSell + "]";
		row3[1] = "[" + _minBuy + ", " + _maxBuy + "]";
		row3[2] = "[" + df.format(_SellBuyLowerBound) + ", " + df.format(_SellBuyUpperBound) + "]";
		row3[3] = "[" + df.format(_BuySellLowerBound) + ", " + df.format(_BuySellUpperBound) + "]";
		
		StringBuilder sb = new StringBuilder(filler + "\n");
		sb.append(createSpacedString(row1, maxSpaces) + "\n");
		sb.append(createSpacedString(row2, maxSpaces) + "\n");
		sb.append(createSpacedString(row3, maxSpaces) + "\n");

		if (_exchangeDataList.size() != 0) {
			for (ExchangeData exchangeData : _exchangeDataList) {
				sb.append(exchangeData.toString(maxSpaces, _sellChaosValue, _buyChaosValue) + "\n");
			}
			sb.setLength(sb.length() - 2);
		}
		sb = allExchangeDataToString(maxElementsPerLine, biggerMaxSpaces, sb);
		return sb.toString();
	}
	//--------------------
	@SuppressWarnings("unused")
	private StringBuilder allExchangeDataToString(int maxElementsPerLine, int maxSpaces, StringBuilder sb) {
		String dataString;
		int counter;
		//WebExchanges
		sb = iterateExchangeDataListToString(maxElementsPerLine, maxSpaces, sb, _exchangeDataList, "Web Exchanges:");
		//Outliers
		sb = iterateExchangeDataListToString(maxElementsPerLine, maxSpaces, sb, _outlierList, "Outliers:");
		//Ratio set
		sb = iterateExchangeDataListToString(maxElementsPerLine, maxSpaces, sb, _exchangeRatioSet, "Ratios:");
		//Final offer
		sb.append("\n" + new String(new char[maxSpaces * 5]).replace('\0', '~') + "\n" + "Final Offer: " + "\n");
		if (_finalOffer != null) {
			sb.append(_finalOffer.toString(_sellChaosValue, _buyChaosValue));
		}
		//Potentials
		sb = iterateExchangeDataListToString(maxElementsPerLine, maxSpaces, sb, _potentialExchangeDataList, "Potential Offers:");
		return sb;
	}
	//--------------------
	private StringBuilder iterateExchangeDataListToString(int maxElementsPerLine, int maxSpaces, StringBuilder sb, Collection<ExchangeData> exDataList, String exDataListName) {
		sb.append("\n" + new String(new char[maxSpaces * 5]).replace('\0', '~') + "\n" + exDataListName + "\n");
		if (exDataList.size() != 0) {
			String dataString;
			int counter = 0;
			for (ExchangeData exData : exDataList) {
				dataString = exData.toString(_sellChaosValue, _buyChaosValue);
				sb.append(dataString + addSpaces(dataString, maxSpaces));
				counter++;
				if (counter == maxElementsPerLine) {
					sb.append("\n");
					counter = 0;
				}
			}
			if (counter == 0) {
				sb.setLength(sb.length() - 2);
			}
		}
		return sb;
	}
	//--------------------
	public void setMinMaxAmounts(int minSell, int minBuy, int maxSell, int maxBuy) {
		double maxValueStock;
		_maxSell = maxSell;
		_minSell = minSell;
		_maxBuy = maxBuy;
		_minBuy = minBuy;
		
		//Sell shared
		maxValueStock = MAX_EXCHANGE_VALUE/ _sellChaosValue;
		_maxSell = (int) (maxValueStock % 1 >= 0.8 ? Math.ceil(maxValueStock) : Math.floor(maxValueStock));
		_minSell = Math.min(_minSell, (int) Math.floor(maxValueStock/4.0));
		
		if (_sellChaosValue > 0.75) {
			_maxSell = Math.min(_maxSell, 600);
			_minSell = Math.min(_minSell, 450);
		} else if (_sellChaosValue > 0.0175) {
			_maxSell = Math.min(_maxSell, 1200);
			_minSell = Math.min(_minSell, 900);
		} else if (_sellChaosValue > 0.005) {
			_maxSell = Math.min(_maxSell, 2400);
			_minSell = Math.min(_minSell, 1800);
		} else {
			_maxSell = Math.min(_maxSell, 9999);
			_minSell = Math.min(_minSell, 7500);
		}
		
		if (_maxSell > 0 && _minSell == 0) {
			_minSell = 1;
		}
		
		//Buy shared
		maxValueStock = MAX_EXCHANGE_VALUE/ _buyChaosValue;
		_maxBuy = (int) (maxValueStock % 1 >= 0.8 ? Math.ceil(maxValueStock) : Math.floor(maxValueStock)); //SOMETHIS WRONG HERE
		_minBuy = Math.min(_minBuy, (int) Math.floor(maxValueStock/ 4.0));
		
		if (_buyChaosValue > 0.75) {
			_maxBuy = Math.min(_maxBuy, 600);
			_minBuy = Math.min(_minBuy, 450);
		} else if (_buyChaosValue > 0.0175) {
			_maxBuy = Math.min(_maxBuy, 1200);
			_minBuy = Math.min(_minBuy, 900);
		} else if (_buyChaosValue > 0.005) {
			_maxBuy = Math.min(_maxBuy, 2400);
			_minBuy = Math.min(_minBuy, 1800);
		} else {
			_maxBuy = Math.min(_maxBuy, 9999);
			_minBuy = Math.min(_minBuy, 7500);
		}
		
		if (_maxBuy > 0 && _minBuy == 0) {
			_minBuy = 1;
		}

		if (_minSell == 0 || _minBuy == 0) {
			_isEmpty = true;
		}
	}
	
	public void setBounds(double lowerBound, double upperBound) {
		_BuySellLowerBound = lowerBound;
		_BuySellUpperBound = upperBound;
		_SellBuyLowerBound = 1/upperBound;
		_SellBuyUpperBound = 1/lowerBound;
	}
	public void setAndAddFinalOffer(ExchangeData finalOffer, double profit) {
		if (finalOffer != null) {
			finalOffer.set_finalOffer(_sellId, _buyId, profit);
			Global.get_finalOfferList().add(finalOffer);
			_finalOffer = finalOffer;
		}
	}
//--------------------------------------------------------------Getters and Setters--------------------------------------------------------------//
	public List<ExchangeData> get_exchangeDataList() {
		return _exchangeDataList;
	}

	public List<ExchangeData> get_potentialExchangeDataList() {
		return _potentialExchangeDataList;
	}
	
	public int get_sellId() {
		return _sellId;
	}

	public int get_buyId() {
		return _buyId;
	}

	public List<ExchangeData> get_outlierList() {
		return _outlierList;
	}

	public void set_outlierList(List<ExchangeData> outlierList) {
		_outlierList = outlierList;
	}

	public double get_BuySellLowerBound() {
		return _BuySellLowerBound;
	}

	public double get_BuySellUpperBound() {
		return _BuySellUpperBound;
	}

	public double get_SellBuyLowerBound() {
		return _SellBuyLowerBound;
	}

	public double get_SellBuyUpperBound() {
		return _SellBuyUpperBound;
	}

	public int get_minSell() {
		return _minSell;
	}

	public int get_minBuy() {
		return _minBuy;
	}

	public int get_maxSell() {
		return _maxSell;
	}

	public int get_maxBuy() {
		return _maxBuy;
	}

	public double get_SellChaosValue() {
		return _sellChaosValue;
	}

	public void set_SellChaosValue(double sellChaosValue) {
		_sellChaosValue = sellChaosValue;
	}

	public double get_BuyChaosValue() {
		return _buyChaosValue;
	}

	public void set_BuyChaosValue(double buyChaosValue) {
		_buyChaosValue = buyChaosValue;
	}

	public void set_potentialExchangeDataList(List<ExchangeData> potentialExchangeDataList) {
		_potentialExchangeDataList = potentialExchangeDataList;
	}

	public ExchangeData get_GeneratedExchange() {
		return _generatedExchange;
	}

	public void set_GeneratedExchange(ExchangeData generatedExchange) {
		_generatedExchange = generatedExchange;
	}

	public ExchangeData get_finalOffer() {
		return _finalOffer;
	}



	public double get_minimumProfit() {
		return _minimumProfit;
	}

	public void set_minimumProfit(double _minimumProfit) {
		this._minimumProfit = _minimumProfit;
	}

	public List<ExchangeData> get_exchangeRatioSet() {
		return _exchangeRatioSet;
	}

	public boolean isCommon() {
		return _isCommon;
	}

	public boolean is_isEmpty() {
		return _isEmpty;
	}

	public boolean is_isFrequent() {
		return _isFrequent;
	}

	public void set_isFrequent(boolean _isFrequent) {
		this._isFrequent = _isFrequent;
	}

	public Currency get_buyCurrency() {
		return _buyCurrency;
	}
	
	public Currency get_sellCurrency() {
		return _sellCurrency;
	}

}
