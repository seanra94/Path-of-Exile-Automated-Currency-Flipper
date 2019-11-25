package gui.views;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


public class Menu extends JMenuBar {
//--------------------------------------------------------------Fields--------------------------------------------------------------//
	private static final long serialVersionUID = -3887411559288132121L;
//--------------------------------------------------------------Constructor--------------------------------------------------------------//
	
	public Menu() throws Exception {
		JMenu fileMenu = new JMenu("File");
		add(fileMenu);
		
		JMenuItem menu_exportNormalData = new JMenuItem(new AbstractAction("Menu Item") {
			private static final long serialVersionUID = -6420377401575198442L;
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					//fireExportNormalDataEvent(new ExportNormalDataEvent(this));
				} catch (Exception e) {
					e.printStackTrace();	
				}
		    }
		});
		fileMenu.add(menu_exportNormalData);
	}
}
	
	/*
	public Menu throws Exception {

		
		

		
		JMenuItem menu_exportNormalWorksheet = new JMenuItem(new AbstractAction("Export data to .csv file") {
			private static final long serialVersionUID = 8073815173150128492L;
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					fireExportNormalWorksheetEvent(new ExportNormalWorksheetEvent(this));
				} catch (Exception e) {
					new MiscClasses.Views.Frame_Crash(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(e));
					e.printStackTrace();	
				}
		    }
		});
		fileMenu.add(menu_exportNormalWorksheet);

		
	
		JMenu helpMenu = new JMenu("Help");
		add(helpMenu);
		
		JMenuItem menu_About = new JMenuItem(new AbstractAction("About") {
			private static final long serialVersionUID = -528331841370581818L;
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					new Frame_About();
				} catch (Exception e) {
					new MiscClasses.Views.Frame_Crash(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(e));
					e.printStackTrace();	
				}
			}
		});
		helpMenu.add(menu_About);
	}
	
	*/
//--------------------------------------------------------------Event Handling--------------------------------------------------------------//
	
	
	//**********Export normal data**********//
	/*
	public void addExportNormalDataListener(ExportNormalDataListener listener) {
		listenerList.add(ExportNormalDataListener.class, listener);
	}
	
	public void fireExportNormalDataEvent(ExportNormalDataEvent event) throws Exception {
		Object[] listeners = listenerList.getListenerList();
		
		for(int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == ExportNormalDataListener.class) {
				((ExportNormalDataListener) listeners[i + 1]).ExportNormalDataEventOccured(event);
			}
		}
	}
	
	//**********Export normal worksheet**********///
	
	/*
	public void addExportNormalWorksheetListener(ExportNormalWorksheetListener listener) {
		listenerList.add(ExportNormalWorksheetListener.class, listener);
	}
	
	public void fireExportNormalWorksheetEvent(ExportNormalWorksheetEvent event) throws Exception {
		Object[] listeners = listenerList.getListenerList();
		
		for(int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == ExportNormalWorksheetListener.class) {
				((ExportNormalWorksheetListener) listeners[i + 1]).ExportNormalWorksheetEventOccured(event);
			}
		}
	}
	*/
	

