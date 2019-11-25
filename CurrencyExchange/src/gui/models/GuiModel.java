package gui.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.StringUtils;
import backend.dataStructures.Currency;
import backend.dataStructures.Exchange;
import backend.dataStructures.ExchangeData;
import gui.eventHandling.SmartGenerateEvent;

public class GuiModel {
	
//--------------------------------------------------------------Fields--------------------------------------------------------------//

	private final Map<Integer, Currency> _currMap;
	private final List<Currency> _currList;
	private final List<ExchangeData> _finalOfferList;
	private final Map<String, Integer> _nameMap;
	private final String[] _currNames;
	
//--------------------------------------------------------------Constructor--------------------------------------------------------------//
	
	public GuiModel(Map<Integer, Currency> currMap, List<Currency> currList,
			List<ExchangeData> finalOfferList) {
		_currMap = currMap;
		_currList = currList;
		_finalOfferList = finalOfferList;
		
		_currNames = new String[_currList.size() + 1];
		_nameMap = new HashMap<String, Integer>();
		Currency curr;
		String name;
		_currNames[0] = "";
		for (int i = 0; i < _currList.size(); i++) {
			curr = _currList.get(i);
			name = curr.get_shortName();
			_currNames[i + 1] = name;
			_nameMap.put(name, curr.get_id());
		}
	}
//--------------------------------------------------------------Methods--------------------------------------------------------------//
	
	
	public SmartGenerateEvent respondToSmartEvent(SmartGenerateEvent event) {
		SmartAnalyser analyser = new SmartAnalyser(event);
		return analyser.respond();
	}

	private class SmartAnalyser {
		private final SmartGenerateEvent _event;
	
		
		private SmartAnalyser (SmartGenerateEvent event) {
			_event = event;
		}
		
		
		private SmartGenerateEvent respond() {
			Integer sellId = _nameMap.get(_event.get_sellName());
			Integer buyId = _nameMap.get(_event.get_buyName());
			
		
			
			if (buyId == null || sellId == null) {
				_event.set_smartEnum(SmartEnum.DO_NOTHING);
				return _event;
			}
			
			_event.set_sellId(sellId);
			_event.set_buyId(buyId);
			
			String sellString = _event.get_sellString();
			String buyString = _event.get_buyString();
			Integer sellAmount = null;
			Integer buyAmount = null;
			
			if ((sellString == null || !StringUtils.isNumeric(sellString)) && (buyString == null || !StringUtils.isNumeric(buyString))) {
				_event.set_smartEnum(SmartEnum.SELECT_EXCHANGE);
				return _event;
			} else if (sellString == null || !StringUtils.isNumeric(sellString)) {
				buyAmount = Integer.valueOf(buyString);
				_event.set_smartEnum(SmartEnum.CALC_SELL);

			} else if (buyString == null || !StringUtils.isNumeric(buyString)) {
				sellAmount = Integer.valueOf(sellString);
				_event.set_smartEnum(SmartEnum.CALC_BUY);
			} else {
				sellAmount = Integer.valueOf(sellString);
				buyAmount = Integer.valueOf(buyString);
				_event.set_smartEnum(SmartEnum.SUMMARISE);
						
			}
			
			if (buyAmount != null && buyAmount <= 0) {
				_event.set_smartEnum(SmartEnum.SELECT_EXCHANGE);
				return _event;
			} else if (sellAmount != null && sellAmount <= 0) {
				_event.set_smartEnum(SmartEnum.SELECT_EXCHANGE);
				return _event;
			} 
			
			
			
			
			Exchange exchange = _currMap.get(sellId).get_webOffersMap().get(buyId);
			double finalBuySell;
			try {
				finalBuySell = exchange.get_finalOffer().get_buySellRatio();
			} catch (NullPointerException e) {
				_event.set_smartEnum(SmartEnum.SELECT_EXCHANGE);
				JOptionPane.showMessageDialog(null, "ERROR: A final offer was never generated for this exchange. Unable to generate offers");
				return _event;
			}
			
			ExchangeData exDataLower;
			ExchangeData exDataHigher;
			if (_event.get_smartEnum() == SmartEnum.CALC_SELL) {
			
				double genSellAmount = buyAmount / finalBuySell;
				if (genSellAmount < 1) {
					_event.set_smartEnum(SmartEnum.SELECT_EXCHANGE);
					JOptionPane.showMessageDialog(null, "ERROR: Unable to generate a sell value > 0");
					return _event;
				}
				
				exDataHigher = new ExchangeData((int) Math.floor(genSellAmount), buyAmount);
				exDataLower = new ExchangeData((int) Math.ceil(genSellAmount), buyAmount);
			} else if (_event.get_smartEnum() == SmartEnum.CALC_BUY){
				double genBuyAmount = sellAmount * finalBuySell;
				if (genBuyAmount < 1) {
					_event.set_smartEnum(SmartEnum.SELECT_EXCHANGE);
					JOptionPane.showMessageDialog(null, "ERROR: Unable to generate a buy value > 0");
					return _event;
				}
				exDataLower = new ExchangeData(sellAmount, (int) Math.floor(genBuyAmount));
				exDataHigher = new ExchangeData(sellAmount, (int) Math.ceil(genBuyAmount));
			} else {
				exDataLower = new ExchangeData(sellAmount, buyAmount);
				exDataHigher = exDataLower;
			}
			
			
			double sellValue = exchange.get_SellChaosValue();
			double buyValue = exchange.get_BuyChaosValue();
			exDataLower.set_finalOffer(sellId, buyId, sellValue, buyValue);
			exDataHigher.set_finalOffer(sellId, buyId, sellValue, buyValue);
			_event.set_exDataLower(exDataLower);
			_event.set_exDataHigher(exDataHigher);
			return _event;
			
		}

		
		
	}
	
//--------------------------------------------------------------Getters and Setters--------------------------------------------------------------//
	
	public Map<Integer, Currency> get_currMap() {
		return _currMap;
	}

	public List<Currency> get_currList() {
		return _currList;
	}

	public List<ExchangeData> get_finalOfferList() {
		return _finalOfferList;
	}

	public Exchange getExchange(Currency _selectedSellCurr, Currency _selectedBuyCurr) {
		
		return _currMap.get(_selectedSellCurr.get_id()).get_webOffersMap().get(_selectedBuyCurr.get_id());
		
	}
	
	public class IdComparitor implements Comparator<Currency> {
		@Override
		public int compare(Currency c1, Currency c2) {
			Integer id = c1.get_id();
			return id.compareTo(c2.get_id());
		}
	}	


	public List<Currency> buyMapToList(Map<Integer, Exchange> buyMap) {
		ArrayList<Currency> buyCurrList = new ArrayList<Currency>();
		for (Entry<Integer, Exchange> buyEntry : buyMap.entrySet()) {
			buyCurrList.add(_currMap.get(buyEntry.getKey()));
		}
		
		Collections.sort(buyCurrList, new IdComparitor());
		return buyCurrList;
	}

	public String[] get_currNames() {
		return _currNames;
	}

	public Map<String, Integer> get_nameMap() {
		return _nameMap;
	}


}
