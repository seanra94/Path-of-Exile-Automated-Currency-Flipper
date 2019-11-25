package gui.views;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import gui.models.WebOfferTableModel;

public class Panel_WebOfferTable extends JPanel {

	private static final long serialVersionUID = -752869421894463672L;
	private DefaultTableModel _tableModel_EmptyWebOffers;
	private JTable _table_WebOffers;

	public Panel_WebOfferTable(int width, int height) {


		setPreferredSize(new Dimension(width, height));

		JScrollPane scrollPane_Currency = new JScrollPane();

		scrollPane_Currency.setPreferredSize(new Dimension(width, height));
		add(scrollPane_Currency);
		
		//**********Table**********//
		_tableModel_EmptyWebOffers = new DefaultTableModel() {
			private static final long serialVersionUID = 7459672973649305004L;
			@Override
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		};
		_tableModel_EmptyWebOffers.addColumn("Buy"); 
		_tableModel_EmptyWebOffers.addColumn("Sell"); 
		_tableModel_EmptyWebOffers.addColumn("Buy/Sell"); 
		_tableModel_EmptyWebOffers.addColumn("Sell/Buy"); 
		_tableModel_EmptyWebOffers.addColumn("Profit"); 
		
		_table_WebOffers = new JTable(_tableModel_EmptyWebOffers);
		_table_WebOffers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_table_WebOffers.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
		scrollPane_Currency.setViewportView(_table_WebOffers);
	}
	
	
	public void setWebOfferTableModel(WebOfferTableModel webOfferTableModel) throws Exception {
		if (webOfferTableModel != null) {
			_table_WebOffers.setModel(webOfferTableModel);
		} else {
			_table_WebOffers.setModel(_tableModel_EmptyWebOffers);
		}
	}
	
	
}
