package petrinets.petrinetModel;

import java.awt.Point;
import java.util.Comparator;
import java.util.Objects;

/**
 * Dies Klasse repräsentiert das Element eines Graphen<br>
 * Ein Element verfügt über eine ID, einen Namen und eine Position<br>
 * Stellen und Transitionen erben von dieser Klasse.
 * 
 * @author Michael Assmair
 * 
 * @see Place
 * @see Transition
 *
 */
abstract class Element implements Comparable<Element>{
	
	//ID des Elements
	private String id;
	
	//Name des Elements
	private String name;
	
	//Position des Elements
	private Point position;
	
	/**
	 * Im Konstruktor wird ein Element mit zugehöriger ID erzeugt.
	 * 
	 * @param id ID des Elementes.
	 */
	Element(String id) {	
		this.id = id;
	}
	
	/**
	 * Getter-Methode, die die ID des Elements liefert
	 * 
	 * @return ID des ELement
	 */
	public final String getId() {	
		return id;
	}
	
	/**
	 * Getter-Methode, die den Namen des Elements liefert.
	 * 
	 * @return Namen des Elementes
	 */
	public final String getName() {	
		return name;
	}
	
	/**
	 * Getter-Methode, die ID und Name in der Form [id]Name liefert.
	 * 
	 * @return ID und Namens in der Form [id]Name.
	 */
	public final String getIdAndName() {
		return "[" + id + "]" + name;
	}
	
	/**
	 * Setter-Methode, die den Namen des Elementes setzt.
	 * 
	 * @param name Name des Elementes.
	 */
	final void setName(String name) {	
		this.name = name;
	}
	
	/**
	 * Getter-Methode, die die Position des Elementes mit x- und y-Koordinate liefert.
	 * 
	 * @return Positions des Elementes.
	 * 
	 * @see Point
	 */
	public final Point getPosition() {	
		return position;
	}

	/**
	 * Setter-Methode, die die Position des Elementes setzt.
	 * 
	 * @param x x-Koordinate.
	 * @param y y-Koordinate.
	 * 
	 * @see Point
	 */
	final void setPosition(int x, int y) {	
		this.position = new Point(x, y);
	}	
	
	/**
	 * Überschreit die equals Methode der Object-Klasse.<br>
	 * Vergleicht Elemente anhand ihrer ID.
	 * 
	 * @return true falls die Elemente die selbe ID haben
	 */
	@Override
	public boolean equals(Object obj) {	
		if (obj == this) return true;	
        if (!(obj instanceof Element)) {
            return false;
        }
        Element tmpElem = (Element) obj;
        return this.id.equals(tmpElem.id);
	}
	
	/**
	 * Überschreibt die hashCode Methode der Object-Klasse.<br>
	 * Erzeugt aus der ID des Elementes den Hashcode.
	 */
	@Override
	public int hashCode() {	
		return Objects.hash(id);
	}

	/**
	 * {@link Comparator} der zwei Elemente nach ihrer ID vergleicht
	 */
	@Override
	public int compareTo(Element o) {
		return id.compareTo(o.getId());
	}
}
