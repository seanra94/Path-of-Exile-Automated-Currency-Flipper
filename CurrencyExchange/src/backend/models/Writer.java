package backend.models;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import backend.Global;
import backend.dataStructures.Currency;
import backend.dataStructures.Exchange;

public class Writer {

//--------------------------------------------------------------Fields--------------------------------------------------------------//
	
	private static Map<Integer, Currency> _webCurrenciesMap;
	private static final int MAX_ELEMENTS_PER_LINE = Global.getMaxElementsPerLine();
	private static final int MAX_SPACES = Global.getMaxSpaces();
	
//--------------------------------------------------------------Constructor--------------------------------------------------------------//

	//Utility.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
	public static void exportCurrenciesToText() throws Exception {
		String outputPath = Writer.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath() + "output";
		File outputDirectory = new File (outputPath);
		if (!outputDirectory.isDirectory()) {
			System.out.println("Making new output dir...");
			outputDirectory.mkdir();
		}
		purgeDirectory(outputDirectory);

		_webCurrenciesMap = Global.get_currMapNoC();
		ExecutorService es = Executors.newFixedThreadPool(_webCurrenciesMap.size());
		Currency sellCurrency;
		Currency buyCurrency;
		Exchange exchange;
		Writer writer = new Writer();
		File file;
		File newDirectory;

		for (Entry<Integer, Currency> currencyEntry : _webCurrenciesMap.entrySet()) {
			sellCurrency = currencyEntry.getValue();
			newDirectory = new File(Writer.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath() + "output\\(" + sellCurrency.get_id() + ") " + sellCurrency.get_name());
			newDirectory.mkdir();
			//System.out.println(newDirectory.getAbsolutePath());

			for (Entry<Integer, Exchange> exchangeEntry : sellCurrency.get_webOffersMap().entrySet()) {
				exchange = exchangeEntry.getValue();
				buyCurrency = _webCurrenciesMap.get(exchange.get_buyId());
				if (buyCurrency == null) {

					file = new File(newDirectory.getAbsolutePath() + "\\(" + buyCurrency.get_id() + ") " + buyCurrency.get_name() + ".txt");
					es.execute(writer.new WriterRunnable(exchange, file, newDirectory));
				}

			}

			es.shutdown();	
			try {
				es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(0);

			}
		}
	}
	
	private static void purgeDirectory(File dir) {
	    for (File file: dir.listFiles()) {
	        if (file.isDirectory()) {
	        	purgeDirectory(file);
	        }
	        file.delete();
	    }
	}
	
	private void exportExchangeToText(Exchange exchange, File file, File folder) {
		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fileWriter);
			bWriter.write(exchange.toString(MAX_ELEMENTS_PER_LINE, MAX_SPACES));
			bWriter.close();  	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//--------------------
	private class WriterRunnable implements Runnable {
		private final Exchange _exchange;
		private final File _file;
		private final File _folder;

		//--------------------
		WriterRunnable(Exchange exchange, File file, File folder){
			_exchange = exchange;
			_file = file;
			_folder = folder;
		}

		//--------------------
		@Override
		public void run() {
			exportExchangeToText(_exchange, _file, _folder);
		}
	}
	
	
}
