package gui.eventHandling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import backend.dataStructures.Currency;
import backend.dataStructures.ExchangeData;

public class DataGeneratedEvent extends EventObject{
	private static final long serialVersionUID = 4443584674068796087L;
	private final Map<Integer, Currency> _currMap;
	private final List<ExchangeData> _finalOfferList;

	public DataGeneratedEvent(Object source, Map<Integer, Currency> currMap, List<ExchangeData> finalOfferList) {
		super(source);
		_currMap = currMap;
		_finalOfferList = finalOfferList;
	}
	
	
	public class IdComparitor implements Comparator<Currency> {
		@Override
		public int compare(Currency c1, Currency c2) {
			Integer id = c1.get_id();
			return id.compareTo(c2.get_id());
		}
	}	

	public Map<Integer, Currency> get_currMap() {
		return _currMap;
	}

	public List<ExchangeData> get_finalOfferList() {
		return _finalOfferList;
	}
	
	public List<Currency> get_currList() {
		ArrayList<Currency> currList = new ArrayList<Currency>(_currMap.values());
		Collections.sort(currList, new IdComparitor());
		return currList;
	}
	
}
