package gui.views;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.stream.IntStream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import backend.Utility;
import backend.dataStructures.ExchangeData;
import gui.eventHandling.SmartGenerateEvent;
import gui.eventHandling.SmartGenerateListener;
import gui.models.SmartEnum;
import gui.models.SwingObj;

public class Panel_Search extends JPanel {
	

//--------------------------------------------------------------Fields--------------------------------------------------------------//
	private static final long serialVersionUID = -4909421300342614404L;
	private final JTextField _textField_Sell;
	private final JTextField _textField_Buy;
	private final int _width;
	private final int _height;
	private final int _defaultYGap = 30;
	private final int _defaultXGap = 20;
	private final int _section1XGap;
	private final int[] _section1Widths;
	private final int _section2XGap;
	private final int[] _section2Widths;
	private String[] _currNameList;
	private JComboBox<String> _comboBox_sellingName;
	private JComboBox<String> _comboBox_buyingName;
	private JLabel _label_buyLower;
	private JLabel _label_sellLower;
	private JLabel _label_buySellLower;
	private JLabel _label_sellBuyLower;
	private JLabel _label_profitLower;
	private JLabel _label_buyHigher;
	private JLabel _label_sellHigher;
	private JLabel _label_buySellHigher;
	private JLabel _label_sellBuyHigher;
	private JLabel _label_profitHigher;
	private int[] _section3Widths;
	private int _section3XGap;
	
	private static final SwingObj COMBO = SwingObj.COMBO;
	private static final SwingObj TEXT = SwingObj.TEXT;
	private static final SwingObj LABEL = SwingObj.LABEL;
	
//--------------------------------------------------------------Constructor--------------------------------------------------------------//
	

