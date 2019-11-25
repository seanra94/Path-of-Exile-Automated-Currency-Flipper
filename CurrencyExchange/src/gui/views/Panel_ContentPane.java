package gui.views;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Panel_ContentPane extends JPanel {
	
//--------------------------------------------------------------Fields--------------------------------------------------------------//

	private static final long serialVersionUID = -29557018155874192L;
	
//--------------------------------------------------------------Constructor--------------------------------------------------------------//

	public Panel_ContentPane(Panel_Search searchView, Panel_CurrencyTable sellTableView, Panel_CurrencyTable buyTableView, Panel_ExchangeSummary exchangeSummaryView, Panel_TabbedPanel tabbedView) {
		setLayout(null);
		setBorder(new EmptyBorder(0, 0, 0, 0));
		
		add(searchView);
		add(sellTableView);
		add(buyTableView);
		add(exchangeSummaryView);
		add(tabbedView);
	}

}
