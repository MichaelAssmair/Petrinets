package petrinets.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import petrinets.ModelAction;
import petrinets.ModelEvent;
import petrinets.ModelListener;
import petrinets.controller.ButtonActions;
import petrinets.markingGraphModel.Marking;
import petrinets.markingGraphModel.MarkingGraphEdge;
import petrinets.markingGraphModel.MarkingGraph;


/**
 * Diese Klasse ist ein Panel, in dem ein Markierungsgraph mittels GraphStream<br>
 * dargestellt wird.<p>
 * Bei einer Instanz dieser Klasse kann sich ein {@link ActionListener} anmelden<br>
 * der über Klicks auf Knoten informiert wird.<p>
 * Die Klasse implementiert das {@link ModelListener} interface und Meldet sich<br>
 * bei dem MarkingGraph als Beobachter an, von dem er über Veränderungen informiert wird.
 * Welche Veränderungen verarbeitet werden können ist in der {@link #modelChanged(ModelEvent)}
 * Methode beschrieben.
 * 
 * 
 * @author Michael Assmair
 * 
 * @see Graph
 * @see ActionListener
 * @see MarkingGraph
 * @see #modelChanged(ModelEvent)
 *
 */
class MarkingGraphView extends JPanel implements ModelListener {

	//default serial version ID
	private static final long serialVersionUID = 1L;

	//link zur graph.css Datei.
	private static final String CSS_FILE = "url(" + MarkingGraphView.class.getResource("/graph.css") + ")";
	
	
	private final Graph graph;
	private final Viewer viewer;
	private final ViewerPipe viewerPipe;
	private final ViewPanel viewPanel;
	
	//Beobachter der über Klicks informiert wird
	private ActionListener listener;
	
	//der aktuell hervorgehobene Knoten
	private Edge highlightedEdge;
	//die aktuell hervorgehobene Kante
	private Node highlightedNode;

