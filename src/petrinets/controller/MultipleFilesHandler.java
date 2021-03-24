package petrinets.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingWorker;

import petrinets.ModelAction;
import petrinets.ModelEvent;
import petrinets.ModelListener;
import petrinets.markingGraphModel.Marking;
import petrinets.markingGraphModel.MarkingGraphEdge;
import petrinets.markingGraphModel.MarkingGraph;
import petrinets.petrinetModel.Petrinet;

/**
 * SwingWorker, der im Hintergrund die Stapelverarbeitung erledigt
 * Die Klasse ist von {@link SwingWorker} abgeleitet
 * 
 * @author Michael Assmair
 * 
 * @see Petrinet
 * @see MarkingGraph
 * @see BoundednessAlgorithm
 *
 */
class MultipleFilesHandler extends SwingWorker<List<String>, ModelEvent> implements ModelListener{
	
	//Pfad von der Startmarkierung zur Markierung des Abbruchkriteriums
	private final List<String> omegaPath = new ArrayList<>();
	
	//Beide Markierungen des Abbruchkriteriums
	private final List<String> omgeaMarkings = new ArrayList<>();
	
	//Liste von Beobachtern
	private final Set<ModelListener> listenerList = new HashSet<>();
	
	//Petri-Netz der gerade zu verarbeitende Datei
	private final Petrinet petrinet = new Petrinet();
	
	//Markierungsgraph des gerade zu verarbeitenden Petri-Netzes
	private final MarkingGraph markingGraph = petrinet.getMarkingGraph();
	
	//Dateien die verarbeitet werden
	private final File[] files;

	
	/**
	 * Konstruktor der einen {@link SwingWorker} für die Stapelverarbeitung<br>
	 * von mehreren Petri-Netzen liefert<br>
	 * Muss nach Initialisierung mit {@link #execute()} gestartet werden
	 * 
	 * 
	 * @param files Dateien die verarbeitet werden sollen
	 */
	MultipleFilesHandler(File[] files) {
		this.files = files;
		petrinet.addListener(this);
		markingGraph.addListener(this);
	}
	
	
	/**
	 * Background task für die Stapelverarbeitung
	 * 
	 * 
	 */
	@Override
	protected List<String> doInBackground() throws Exception {
		//liste von Zwischenergebnissen 
		List<String> list = new ArrayList<>();
		//Schleife über alle zu verarbeitenden Dateien
		for(File file : files) {
			//StringBuilder für die Textausgabe
			final StringBuilder sb = new StringBuilder();
			
			//neues Petri-Netz wird geladen
			sb.append(String.format("%1$-54.54s %2$s", file.getName(), "|"));
			loadNewPetrinet(file);
			
			//Markierungsgraph ist unbeschränkt
			if(BoundednessAlgorithm.analysePetrinet(petrinet)) {
				//Pfad des Abbruchkriteriums in umgekehrter Reihenfolge
				//der Geschalteten Transitionen
				Collections.reverse(omegaPath);
				
				//Ausgabe für geschaltete Transitionen
				//und Pfad des Abbruchkriteriums
				sb.append(String.format("%1$-11s %2$s", "nein", "|"));
				sb.append(String.format("%1$-30.30s", omegaPath.size() + ":" + omegaPathToString() + ";"));
				sb.append(String.format("%1$-20.20s %2$-20.20s", omgeaMarkings.get(1) + ",", omgeaMarkings.get(0)));
				
				//Markierungsgraph ist beschränkt
			} else {
				//Ausgabe Anzahl der Knoten und Kanten des Markierungsgraphen
				sb.append(String.format("%1$-11.11s %2$s", "ja", "|"));
				sb.append(markingGraph.size() + "/" + markingGraph.getEdgesNumber());
			}
			//Zwischenergebnis wird Liste hinzugefügt für
			//die Ausgabe nach Beendigung der Stapelverarbeitung 
			list.add(sb.toString());
		}
		return list;
	}
	
