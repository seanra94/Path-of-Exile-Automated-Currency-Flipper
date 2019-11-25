package gui.views;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import backend.Global;
import backend.Utility;
import backend.models.PushOffersModel;

public class MainFrame extends JFrame {

//--------------------------------------------------------------Fields--------------------------------------------------------------//
	private static final long serialVersionUID = -5241270804193453152L;
	private static MainFrame _frame;
//--------------------------------------------------------------Constructor--------------------------------------------------------------//
	public MainFrame(Panel_ContentPane contentPane, Menu menu) {
		//setResizable(false);

		addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent we) {
				  if (JOptionPane.showConfirmDialog(null, "Delete offers?", "Alert",
					        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					  new PushOffersModel(true);
					}
				  System.exit(0);
			  }
			});
		
		setBounds(0, 0, Global.getxRes(), Global.getyRes());
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2 - getSize().width/2, dim.height/2 - getSize().height/2);
		set_frame(this);

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ImageIcon icon = new ImageIcon(Utility.icon());
					setIconImage(icon.getImage());
					setContentPane(contentPane);
					setJMenuBar(menu);
					setTitle("POE App");
					setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	
	}
//--------------------------------------------------------------Getters and Setters--------------------------------------------------------------//

	public static MainFrame get_frame() {
		return _frame;
	}
	public static void set_frame(MainFrame _frame) {
		MainFrame._frame = _frame;
	}
	
	/*
	 * 			Panel_ContentPane contentPane = new Panel_ContentPane();
				JScrollPane test = new JScrollPane(contentPane,  JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	 */
}