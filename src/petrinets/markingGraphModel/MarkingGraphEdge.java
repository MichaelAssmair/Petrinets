package petrinets.markingGraphModel;

import java.util.Objects;

import petrinets.petrinetModel.Transition;

/**
 * Diese Kasse repräsentiert eine Kante zwischen zwei Markierungen<br>
 * Zusätzlich wird für jede Instanz gespeichert, 
 * welche Transition geschaltet wurde, als die Instanz erzeugt wurde.
 * 
 * @author Michael Assmair
 * 
 * @see Marking
 * @see Transition
 *
 */
public class MarkingGraphEdge {
	
	//Markierung im Nachbereich der Kante
	private Marking source;
	
	//Markierung im Nachbereich der Kante
	private Marking target;
	
	//Beim erzeugen geschaltete Transition
	private Transition transition;
	
	/**
	 * Konstruktor für die Kante zwischen zwei Markierungen.
	 * 
	 * @param transition die Transition, welche geschaltet wurde, um die Kante zu erzeugen
	 * @param source Quellknoten der Kante
	 * @param target Zielknoten der Kante
	 * 
	 * @see Transition
	 * @see Marking
	 * 
	 */
	public MarkingGraphEdge(Transition transition, Marking source, Marking target) {
		this.transition = transition;
		this.source = source;
		this.target = target;
	}
	
	/**
	 * Getter-Methode, die die ID der Kante bestehend aus ID der vorherigen Markierung,<br> 
	 * ID der Transition und ID der nachfolgenden Markierung liefert
	 * 
	 * @return ID der Kante
	 */
	public final String getEdgeID() {
		if(source != null && transition != null && target != null) {
			return source.getMarkingID() + transition.getId() + target.getMarkingID();
		}
		return null;	
	}
	
	/**
	 * Getter-Methode, die die Transition, welche geschaltet wurde, um die Markierung im Nachbereich zu erzeugen liefert. 
	 * 
	 * @return transition die geschaltet wurde.
	 */
	public final Transition getTransition() {
		return transition;
	}
	
	/**
	 * Getter-Methode, die die Markierung im Vorbereich der Kante liefert.
	 * 
	 * @return Markierung im Vorbereich der Kante.
	 */
	public final Marking getSuccMakring() {
		return target;
	}
	
	/**
	 * Getter-Methode, die die Markierung im Nachbereich der Kante Liefert
	 * 
	 * @return Markierung im Nachbereich der Kante
	 */
	public final Marking getPredMarking() {
		return source;
	}
	
	/**
	 * Überschreit die equals Methode der Object-Klasse<br>
	 * Vergleicht Kanten anhand der Knoten im Vorbereich und Nachbereich
	 * 
	 * @return true, falls die Kanten die selben Quellknoten und die Selben Zielknoten haben
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
        if (!(obj instanceof MarkingGraphEdge)) {
            return false;
        }
		MarkingGraphEdge tmpElem = (MarkingGraphEdge) obj;
		return source.equals(tmpElem.source) && transition.equals(tmpElem.transition);
	}
	
	/**
	 * Überschreibt die hashCode Methode der Object-Klasse.<br>
	 * Erzeugt aus dem Knoten im Vorbereich und dem Knoten im Nachbereich den Hashcode.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(source, transition);
	}
}