package gui.models;

import java.util.List;

import backend.Global;
import backend.dataStructures.Currency;
import backend.dataStructures.Exchange;
import gui.eventHandling.CurrencyTableSelectedEvent;
import gui.eventHandling.CurrencyTableSelectedListener;
import gui.eventHandling.DataGeneratedEvent;
import gui.eventHandling.DataGeneratedListener;
import gui.eventHandling.SmartGenerateEvent;
import gui.eventHandling.SmartGenerateListener;
import gui.views.Panel_ExchangeSummary;
import gui.views.Panel_Search;
import gui.views.Panel_CurrencyTable;
import gui.views.Panel_WebOfferTable;



public class Controller {
//--------------------------------------------------------------Fields--------------------------------------------------------------//
		
	private CurrencyTableModel _sellTableModel;
	private CurrencyTableModel _buyTableModel;
	private GuiModel _guiModel;
	private Currency _selectedSellCurr;
	private Currency _selectedBuyCurr;
//--------------------------------------------------------------Constructor--------------------------------------------------------------//


	public Controller(Panel_Search searchView, Panel_CurrencyTable sellTableView, Panel_CurrencyTable buyTableView, Panel_ExchangeSummary exchangeSummaryView, Panel_WebOfferTable webOfferTableView ) {

		DataGeneratedListener DataGeneratedListener = new DataGeneratedListener() {
			@Override
			public void DataGeneratedEventOccured(DataGeneratedEvent event) throws Exception {
				List<Currency> currList = event.get_currList();
				_sellTableModel = new CurrencyTableModel(currList);
				_guiModel = new GuiModel(event.get_currMap(), event.get_currList(), event.get_finalOfferList());
				sellTableView.setCurrencyTableModel(_sellTableModel);
				searchView.set_nameComboBoxes(_guiModel.get_currNames());
			}
		};
		Global.addDataGeneratedListener(DataGeneratedListener);

		CurrencyTableSelectedListener CurrencyTableSelectedListener = new CurrencyTableSelectedListener() {
			@Override
			public void CurrencyTableSelectedEventOccured(CurrencyTableSelectedEvent event) throws Exception {
				if (event.is_selling()) {
					_selectedSellCurr = (Currency) _sellTableModel.getValueAt(event.get_index(), 3);
					_selectedBuyCurr = null;
					_buyTableModel = new CurrencyTableModel(_guiModel.buyMapToList(_selectedSellCurr.get_webOffersMap()));
					buyTableView.setCurrencyTableModel(_buyTableModel);
					exchangeSummaryView.setFields(_selectedSellCurr.get_name(), null, null);
					searchView.setComboBoxSelection(_selectedSellCurr.get_shortName(), true);
					searchView.resetTextFields();
				} else {
					_selectedBuyCurr =  (Currency) _buyTableModel.getValueAt(event.get_index(), 3);
					Exchange exchange = _guiModel.getExchange(_selectedSellCurr, _selectedBuyCurr);
					exchangeSummaryView.setFields(_selectedSellCurr.get_name(), _selectedBuyCurr.get_name(), exchange);
					webOfferTableView.setWebOfferTableModel(new WebOfferTableModel(exchange));
					searchView.setComboBoxSelection(_selectedBuyCurr.get_shortName(), false);
					searchView.setValues(exchange.get_finalOffer());
				}
				

			}

		};
		sellTableView.addCurrencyTableSelectedListener(CurrencyTableSelectedListener);
		buyTableView.addCurrencyTableSelectedListener(CurrencyTableSelectedListener);
		
		
		SmartGenerateListener SmartGenerateListener = new SmartGenerateListener() {
			@Override
			public void SmartGenerateEventOccured(SmartGenerateEvent event) {
				event = _guiModel.respondToSmartEvent(event);
			
	
				searchView.displayOffers(event);
				
				if (event.get_smartEnum() != SmartEnum.DO_NOTHING) {
					
					Integer sellRowIndex = _sellTableModel.getRowById(event.get_sellId());
					if (sellRowIndex != null) {
						sellTableView.selectRow(sellRowIndex);
						Integer buyRowIndex = _buyTableModel.getRowById(event.get_buyId());
						if (buyRowIndex != null) {
							buyTableView.selectRow(buyRowIndex);
						}
					}	
				}

			}
		};
		searchView.addSmartGenerateListener(SmartGenerateListener);
	}
	
	
	


}
