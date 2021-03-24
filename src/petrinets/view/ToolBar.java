package petrinets.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import petrinets.controller.ButtonActions;

/**
 * Diese Klasse repräsentiert die ToolBar für das Programm
 * 
 * 
 * @author Michael Assmair
 *
 */
class ToolBar extends JToolBar {

	//default serial version ID
	private static final long serialVersionUID = 1L;
	
	//Buttons in der ToolBar
	private final JButton resetPetrinet = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/previous.png")));
	private final JButton clearMarkingGraph = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/repeat.png")));
	private final JButton plusToken = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/plus.png")));
	private final JButton minusToken = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/minus.png")));
	private final JButton analyseGraph = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/idee.png")));
	private final JButton resetPetrinetView = new JButton("resetPnKamera");
	private final JButton resetMarkingGraphView = new JButton("resetEgKamera");
	
	//Referenzen um die Kamera von petrinetView und 
	//markingGraphView zurückzusetzen. 
	private final PetrinetView petrinetView;
	private final MarkingGraphView markingGraphView;
	
	private ActionListener actionListener;
	
	/**
	 * Im Konstruktor wird die ToolBar initialisiert.
	 * 
	 * @param petrinetView Darstellung des Petri-Netzes 
	 * @param markingGraphView Darstellung des Markierungsgraphen
	 * 
	 * @see View
	 * @see MarkingGraphView
	 * @see PetrinetView
	 */
	ToolBar(PetrinetView petrinetView, MarkingGraphView markingGraphView) {
		this.petrinetView = petrinetView;
		this.markingGraphView = markingGraphView;
		
		//Buttons werden eingefügt 
		add(resetPetrinet);
		add(clearMarkingGraph);
		add(plusToken);
		add(minusToken);
		add(analyseGraph);
		addSeparator(new Dimension(5, 0));
		add(resetPetrinetView);
		add(resetMarkingGraphView);
		
		setToolTips();
		
		addActionListener();
	}
	
	//Setzt die ToolTips für die Buttons
	private void setToolTips() {
		resetPetrinet.setToolTipText(
				"Setzt das Petrinetz auf die Anfangsmarkierung zurück");
		
		clearMarkingGraph.setToolTipText(
				"Lösche den Markierungsgraph und setzt Petrinetz auf die Anfangsmarkierung zurück");
		
		plusToken.setToolTipText(
				"Füge einen Token zur markierten Stelle hinzu");
		
		minusToken.setToolTipText(
				"Lösche einen Token der markierten Stelle");
		
		analyseGraph.setToolTipText(
				"Analysiere Petri-Netz");	
		
		resetPetrinetView.setToolTipText(
				"Setzt die Kamera des Petri-Netzes zurück");
		
		resetMarkingGraphView.setToolTipText(
				"Setzt die Kamera des Markierungsgraphen zurück");
	}
	
	/**
	 * Add-Methode, die einen ActionListener bei der ToolBar anmeldet
	 * 
	 * @param listener ActionListener Controller
	 */
	void addActionListener(ActionListener listener) {
		actionListener = listener;
	}
	
	//Aktionen werden zu den Buttons hinzugefügt
	private void addActionListener() {
		//meldet resetPetrinet an den Controller
		resetPetrinet.addActionListener(e -> actionListener
				.actionPerformed(new ActionEvent(ButtonActions.RESET_PETRINET, 0, null)));	
		
		//meldet clearMarkingGraph an den Controller
		clearMarkingGraph.addActionListener(e -> actionListener
				.actionPerformed(new ActionEvent(ButtonActions.CLEAR_MARKING_GRAPH, 0, null)));
		
		//meldet plusTokken an den Controller
		plusToken.addActionListener(e -> actionListener
				.actionPerformed(new ActionEvent(ButtonActions.PLUS_TOKEN, 0, null)));	
		
		//meldet minusTokken an den Controller
		minusToken.addActionListener(e -> actionListener
				.actionPerformed(new ActionEvent(ButtonActions.MINUS_TOKEN, 0, null)));	
		
		//meldet analyseGraph an den Controller
		analyseGraph.addActionListener(e -> actionListener
				.actionPerformed(new ActionEvent(ButtonActions.ANALYSE_GRAPH, 0, null)));	
		
		//setzt die Kamera des petrinetViews zurück
		resetPetrinetView.addActionListener(e -> petrinetView
				.getViewPanel().getCamera().resetView());
		
		//setzt die Kamera des markingGraphViews zurück
		resetMarkingGraphView.addActionListener(e -> markingGraphView
				.getViewPanel().getCamera().resetView());
	}
}