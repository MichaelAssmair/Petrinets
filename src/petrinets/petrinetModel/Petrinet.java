package petrinets.petrinetModel;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import petrinets.ModelEvent;
import petrinets.ModelListener;
import petrinets.markingGraphModel.Marking;
import petrinets.markingGraphModel.MarkingGraph;

/**
 * Diese Klasse repräsentiert das Datenmodell eines Petri-Netzes.<br>
 * Die Stellen sind nach ID sortiert in einer TreeMap gespeichert,<br>
 * die Transitionen in einer HashMap. Auf beide<br>
 * Elemente kann über ihre ID direkt zugegriffen werden. Kanten des<br>
 * Petri-Netzes sind als Listen in den Transitionen gespeichert.<p>
 * 
 * Das Datenmodell hat eine Referenz auf den zum Petri-Netz gehörenden<br>
 * Markierungsgraphen.<p>
 * 
 * Bei dem Datenmodell können sich {@link ModelListener} anmelden, die über<br>
 * Änderungen der Daten informiert werden.
 * 
 * @author Michael Assmair
 * 
 * @see Place
 * @see TreeMap
 * @see Transition
 * @see HashMap
 * @see ModelListener
 * @see ModelEvent
 * @see MarkingGraph
 *
 */
public class Petrinet {
	
	//Liste von Beobachtern
	private final Set<ModelListener> listenerList = new HashSet<>();
	
	//Menge von Stellen, nach ID geordnet und über diese abrufbar
	private final Map<String, Place> places = new TreeMap<>();
	
	//Menge von Transitionen, über die ID abrufbar
	private final Map<String, Transition> transitions = new HashMap<>();
	
	//zum Petri-Netz gehörender Markierungsgraph
	private final MarkingGraph markingGraph = new MarkingGraph();
	
	
	/**
	 * Löscht ein gegebenenfalls bereits geladenes Petri-Netz<br>
	 * und lädt aus der übergebenen Datei ein neues Petri-Netz
	 * 
	 * @param file Datei aus der das Petri-Netz geladen wird
	 */
	public final void loadPetrinetFromFile(File file) {
		//setzt Petri-Netz zurück
		clearPetrinet();
		
		//lädt Petri-Netz aus Datei
		notifyListener(new ModelEvent(file, "loadFile"));
		PetrinetParser.loadFile(file, this);
		
		//initialisiert Markierungsgraph
		markingGraph.initMarkingGraph(new Marking(places));
		setMarking("0");
	}
	
	//löscht Daten des Petri-Netzes
	private void clearPetrinet() {
		places.clear();
		transitions.clear();
	}
	
	
	/**
	 * Methode,um das Petri-Netz zu aktualisieren<p>
	 * 
	 * Wird der Methode id ID einer Stelle übergeben,<br>
	 * wird die hervorgehobene Stelle aktualisiert, wird<br>
	 * die ID einer Transition übergeben wird das Petri-Netz<br>
	 * gemäß der Schaltregel aktualisiert.
	 * 
	 * @param id Stelle oder Transition des Petri-Netzes
	 * 
	 * @return true, falls beim aktualisieren eine neue Markierung <br>
	 * 			in den Markierungsgraph eingefügt wurde
	 * 			
	 */
	public final boolean update(String id) {
		//ID ist ID einer Transition und Transition ist aktiv
		if(transitions.get(id).isActiv()) {
			//Stellen, Transitionen und Markierungsgraph werden aktualisiert
			updatePlaces(id);
			updateTransitions();
			return markingGraph.update(transitions.get(id), new Marking(places));
		}
		return false;
	}
	
	
	//aktualisiert das Petri-Netz anhand der geschalteten Transition
	private void updatePlaces(String transitionID) {	
		notifyListener(new ModelEvent(transitions.get(transitionID).getIdAndName() + " wurde geschalten.", "printLine"));
		
		//Schleife über die Stellen im Vorbereich
		for(Place place : transitions.get(transitionID).getPreviousPlaces()) {
			place.setTokens(place.getTokens()-1);
			//Listener werden informiert
			notifyListener(new ModelEvent(place, "updatePlace"));
		}
		
		//Schleife über die Stellen im Nachbereich
		for(Place place : transitions.get(transitionID).getNextPlaces()) {
			place.setTokens(place.getTokens()+1);
			//Listener werden informiert
			notifyListener(new ModelEvent(place, "updatePlace"));

		}	
	}
	
	
	//Alle Transitionen werden anhand der Stellen
	//im Vorbereich aktualisiert
	private void updateTransitions() {
		for(Transition transition : transitions.values()) {
			transition.setActiv();
			//Listener werden informiert
			notifyListener(new ModelEvent(transition, "updateTransition"));;
		}
	}
	
	/**
	 * Der Aktuell hervorgehobenen Stelle wird<br>
	 * eine Marke hinzugefügt
	 * 
	 * @param highlightedPlace die zu ändernde Stelle
	 */
	public final void plusToken(Place highlightedPlace) {
		highlightedPlace.setTokens(highlightedPlace.getTokens()+1);
		markingGraph.initMarkingGraph(new Marking(places));
		setMarking("0");
	}
	
