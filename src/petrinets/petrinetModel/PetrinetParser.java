package petrinets.petrinetModel;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import petrinets.pnml.PNMLWopedParser;

/**
 * Diese Klasse ist abgeleitet von der Klasse {@link PetrinetParser}.<br>
 * Die Transitionen werden in eine TreeMap eingefügt um sicherzustellen, dass<br>
 * sie alphabetisch geordnet sind.<br>
 * 
 * @author Michael Assmair
 * 
 * @see Petrinet
 * @see Place
 * @see Transition
 * @see PetrinetParser
 *
 */

class PetrinetParser extends PNMLWopedParser{
	
	//Kanten werden zwischengespeichert
	private Set<PertinetEdge> arcs = new HashSet<>();
	
	//zu erzeugendes Petri-Netz
	private Petrinet petrinet;
	
	//Stelle die eingelesen wird
	private Place place;
	
	//Transition die eingelesen wird
	private Transition transition;
	
	//Konstruktor des PetrinetParsers
	//lädt das Petri-Netz aus der pnml File und
	private PetrinetParser(File pnml, Petrinet petrinet) {
		super(pnml);
		
		this.petrinet = petrinet;
		
		initParser();
		parse();	
	}
	
	/**
	 * Methode, die einen Parser erzeugt, die übergebene<br>
	 * Datei einliest und das übergebene Petrinet initialisiert
	 * 
	 * 
	 * @param pnml einzulesende Datei
	 * @param petrinet zu initialisierendes Petrinet
	 */
	static void loadFile(File pnml, Petrinet petrinet){
		PetrinetParser parser = new PetrinetParser(pnml, petrinet);
		//Kanten des Petri-Netzes werden erst nach dem einlesen gesetzt,
		//da die Reihenfolge der Element nicht sichergestellt ist
		petrinet.addTransitionsPreAndNext(parser.arcs);
	}
	
	
	/**
	 * Neue Stelle mit ID wird erzeugt
	 */
	@Override
	public void newPlace(String id) {
		
		//Stelle wird mit ID erzeugt.
		place = new Place(id);
		super.newPlace(id);
	}
	
	
	/**
	 * Neue Transition mit ID wird erzeugt
	 */
	@Override
	public void newTransition(String id) {
		
		//Transition wird mit ID erzeugt.
		transition = new Transition(id);
		super.newTransition(id);
	}
	
	
	/**
	 * Neue Kante mit Ziel und Quelle wird erzeugt<br>
	 * und einer Liste als Zwischenspeicher hinzugefügt
	 */
	@Override
	public void newArc(String id, String source, String target) {
		
		//Kante wird mit Vorgänger, Nachfolger und ID erzeugt.
		arcs.add(new PertinetEdge(id,source, target));
		super.newArc(id, source, target);
	}
	
	
	/**
	 * Name der Stelle bzw. Transition wird gesetzt
	 */
	@Override
	public void setName(String id, String name) {
		
		//Stellen und Transitionen sind in einer Eigenen Liste gespeichert,
		//sodass erst geprüft werden muss ob der Name zu einer Stelle oder Transition gehört. 
		if(id.equals(place.getId())) {
			place.setName(name);
		} else {
			transition.setName(name);
		}
		super.setName(id, name);
	}
	
	
	/**
	 * Position der Stelle bzw. Transition wird gesetzt
	 * und das Element wird dem Petri-Netz übergeben
	 */
	@Override
	public void setPosition(String id, String x, String y) {
		
		//Stellen und Transitionen sind in einer Eigenen Liste gespeichert,
		//sodass erst geprüft werden muss ob die Position zu einer Stelle oder Transition gehört. 
		if(id.equals(place.getId())) {
			place.setPosition(Integer.parseInt(x), Integer.parseInt(y));
			petrinet.addPlace(place);
		} else {
			transition.setPosition(Integer.parseInt(x), Integer.parseInt(y));
			petrinet.addTransition(transition);
		}	
		super.setPosition(id, x, y);
	}
	
	
	/**
	 * Anzahl der Marken einer Stelle wird gesetzt
	 */
	@Override
	public void setTokens(String id, String tokens) {
		//Setzt Marken zur entsprechenden Stelle.
		place.setTokens(Integer.parseInt(tokens));
		super.setTokens(id, tokens);
	}
}