	public Panel_Search(int x, int y, int width, int height) {
		_width = width;
		_height = height;
		_currNameList = new String[0];
		
		setBounds(x, y, width, height);
		setLayout(null);
		setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Exchange Generator", 
				TitledBorder.CENTER, TitledBorder.TOP, new Font(Font.SANS_SERIF, Font.PLAIN, 24), new Color(0, 0, 0)));

		//Row 1
		//-----Gap-----
		
		//Row 2 - Section 1
		_section1Widths = new int[] {45, 185, 70};
		_section1XGap = Math.max((_width - 2 * _defaultXGap - IntStream.of(_section1Widths).sum())/(_section1Widths.length - 1), 0);
		
		JLabel label_Selling = new JLabel("Selling:");
		label_Selling.setBounds(calculateSectionX(1,1), calculateY(1, LABEL), _section1Widths[0], LABEL.getH());
		
		_comboBox_sellingName = new JComboBox<String>(_currNameList);
		_comboBox_sellingName.setBounds(calculateSectionX(1,2), calculateY(1, COMBO), _section1Widths[1], COMBO.getH());
		AutoCompleteDecorator.decorate(_comboBox_sellingName);
		
		_textField_Sell = new JTextField();
		_textField_Sell.setBounds(calculateSectionX(1,3), calculateY(1, TEXT), _section1Widths[2], TEXT.getH());
		_textField_Sell.setColumns(10);
		
		add(label_Selling);
		add(_comboBox_sellingName);
		add(_textField_Sell);
		
		//Row 3 - Section 1
		JLabel label_Buying = new JLabel("Buying:");
		label_Buying.setBounds(calculateSectionX(1,1), calculateY(2, LABEL), _section1Widths[0], LABEL.getH());
		
		_comboBox_buyingName = new JComboBox<String>(_currNameList);
		_comboBox_buyingName.setBounds(calculateSectionX(1,2), calculateY(2, COMBO), _section1Widths[1], COMBO.getH());
		AutoCompleteDecorator.decorate(_comboBox_buyingName);
		
		_textField_Buy = new JTextField();
		_textField_Buy.setColumns(10);
		_textField_Buy.setBounds(calculateSectionX(1,3), calculateY(2, TEXT), _section1Widths[2], TEXT.getH());
		
		add(label_Buying);
		add(_comboBox_buyingName);
		add(_textField_Buy);

		//Row 4 - Section 2
		_section2Widths = new int[] {0, 150, 0};
		_section2XGap = Math.max((_width - 2 * _defaultXGap - IntStream.of(_section2Widths).sum())/(_section2Widths.length - 1), 0);
		
		JButton smartButton = new JButton("Smart Button");
		smartButton.setBounds(calculateSectionX(2,2), calculateY(3, SwingObj.BUTTON), _section2Widths[1], 24);
		smartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object sellNameObj = _comboBox_sellingName.getSelectedItem();
				Object buyNameObj = _comboBox_buyingName.getSelectedItem();
				if (sellNameObj == null || buyNameObj == null) {
					resetAllLabels();
				} else {
					fireSmartGenerateEvent(new SmartGenerateEvent(this, 
							sellNameObj.toString(),  _textField_Sell.getText(),
							buyNameObj.toString(),   _textField_Buy.getText()));
				}
				
			}
		});
		add(smartButton);
		

		


		_section3Widths = new int[] {56, 56, 56, 56, 56};
		_section3XGap = Math.max((_width - 2 * _defaultXGap - IntStream.of(_section3Widths).sum())/(_section3Widths.length - 1), 0);

		//Row 5 - Section 3
		JLabel label_finalOfferTitle = new JLabel("Generated Offer/s");
		label_finalOfferTitle.setHorizontalAlignment(SwingConstants.CENTER);
		label_finalOfferTitle.setBounds(0, calculateY(4, LABEL), _width, LABEL.getH());

		//Row 6 - Section 3
		JLabel label_buyTitle = new JLabel("Buy");
		label_buyTitle.setHorizontalAlignment(SwingConstants.CENTER);
		label_buyTitle.setBounds(calculateSectionX(3,1), calculateY(5, LABEL), _section3Widths[0], LABEL.getH());

		JLabel label_sellTitle = new JLabel("Sell");
		label_sellTitle.setHorizontalAlignment(SwingConstants.CENTER);
		label_sellTitle.setBounds(calculateSectionX(3,2), calculateY(5, LABEL), _section3Widths[1], LABEL.getH());

		JLabel label_buySellTitle = new JLabel("Buy/Sell");
		label_buySellTitle.setHorizontalAlignment(SwingConstants.CENTER);
		label_buySellTitle.setBounds(calculateSectionX(3,3), calculateY(5, LABEL), _section3Widths[2], LABEL.getH());

		JLabel label_sellBuyTitle = new JLabel("Sell/Buy");
		label_sellBuyTitle.setHorizontalAlignment(SwingConstants.CENTER);
		label_sellBuyTitle.setBounds(calculateSectionX(3,4), calculateY(5, LABEL), _section3Widths[3], LABEL.getH());

		JLabel label_profitTitle = new JLabel("Profit");
		label_profitTitle.setHorizontalAlignment(SwingConstants.CENTER);
		label_profitTitle.setBounds(calculateSectionX(3,5), calculateY(5, LABEL), _section3Widths[4], LABEL.getH());

		add(label_finalOfferTitle);
		add(label_buyTitle);
		add(label_sellTitle);
		add(label_buySellTitle);
		add(label_sellBuyTitle);
		add(label_profitTitle);

		//Row 6 - Section 3
		_label_buyLower = new JLabel("");
		_label_buyLower.setHorizontalAlignment(SwingConstants.CENTER);
		_label_buyLower.setBounds(calculateSectionX(3,1), calculateY(6, LABEL), _section3Widths[0], LABEL.getH());

		_label_sellLower = new JLabel("");
		_label_sellLower.setHorizontalAlignment(SwingConstants.CENTER);
		_label_sellLower.setBounds(calculateSectionX(3,2), calculateY(6, LABEL), _section3Widths[1], LABEL.getH());

		_label_buySellLower = new JLabel("");
		_label_buySellLower.setHorizontalAlignment(SwingConstants.CENTER);
		_label_buySellLower.setBounds(calculateSectionX(3,3), calculateY(6, LABEL), _section3Widths[2], LABEL.getH());

		_label_sellBuyLower = new JLabel("");
		_label_sellBuyLower.setHorizontalAlignment(SwingConstants.CENTER);
		_label_sellBuyLower.setBounds(calculateSectionX(3,4), calculateY(6, LABEL), _section3Widths[3], LABEL.getH());

		_label_profitLower = new JLabel("");
		_label_profitLower.setHorizontalAlignment(SwingConstants.CENTER);
		_label_profitLower.setBounds(calculateSectionX(3,5), calculateY(6, LABEL), _section3Widths[4], LABEL.getH());

		add(_label_buyLower);
		add(_label_sellLower);
		add(_label_buySellLower);
		add(_label_sellBuyLower);
		add(_label_profitLower);

		//Row 7 - Section 3
		_label_buyHigher = new JLabel("");
		_label_buyHigher.setHorizontalAlignment(SwingConstants.CENTER);
		_label_buyHigher.setBounds(calculateSectionX(3,1), calculateY(7, LABEL), _section3Widths[0], LABEL.getH());

		_label_sellHigher = new JLabel("");
		_label_sellHigher.setHorizontalAlignment(SwingConstants.CENTER);
		_label_sellHigher.setBounds(calculateSectionX(3,2), calculateY(7, LABEL), _section3Widths[1], LABEL.getH());

		_label_buySellHigher = new JLabel("");
		_label_buySellHigher.setHorizontalAlignment(SwingConstants.CENTER);
		_label_buySellHigher.setBounds(calculateSectionX(3,3), calculateY(7, LABEL), _section3Widths[2], LABEL.getH());

		_label_sellBuyHigher = new JLabel("");
		_label_sellBuyHigher.setHorizontalAlignment(SwingConstants.CENTER);
		_label_sellBuyHigher.setBounds(calculateSectionX(3,4), calculateY(7, LABEL), _section3Widths[3], LABEL.getH());

		_label_profitHigher = new JLabel("");
		_label_profitHigher.setHorizontalAlignment(SwingConstants.CENTER);
		_label_profitHigher.setBounds(calculateSectionX(3,5), calculateY(7, LABEL), _section3Widths[4], LABEL.getH());

		add(_label_buyHigher);
		add(_label_sellHigher);
		add(_label_buySellHigher);
		add(_label_sellBuyHigher);
		add(_label_profitHigher);

	}

