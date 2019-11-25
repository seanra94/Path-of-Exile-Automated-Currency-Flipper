package gui.eventHandling;

import java.util.EventObject;
import backend.dataStructures.ExchangeData;
import gui.models.SmartEnum;


public class SmartGenerateEvent extends EventObject{
	private static final long serialVersionUID = 4443584674068796087L;
	private final String _sellName;
	private final String _sellString;
	private final String _buyName;
	private final String _buyString;
	
	private SmartEnum _smartEnum;
	private int _sellId;
	private int _buyId;
	private ExchangeData _exDataLower;
	private ExchangeData _exDataHigher;
	

	


	public SmartGenerateEvent(Object source, String sellName, String sellAmount, String buyName, String buyAmount) {
		super(source);
		_sellName = sellName;
		_sellString = sellAmount;
		_buyName = buyName;
		_buyString = buyAmount;
		
	}


	public SmartEnum get_smartEnum() {
		return _smartEnum;
	}


	public void set_smartEnum(SmartEnum _smartEnum) {
		this._smartEnum = _smartEnum;
	}


	public String get_sellName() {
		return _sellName;
	}


	public String get_sellString() {
		return _sellString;
	}


	public String get_buyName() {
		return _buyName;
	}


	public String get_buyString() {
		return _buyString;
	}


	public ExchangeData get_exDataLower() {
		return _exDataLower;
	}


	public ExchangeData get_exDataHigher() {
		return _exDataHigher;
	}


	public void set_exDataLower(ExchangeData _exDataLower) {
		this._exDataLower = _exDataLower;
	}


	public void set_exDataHigher(ExchangeData _exDataHigher) {
		this._exDataHigher = _exDataHigher;
	}


	public int get_sellId() {
		return _sellId;
	}


	public int get_buyId() {
		return _buyId;
	}


	public void set_sellId(int _sellId) {
		this._sellId = _sellId;
	}


	public void set_buyId(int _buyId) {
		this._buyId = _buyId;
	}




	
	/*
	public void respond(SmartEnum smartEnum, Integer sellId, Integer buyId) {
		_smartEnum = smartEnum;
		_sellId = sellId;
		_buyId = buyId;
	}
	
	public void respond(SmartEnum smartEnum, Integer sellId, Integer buyId, 
			Integer sellLower, Integer sellHigher, Integer buyLower, Integer buyHigher) {
		respond(smartEnum, sellId, buyId);
		_sellLower = sellLower;
		_sellHigher = sellHigher;
		_buyLower = buyLower;
		_buyHigher = buyHigher;
	}
	*/
	



}
