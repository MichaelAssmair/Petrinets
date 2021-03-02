package petrinets.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import petrinets.ModelEvent;
import petrinets.petrinetModel.Petrinet;
import petrinets.petrinetModel.Place;
import petrinets.view.View;

/**
 * Der Controller verarbeitet die Eingaben des Benutzers und gibt sie an das Datenmodell weiter.<br>
 * Der Controller übernimmt mit der Klasse {@link BoundednessAlgorithm} die Beschraenktheits-Analyse und<br>
 * mit der Klasse {@link MultipleFilesHandler} die Stapelverarbeitung
 * 
 * @see View
 * @see Petrinet
 * @see BoundednessAlgorithm
 * @see MultipleFilesHandler
 * 
 * @author Michael Assmair
 *
 */
public class Controller {
	//das Hauptfenster des Programmes
	private final View view;
	
	//das Datenmodell der Programmes
	private final Petrinet petrinet;
	
	//Pfad bestimmt das Verzeichnis, mit dem der Dateiauswahl-Dialog geöffnet wird
	private String path = "Beispiele";
	
	//Eine Referenz auf die geöffnete Datei
	private File file;
	
	//mit der Maus markierte Stelle
	private Place highlightedPlace;

	
	/**
	 * Konstruktor, der einen Controller erzeugt und sich<br>
	 * bei dem View als Listener anmeldet
	 * 
	 * 
	 * @param view View des Programmes
	 * @param petrinet Datenmodell des Programmes
	 * 
	 * @see View
	 * @see Petrinet
	 * @see MenuListener
	 * @see ToolBarListener
	 * @see GraphStreamViewerListener
	 * 
	 */
	public Controller(View view, Petrinet petrinet) {
		this.view = view;
		this.petrinet = petrinet;
		//Menüleiste, Toolbar und Graphen haben ihren eigenen ActionListener
		view.addActionListener(new MenuListener(), new ToolBarListener(), new GraphStreamViewerListener());
	}

	
	//öffnet einen Dateiauswahl-Dialog. multi true, um mehrere Dateien auszuwählen
	private void openFile(boolean multi) {
		//initialisiert neuen JFilechooser der im Ordner
		//auf den path verweist öffnen.
		final JFileChooser fileChooser = new JFileChooser(path);
		
		//filter für pnml Dateien 
		fileChooser.setFileFilter(new FileNameExtensionFilter("PNML file (*.pnml)", "pnml"));
		//Einstellung um Ordner anzuzeigen
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		//wenn multi true ist, können mehrere Dateien ausgewählt werden
		fileChooser.setMultiSelectionEnabled(multi);
		
		//Rückgabewert, welcher Button des Dialogs betätigt wurde
		final int returnVal = fileChooser.showOpenDialog(view);
		
		//hier kann nur eine Datei ausgewählt werden
		if (returnVal == JFileChooser.APPROVE_OPTION && !multi) {
			path = fileChooser.getCurrentDirectory().getPath();
			file = fileChooser.getSelectedFile();
			petrinet.loadPetrinetFromFile(file);
			
			//hier können mehrere Dateien ausgewählt werden
		} else if(returnVal == JFileChooser.APPROVE_OPTION && multi){
			//initialisiert Stapelverarbeitung zur Analyse mehrere Petri-Netze
			final MultipleFilesHandler handler = new MultipleFilesHandler(fileChooser.getSelectedFiles());
			path = fileChooser.getCurrentDirectory().getPath();
			handler.addListener(view.getTextPanel());
			//startet background task zur Analyse der übergebenen Petri-Netze 
			handler.execute();
			//Auswahl-Dialog wurde mit Abbrechen beendet 
		} else {
			path = fileChooser.getCurrentDirectory().getPath();
		}
	}

	
	//startet die Analyse des im View dargestellte Petri-Netzes
	private void analysePetrinet() {
		//es muss ein Petri-Netz geladen sein
		if (file != null) {
			if(BoundednessAlgorithm.analysePetrinet(petrinet)){
				JOptionPane.showMessageDialog(view, "Der Markierungs-Graph ist unbeschränkt.");	
			} else {
				JOptionPane.showMessageDialog(view, "Der Markierungs-Graph ist beschränkt.");	
			}	
		}
	}

	
	//ActionListener für die Menüleiste
	private class MenuListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent evt) {
			//Schaltfläche "Öffnen.." wurde gewählt
			if("openFile".equals(evt.getActionCommand())) {
				highlightedPlace = null;
				openFile(false);
				
				//Schaltfläche "Neu Laden" wurde gewählt
			} else if("reset".equals(evt.getActionCommand()) && file != null) {
				highlightedPlace = null;
				petrinet.loadPetrinetFromFile(file);
				
				//Schaltfläche "Analyse mehrerer Dateien..." wurde gewählt
			} else if("chooseMultipleData".equals(evt.getActionCommand())) {
				openFile(true);
			}	
		}
	}
	
	
	//ActionListener für die Toolbar
	private class ToolBarListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent evt) {
			//Button "resetPetrinet" wurde betätigt
			if("resetPetrinet".equals(evt.getActionCommand()) && file != null) {
				petrinet.setMarking("0");
				
				//Button "clearMarkingGraph" wurde betätigt
			} else if("clearMarkingGraph".equals(evt.getActionCommand()) && file != null) {
				petrinet.setMarking("0");
				petrinet.getMarkingGraph().resetMarkingGraph();
				
				//Button "plusToken" wurde betätigt
			} else if("plusToken".equals(evt.getActionCommand()) && highlightedPlace != null) {
				petrinet.plusToken(highlightedPlace);
				
				//Button "minusToken" wurde betätigt
			} else if("minusToken".equals(evt.getActionCommand()) && highlightedPlace != null) {
				petrinet.minusToken(highlightedPlace);
				
				//Button "analyseGraph" wurde betätigt
			} else if("analyseGraph".equals(evt.getActionCommand())) {
				analysePetrinet();
			}	
		}	
	}
	
	
	//ActionListener für die GraphStreamViewer
	private class GraphStreamViewerListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent evt) {
			//Klick auf Knoten des Petri-Netzes
			if("petrinetClick".equals(evt.getActionCommand())) {
				if(petrinet.getTransitions().containsKey((String)evt.getSource())){
					petrinet.update((String)evt.getSource());
				} else {
					updateHighlightedPlace((String)evt.getSource());
				}
				
				//Klick auf Knoten des Markierungsgraphen
			} else if("markingGraphClick".equals(evt.getActionCommand())) {
				petrinet.setMarking((String)evt.getSource());
			}
			
		}
		
	}
	
	//Logik um Stellen zu markieren, um die Anzahl der Marken zu ändern
	private void updateHighlightedPlace(String id) {
			//wenn die ID der bereits hervorgehobenen ID
			//entspricht, wird die Hervorhebung aufgehoben
			if(highlightedPlace != null && id.equals(highlightedPlace.getId())) {
				view.getPetrinetView().modelChanged(new ModelEvent(highlightedPlace, "highlightPlace"));
				highlightedPlace = null;
			//sonst wird die Stelle mit der übergebenen ID 
			//zur neuen hervorgehobenen Stelle	
			} else {
				highlightedPlace = petrinet.getPlaces().get(id);
				view.getPetrinetView().modelChanged(new ModelEvent(highlightedPlace, "highlightPlace"));
			}
	}
}