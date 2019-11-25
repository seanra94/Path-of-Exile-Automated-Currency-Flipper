package gui.eventHandling;
import java.util.EventListener;

public interface CurrencyTableSelectedListener extends EventListener {
	public void CurrencyTableSelectedEventOccured(CurrencyTableSelectedEvent event) throws Exception;
}
