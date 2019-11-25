package backend.dataStructures;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Timer {
	
//--------------------------------------------------------------Fields--------------------------------------------------------------//

	private final static  ArrayList<Timer> timers = new ArrayList<Timer>();
	private final static DecimalFormat df = new DecimalFormat("#.##");
	private String _name;
	private long _startTime;
	private long _totalTime;
	
//--------------------------------------------------------------Constructor--------------------------------------------------------------//

	public Timer(String name) {
		_name = name;
		_startTime = 0;
		_totalTime = 0;
		timers.add(this);
	}
	
//--------------------------------------------------------------Methods--------------------------------------------------------------//
	
	public void start() {
		_startTime = System.nanoTime();
	}
	
	public void stop() {
		_totalTime += (System.nanoTime() - _startTime);
	}
	
	public static void printTimes() {
		for (Timer timer : timers) {
			System.out.println(timer._name + ": " + timer._totalTime / 1000000 +"ms" + " (" + df.format((double) timer._totalTime / 1000000000) +"s)");
		}
	}

}
