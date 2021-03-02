package petrinets;

import petrinets.controller.Controller;
import petrinets.petrinetModel.Petrinet;
import petrinets.view.View;

/**
 * Diese Klasse enthält die main-Methode zum Starten des Petrinet-Programmes.
 * 
 * @author Michael Assmair
 *
 */
public class Main {
	
	/**
	 * In der main-Methode wird wird das Datenmodell, die Visualisierung und der Controller initialisiert
	 * 
	 * @param args wird nicht genutzt.
	 * 	
	 */
	public static void main(String[] args) {
		
		//Renderer mit Unterstützung für Multigraphen und aller CSS Attribute
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		
		//event dispatching thread wird gestartet.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				//Initialisiert View, Model und Controller 
				final Petrinet petrinet = new Petrinet();
				final View view = new View("Petrinets", petrinet);
				new Controller(view, petrinet);
			}
		});	
	}
}
