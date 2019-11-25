package gui.models;

import java.text.DecimalFormat;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import backend.dataStructures.Currency;



public class CurrencyTableModel extends AbstractTableModel {
	
//--------------------------------------------------------------Fields--------------------------------------------------------------//

	private static final long serialVersionUID = 1678228617697174160L;
	private static final DecimalFormat df = new DecimalFormat("#.###");
	private final List<Currency> _currencyList;
	private final String columnNames[] = { "Id", "Name", "($) Value"};
//--------------------------------------------------------------Constructor--------------------------------------------------------------//
	public CurrencyTableModel(List<Currency> list) throws Exception {
		_currencyList = list;
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
		return _currencyList.size();
	}

    
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (_currencyList.isEmpty()) {
            return Object.class;
        }
        return getValueAt(0, columnIndex).getClass();
    }
    
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Currency currency = _currencyList.get(rowIndex);
		switch (columnIndex) {
		case 0: return currency.get_id();
		case 1: return currency.get_shortName();
		case 2: return Double.parseDouble(df.format(currency.get_chaosValue()));
		case 3: return currency;
		default: return null;
		}
	}
	
	public Integer getRowById(int currId) {
		for (int i = 0; i < _currencyList.size(); i++) {
			if ((int) getValueAt(i, 0) == currId) {
				return i;
			}
		}
		
		return null;
	}

	


}
