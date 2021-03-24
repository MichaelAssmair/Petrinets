package petrinets;

import java.util.EventObject;

/**
 * Ein Event der von einem Datenmodell, nach Änderung an die<br>
 * bei ihm angemeldeten Beobachter übergeben wird
 * 
 * 
 * @author Michael Assmair
 * 
 *
 * @see ModelListener
 */

public class ModelEvent extends EventObject {

	//default serial version ID
	private static final long serialVersionUID = 1L;
	
	//Befehl des Events
	private final ModelAction action;
	
	/**
	 * Konstruktor für einen ModelEvent.
	 * 
	 * 
	 * @param source Quelle des Events.
	 * @param action Befehl es Events
	 * 
	 * @see #getSource()
	 * @see #getAction()
	 * @see ModelListener
	 */
	public ModelEvent(Object source, ModelAction action) {
		super(source);
		this.action = action;
	}
	
	/**
	 * Getter-Methode, die den Befehl des Events liefert
	 * 
	 * @return Befehl des Events
	 * 
	 */
	public ModelAction getAction() {
		return action;
	}
}