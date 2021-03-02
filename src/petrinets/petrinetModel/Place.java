package petrinets.petrinetModel;

/**
 * Diese Klasse repräsentiert eine Stelle eines Petri-Netzes.<br>
 * Die Klasse ist von {@link Element} abgeleitet.
 * 
 * @author Michael Assmair
 * 
 * @see Element
 * @see Petrinet
 *
 */
public class Place extends Element{
	//Anzahl der Markierungen
	private int tokens;
	
	/**
	 * Konstruktor der eine neue Stelle mit ID liefert.
	 * 
	 * @param id ID der Stelle
	 */
	public Place(String id) {
		super(id);
	}
	
	/**
	 * Getter-Methode, die die Anzahl der zur Stelle gehörenden Marken liefert.
	 * 
	 * @return Anzahl an Marken der Stelle.
	 */
	public final int getTokens() {
		return tokens;
	}

	/**
	 * Setter-Methode, die die Anzahl von Marken der Stelle setzt.
	 * 
	 * @param tokens Anzahl von Marken.
	 */
	final void setTokens(int tokens) {
		this.tokens = tokens;
	}
}