package gui.views;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import backend.dataStructures.Currency;


public class Renderer_Currency extends DefaultTableCellRenderer {
	//--------------------------------------------------------------Fields--------------------------------------------------------------//
	
		private static final long serialVersionUID = -8779708185609240801L;
		private static Color _lightGreen = new Color(0, 255, 0, 25);
		private static Color _darkGreen = new Color(0, 100, 0);
	
	//--------------------------------------------------------------Constructor--------------------------------------------------------------//

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) { 
			JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); 
			

			TableModel model = table.getModel();
			Currency curr = (Currency) model.getValueAt(row, 3);
		
			Object cell = model.getValueAt(row, column);
			if (cell == null) {
				c.setBackground(Color.RED);
			} else if (curr.is_Common()) {
				c.setBackground(_lightGreen);
				c.setForeground(Color.BLACK);
				if (isSelected) {
					c.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, _darkGreen));
				}
			} else {
				c.setBackground(table.getBackground());
				c.setForeground(table.getForeground());
			}
			
			
			
			//Align text to center
			if (column == 0) {
				setHorizontalAlignment(JLabel.CENTER);
			}

			return c; 
		}
}
