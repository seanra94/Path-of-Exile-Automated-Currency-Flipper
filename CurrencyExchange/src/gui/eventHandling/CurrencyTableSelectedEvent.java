package gui.eventHandling;

import java.util.EventObject;


public class CurrencyTableSelectedEvent extends EventObject{
	private static final long serialVersionUID = 4443584674068796087L;
	private final boolean _selling;
	private final Integer _index;
	
	public CurrencyTableSelectedEvent(Object source, boolean selling, Integer index) {
		super(source);
		_selling = selling;
		_index = index;
	}

	public boolean is_selling() {
		return _selling;
	}

	public Integer get_index() {
		return _index;
	}


}
