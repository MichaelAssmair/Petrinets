package petrinets.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JPanel;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.util.DefaultMouseManager;

import petrinets.ModelAction;
import petrinets.ModelEvent;
import petrinets.ModelListener;
import petrinets.controller.ButtonActions;
import petrinets.petrinetModel.PertinetEdge;
import petrinets.petrinetModel.Petrinet;
import petrinets.petrinetModel.Place;
import petrinets.petrinetModel.Transition;


/**
 * Diese Klasse ist ein Panel, in dem ein Petri-Netz mittels GraphStream<br>
 * dargestellt wird.<p>
 * Bei einer Instanz dieser Klasse kann sich ein {@link ActionListener} anmelden<br>
 * der über Klicks auf Knoten informiert wird.<p>
 * Die Klasse implementiert das {@link ModelListener} interface und Meldet sich<br>
 * bei dem Petrinet als Beobachter an, von dem er über Veränderungen informiert wird.
 * Welche Veränderungen verarbeitet werden können ist in der {@link #modelChanged(ModelEvent)}
 * Methode beschrieben.
 * 
 * @author Michael Assmair
 * 
 * @see Graph
 * @see ActionListener
 * @see Petrinet
 * @see #modelChanged(ModelEvent)
 *
 */
class PetrinetView extends JPanel implements ModelListener{

	//default serial version ID
	private static final long serialVersionUID = 1L;
	
	//link zur graph.css Datei.
	private static final String CSS_FILE = "url(" + PetrinetView.class.getResource("/graph.css") + ")";

	//GraphStream Graph und Darstellung
	private final Graph graph;
	private final Viewer viewer;
	private final ViewerPipe viewerPipe;
	private final ViewPanel viewPanel;
	
	//Listener wird über Mausklicks informiert
	private ActionListener listener;
	
	//merkt sich die aktuell hervorgehobene Stelle.
	private Node highlightedNode;

	/**
	 * Im Konstruktor meldet sich eine Instanz der Klasse bei dem<br> 
	 * übergebene Petrinet als Beobachter an. Die visuelle<br>
	 * Darstellung des Graphen wird initialisiert und zur Instanz hinzugefügt.
	 * 
	 * @param petrinetModel anzuzeigendes Datenmodell
	 * 
	 * @see Petrinet
	 */
	PetrinetView(Petrinet petrinetModel) {
		//Meldet sich als Beobachter beim Datenmodell an.
		petrinetModel.addListener(this);
		
		//JPanel wird initialisiert
		setMinimumSize(new Dimension(150, 150));
		setLayout(new BorderLayout());
		
		//Graph wird initialisiert
		graph = new MultiGraph("PetrinetGraph");
		graph.addAttribute("ui.stylesheet", CSS_FILE);
		
		//Erzeuge Viewer mit passendem Threading-Model für Zusammenspiel mit
		//Swing
		viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewPanel = viewer.addDefaultView(false);
		
		//fügt MouseListener zum ViewPanel
		addMouseListenerToViewPanel();
		
		//deaktiviert die Möglichkeit Knoten zu verschieben.
		setMouseDraggDisabled(true);
			
		//Neue ViewerPipe erzeugen, um über Ereignisse des Viewer informiert
		//werden zu können
		viewerPipe = viewer.newViewerPipe();
		addViewerListenerToViewerPipe();
		
		//fügt die Darstellung des Graphen in das JPanel ein.
		add(viewPanel, BorderLayout.CENTER);
	}
	
	
	/**
	 * Setzt ob Knoten örtlich verschoben werden können.
	 * 
	 * @param disable false aktiviert das verschieben von Knoten, true aktiviert es
	 */
	void setMouseDraggDisabled(boolean disable) {
		if(!disable) {
			viewPanel.setMouseManager(new DefaultMouseManager());
		} else {
			viewPanel.setMouseManager(new DefaultMouseManager() {
				@Override
				public void mouseDragged(MouseEvent event) {
				}
			});
		}
	}
	
