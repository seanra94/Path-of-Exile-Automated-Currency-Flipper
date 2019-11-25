package gui.views;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Panel_Unused extends JPanel{

//--------------------------------------------------------------Fields--------------------------------------------------------------//
	private static final long serialVersionUID = -4258017188436816294L;
	private int _width;
	private int _height;

//--------------------------------------------------------------Constructor--------------------------------------------------------------//

	public Panel_Unused(int x, int y, int width, int height) {

		_width = width;
		_height = height;
		setBounds(x, y, width, height);
		setBorder(new LineBorder(new Color(0, 0, 0)));
	}

//--------------------------------------------------------------Methods--------------------------------------------------------------//

}
