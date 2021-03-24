package petrinets.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import petrinets.controller.ButtonActions;

/**
 * Diese Klasse repräsentiert die Menüleiste des Programms
 * 
 * @author Michael
 *
 * @see View
 */
class MenuBar extends JMenuBar {

	//default serial version ID
	private static final long serialVersionUID = 1L;

	//MenüBar Schaltflächen
	private final JMenuItem openFile = new JMenuItem("Öffnen...");
	private final JMenuItem reset = new JMenuItem("Neu Laden");
	private final JMenuItem chooseMultipleData = new JMenuItem("Analyse mehrerer Dateien...");
	private final JCheckBoxMenuItem setChangeable = new JCheckBoxMenuItem("Petri-Netz Knoten fixiert");
	private final JMenuItem closeWindow = new JMenuItem("Beenden");
	
	//PetrinetView um MouseDragg im PetrinetView zu aktivieren bzw. deaktivieren.
	private final PetrinetView petrinetView;
	
	private ActionListener actionListener;

	/**
	 * Im Konstruktor wird die MenueBar für den View initialisiert.
	 * 
	 * @param petrinetView {@link PetrinetView}
	 * 
	 * @see View
	 */
	MenuBar(PetrinetView petrinetView) {
		this.petrinetView = petrinetView;
		JMenu fileMenu = new JMenu("Datei");
		
		
		fileMenu.add(openFile);
		fileMenu.add(reset);
		fileMenu.add(chooseMultipleData);
		fileMenu.add(setChangeable);
		fileMenu.add(closeWindow);
		
		//Setzt haken der Option "Petri-Netz Knoten fixiert".
		setChangeable.setState(true);

		add(fileMenu);
		
		addActionListener();

	}
	
	/**
	 * Add-Methode, die einen ActionListener bei der MenueBar anmeldet
	 * 
	 * @param listener ActionListener im Controller
	 */
	final void addActionListener(ActionListener listener) {
		actionListener = listener;
	}

	//fügt ActionListener zu den Menüfunktionen hinzu.
	private void addActionListener() {
		//meldet openFile an den Controller
		openFile.addActionListener(e -> actionListener
				.actionPerformed(new ActionEvent(ButtonActions.OPEN_FILE, 0, null)));
		
		//meldet reset an den Controller
		reset.addActionListener(e -> actionListener
				.actionPerformed(new ActionEvent(ButtonActions.RESET, 0, null)));
		
		//meldet chooseMultipleData an den Controller
		chooseMultipleData.addActionListener(e -> actionListener
				.actionPerformed(new ActionEvent(ButtonActions.CHOOSE_MULTIPLE_DATA, 0, null)));
		
		//wechselt zwischen Konten sind verschiebbar und nicht verschiebbar
		setChangeable.addActionListener(e -> petrinetView
				.setMouseDraggDisabled(setChangeable.getState()));
		
		//beendet das Programm
		closeWindow.addActionListener(e -> System.exit(0));
		}
}