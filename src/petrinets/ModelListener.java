package petrinets;

import java.util.EventListener;

/**
 * Dieser Beobachter reagiert auf Veränderungen eines Datenmodells.<br>
 * Der Beobachter meldet sich bei einem Datenmodell über die dort implementierte <code>addListener()</code> Methode an.
 *
 * @see ModelEvent
 *
 * @author Michael Assmair
 * 
 */
public interface ModelListener extends EventListener {
	
	/**
	 * Wird vom Datenmodell nach Veränderung aufgerufen
	 * 
	 * @param evt Event der übergeben werden soll
	 * 
	 * @see ModelEvent
	 */
	void modelChanged(ModelEvent evt);
	
}
