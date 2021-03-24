package petrinets.controller;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import petrinets.ModelAction;
import petrinets.ModelEvent;
import petrinets.ModelListener;
import petrinets.markingGraphModel.Marking;
import petrinets.markingGraphModel.MarkingGraph;
import petrinets.markingGraphModel.MarkingGraphEdge;
import petrinets.petrinetModel.Petrinet;
import petrinets.petrinetModel.Transition;

/**
 * Diese Klasse repräsentiert den Beschränktheits-Algorithmus,<br>
 * sie verfügt über keinen öffentlichen Konstruktor und kann<br>
 * über die Methode <code>analysePetrinet</code> aufgerufen werden
 * 
 * @author Michael Assmair
 * 
 * @see Petrinet
 * @see MarkingGraph
 *
 */
class BoundednessAlgorithm {
	//Liste von angemeldeten Beobachtern
	private final Set<ModelListener> listenerList = new HashSet<>();
	
	//Referenz auf zu verarbeitende Datenmodelle
	private final Petrinet petrinet;
	private final MarkingGraph markingGraph;
	
	
	//Konstruktor übernimmt Petrinet, MarkinGraph 
	//und dessen Beobachter
	private BoundednessAlgorithm(Petrinet petrinet) {
		this.petrinet = petrinet;
		this.markingGraph = petrinet.getMarkingGraph();
		listenerList.addAll(petrinet.getListener());
		listenerList.addAll(markingGraph.getListener());
	}
	
	
	/**
	 * Methode die den Beschräbktheits-Algorithmus startet
	 * 
	 * @param petrinet das zu analysierende Petri-Netz
	 * 
	 * @return true, falls das Petri-Netz unbeschränkt ist
	 * 
	 * @see Petrinet
	 */
	static final boolean analysePetrinet(Petrinet petrinet) {
		BoundednessAlgorithm algorithm = new BoundednessAlgorithm(petrinet);
		return algorithm.analyse();
	}
	
	
	/**
	 * Methode, die ähnlich einer Breitensuche alle<br>
	 * erreichbaren Markierungen eines Petri-Netzes findet<p>
	 * 
	 * Zusätzlich gibt es noch ein Abbruchkriterium, welchen<br>
	 * die Schleife abbricht, falls der Markierungsgraph unbeschränkt<br> 
	 * ist {@link #isOmega()}
	 * 
	 * @return true, falls der Markierungsgraph unbeschränkt ist
	 * 
	 */
	protected boolean analyse() {
		//informiert Beobachter, dass der Algorithmus gestartet wurde
		notifyListener(new ModelEvent("Automatlisch analyse des Petrinetz wurde gestartet.", ModelAction.PRINT_LINE));
		notifyListener(new ModelEvent("---------------------------------------------------", ModelAction.PRINT_LINE));
		
		//setzt das Petri-Netz auf die Startmarkierung 
		//und setzt den Markierungsgraph zurück
		petrinet.getMarkingGraph().resetMarkingGraph();
		
		//Warteschlange zum einfügen und abarbeiten von Markierungen
		final Deque<Marking> queue = new ArrayDeque<Marking>(markingGraph);
		
		//Schleife läuft bis die Warteschlange leer ist oder
		//das Abbruchkriterium für unbeschränkte Markierungsgraphen erreicht ist
		do {
			
			//setzt Petri-Netz auf die aktuell zu verarbeitende Markierung
			petrinet.setMarking(queue.peekFirst().getMarkingID());
			
			//versucht alle Transitionen zu schalten
			for(Transition transition : petrinet.getTransitions().values()) {
				
				//true falls neue Markierung eingefügt wurde
				//diese Markierung wird der Warteschlange hinzugefügt
				if(petrinet.update(transition.getId())) {
					queue.add(markingGraph.getCurrentMarking());
					
					//true falls das Abbruchkriterium für unbeschränkte
					//Markierungsgraphen erreicht wurde
					if(isOmega()) {
						
						//Setzt Petri-Netz auf die Markierung die zum Abbruch geführt hat
						//informiert Beobachter und gibt true an den Aufrufer zurück
						petrinet.setMarking(markingGraph.getCurrentMarking().getMarkingID());
						notifyListener(new ModelEvent("Das Petrin-Netz ist unbeschränkt. Knoten: " + markingGraph.size() + " Kanten: " + markingGraph.getEdgesNumber(), ModelAction.PRINT_LINE));
						return true;
					}
				}
				
				//nach dem Schalten wir das Petri-Netz wieder auf die 
				//zu verarbeitende Markierung gesetzt
				petrinet.setMarking(queue.peekFirst().getMarkingID());
			}
			//entfernt die erste Markierung in der Warteschlange
			queue.pop();
		} while(!queue.isEmpty());
		
		//informiert Beobachter, dass das Petri-Netz beschränkt ist
		notifyListener(new ModelEvent("Das Petrin-Netz ist beschränkt. Knoten: " + markingGraph.size() + " Kanten: " + markingGraph.getEdgesNumber(), ModelAction.PRINT_LINE));
		return false;
	}
	
	
	/**
	 * prüft das Abbruchkriterium für unbeschränkte Petri-Netze
	 * und gibt true zurück falls dieses zutrifft 
	 * 
	 * @return true, falls Abbruchkriterium
	 */
	protected boolean isOmega() {	
		
		//Schleife über alle Markierungen des Graphen
		for(Marking marking : markingGraph) {
			
			//prüft Abbruchkriterium
			if(marking.isOmega(markingGraph.getCurrentMarking()) && breadthFirstSearch(marking, markingGraph.getCurrentMarking())) {	
				notifyListener(new ModelEvent(markingGraph.getCurrentMarking(), ModelAction.SET_SECOND_OMEGA_MARKING));
				notifyListener(new ModelEvent(marking, ModelAction.SET_FIRST_OMEGA_MARKING));
				// MarkingEdge mit (null, null, null) initialisiert bewirkt,
				// dass eine in der Visualisierung hervorgehobene Kante nicht mehr hervorgehoben ist.
				notifyListener(new ModelEvent(new MarkingGraphEdge(null, null, null), ModelAction.HIGHLIGHT_EDGE));
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Breitensuche von einer übergebenen Markierung beginnend <code>startKnoten</code>.<p>
	 * Wenn die Zielmarkierung <code>zielKnoten</code> erreicht wurde, wird die Breitensuche abgebrochen<br>
	 * und es wird true zurückgegeben, wurde am Ende der Breitensuche diese Markierung nicht erreicht, wird false zurückgegeben.
	 * 
	 * @param startKnoten Startknoten der Breitensuche
	 * @param zielKnoten Zielknoten der Breitensuche
	 * 
	 * @return boolean - true, falls die Zielknoten von der Startknoten  aus erreichbar ist
	 */
	protected boolean breadthFirstSearch(Marking startKnoten, Marking zielKnoten) {
		//Warteschlange für die Breitensuche.
		final Deque<TreeNode> queue = new ArrayDeque<TreeNode>();
		
		//besuchte Markierungen
		final Set<Marking> visitedMarkings = new HashSet<>();
		
		queue.add(new TreeNode(startKnoten, null, null));

		//die Schleife läuft bis die Warteschlange leer ist
		//oder Abbruchkriterium erreicht
		while(!queue.isEmpty()) {
			
			//die von der in der Warteschlange als erstes stehende Markierung aus erreichbaren Markierungen
			//werden in der for-Schleife durchlaufen und falls noch nicht besucht am Ende der Warteschlange hinzugefügt.
			for(MarkingGraphEdge edge : queue.peekFirst().marking.getAdjList()) {
				
				//Abbruch der Schleife, falls zu erreichende Markierung gefunden
				if(edge.getSuccMakring().equals(zielKnoten)) {
					queue.add(new TreeNode(zielKnoten, edge.getTransition(), queue.peekFirst()));
					
					//zeichnet den Weg des Abbruchkriteriums
					drawOmegaPath(queue.pollLast());
					return true;
				}
				
				//wenn Markierung noch nicht besucht, wird sie der Warteschlange hinzugefügt
				if(!visitedMarkings.contains(edge.getSuccMakring())) {
					queue.add(new TreeNode(edge.getSuccMakring(), edge.getTransition(), queue.peekFirst()));
					queue.peekFirst().addChildren(queue.peekLast());
					visitedMarkings.add(edge.getSuccMakring());
				}
			}
			queue.pop();
		} 
		return false;
	}
	
	
	//wird aufgerufen falls zu untersuchenden Petri-Netz unbeschränkt ist
	//läuft vom Zielknoten der Breitensuche zu Startknoten und
	//übergibt dem Beobachter jede Kante des Weges
	private void drawOmegaPath(TreeNode treeNode) {
		while(treeNode.parent != null) {	
			notifyListener(new ModelEvent(treeNode.getEdgeFromParent(), ModelAction.SET_OMEGA_PATH));
			treeNode = treeNode.parent;
		} 
		//rekursiver Aufruf, bis Startmarkierung erreicht ist
		if(treeNode.marking.getMarkingID() != "0") {
			breadthFirstSearch(markingGraph.get(0), treeNode.marking);
		}
	}
	
	
	//alle Beobachter werden über den Event informiert
	private void notifyListener(ModelEvent evt) {
		for(ModelListener listener : listenerList) {
			listener.modelChanged(evt);
		}
	}
	
	
	
	//Hilfsklasse für eine Breitensuche
	//im Markierungsgraph
	private static class TreeNode{
		
		//Liste von nachfolgenden Baumknoten
		private final List<TreeNode> children = new ArrayList<TreeNode>();
		
		//Nach dem Schalten aktuelle Markierung
		private final Marking marking;
		
		//Markierung vor dem Schalten
		private final TreeNode parent;
		
		//Transition die geschaltet wurde
		private final Transition transition;

		
		// der Konstruktor liefert einen Baumknoten
		private TreeNode(Marking marking, Transition transition, TreeNode parent) {
			this.marking = marking;
			this.transition = transition;
			this.parent = parent;
		}
		
		
		// Add-Methode, die einen nachfolgenden Knoten zum Baumknoten hinzufügt.
		private void addChildren(TreeNode child) {
			children.add(child);
		}
		
		
		// Getter-Methode, die die Kanten zwischen Elternknoten und diesem Knoten liefer.
		private MarkingGraphEdge getEdgeFromParent() {
			return new MarkingGraphEdge(transition, parent.marking, marking);
		}
	}
}
