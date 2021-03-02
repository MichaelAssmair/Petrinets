package petrinets.markingGraphModel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import petrinets.petrinetModel.Place;

/**
 * Diese Klasse repräsentiert die Marken aller Stellen eines Petri-Netzes.<br>
 * Die Klasse verfügt über eine Methode <code>isOmega()</code>, die einen Teil der Abbruchbedingung prüft, 
 * der für den Beschränktheits-Algorithmus benötigt wird.
 * 
 * 
 * @author Michael Assmair
 * 
 * @see Place
 * @see #isOmega(Marking)
 *
 */
public class Marking {
	
	//Array der die Anzahl der Marken für jede Stelle speichert
	private int[] marking;
	
	//ID der Markierung
	private int markingID;
	
	//Adjazenztliste der ausgehenden Kanten
	private Set<MarkingGraphEdge> adjList = new HashSet<>();
	
	/**
	 * Konstruktor für eine Markierung.<br>
	 * Aus der übergebene Menge an Stellen wird die Markierung des Petri-Netzes bestimmt.
	 * 
	 * @param places Menge der Stellen des Petri-Netzes mit der aktuellen Anzahl von Tokens.
	 * 
	 * @see Place
	 * @see LinkedHashMap
	 */
	public Marking(Map<String, Place> places) {
		//initialisiert den Array anhand der Anzahl der Stellen und 
		//schreibt für jede Stelle die Anzahl der Tokens in einen eigenen Eintrag des Arrays.
		marking = new int[places.size()];
		//Hilfsvariable um in der Schleife auf den Array
		//zugreifen zu können
		int idx = 0;	
		for(Place place : places.values()) {
			marking[idx] = place.getTokens();
			idx++;
		}
	}
	
	/**
	 * Getter-Methode, die die Liste der ausgehende Kanten liefert.
	 * 
	 * @return Liste der ausgehenden Kanten.
	 */
	public final Set<MarkingGraphEdge> getAdjList() {
		return adjList;
	}
	
	/**
	 * Getter-Methode, die die Array-Darstellung der Markierung liefert.
	 * 
	 * @return Array mit Marken
	 */
	public final int[] getMarking() {	
		return marking;
	}
	
	/**
	 * Getter-Methode, die die String-Darstellung der ID der Markierung liefert.
	 * 
	 * @return ID der Markierung
	 */
	public final String getMarkingID() {	
		return Integer.toString(markingID);
	}

	/**
	 * Setter-Methoden, die die ID der Markierung setzt.
	 * 
	 * @param id ID der Markierung
	 */
	public final void setMarkingID(int id) {	
		markingID = id;
	}
	
	/**
	 * Überschreibt die toString Methode der Object-Klasse.<br>
	 * Liefert eine String-Darstellung der Markierung in der Form<br> 
	 * "(Token der ersten Stelle|Token der zweiten Stelle|...|Token der letzten Stelle)"
	 * 
	 * @return String-Darstellung der Markierung
	 * 
	 */
	@Override
	public final String toString() {	
		StringBuilder sb = new StringBuilder("(");	
		for(int i = 0; i < marking.length; i++) {
			sb.append(marking[i]);
			if(i < marking.length-1)
				sb.append("|");
		}		
		sb.append(")");
		return sb.toString();
	}
	
	/**
	 * /**
	 * Überschreit die equals Methode der Object-Klasse.<br>
	 * Vergleicht Instanzen anhand ihrer Markierung.
	 * 
	 * @param obj die mit der Instanz zu vergleichende Markierung
	 * 
	 * @return true falls die Instanzen die selbe Markierung hat, sonst false.
	 */
	@Override
	public final boolean equals(Object obj) {	
		if (obj == this) return true;	
        if (!(obj instanceof Marking)) {
            return false;
        }  
		Marking tmpElem = (Marking) obj;
		return Arrays.equals(marking, tmpElem.marking);
	}
	
	/**
	 * Überschreibt die hashCode Methode der Object-Klasse.<br>
	 * Erzeugt aus der ID des Elementes den Hashcode.
	 */
	@Override
	public final int hashCode() {
		return Objects.hash(marking);
	}
	
	/**
	 * Vergleicht zwei Instanzen anhand ihrer Markierung<br>
	 * Es wird jede Stelle der Markierungen einzeln verglichen.<br>
	 * Falls die übergebene Markierung an jeder Stelle gleich viele Tokens hat<br>
	 * und an mindestens einer Stelle um mindestens einen Token mehr liefert die Methode true, sonst false.
	 * 
	 * 
	 * @param newMarking die mit der Instanz zu vergleichende Markierung.
	 * 
	 * @return falls die übergebene Markierung an jeder Stelle gleich viele Tokens hat<br>
	 * 	und an mindestens einer Stelle um mindestens einen Token mehr, liefert die Methode true
	 * 
	 */
	public final boolean isOmega(Marking newMarking) {	
		//summiert alle Stellen einer Markierung 
		//und bildet die Differenz mit der zu vergleichenden Markierung
		int differenz = 0;	
		for(int i = 0; i < marking.length; i++) {
			if(newMarking.marking[i] < marking[i]) {
				//eine Stelle hat weniger Marken
				return false;
			}
			differenz = differenz + newMarking.marking[i] - marking[i];		
		}
		//alle Stellen haben mehr Marken und 
		//die Differenz ist größer Null.
		return(differenz > 0);
	}
}