	/**
	 * Der Aktuell hervorgehobenen Stelle wird<br>
	 * wird eine Marke entfernt, dabei muss die Stelle
	 * mindestens eine Marke tragen
	 * 
	 * @param highlightedPlace die zu ändernde Stelle
	 */
	public final void minusToken(Place highlightedPlace) {	
		if(highlightedPlace.getTokens() > 0) {
			highlightedPlace.setTokens(highlightedPlace.getTokens()-1);
			markingGraph.initMarkingGraph(new Marking(places));
			setMarking("0");
		}
	}
	
	/**
	 * Methode, um das Petri-Netz und den Markierungsgraph<br>
	 * zu aktualisieren, so dass die Markierungen des Petri-Netzes<br>
	 * der der übergebenen Markierung entspricht
	 * 
	 * @param id ID der Markierung auf die das Petri-Netz aktualisiert werden soll
	 * 
	 * @throws NumberFormatException wenn der Methode eine ID übergeben wird, die keine Ganzzahl ist
	 * @throws IndexOutOfBoundsException wenn die übergebene ID größer als size ist
	 * 
	 */
	public final void setMarking(String id) {
		
		//speichert die Markierung in einem Array
		final int[] startmarking = markingGraph.get(Integer.parseInt(id)).getMarking();
		int idx = 0;
		
		//alle Stellen werden aktualisiert
		for(Place place : places.values()) {
			place.setTokens(startmarking[idx++]);
			//Listener werden informiert
			notifyListener(new ModelEvent(place, "updatePlace"));
		}
		//Transitionen und Markierungsgraph werden
		//nach der Änderung aktualisiert
		updateTransitions();
		markingGraph.setToMarking(id);	
	}
	
	/**
	 * Fügt die Kanten des Petri-Netzes als<br>
	 * Liste der Stellen im Vor- oder Nachbereich einer Transition hinzu
	 * 
	 * @param arcs Kanten der Petri-Netzes
	 */
	final void addTransitionsPreAndNext(Set<PertinetEdge> arcs) {
		for(PertinetEdge arc : arcs) {	
			notifyListener(new ModelEvent(arc, "addArc"));
			//Stelle im Vorbereich
			if(places.containsKey(arc.getSourceID())) {
				transitions.get(arc.getTargetID()).addPreviousPlace(places.get(arc.getSourceID()));
				
				//Stelle im Nachbereich
			} else {
				transitions.get(arc.getSourceID()).addNextPlace(places.get(arc.getTargetID()));
			}
		}
	}
	
	
	/**
	 * Getter-Methode, die den Markierungsgraphen des Petri-Netzes liefert
	 * 
	 * @return Markierungsgraph des Petri-Netzes
	 */
	public final MarkingGraph getMarkingGraph() {
		return markingGraph;
	}
	
	/**
	 * Fügt der Menge der Stellen eine Stelle hinzu
	 * 
	 * @param place hinzuzufügende Stelle
	 */
	final void addPlace(Place place) {
		places.put(place.getId(), place);
		//Listener werden informiert
		notifyListener(new ModelEvent(place, "addPlace"));
	}
	
	/**
	 * Fügt der Menge der Transitionen eine Transition hinzu
	 * 
	 * @param transition hinzuzufügende Transition
	 */
	final void addTransition(Transition transition) {
		transitions.put(transition.getId(), transition);
		//Listener werden informiert
		notifyListener(new ModelEvent(transition, "addTransition"));
	}
	
	/**
	 * Getter-Methode, die die Stellen des Petri-Netzes liefert
	 * 
	 * @return Stellen des Petri-Netzes
	 */
	public final Map<String, Place> getPlaces() {
		return places;
	}

	/**
	 * Getter-Methode, die die Transitionen des Petri-Netzes liefert
	 * 
	 * @return Transitionen des Petri-Netzes
	 */
	public final Map<String, Transition> getTransitions() {
		return transitions;
	}


	//alle Beobachter werden über den Event informiert
	private void notifyListener(ModelEvent evt) {
		for(ModelListener listener : listenerList) {
			listener.modelChanged(evt);
		}
	}
	
	/**
	 * Meldet den übergebenen Beobachter an
	 * 
	 * @param listener hinzuzufügender Beobachter
	 */
	public final void addListener(ModelListener listener) {
		listenerList.add(listener);
	}
	
	
	/**
	 * Meldet den übergebenen Beobachter ab
	 * 
	 * @param listener zu löschender Beobachter
	 */
	public final void removeListener(ModelListener listener) {
		listenerList.remove(listener);
	}
	
	
	/**
	 * Getter-Methode, die die Liste der angemeldeten Beobachter liefert
	 * 
	 * @return Liste der angemeldeten Beobachter
	 */
	public final Set<ModelListener> getListener() {
		return listenerList;
	}
}