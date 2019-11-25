package gui.views;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

public class Panel_ExchangeTextGenerator extends JPanel {
	
//--------------------------------------------------------------Fields--------------------------------------------------------------//

	private static final long serialVersionUID = -2993976337148283713L;
	private final int _width;
	private final int _height;
	
//--------------------------------------------------------------Constructor--------------------------------------------------------------//

	public Panel_ExchangeTextGenerator(int x, int y, int width, int height) {
		
		_width = width;
		_height = height;
		
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setBounds(x, y, width, height);
		setLayout(null);

		JTextArea textArea = new JTextArea();
		textArea.setBounds(10, 84, 344, 116);
		add(textArea);
	}
}
