package petrinets.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import petrinets.ModelListener;
import petrinets.petrinetModel.Petrinet;

/**
 * Diese Klasse repräsentiert das Fenster des Programmes<br>
 * Alle GUI-Elemente und Bedienelemente sind Teil dieser Klasse.
 * 
 * @author Michael Assmair
 *
 */
public class View extends JFrame {

	//default serial version ID
	private static final long serialVersionUID = 1L;
	
	//In diesem Panel werden die MenüBar und die ToolBar eingefügt
	private final JPanel northPanel = new JPanel(new BorderLayout());
	
	//Panels für die Darstellung von Petrinet, MarkingGraph und
	//Textfeld zur Ausgabe von Informationen
	private final PetrinetView petrinetView;
	private final MarkingGraphView markingGraphView;
	private final TextPanel textPanel;
	
	private final MenuBar menuBar;
	private final ToolBar toolBar;

	//in diesem SplitPanel befinden sich links die Darstellung des Petrinet
	//und rechts die Darstellung des MarkingGraph
	private final JSplitPane centerSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	//in diesem SplitPanel befinden sich oben das centerSplitPanel
	//und unten das Textfeld
	private final JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	
	//die Größe der beiden Fenster des centerSplitPanel
	//im Verhältnis zum Programmfenster
	private double centerRatio = 0.5;
	//die Größe des centerSplitPanel und des Textfeldes
	//im Verhältnis zum Programmfenster
	private double mainRatio = 0.75;
	
	/**
	 * Im Konstruktor wird das Fenster des Programmes erzeugt<br>
	 * und die GUI-ELemente erzeugt und hinzugefügt
	 * 
	 * @param name Name des Fensters
	 * @param petrinet Datenmodell des Programmes
	 */
	public View(String name, Petrinet petrinet) {
		super(name);
		
		//initialisiert die Präsentation
		petrinetView = new PetrinetView(petrinet);
		markingGraphView = new MarkingGraphView(petrinet.getMarkingGraph());
		textPanel = new TextPanel(petrinet);
		
		//initialisiert das Fenster
		initFrame();
		
		//initialisiert die Menüleiste und fügt
		//sie im Hauptfenster ein
		menuBar = new MenuBar(petrinetView);
		northPanel.add(menuBar, BorderLayout.NORTH);
		
		//initialisiert die Toolbar und fügt
		//sie im Hauptfenster ein
		toolBar = new ToolBar(petrinetView, markingGraphView);
		northPanel.add(toolBar, BorderLayout.SOUTH);
		
		//initialisiert die die SplitPanel
		//mit den Präsentationen
		initCenterSplitPane(petrinetView, markingGraphView);
		initMainSplitPane(textPanel);
		
		//fügt Panel mit Menüleiste und Toolbar,
		//sowie die drei GUI-Elemente im Hauptfenstern ein
		add(northPanel, BorderLayout.NORTH);
		add(mainSplitPane, BorderLayout.CENTER);	
		
		//setzt ComponentListener um beim verändern
		//des Hauptfensters die SplitPanel mit anzupassen
		setComponentListener();
	}
	
	//initialisiert das Hauptfenster
	private void initFrame() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1280, 768));
		setMinimumSize(new Dimension(800, 480));
		setVisible(true);
		
		pack();
		
		setLocationRelativeTo(null);
	}
	
	//initialisiert das centerSplitPane fügt links den petrinetView
	//und rechts den markingGraphView ein.
	private void initCenterSplitPane(JPanel petrinetView, JPanel markingGraphView) {	
		centerSplitPanel.setLeftComponent(petrinetView);
		centerSplitPanel.setRightComponent(markingGraphView);
		centerSplitPanel.setDividerSize(5);
		centerSplitPanel.setContinuousLayout(true);
		
		//Meldet MouseListener am Divider an, um Veränderungen der relativen
		//Fenstergröße zu verarbeiten
		BasicSplitPaneUI ui = (BasicSplitPaneUI)centerSplitPanel.getUI();
		ui.getDivider().addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				centerRatio = (double)petrinetView.getWidth()/(double)(centerSplitPanel.getWidth()-8);
			}
		});
	}
	
	//initialisiert das mainSplitPane fügt oben das centerSplitPanel
	//und unten das Textfeld ein.
	private void initMainSplitPane(JPanel textPanel) {
		mainSplitPane.setTopComponent(centerSplitPanel);
		mainSplitPane.setBottomComponent(textPanel);
		mainSplitPane.setDividerSize(5);
		mainSplitPane.setContinuousLayout(true);
		
		//Meldet MouseListener am Divider an, um Veränderungen der relativen
		//Fenstergröße zu verarbeiten
		BasicSplitPaneUI ui = (BasicSplitPaneUI)mainSplitPane.getUI();
		ui.getDivider().addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				mainRatio = (double)centerSplitPanel.getHeight()/(double)(mainSplitPane.getHeight()-8);
			}
		});
	}
	
	/**
	 * Methode, die die ActionListener für die Bedienung des Programmes anmeldet
	 * 
	 * @param menuBarListener ActionListener für die Menüleiste
	 * @param toolBarListener ActionListener für die Toolbar
	 * @param graphStreamViewerListener ActionListener für die Repräsentation der Graphen
	 * 
	 * @see MenuBar
	 * @see ToolBar
	 * @see PetrinetView
	 * @see MarkingGraphView
	 */
	public void addActionListener(ActionListener menuBarListener, ActionListener toolBarListener, ActionListener graphStreamViewerListener) {	
		menuBar.addActionListener(menuBarListener);
		toolBar.addActionListener(toolBarListener);
		petrinetView.addActionListener(graphStreamViewerListener);
		markingGraphView.addActionListener(graphStreamViewerListener);
	}

	
	// beobachtet ob die Größe des Frames verändert wurde und
	// und setzt die Divider der SplitPanel anhand ihrer vorherigen Größe neu.
	private void setComponentListener() {
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				centerSplitPanel.setDividerLocation(centerRatio);
				mainSplitPane.setDividerLocation(mainRatio);
			}
		});
	}

	
	/**
	 * Getter-Methode, die das ModelListener-interface des TextPanel liefert
	 * 
	 * @return ModelListener-interface des TextPanel
	 * 
	 * @see TextPanel
	 */
	public ModelListener getTextPanel() {
		return textPanel;
	}

	/**
	 * Getter-Methode, die das ModelListener-interface des PetrinetView liefert
	 * 
	 * @return ModelListener-interface des PetrinetView
	 */
	public ModelListener getPetrinetView() {
		return petrinetView;
	}

	/**
	 * Getter-Methode, die das ModelListener-interface des MarkingGraphView liefert
	 * 
	 * @return ModelListener-interface des MarkingGraphView
	 */
	public ModelListener getMarkingGraphView() {
		return markingGraphView;
	}
}