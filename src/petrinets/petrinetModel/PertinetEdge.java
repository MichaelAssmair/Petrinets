package petrinets.petrinetModel;


/**
 * Diese Klasse repräsentiert eine Kante zwischen<br> 
 * zwei Knoten eines Markierungsgraphen.
 * 
 * @author Michael Assmair
 * 
 */
public class PertinetEdge {

	//ID der Kante
	private String id;
	
	//ID des Quellknoten der Kante
	private String sourceID;
	
	//ID des Zielknoten der Kante
	private String targetID;
	
	
	/**
	 * Konstruktor für eine Kante mit Quell- und Zielknoten.
	 * 
	 * @param id String - ID der Kante
	 * @param sourceID String - ID des Quellknoten
	 * @param targetID String - ID des Zielknoten
	 */
	PertinetEdge(String id, String sourceID, String targetID) {	
		this.id = id;
		this.sourceID = sourceID;
		this.targetID = targetID;
	}
	
	/**
	 * Getter-Methode, die die ID der Kante liefert.
	 * 
	 * @return ID der Kante.
	 */
	public final String getId() {	
		return id;
	}
	
	/**
	 * Getter-Methode, die die ID des Quellknoten liefert.
	 * 
	 * @return ID des Quellknoten.
	 */
	public final String getSourceID() {	
		return sourceID;
	}
	
	/**
	 * Getter-Methode, die die ID des Zielknoten liefert.
	 * 
	 * @return ID des Zielknoten.
	 */
	public final String getTargetID() {	
		return targetID;
	}
}

