package gui.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import gui.eventHandling.CurrencyTableSelectedEvent;
import gui.eventHandling.CurrencyTableSelectedListener;
import gui.models.CurrencyTableModel;




public class Panel_CurrencyTable extends JPanel {

//--------------------------------------------------------------Fields--------------------------------------------------------------//
	private static final long serialVersionUID = -8892246845844378133L;
	private int _width;
	private int _height;
	private DefaultTableModel _tableModel_EmptyCurrency;
	private JTable _table_Currency;
	private final boolean _selling;
	private final int _portWidth;
	private final Renderer_Currency _currencyTableRenderer;

//--------------------------------------------------------------Constructor--------------------------------------------------------------//

	public Panel_CurrencyTable(int x, int y, int width, int height, boolean selling) {

		_width = width;
		_portWidth = _width - 10;
		_height = height;
		_selling = selling;
		_currencyTableRenderer = new Renderer_Currency();
		
		String tableName;
		
		if (_selling) {
			tableName = "Selling";
		} else {
			tableName = "Buying";
		}

		setBounds(x, y, width, height);

		
		JScrollPane scrollPane_Currency = new JScrollPane();
		scrollPane_Currency.setPreferredSize(new Dimension(_width, _height - 3));
		scrollPane_Currency.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), tableName, 
				TitledBorder.CENTER, TitledBorder.TOP, new Font(Font.SANS_SERIF, Font.PLAIN, 24), new Color(0, 0, 0)));
		add(scrollPane_Currency);
		
		//**********Table**********//
		_tableModel_EmptyCurrency = new DefaultTableModel() {
			private static final long serialVersionUID = 7459672973649305004L;
			@Override
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		};
		_tableModel_EmptyCurrency.addColumn("Id"); 
		_tableModel_EmptyCurrency.addColumn("Name"); 
		_tableModel_EmptyCurrency.addColumn("($) Value"); 
		
		_table_Currency = new JTable(_tableModel_EmptyCurrency);
		_table_Currency.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		_table_Currency.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_table_Currency.setAutoCreateRowSorter(true);
		_table_Currency.setPreferredScrollableViewportSize(new Dimension(width, height));
		_table_Currency.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
		_table_Currency.setDefaultRenderer(Object.class, _currencyTableRenderer);
	
		
		TableColumnModel currencyColumnModel = _table_Currency.getColumnModel();
		currencyColumnModel.getColumn(0).setCellRenderer(_currencyTableRenderer);
		currencyColumnModel.getColumn(2).setCellRenderer(_currencyTableRenderer);
		currencyColumnModel.getColumn(0).setPreferredWidth((int) Math.round((double) 0.15 * _portWidth));
		currencyColumnModel.getColumn(1).setPreferredWidth((int) Math.round((double) 0.65 * _portWidth));
		currencyColumnModel.getColumn(2).setPreferredWidth((int) Math.round((double) 0.2 * _portWidth));
		
		//**********Row selected**********//
		_table_Currency.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent event) {
	        	if (_table_Currency.getRowCount() > 0 && _table_Currency.getSelectedRow() >= 0) {
	        		try {
	        			Integer index = _table_Currency.getSelectedRow();
						fireCurrencyTableSelectedEvent(new CurrencyTableSelectedEvent(this, _selling, index));
					} catch (Exception e) {
						e.printStackTrace();
					}
	        	}
	        }
	    });
		scrollPane_Currency.setViewportView(_table_Currency);
	}

//--------------------------------------------------------------Methods--------------------------------------------------------------//

	public void setCurrencyTableModel(CurrencyTableModel model) throws Exception {
		if (model != null) {
			TableColumnModel currencyColumnModel = _table_Currency.getColumnModel();

			_table_Currency.setModel(model);
			
			if (model.getRowCount() > 56) {
				currencyColumnModel.getColumn(0).setPreferredWidth((int) Math.round((double) 0.15 * _portWidth));
				currencyColumnModel.getColumn(1).setPreferredWidth((int) Math.round((double) 0.65 * _portWidth) - 18);
				currencyColumnModel.getColumn(2).setPreferredWidth((int) Math.round((double) 0.2 * _portWidth));
				currencyColumnModel.getColumn(0).setCellRenderer(_currencyTableRenderer);
				currencyColumnModel.getColumn(2).setCellRenderer(_currencyTableRenderer);
			} else {
				currencyColumnModel.getColumn(0).setPreferredWidth((int) Math.round((double) 0.15 * _portWidth));
				currencyColumnModel.getColumn(1).setPreferredWidth((int) Math.round((double) 0.65 * _portWidth));
				currencyColumnModel.getColumn(2).setPreferredWidth((int) Math.round((double) 0.2 * _portWidth));
				currencyColumnModel.getColumn(0).setCellRenderer(_currencyTableRenderer);
				currencyColumnModel.getColumn(2).setCellRenderer(_currencyTableRenderer);
			}
		
			
		} else {
			_table_Currency.setModel(_tableModel_EmptyCurrency);
		}

	}
	
	public void selectRow(int index) {
		_table_Currency.setRowSelectionInterval(index, index);
	}
	
	public void unselectRow() {
		_table_Currency.clearSelection();
	}
	
	//**********CurrencyTableSelected event handling**********//
	public void addCurrencyTableSelectedListener(CurrencyTableSelectedListener listener) {
		listenerList.add(CurrencyTableSelectedListener.class, listener);
	}

	private void fireCurrencyTableSelectedEvent(CurrencyTableSelectedEvent event) throws Exception {
		Object[] listeners = listenerList.getListenerList();

		for(int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == CurrencyTableSelectedListener.class) {
				((CurrencyTableSelectedListener) listeners[i + 1]).CurrencyTableSelectedEventOccured(event);
			}
		}
	}

}
