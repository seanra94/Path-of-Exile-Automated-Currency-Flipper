package gui.views;

import java.awt.Color;
import java.awt.Font;
import java.util.stream.IntStream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import backend.Utility;
import backend.dataStructures.Exchange;
import backend.dataStructures.ExchangeData;

public class Panel_ExchangeSummary extends JPanel {

//--------------------------------------------------------------Fields--------------------------------------------------------------//
	private static final long serialVersionUID = 3494087116478578099L;
	
	private final JLabel _label_buyingName;
	private final JLabel _label_sellingName;
	private final JLabel _label_buyingValue;
	private final JLabel _label_sellingValue;
	private final JLabel _label_buyingStock;
	private final JLabel _label_sellingStock;
	
	private final JLabel _label_buySellBounds;
	private final JLabel _label_sellBuyBounds;

	private final JLabel _label_buy;
	private final JLabel _label_sell;
	private final JLabel _label_buySell;
	private final JLabel _label_sellBuy;
	private final JLabel _label_profit;
	
	private int _width;
	//private int _height;
	
	private int _defaultYGap = 20;
	private int _defaultXGap = 20;

	private int _rowTitleWidth = 78;
	private int _section1Width;
	private int _section1Column1;
	private int _section1Column2;
	private int _section1Column3;
	private int _section3XGap;
	private int[] _section3Widths;
	
	
	
	
//--------------------------------------------------------------Constructor--------------------------------------------------------------//