	//neuen MouseListener beim viewPanel anmelden
	private void addMouseListenerToViewPanel() {
		viewPanel.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent event) {
				viewerPipe.pump();
			}

			@Override
			public void mouseReleased(MouseEvent event) {
				viewerPipe.pump();
			}
		});	
	}
	
	//neuer ViewerListener wird angemeldet
	//wird vom MouseListener über Klicks informiert
	private void addViewerListenerToViewerPipe() {
		viewerPipe.addViewerListener(new ViewerListener() {
			
			@Override
			public void viewClosed(String viewName) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void buttonReleased(String id) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void buttonPushed(String id) {
				listener.actionPerformed(new ActionEvent(ButtonActions.PETRINET_CLICK, 0, id));
			}
		});
	}
	
	
	/**
	 * Add-Methode, die einen ActionListener für die ViewerPipe anmeldet.
	 * 
	 * @param listener ActionListener im Controller
	 */
	void addActionListener(ActionListener listener) {
		this.listener = listener;
	}
	
	
	//löscht die aktuelle Darstellung.
	private void clear() {
		graph.getNodeSet().clear();
		graph.getEdgeSet().clear();
		highlightedNode = null;
		//Kamera wird zurückgesetzt, um den dargestellten Graphen richtig anzuzeigen.
		viewPanel.getCamera().resetView();
	}
	
	//neue Stelle wird eingefügt
	private void addPlaces(Place place) {
		Node node = graph.addNode(place.getId());
			
		node.addAttribute("ui.label", place.getIdAndName() +"<"+ place.getTokens() +">");
		node.addAttribute("ui.class", "place ," + getTokenString(place.getTokens()));
		node.addAttribute("name", place.getName());
		node.addAttribute("xy", place.getPosition().getX(), - place.getPosition().getY());
		node.addAttribute("tokens", place.getTokens());
	}

	//neue Transition wird eingefügt
	private void addTransitions(Transition transition) {
		Node node = graph.addNode(transition.getId());
			
		node.addAttribute("ui.label", transition.getIdAndName());
		node.addAttribute("name", transition.getName());
		node.addAttribute("xy", transition.getPosition().x, - transition.getPosition().y);
		node.addAttribute("ui.class", "transition");
	}
	
	//neue Kante wird eingefügt
	private void addEdge(PertinetEdge arc) {
		Edge edge = graph.addEdge(arc.getId(), arc.getSourceID(), arc.getTargetID(), true);
		edge.setAttribute("ui.label", arc.getId());

	}
	
	//aktualisiert eine geänderte Stelle
	private void updatePlace(Place place) {	
		Node node = graph.getNode(place.getId());
		
		//markierte Stellen bleiben markiert
		if(highlightedNode != null && highlightedNode.getId().equals(place.getId())) {
			node.changeAttribute("tokens", place.getTokens());
			node.changeAttribute("ui.class", "place ," + getTokenString(place.getTokens()) + ", highlighted");
			node.changeAttribute("ui.label", place.getIdAndName() +"<"+ place.getTokens() +">");
			
			// nicht markierte Stellen bleiben bei Änderung unmarkiert
		} else { 
			node.changeAttribute("tokens", place.getTokens());
			node.changeAttribute("ui.class", "place ," + getTokenString(place.getTokens()));
			node.changeAttribute("ui.label", place.getIdAndName() +"<"+ place.getTokens() +">");
		}
	}

	
	//Darstellung einer Transition wird anhand der Eigenschaft isSwitschable aktualisiert
	private void updateTransition(Transition transition) {
		graph.getNode(transition.getId()).setAttribute("ui.color", transition.isActiv() ? 1.0 : 0.0);
	}
	
	
	//Stelle wird markiert
	private void highlightNode(Place place) {
		//falls noch keine Stelle markiert ist wird
		//die übergebene Stelle markiert
		if(highlightedNode == null) {
			highlightedNode = graph.getNode(place.getId());
			highlightedNode.setAttribute("ui.class", highlightedNode.getAttribute("ui.class") + ", highlighted");
			
			//sonst wird die Markierung der aktuell
			//markierten Stelle entfernt
		} else {
			highlightedNode.setAttribute("ui.class", "place ," + getTokenString(highlightedNode.getAttribute("tokens")));
			//war die aktuell markierte Stelle die übergebene
			//Stelle wird keine neue Stelle Markiert
			if(place.getId().equals(highlightedNode.getId())) {
				highlightedNode = null;
				
				//neue Stelle wird markiert
			} else {
				highlightedNode = graph.getNode(place.getId());
				highlightedNode.setAttribute("ui.class", highlightedNode.getAttribute("ui.class") + ", highlighted");
			}
		}
	}
	
	
	//mehr als 9 Tokens?
	//gibt für int 0-9 String "0"-"9" zurück, sonst "more"
	private String getTokenString(int tokens) {
		if(tokens < 10) {
			return String.valueOf(tokens);
		}
		return "more";
	}
	
	
	/**
	 * Dieser ModelListener kann die folgenden Befehle verarbeiten:<br>
	 * <ul>
	 * <li><Strong>loadFile</Strong> - Darstellung wird zurückgesetzt. Quelle {@link File}
	 * <li><Strong>addPlace</Strong> - fügt der Darstellung eine Stelle hinzu. Quelle {@link Place}
	 * <li><Strong>addTransition</Strong> - fügt der Darstellung eine Transition hinzu. Quelle {@link Transition}
	 * <li><Strong>addArc</Strong> - fügt der Darstellung eine Kante hinzu. Quelle {@link PertinetEdge}
	 * <li><Strong>updatePlace</Strong> - Aktualisierung einer Stelle. Quelle {@link Place}
	 * <li><Strong>updateTransition</Strong> - Aktualisierung einer Transition. Quelle {@link Transition}
	 * <li><Strong>highlightPlace</Strong> - eine Stelle wird hervorgehoben. Quelle {@link Place}
	 * </ul>
	 * 
	 * andere Befehle werden ignoriert
	 * 
	 * @see Place
	 * @see Transition
	 * 
	 * @throws ClassCastException falls nicht passende Quelle übergeben wurde
	 */
	@Override
	public void modelChanged(ModelEvent evt) {
		//File wird übergeben
		if(ModelAction.LOAD_FILE.equals(evt.getAction())) {
			clear();
			
		//Place wird übergeben
		} else if(ModelAction.ADD_PLACE.equals(evt.getAction())) {
			addPlaces((Place)evt.getSource());
			
		//Transition wird übergeben
		} else if(ModelAction.ADD_TRANSITION.equals(evt.getAction())) {
			addTransitions((Transition)evt.getSource());
			
		//PertinetEdge wird übergeben
		} else if(ModelAction.ADD_ARC.equals(evt.getAction())) {
			addEdge((PertinetEdge)evt.getSource());
			
		//Place wird übergeben
		} else if(ModelAction.UPDATE_PLACE.equals(evt.getAction())) {
			updatePlace((Place)evt.getSource());
			
		//Transition wird übergeben
		} else if(ModelAction.UPDATE_TRANSITION.equals(evt.getAction())) {
			updateTransition((Transition)evt.getSource());
			
		//Place wird übergeben
		} else if(ModelAction.HIGHLIGHT_PLACE.equals(evt.getAction())) {
			highlightNode((Place)evt.getSource());
		}
	}
	
	/**
	 * Getter-Methode, die das ViewPanel der Präsentation liefert
	 * 
	 * @return das ViewPanel der Präsentation
	 */
	final ViewPanel getViewPanel() {
		return this.viewPanel;
	}
}