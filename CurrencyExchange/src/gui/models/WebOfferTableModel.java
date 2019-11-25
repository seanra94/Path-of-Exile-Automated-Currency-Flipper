package gui.models;

import java.text.DecimalFormat;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import backend.dataStructures.Exchange;
import backend.dataStructures.ExchangeData;

public class WebOfferTableModel extends AbstractTableModel {
	
//--------------------------------------------------------------Fields--------------------------------------------------------------//
	private static final long serialVersionUID = 1678228617697174160L;
	private static final DecimalFormat dfShort = new DecimalFormat("#.##");
	private static final DecimalFormat dfLong = new DecimalFormat("#.####");
	private final Exchange _exchange;
	private final List<ExchangeData> _webOfferList;
	private final String columnNames[] = { "Buy", "Sell", "Buy/Sell", "Sell/Buy", "Profit"};
//--------------------------------------------------------------Constructor--------------------------------------------------------------//
	public WebOfferTableModel(Exchange exchange) throws Exception {
		_exchange = exchange;
		_webOfferList = exchange.get_exchangeDataList();
	}
//--------------------------------------------------------------Methods--------------------------------------------------------------//
	@Override
	public String getColumnName(int index) {
		return columnNames[index];
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return _webOfferList.size();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ExchangeData webOffer = _webOfferList.get(rowIndex);
		switch (columnIndex) {
		case 0: return webOffer.get_buyValue();
		case 1: return webOffer.get_sellValue();
		case 2: return dfLong.format(webOffer.get_buySellRatio());
		case 3: return dfLong.format(webOffer.get_sellBuyRatio());
		case 4: return "$" + dfShort.format(
				webOffer.get_buyValue() * _exchange.get_BuyChaosValue() -
				webOffer.get_sellValue() * _exchange.get_SellChaosValue());
	
		default: return null;
		}
	}
}
