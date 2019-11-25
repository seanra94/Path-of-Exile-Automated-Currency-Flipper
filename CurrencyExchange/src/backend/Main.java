package backend;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import backend.dataStructures.Timer;
import backend.models.CommonModel;
import backend.models.CurrencyModel;
import backend.models.FinalOfferModel;
import backend.models.PotentialOfferModel;
import backend.models.PushOffersModel;
import backend.models.PushOffersModel2;
import backend.models.StatisticalAnalysisModel;
import backend.models.Writer;
import gui.models.Controller;
import gui.views.MainFrame;
import gui.views.Menu;
import gui.views.Panel_ContentPane;
import gui.views.Panel_ExchangeSummary;
import gui.views.Panel_Search;
import gui.views.Panel_TabbedPanel;
import gui.views.Panel_CurrencyTable;
import gui.views.Panel_WebOfferTable;


public class Main {
	
	private static int[] _widths;
	private static int[] _heights;

//--------------------------------------------------------------Methods--------------------------------------------------------------//

	//--------------------
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		Timer mainTimer = new Timer("mainTimer");
		mainTimer.start();

		Main main = new Main();
		ExecutorService es = Executors.newFixedThreadPool(2);
		es.execute(main.new MainThread(true));
		es.execute(main.new MainThread(false));
		es.shutdown();	
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Program successfully shut down");
		
		mainTimer.stop();
		Timer.printTimes();

	}
	
	//--------------------
	private static void generateGuiThread() throws Exception {
	
		Menu menu = new Menu();
		
		_widths = new int[] {370, 275, 275};
		_heights = new int[] {258, 265};
		
		//Column 1
		Panel_Search searchView = new Panel_Search(calculateX(1), -4, _widths[0], _heights[0]);
		Panel_ExchangeSummary exchangeSummaryView = new Panel_ExchangeSummary(calculateX(1), 254, _widths[0], _heights[1]);
		Panel_WebOfferTable webOfferTableView = new Panel_WebOfferTable(_widths[0], 638);
		Panel_TabbedPanel tabbedView = new Panel_TabbedPanel(calculateX(1), _heights[0] + _heights[1] + 10, _widths[0], Global.getyRes() - 100 - _heights[0] - _heights[1] - 20, webOfferTableView);
		//Column 2
		Panel_CurrencyTable sellTableView = new Panel_CurrencyTable(calculateX(2), -9, _widths[1], Global.getyRes() - 100, true);
		
		//Column 3
		Panel_CurrencyTable buyTableView = new Panel_CurrencyTable(calculateX(3), -9, _widths[2], Global.getyRes() - 100, false);
		
		Panel_ContentPane contentPane = new Panel_ContentPane(searchView, sellTableView, buyTableView, exchangeSummaryView, tabbedView);
		@SuppressWarnings("unused")
		Controller controller = new Controller(searchView, sellTableView, buyTableView, exchangeSummaryView, webOfferTableView);
		
		try {
			new MainFrame(contentPane, menu);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	

	private static int calculateX(int column) {
		
		int x = column * Global.getDefaultXGap();
		for (int i = 0; i < column - 1; i++) {
			x += _widths[i];
		}
		return x;
	}
	
	
	//--------------------
	private static void generateDataThread() throws Exception {
		Global.reset();
		Timer currencyModelTimer = new Timer("currencyModelTimer");
		Timer statModelTimer = new Timer("statModelTimer");
		Timer commonModelTimer = new Timer("commonModelTimer");
		Timer potentialModelTimer = new Timer("potentialModelTimer");
		Timer finalOfferModelTimer = new Timer("finalOfferModelTimer");
		Timer writerTimer = new Timer("writerTimer");
		Timer pushTimer = new Timer("pushTimer");
		
		//Currency model
		currencyModelTimer.start();
		CurrencyModel.generateAllCurrencyData();
		currencyModelTimer.stop();
		System.out.println("--------------------------CurrencyModel Finished--------------------------");
		
		//Stats model
		statModelTimer.start();
		StatisticalAnalysisModel.analyseAllExchanges();
		statModelTimer.stop();
		System.out.println("--------------------------StatisticalAnalysisModel Finished--------------------------");
		
		//Common model
		commonModelTimer.start();
		CommonModel.generateCommonExchanges();
		commonModelTimer.stop();
		System.out.println("--------------------------CommonModel Finished--------------------------");
		
		//Potential model
		potentialModelTimer.start();
		PotentialOfferModel.generatePotentialOffers();
		potentialModelTimer.stop();
		System.out.println("--------------------------PotentialOfferModel Finished--------------------------");
		
		//Final offer model
		finalOfferModelTimer.start();
		FinalOfferModel.generateFinalOffers();
		finalOfferModelTimer.stop();
		System.out.println("--------------------------FinalOfferModel Finished--------------------------");
		
		Global.fireDataGeneratedEvent(); //Update the GUI with new info
		
		
		//Writer
		writerTimer.start();
		Writer.exportCurrenciesToText();
		writerTimer.stop();
		System.out.println("--------------------------Writer Finished--------------------------");
		
		
		//Push
		pushTimer.start();
		new PushOffersModel(Global.isDeleteOnly());
		//PushOffersModel2.pushExchanges();
		pushTimer.stop();
		System.out.println("--------------------------PushExchanges Finished--------------------------");
	}

//--------------------------------------------------------------Runnable--------------------------------------------------------------//

	//--------------------
	private class MainThread implements Runnable {
		private boolean _generateData;

		//--------------------
		MainThread(boolean generateData){
			_generateData = generateData;
		}

		//--------------------
		@Override
		public void run() {
			if (_generateData) {
				try {
					generateDataThread();
				} catch (Exception e) {
					e.printStackTrace();
					run();
				}
			} else {
				try {
					generateGuiThread();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
