package petrinets.petrinetModel;

import java.util.HashSet;
import java.util.Set;

/**
 * Diese Klasse repräsentiert eine Transition eines Petri-Netzes.<br>
 * Die Klasse ist von {@link Element} abgeleitet.
 * Die Klasse besitzt zwei Listen, in denen die Stellen im Vor- und Nachbereich aufgelistet sind.
 * 
 * @author Michael Assmair
 * 
 * @see Element
 * @see Petrinet
 * @see Place
 * @see Element
 */
public class Transition extends Element {
	
	//Stellen im Vorbereich der Transition
	private Set<Place> previousPlaces = new HashSet<>();
	
	//Stellen im Nachbereich der Transition
	private Set<Place> nextPlaces = new HashSet<>();
	
	//ist true falls die Transition aktiv ist
	private boolean activ;
	
	/**
	 * Konstruktor, der eine neue Transition mit ID erzeugt.
	 * 
	 * @param id ID der Transition.
	 */
	Transition(String id) {
		super(id);
	}
	
	/**
	 * Add-Methode, die zur Adjazenzliste im Vorbereich eine Stelle hinzufügt.
	 * 
	 * @param place Stelle im Vorbereich.
	 */
	final void addPreviousPlace(Place place) {	
		previousPlaces.add(place);
	}
	
	/**
	 * Add-Methode, die zur Adjazenzliste im Nachbereich eine Stelle hinzufügt.
	 * 
	 * @param place Stelle im Nachbereich
	 */
	final void addNextPlace(Place place) {
		nextPlaces.add(place);
	}
	
	/**
	 * Getter-Methode, die die Adjazenzliste von Stellen im Vorbereich der Transition liefert.
	 * 
	 * @return Liste von Stellen im Vorbereich.
	 */
	public final Set<Place> getPreviousPlaces(){
		return previousPlaces;
	}
	
	/**
	 * Getter-Methode, die die Adjazenzliste von Stellen im Nachbereich der Transition liefert.
	 * 
	 * @return Liste von Stellen im Nachbereich.
	 */
	public final Set<Place> getNextPlaces(){
		return nextPlaces;
	}

	/**
	 * Gibt true zurück falls eine Transition aktiv ist, sonst false.
	 * 
	 * @return true, falls die Transition aktiv ist
	 */
	public final boolean isActiv() {
		return activ;
	}

	/**
	 * Methode, die die Transition auf Schaltbar oder nicht Schaltbar setzt, anhand der Stellen im Vorbereich.
	 * 
	 */
	final void setActiv() {
		boolean tmpSwitch = true;
		//Schleife über alle Stellen im Vorbereich
		//wenn jede stelle mindestens eine Marke besitzt, wir die Transition auf Aktiv gesetzt
		for(Place place : previousPlaces) {
			if(place.getTokens() < 1)
				//nicht schaltbar, falls eine Stelle im Vorbereich keine Marken trägt.
				tmpSwitch = false;
		}
		activ = tmpSwitch;
	}
}