	public Panel_ExchangeSummary(int x, int y, int width, int height) {
		
		_width = width;
		//_height = height;
		
		setBounds(x, y, width, height);
		setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Exchange Summary", 
				TitledBorder.CENTER, TitledBorder.TOP, new Font(Font.SANS_SERIF, Font.PLAIN, 24), new Color(0, 0, 0)));
		setLayout(null);
		
		_section1Width = (_width - 4 * _defaultXGap - _rowTitleWidth)/2;
		_section1Column1 = _defaultXGap;
		_section1Column2 = _defaultXGap * 2 + _rowTitleWidth;
		_section1Column3 = _defaultXGap * 3 + _rowTitleWidth + _section1Width;

		//Row 1 - Section 1
		JLabel label_buyingTitle = new JLabel("Buying:");
		label_buyingTitle.setHorizontalAlignment(SwingConstants.CENTER);
		label_buyingTitle.setBounds(_section1Column2, calculateY(1), _section1Width, 14);
		
		JLabel label_sellingTitle = new JLabel("Selling:");
		label_sellingTitle.setHorizontalAlignment(SwingConstants.CENTER);
		label_sellingTitle.setBounds(_section1Column3, calculateY(1), _section1Width, 14);
		
		add(label_buyingTitle);
		add(label_sellingTitle);
		
		//Row 2 - Section 1
		JLabel label_nameTitle = new JLabel("Name:");
		label_nameTitle.setBounds(_section1Column1, calculateY(2), _rowTitleWidth, 14);
		
		_label_buyingName = new JLabel("");
		_label_buyingName.setHorizontalAlignment(SwingConstants.CENTER);
		_label_buyingName.setBounds(_section1Column2, calculateY(2), _section1Width, 14);
		
		_label_sellingName = new JLabel("");
		_label_sellingName.setHorizontalAlignment(SwingConstants.CENTER);
		_label_sellingName.setBounds(_section1Column3, calculateY(2), _section1Width, 14);
		
		add(label_nameTitle);
		add(_label_buyingName);
		add(_label_sellingName);

		//Row 3 - Section 1
		JLabel label_valueTitle = new JLabel("Value:");
		label_valueTitle.setBounds(_section1Column1, calculateY(3), _rowTitleWidth, 14);
	
		_label_buyingValue = new JLabel("");
		_label_buyingValue.setHorizontalAlignment(SwingConstants.CENTER);
		_label_buyingValue.setBounds(_section1Column2, calculateY(3), _section1Width, 14);
		
		_label_sellingValue = new JLabel("");
		_label_sellingValue.setHorizontalAlignment(SwingConstants.CENTER);
		_label_sellingValue.setBounds(_section1Column3, calculateY(3), _section1Width, 14);
		
		add(label_valueTitle);
		add(_label_buyingValue);
		add(_label_sellingValue);
		
		//Row 4 - Section 1
		JLabel label_stockTitle = new JLabel("Stock:");
		label_stockTitle.setBounds(_section1Column1, calculateY(4), _rowTitleWidth, 14);
		
		_label_buyingStock = new JLabel("");
		_label_buyingStock.setHorizontalAlignment(SwingConstants.CENTER);
		_label_buyingStock.setBounds(_section1Column2, calculateY(4), _section1Width, 14);

		_label_sellingStock = new JLabel("");
		_label_sellingStock.setHorizontalAlignment(SwingConstants.CENTER);
		_label_sellingStock.setBounds(_section1Column3, calculateY(4), _section1Width, 14);
		
		add(label_stockTitle);
		add(_label_buyingStock);
		add(_label_sellingStock);
		
		//Row 5
		//-----Gap-----
		
		//Row 6 - Section 2
		JLabel label_BuySellTitle = new JLabel("Buy/Sell");
		label_BuySellTitle.setHorizontalAlignment(SwingConstants.CENTER);
		label_BuySellTitle.setBounds(_section1Column2, calculateY(6), _section1Width, 14);
		
		JLabel label_SellBuyTitle = new JLabel("Sell/Buy");
		label_SellBuyTitle.setHorizontalAlignment(SwingConstants.CENTER);
		label_SellBuyTitle.setBounds(_section1Column3, calculateY(6), _section1Width, 14);
		
		add(label_BuySellTitle);
		add(label_SellBuyTitle);
		
		//Row 7 - Section 2
		JLabel label_boundsTitle = new JLabel("Bounds:");
		label_boundsTitle.setBounds(_section1Column1, calculateY(7), _rowTitleWidth, 14);
		
		_label_buySellBounds = new JLabel("");
		_label_buySellBounds.setHorizontalAlignment(SwingConstants.CENTER);
		_label_buySellBounds.setBounds(_section1Column2, calculateY(7), _section1Width, 14);
		
		_label_sellBuyBounds = new JLabel("");
		_label_sellBuyBounds.setHorizontalAlignment(SwingConstants.CENTER);
		_label_sellBuyBounds.setBounds(_section1Column3, calculateY(7), _section1Width, 14);
		
		add(label_boundsTitle);
		add(_label_buySellBounds);
		add(_label_sellBuyBounds);

		
		//Row 8
		//-----Gap-----
		
		
		_section3Widths = new int[] {56, 56, 56, 56, 56};
		_section3XGap = Math.max((_width - 2 * _defaultXGap - IntStream.of(_section3Widths).sum())/(_section3Widths.length - 1), 0);
		
		//Row 9 - Section 3
		JLabel label_finalOfferTitle = new JLabel("Final Offer");
		label_finalOfferTitle.setHorizontalAlignment(SwingConstants.CENTER);
		label_finalOfferTitle.setBounds(0, calculateY(9), _width, 14);
		
		//Row 10 - Section 3
		JLabel label_buyTitle = new JLabel("Buy");
		label_buyTitle.setHorizontalAlignment(SwingConstants.CENTER);
		label_buyTitle.setBounds(calculateSectionX(3,1), calculateY(10), _section3Widths[0], 14);

		JLabel label_sellTitle = new JLabel("Sell");
		label_sellTitle.setHorizontalAlignment(SwingConstants.CENTER);
		label_sellTitle.setBounds(calculateSectionX(3,2), calculateY(10), _section3Widths[1], 14);
		
		JLabel label_buySellTitle = new JLabel("Buy/Sell");
		label_buySellTitle.setHorizontalAlignment(SwingConstants.CENTER);
		label_buySellTitle.setBounds(calculateSectionX(3,3), calculateY(10), _section3Widths[2], 14);
		
		JLabel label_sellBuyTitle = new JLabel("Sell/Buy");
		label_sellBuyTitle.setHorizontalAlignment(SwingConstants.CENTER);
		label_sellBuyTitle.setBounds(calculateSectionX(3,4), calculateY(10), _section3Widths[3], 14);
		
		JLabel label_profitTitle = new JLabel("Profit");
		label_profitTitle.setHorizontalAlignment(SwingConstants.CENTER);
		label_profitTitle.setBounds(calculateSectionX(3,5), calculateY(10), _section3Widths[4], 14);
		
		add(label_finalOfferTitle);
		add(label_buyTitle);
		add(label_sellTitle);
		add(label_buySellTitle);
		add(label_sellBuyTitle);
		add(label_profitTitle);
		
		//Row 11 - Section 3
		_label_buy = new JLabel("");
		_label_buy.setHorizontalAlignment(SwingConstants.CENTER);
		_label_buy.setBounds(calculateSectionX(3,1), calculateY(11), _section3Widths[0], 14);
		
		_label_sell = new JLabel("");
		_label_sell.setHorizontalAlignment(SwingConstants.CENTER);
		_label_sell.setBounds(calculateSectionX(3,2), calculateY(11), _section3Widths[1], 14);
		
		_label_buySell = new JLabel("");
		_label_buySell.setHorizontalAlignment(SwingConstants.CENTER);
		_label_buySell.setBounds(calculateSectionX(3,3), calculateY(11), _section3Widths[2], 14);
		
		_label_sellBuy = new JLabel("");
		_label_sellBuy.setHorizontalAlignment(SwingConstants.CENTER);
		_label_sellBuy.setBounds(calculateSectionX(3,4), calculateY(11), _section3Widths[3], 14);
		
		_label_profit = new JLabel("");
		_label_profit.setHorizontalAlignment(SwingConstants.CENTER);
		_label_profit.setBounds(calculateSectionX(3,5), calculateY(11), _section3Widths[4], 14);
		
		add(_label_buy);
		add(_label_sell);
		add(_label_buySell);
		add(_label_sellBuy);
		add(_label_profit);
	}

