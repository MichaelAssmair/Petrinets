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
	private final String command;
	
	/**
	 * Konstruktor für einen ModelEvent.
	 * 
	 * 
	 * @param source Quelle des Events.
	 * @param command Befehl es Events
	 * 
	 * @see #getSource()
	 * @see #getCommand()
	 * @see ModelListener
	 */
	public ModelEvent(Object source, String command) {
		super(source);
		this.command = command;
	}
	
	/**
	 * Getter-Methode, die den Befehl des Events liefert
	 * 
	 * @return Befehl des Events
	 * 
	 */
	public String getCommand() {
		return command;
	}
}