//--------------------------------------------------------------Methods--------------------------------------------------------------//

	private int calculateY(int row, SwingObj type) {
		return (_defaultYGap * row) + type.getV() + 10;
	}

	private int calculateSectionX(int sectionId, int column) {
	
		int gap;
		int[] widths;
		
		switch (sectionId) {
		case 1:
			gap = _section1XGap;
			widths = _section1Widths;
			break;
		case 2:
			gap = _section2XGap;
			widths = _section2Widths;
			break;
		case 3:
			gap = _section3XGap;
			widths = _section3Widths;
			break;
		default:
			return 0;
		}
		
		int x = ((column - 1) * gap) + _defaultXGap;
		for (int i = 0; i < column - 1; i++) {
			x += widths[i];
		}
		return x;
	}
	
	public void displayOffers(SmartGenerateEvent event) {
		resetAllLabels();
		
		if (event == null) {
			return;
		}
		SmartEnum smartEnum = event.get_smartEnum();
		if (smartEnum == SmartEnum.DO_NOTHING || smartEnum == SmartEnum.SELECT_CURR || smartEnum == SmartEnum.SELECT_EXCHANGE) {
			return;
		} else {
			ExchangeData exDataLower = event.get_exDataLower();
			setOfferLabels(_label_buyLower, _label_sellLower, _label_buySellLower, _label_sellBuyLower, _label_profitLower, exDataLower);
		} 
		ExchangeData exDataHigher = event.get_exDataHigher();
		if (smartEnum == SmartEnum.CALC_SELL || smartEnum == SmartEnum.CALC_BUY) {
			setOfferLabels(_label_buyHigher, _label_sellHigher, _label_buySellHigher, _label_sellBuyHigher, _label_profitHigher, exDataHigher);
			if (smartEnum == SmartEnum.CALC_SELL) {
				_textField_Sell.setText("");
			} else {
				_textField_Buy.setText("");
			}
		}
	}

	private int getIndexByName(String name) {
		for (int i = 0; i < _currNameList.length; i++) {
			if (_currNameList[i].equals(name)) {
				return i;
			}
		}
		return 0;
	}

	public void setValues(ExchangeData finaloffer) {
	
		setOfferLabels(_label_buyLower, _label_sellLower, _label_buySellLower, _label_sellBuyLower, _label_profitLower, finaloffer);
		resetOfferLabels(_label_buyHigher, _label_sellHigher, _label_buySellHigher, _label_sellBuyHigher, _label_profitHigher);
		_textField_Sell.setText(_label_sellLower.getText());
		_textField_Buy.setText(_label_buyLower.getText());
	}
	
	public void setComboBoxSelection(String name, boolean isSellComboBox) {
		if (isSellComboBox) {
			_comboBox_sellingName.setSelectedIndex(getIndexByName(name));
			_comboBox_buyingName.setSelectedIndex(0);
		} else {
			_comboBox_buyingName.setSelectedIndex(getIndexByName(name));
		}
	}
	
	private void setOfferLabels(JLabel buy, JLabel sell, JLabel buySell, JLabel sellBuy, JLabel profit, ExchangeData offer) {
		if (offer != null) {
			buy.setText(String.valueOf(offer.get_buyValue()));
			sell.setText(String.valueOf(offer.get_sellValue()));
			buySell.setText(Utility.sigDigRounder(offer.get_buySellRatio(), 3));
			sellBuy.setText(Utility.sigDigRounder(offer.get_sellBuyRatio(), 3));
			profit.setText(Utility.sigDigRounder(offer.get_chaosProfit(), 3));
		} else {
			resetTextFields();
			resetAllLabels();
		}
	}
	
	public void resetTextFields() {
		_textField_Sell.setText("");
		_textField_Buy.setText("");
	}
	
	private void resetAllLabels() {
		resetOfferLabels(_label_buyLower, _label_sellLower, _label_buySellLower, _label_sellBuyLower, _label_profitLower);
		resetOfferLabels(_label_buyHigher, _label_sellHigher, _label_buySellHigher, _label_sellBuyHigher, _label_profitHigher);
	}
	
	private void resetOfferLabels(JLabel buy, JLabel sell, JLabel buySell, JLabel sellBuy, JLabel profit) {
		buy.setText("");
		sell.setText("");
		buySell.setText("");
		sellBuy.setText("");
		profit.setText("");;
	
		
		
		
	
	}
	
//--------------------------------------------------------------Event Handling--------------------------------------------------------------//

	//**********SmartGenerate event handling**********//
	public void addSmartGenerateListener(SmartGenerateListener listener) {
		listenerList.add(SmartGenerateListener.class, listener);
	}

	private void fireSmartGenerateEvent(SmartGenerateEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for(int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == SmartGenerateListener.class) {
				((SmartGenerateListener) listeners[i + 1]).SmartGenerateEventOccured(event);
			}
		}
	}
	
//--------------------------------------------------------------Getters and Setters--------------------------------------------------------------//
	
	public void set_nameComboBoxes(String[] currNameList) {
		_currNameList = currNameList;
		_comboBox_buyingName.setModel(new DefaultComboBoxModel<String>(currNameList));
		_comboBox_sellingName.setModel(new DefaultComboBoxModel<String>(currNameList));
	}


}