	//zu verarbeitendes Petri-Netz wird geladen
	private void loadNewPetrinet(File file) {
		//setzt Pfad und Markierungen
		//des Abbruchkriteriums zurück
		omegaPath.clear();
		omgeaMarkings.clear();

		petrinet.loadPetrinetFromFile(file);
	}
	
	
	/**
	 * Beobachter werden während der Verarbeitung über<br>
	 * Events informiert
	 * 
	 */
	@Override
	protected void process(List<ModelEvent> chunks) {
		for(ModelEvent evt : chunks) {
			notifyListener(evt);
		}
	}
	
	
	/**
	 * Ausgabe der Ergebnisse nach Beendigung der Stapelverarbeitung
	 * 
	 */
	@Override
	protected void done() {
		//StringBulider für die Textausgabe
		final StringBuilder sb = new StringBuilder();
		
		//Kopfzeile
		sb.append(String.format("%1$-54s %2$s %3$-10s %4$s %5$s", "", "|", "", "|", "Knoten / Kanten bzw." + "\n"));
		sb.append(String.format("%1$-54s %2$s %3$-10s %4$s %5$s", "Dateiname", "|", "beschränkt", "|", "Pfadlänge:Pfad; m, m’" + "\n"));
		sb.append(String.format("-------------------------------------------------------|------------|-------------------------------------------------------"));
		notifyListener(new ModelEvent(sb.toString(), ModelAction.PRINT_LINE));
		
		//Ausgabe der Ergebnisse
		try {
			for(String line : get()) {
			notifyListener(new ModelEvent(line, ModelAction.PRINT_LINE));
			}
		} catch (Exception ignore) {}
	}

	
	/**
	 * Gibt Events während der Berechnung
	 * an die <code>process()</code> Methode weiter.
	 * 
	 * Der {@link ModelListener} kann die Befehle<br>
	 * <ul>
	 * <li> <Strong>setOmegaPathEdge</Strong> - verarbeitet den Pfad zum Abbruchkriterium. Quelle {@link MarkingGraphEdge}
	 * <li> <Strong>setSecondOmegaMarking</Strong> - setzt die zweite Markierung für das Abbruchkriterium. Quelle {@link Marking}
	 * <li> <Strong>setFirstOmegaMarking</Strong> - setzt die erste Markierung für das Abbruchkriterium. Quelle {@link Marking}
	 * <li> <Strong>loadFile</Strong> - Ausgabe das eine neue Datei geladen wurde. Quelle {@link File}
	 * </ul>
	 * 
	 * Andere Events werden unbearbeitet an die angemeldeten Beobachter weitergegeben
	 * 
	 * @see ModelEvent
	 * @see ModelListener
	 * 
	 * @throws ClassCastException falls die falsche Quelle uebergeben wurde.
	 */
	@Override
	public void modelChanged(ModelEvent evt) {
		//Anfangsmarkierung des Abbruchkriteriums wird übergeben
		if(ModelAction.SET_OMEGA_PATH.equals(evt.getAction())) {
			omegaPath.add(((MarkingGraphEdge)evt.getSource()).getTransition().getId());
			
			//Endmarkierung des Abbruchkriteriums wird übergeben
		} else if(ModelAction.SET_SECOND_OMEGA_MARKING.equals(evt.getAction())) {
			omgeaMarkings.add(((Marking)evt.getSource()).toString());
			
			//Anfangsmarkierung des Abbruchkriteriums wird übergeben
		} else if(ModelAction.SET_FIRST_OMEGA_MARKING.equals(evt.getAction())) {
			omgeaMarkings.add(((Marking)evt.getSource()).toString());
			
			//neue Datei wurde geladen. Datei soll nicht im label angezeigt werden
		} else if(ModelAction.LOAD_FILE.equals(evt.getAction())) {
			publish(new ModelEvent(((File)evt.getSource()).getName() + " wurde geladen", ModelAction.PRINT_LINE));
			
			//andere Events
		} else {
			publish(evt);
		}
	}
	
	//alle Beobachter werden über den Event informiert
	private void notifyListener(ModelEvent evt) {
		for(ModelListener listener : listenerList) {
			listener.modelChanged(evt);
		}
	}
	
	
	/**
	 * Meldet einen Beobachter für die Instanz an.
	 * 
	 * @param listener anzumeldender Beobachter
	 */
	public void addListener(ModelListener listener) {
		listenerList.add(listener);
	}
	
	
	/**
	 * Meldet einen Beobachter für die Instanz ab.
	 * 
	 * @param listener zu entfernender Beobachter
	 */
	public void removeListener(ModelListener listener) {
		listenerList.remove(listener);
	}
	
	
	//runde statt eckige Klammer
	private String omegaPathToString() {
		StringBuilder sb = new StringBuilder("(");
		omegaPath.stream().forEachOrdered(str -> sb.append(str + ","));
		//letztes Komma ist zu viel
		sb.deleteCharAt(sb.lastIndexOf(",")).append(")");
		return sb.toString();
	}
}