	/**
	 * Im Konstruktor meldet sich eine Instanz der Klasse bei dem<br> 
	 * übergebene MarkingGraph als Beobachter an. Die visuelle<br>
	 * Darstellung des Graphen wird initialisiert und zur Instanz hinzugefügt.
	 * 
	 * @param markingGraphModel anzuzeigendes Datenmodell
	 * 
	 * @see MarkingGraph
	 * @see Graph
	 */
	MarkingGraphView(MarkingGraph markingGraphModel) {
		//Meldet sich als Beobachter beim Datenmodell an.
		markingGraphModel.addListener(this);

		//JPanel wird initialisiert
		setMinimumSize(new Dimension(150, 150));
		setLayout(new BorderLayout());

		//Graph wird initialisiert
		graph = new MultiGraph("MarkingGraph");

		// Erzeuge Viewer mit passendem Threading-Model für Zusammenspiel mit
		// Swing
		viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewPanel = viewer.addDefaultView(false);
		
		//fügt MouseListener zum ViewPanel
		addMouseListenerToViewPanel();

		//AutoLayout wird aktiviert um die Knoten der Graphen automatisch
		//zu positionieren.
		viewer.enableAutoLayout();

		//neue ViewerPipe erzeugen, um über Ereignisse des Viewer informiert
		//werden zu können
		viewerPipe = viewer.newViewerPipe();
		addViewerListenerToViewerPipe();
		
		//fügt die Darstellung des Graphen in das JPanel ein.
		add(viewPanel, BorderLayout.CENTER);
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
				listener.actionPerformed(new ActionEvent(ButtonActions.MARKING_GRAPH_CLICK, 0, id));
			}
		});
	}
	
	/**
	 * Add-Methode, die einen ActionListener für die ViewerPipe anmeldet.
	 * 
	 * @param listener ActionListener im Controller.
	 */
	public void addActionListener(ActionListener listener) {
		this.listener = listener;
	}

	//Löscht alle Knoten und Kanten
	private void clear() {
		graph.clear();
		graph.addAttribute("ui.stylesheet", CSS_FILE);
		//Kamera wird zurückgesetzt, um den dargestellten Graphen richtig anzuzeigen.
		viewPanel.getCamera().resetView();	
	}


	//fügt einen Knoten hinzu.
	private void addMarking(Marking marking) {
		if("0".equals(marking.getMarkingID())) {
			clear();
			Node node = graph.addNode(marking.getMarkingID());
			//Breite nach Anzahl der Stellen
			node.setAttribute("ui.style", "size:" + (5 + marking.toString().length()*7) + "px, 26px;");
			//Knoten verteilen sich besser, wenn sie sich gegenseitig stärker abstoßen
			//bei zu hohem Wert, wir der Graph beim einfügen sehr unruhig
			node.setAttribute("layout.weight", 3);
			node.setAttribute("ui.color", 0.0);
			node.setAttribute("label", marking.toString());
		} else {
			Node node = graph.addNode(marking.getMarkingID());
			//Breite nach Anzahl der Stellen
			node.setAttribute("ui.style", "size:" + (5 + marking.toString().length()*7) + "px, 26px;");
			//Knoten verteilen sich besser, wenn sie sich gegenseitig stärker abstoßen
			//bei zu hohem Wert, wir der Graph beim einfügen sehr unruhig
			node.setAttribute("layout.weight", 3);
			node.setAttribute("ui.color", 0.33);
			node.setAttribute("label", marking.toString());
		}
	}

	
	//fügt Kante hinzu
	private void addEdge(MarkingGraphEdge markingEdge) {
		Edge edge = graph.addEdge(markingEdge.getEdgeID(), markingEdge.getPredMarking().getMarkingID(),
				markingEdge.getSuccMakring().getMarkingID(), true);
		
		edge.addAttribute("label", markingEdge.getTransition().getIdAndName());
		edge.addAttribute("ui.color", 0.0);
	}

	//Hebt Kanten hervor.
	private void highlightEdge(MarkingGraphEdge markingEdge) {
		//wenn schon eine Kannte hervorgehoben ist,
		//wird die Hervorhebung dieser Kante entfernt.
		if (highlightedEdge != null) {
			highlightedEdge.setAttribute("ui.class", "");
		}
		//Übergebene Kante wird hervorgehoben.
		//Wird eine Kante mit dem ID Wert null übergeben,
		//ist nach Ausführung dieser Methode keine Kante hervorgehoben.
		if (markingEdge.getEdgeID() != null) {
			highlightedEdge = graph.getEdge(markingEdge.getEdgeID());
			highlightedEdge.setAttribute("ui.class", "highlighted");
		}
	}
	
	//Hebt einen Knoten hervor.
	private void highlightNode(Marking marking) {
		//wenn schon ein Knoten hervorgehoben ist,
		//wird die Hervorhebung dieses Knoten entfernt.
		if (highlightedNode != null) {
			highlightedNode.setAttribute("ui.class", "marking");
		}
		//übergebener Knoten wird hervorgehoben
		highlightedNode = graph.getNode(marking.getMarkingID());
		highlightedNode.setAttribute("ui.class", "marking, highlighted");
	}
	
	//Setzt mit ui.color die Farbe deines Knoten
	private void setMarkingColor(Marking marking, double color) {
		graph.getNode(marking.getMarkingID()).setAttribute("ui.color", color);
	}
	
	//Setzt mit ui.color die Farbe einer Kante
	private void setOmegaPathEdge(MarkingGraphEdge markingEdge, double color) {
		graph.getEdge(markingEdge.getEdgeID()).setAttribute("ui.color", color);
	}
	
	/**
	 * Diese Methode wird aufgerufen, wenn das MarkingGrapgModel<br>
	 * verändert wurde.<p>
	 * 
	 * Der {@link ModelListener} kann die Befehle<br>
	 * <ul>
	 * <li> <Strong>addMarking</Strong> - falls eine Markierung hinzugefügt wurde. Quelle {@link Marking}
	 * <li> <Strong>addEdge</Strong> - falls eine Kante hinzugefügt wurde. Quelle {@link MarkingGraphView}
	 * <li> <Strong>highlightMarking</Strong> -  um eine Markierung hervorzuheben. Quelle {@link Marking}
	 * <li> <Strong>highlightEdge</Strong> - um eine Kante hervorzuheben. Quelle {@link MarkingGraphEdge}
	 * <li> <Strong>setFirstOmegaMarking</Strong> - um die erste Markierung des Abbruchkriteriums zu markieren. Quelle {@link Marking}
	 * <li> <Strong>setSecondOmegaMarking</Strong> - um die zweite Markierung des Abbruchkriteriums zu markieren. Quelle {@link Marking}
	 * <li> <Strong>setOmegaPathEdge</Strong> -  um den Pfad zu markieren der zum Abbruchkriterium geführt hat. Quelle {@link MarkingGraphView}
	 * </ul>
	 * 
	 * @see ModelListener
	 * @see ModelEvent
	 * @see MarkingGraph
	 * 
	 * @throws ClassCastException falls die falsche Quelle übergeben wurde.
	 * 
	 */
	@Override
	public void modelChanged(ModelEvent evt) {
		//Marking wir übergeben
		if (ModelAction.ADD_MARKING.equals(evt.getAction())) {
			addMarking((Marking)evt.getSource());
			highlightNode((Marking)evt.getSource());
			
		//Marking wir übergeben
		} else if(ModelAction.HIGHLIGHT_MARKING.equals(evt.getAction())) {
			highlightNode((Marking)evt.getSource());
			
		//MarkingGraphEdge wir übergeben
		} else if(ModelAction.ADD_EDGE.equals(evt.getAction())) {
			addEdge((MarkingGraphEdge)evt.getSource());
			highlightEdge((MarkingGraphEdge)evt.getSource());
			
		//MarkingGraphEdge wir übergeben
		} else if(ModelAction.HIGHLIGHT_EDGE.equals(evt.getAction())) {
			highlightEdge((MarkingGraphEdge)evt.getSource());
			
		//Anfangsmarkierung des Abbruchkriteriums wird übergeben
		} else if(ModelAction.SET_FIRST_OMEGA_MARKING.equals(evt.getAction())) {
			setMarkingColor((Marking)evt.getSource(), 0.85);
			
		//Endmarkierung des Abbruchkriteriums wird übergeben
		} else if(ModelAction.SET_SECOND_OMEGA_MARKING.equals(evt.getAction())) {
			setMarkingColor((Marking)evt.getSource(), 0.66);
			
		//Kante des Pfades zum Abbruchkriterium wird übergeben
		} else if(ModelAction.SET_OMEGA_PATH.equals(evt.getAction())) {
			setOmegaPathEdge((MarkingGraphEdge)evt.getSource(), 0.8);
		}
	}

	/**
	 * Getter-Methode, die das ViewPanel der Präsentation liefert
	 * 
	 * @return das ViewPanel der Präsentation
	 */
	ViewPanel getViewPanel() {
		return this.viewPanel;
	}
}