package gui.views;

import javax.swing.JTabbedPane;

public class Panel_TabbedPanel extends JTabbedPane {
	
//--------------------------------------------------------------Fields--------------------------------------------------------------//
	private static final long serialVersionUID = -6501989391172609607L;
	private int _width;
	private int _height;
	
//--------------------------------------------------------------Constructor--------------------------------------------------------------//

	public Panel_TabbedPanel(int x, int y, int width, int height, Panel_WebOfferTable webOfferTableView) {
		super (JTabbedPane.TOP);
		
		_width = width;
		_height = height;

		setBounds(x, y, width, height);
		addTab("Web Offers", webOfferTableView);
	}

//--------------------------------------------------------------Methods--------------------------------------------------------------//


}
