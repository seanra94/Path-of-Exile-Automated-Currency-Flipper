package gui.eventHandling;
import java.util.EventListener;

public interface DataGeneratedListener extends EventListener {
	public void DataGeneratedEventOccured(DataGeneratedEvent event) throws Exception;
}
