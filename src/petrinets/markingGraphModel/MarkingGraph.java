package petrinets.markingGraphModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import petrinets.ModelAction;
import petrinets.ModelEvent;
import petrinets.ModelListener;
import petrinets.petrinetModel.Petrinet;
import petrinets.petrinetModel.Transition;

/**
 * Diese Klasse repräsentiert das Datenmodell eines Markierungsgraphen.<br>
 * Bei dieser Klasse kann sich ein {@link ModelListener} über die Methode <code>addListener</code><br> 
 * anmelden, der über Änderungen des Datenmodells informiert wird.<br>
 * 
 * 
 * @author Michael Assmair
 * 
 * @see Petrinet
 * @see Marking
 * @see ModelListener
 * @see ModelEvent
 *
 */
public class MarkingGraph extends ArrayList<Marking>{
	
	//default serial version ID
	private static final long serialVersionUID = 1L;
	
	//Liste der Beobachter die bei dem Datenmodell angemeldet sind
	private final Set<ModelListener> listenerList = new HashSet<>();
	
	//Aktuell hervorgehobene Markierung
	private Marking currentMarking;
	
	/**
	 * Mit dieser Methode kann ein Markierungsgraph gelöscht werden und<br>
	 * mit einer einem neuen {@link Marking} initialisiert werden.<br>
	 * 
	 * @param marking zu initialisierende Markierung
	 * 
	 * @see Marking
	 */
	public final void initMarkingGraph(Marking marking) {
		//löscht Liste von Markierungen
		clear();
		//löscht ausgehende Kanten der Startmarkierung
		marking.getAdjList().clear();
		//fügt Startmarkierung dem Graphen hinzu
		add(marking);
		notifyListener(new ModelEvent(marking, ModelAction.ADD_MARKING));
		currentMarking = marking;
	}
	
	/**
	 * Diese Methode löscht den Markierungsgraphen und initialisiert ihn mit der aktuellen Startmarkierung<br>
	 * 
	 */
	public final void resetMarkingGraph() {
		//wird nur ausgeführt, falls es mehr als nur die Startmarkierung gibt.
		if(get(0).getAdjList().size() > 0) {
			notifyListener(new ModelEvent("Markierungs-Grapf wird zurückgesetzt.", ModelAction.PRINT_LINE));
			initMarkingGraph(get(0));
		}
	}
	
	/**
	 * Diese Methode wird mit der gerade geschalteten Transition des Petri-Netzes und<br>
	 * mit dem daraus entstandenen Markierung aufgerufen.
	 * <ul>
	 * <li> Falls es noch keine entsprechende Markierung im Graphen gibt, wird diese eingefügt,<br>
	 * anschließend wird die zur Markierung führende Kante eingefügt
	 * <li> Falls es eine entsprechenden Markierung im Graphen gibt aber<br>
	 * noch keine Kante mit der Beschriftung der geschalteten Transition,<br>
	 * wird diese Kante eingefügt
	 * </ul>
	 * 
	 * @param transition Vom Petri-Netz geschaltete Transition
	 * @param marking durch das Schalten des Petri-Netzes erzeugte Markierung
	 * 
	 * @return boolean - true, falls übergebene Markierung noch nicht im Markierungsgraphen
	 */
	public final boolean update(Transition transition, Marking marking) {
		//Markierung noch nicht enthalten
		if(!contains(marking)) {
			//fügt Markierung hinzu und setzt ID
			add(marking);
			marking.setMarkingID(size()-1);
			notifyListener(new ModelEvent(marking, ModelAction.ADD_MARKING));
			//aktualisiert Kanten
			updateEdges(new MarkingGraphEdge(transition, currentMarking, marking));
			//new aktuelle Markierung des Graphen
			currentMarking = marking;
			return true;
			
			//Markierung bereits im Graphen
		} else {
			//holt Markierung mit ID aus dem Graphen
			marking = get(indexOf(marking));
			notifyListener(new ModelEvent(marking, ModelAction.HIGHLIGHT_MARKING));
			//aktualisiert Kanten
			updateEdges(new MarkingGraphEdge(transition, currentMarking, marking));
			//neue aktuelle Markierung
			currentMarking = marking;
			return false;
		}
	}
	
	//aktualisiert die Kanten des Markierungsgraphen.
	private void updateEdges(MarkingGraphEdge edge) {
		//Kante noch nicht vorhanden und wird hinzugefügt
		if(!currentMarking.getAdjList().contains(edge)) {
			currentMarking.getAdjList().add(edge);
			notifyListener(new ModelEvent(edge, ModelAction.ADD_EDGE));
			//Kante bereits vorhanden
		} else {
			notifyListener(new ModelEvent(edge, ModelAction.HIGHLIGHT_EDGE));
		}
	}
	
	
	/**
	 * Diese Methode setzt die aktuell hervorgehobene 
	 * Markierung des Graph auf die Markierung deren ID übergeben wurde.
	 * 
	 * @param id ID der Markierung.
	 * 
	 * @throws IndexOutOfBoundsException - ID größer als <code>size()</code>
	 * @throws NumberFormatException - übergebene id war keine Ganzzahl
	 */
	public final void setToMarking(String id) {
		//Wird nur ausgeführt, falls übergebene ID
		//nicht die der aktuellen Markierung ist
		if(currentMarking != get(Integer.parseInt(id))) {
			notifyListener(new ModelEvent(get(Integer.parseInt(id)), ModelAction.HIGHLIGHT_MARKING));
			notifyListener(new ModelEvent(new MarkingGraphEdge(null, null, null), ModelAction.HIGHLIGHT_EDGE));
			//neue aktuelle Markierung
			currentMarking = get(Integer.parseInt(id));
		}
	}
	
	/**
	 * Diese Methode liefert die Anzahl der Kanten dieses Markierungsgraphen
	 * 
	 * @return Anzahl der Kanten
	 */
	public final int getEdgesNumber() {
		int tmp = 0;
		for(Marking marking : this) {
			tmp = tmp + marking.getAdjList().size();
		}
		return tmp;
	}
	
	/**
	 * Diese Methode informiert alle angemeldeten Beobachter über einen Event.
	 * 
	 * @param evt Event der an Beobachter übergeben wird
	 */
	private void notifyListener(ModelEvent evt) {
		for(ModelListener listener : listenerList) {
			listener.modelChanged(evt);
		}
	}
	
	/**
	 * Fügt dem Datenmodell einen Beobachter hinzu.
	 * 
	 * @param listener hinzuzufügender Beobachter
	 */
	public final void addListener(ModelListener listener) {
		listenerList.add(listener);
	}
	
	/**
	 * Entfernt den übergebenen Beobachter.
	 * 
	 * @param listener zu entfernender Beobachter
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
	
	
	/**
	 * Getter-Methode, die die aktuelle hervorgehobene Markierung liefert.
	 * 
	 * @return die aktuell hervorgehobene Markierung.
	 */
	public final Marking getCurrentMarking() {
		return currentMarking;
	}
}