//--------------------------------------------------------------Methods--------------------------------------------------------------//

	public void setFields(String sellName, String buyName, Exchange exchange) {
		_label_sellingName.setText(sellName);
		
		if (exchange != null) {
			_label_buyingName.setText(buyName);
			_label_buyingValue.setText(Utility.sigDigRounder(exchange.get_BuyChaosValue(), 3));
			 _label_sellingValue.setText(Utility.sigDigRounder(exchange.get_SellChaosValue(), 3));
			_label_buyingStock.setText("[" + String.valueOf(exchange.get_minBuy()) + ", " + String.valueOf(exchange.get_maxBuy()) + "]");
			_label_sellingStock.setText("[" + String.valueOf(exchange.get_minSell()) + ", " + String.valueOf(exchange.get_maxSell()) + "]");
			
			_label_buySellBounds.setText("[" + Utility.sigDigRounder(exchange.get_BuySellLowerBound(), 3) + ", " + Utility.sigDigRounder(exchange.get_BuySellUpperBound(), 3) + "]");
			_label_sellBuyBounds.setText("[" + Utility.sigDigRounder(exchange.get_SellBuyLowerBound(), 3) + ", " + Utility.sigDigRounder(exchange.get_SellBuyUpperBound(), 3) + "]");

			ExchangeData finaloffer = exchange.get_finalOffer();
			if (finaloffer != null) {
				_label_buy.setText(String.valueOf(finaloffer.get_buyValue()));
				_label_sell.setText(String.valueOf(finaloffer.get_sellValue()));
				_label_buySell.setText(Utility.sigDigRounder(finaloffer.get_buySellRatio(), 3));
				_label_sellBuy.setText(Utility.sigDigRounder(finaloffer.get_sellBuyRatio(), 3));
				_label_profit.setText(Utility.sigDigRounder(finaloffer.get_chaosProfit(), 3));
			} else {
				_label_buy.setText("");
				_label_sell.setText("");
				_label_buySell.setText("");
				_label_sellBuy.setText("");
				_label_profit.setText("");
			}
		} else {
			_label_buyingName.setText("");
			_label_buyingValue.setText("");
			 _label_sellingValue.setText("");
			_label_buyingStock.setText("");
			_label_sellingStock.setText("");
			_label_buySellBounds.setText("");
			_label_sellBuyBounds.setText("");

	
			_label_buy.setText("");
			_label_sell.setText("");
			_label_profit.setText("");
			_label_buySell.setText("");
			_label_sellBuy.setText("");
		}
		
	}
	
	private int calculateY(int row) {
		return _defaultYGap * row + 15;
	}

	private int calculateSectionX(int sectionId, int column) {
		
		int gap;
		int[] widths;
		
		switch (sectionId) {

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

	
}
