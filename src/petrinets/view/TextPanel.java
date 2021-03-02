package petrinets.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import petrinets.ModelEvent;
import petrinets.ModelListener;
import petrinets.markingGraphModel.Marking;
import petrinets.markingGraphModel.MarkingGraphEdge;
import petrinets.petrinetModel.Petrinet;

/**
 * Diese Klasse repräsentiert das Textfeld des Programmes<br>
 * und ist von {@link JPanel} abgeleitet.<p>
 * Die Klasse implementiert das {@link ModelListener} interface und Instanzen davor Meldet sich<br>
 * bei Datenmodellen als Beobachter an, von denen sie über Veränderungen informiert wird.
 * Welche Veränderungen verarbeitet werden können ist in der {@link #modelChanged(ModelEvent)}
 * Methode beschrieben.
 * 
 * @author Michael Assmair
 * 
 * @see JPanel
 * @see View
 * @see ModelListener
 * @see #modelChanged(ModelEvent)
 * 
 */
class TextPanel extends JPanel implements ModelListener {

	//default serial version ID
	private static final long serialVersionUID = 1819045918462063856L;
	
	//Button zum löschen des angezeigten Textes.
	private final JButton clearButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/erase.png")));
	//Toolbar in der sich der clearButten befindet.
	private final JToolBar consoleToolBar = new JToolBar();
	
	//Textfeld und Label zur Darstellung von Informationen.
	private final JTextArea text = new JTextArea();
	private final JLabel label = new JLabel();
	private final JScrollPane scrollpane = new JScrollPane(text);

	/**
	 * Im Konstruktor wird das Textfeld initialisiert<br>
	 * 
	 * @param petrinet {@link Petrinet}
	 * 
	 * @see View
	 * @see Petrinet
	 */
	TextPanel(Petrinet petrinet) {
		//ModelListener meldet sich bei Datenmodellen als Beobachter an.
		petrinet.addListener(this);
		petrinet.getMarkingGraph().addListener(this);

		//Panel wird initialisiert.
		setMinimumSize(new Dimension(100, 100));

		clearButton.setToolTipText("Lösche Textfeld");

		setLayout(new BorderLayout());
		
		//Logik zum löschen des Textfeldes
		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				text.selectAll();
				text.setText("");

			}
		});

		//Button wird in ToolBar eingefügt und ToolBar in das Panel eingefügt
		consoleToolBar.setFloatable(false);
		consoleToolBar.add(Box.createGlue());
		consoleToolBar.add(clearButton);

		//Textfeld soll nicht beschreibbar sein
		//Font word als monospace Font gesetzt.
		text.setEditable(false);
		text.setFont(new Font("monospaced", Font.PLAIN, 12));
		
		add(consoleToolBar, BorderLayout.NORTH);
		add(scrollpane, BorderLayout.CENTER);
		add(label, BorderLayout.SOUTH);
	}

	/**
	 * Diese Methode wird aufgerufen, wenn das Model bei dem<br>
	 * dieser Beobachter angemeldet ist verändert wurde.<p>
	 * 
	 * Der {@link ModelListener} kann die Befehle<br>
	 * <ul>
	 * <li> <Strong>printLine</Strong> - Der als Quelle übergebene Text wird der {@link TextArea} hinzugefügt. Quelle {@link String}
	 * <li> <Strong>loadFile</Strong> - falls eine neue File geladen wurde. Quelle {@link File}
	 * <li> <Strong>addMarking</Strong> - falls eine Markierung hinzugefügt wurde. Quelle {@link Marking}
	 * <li> <Strong>addEdge</Strong> - falls eine Kante hinzugefügt wurde. Quelle {@link MarkingGraphView}
	 * </ul>
	 * 
	 * @see ModelEvent
	 * @see ModelListener
	 * 
	 * @throws ClassCastException falls die falsche Quelle übergeben wurde.
	 */
	@Override
	public void modelChanged(ModelEvent evt) {
		//String wird übergeben
		if ("printLine".equals(evt.getCommand())) {
			text.append((String)evt.getSource() + "\n");
			text.setCaretPosition(text.getDocument().getLength());
		}
		
		// FIle wird übergeben
		else if("loadFile".equals(evt.getCommand())) {
			label.setText(((File)evt.getSource()).getName());
			text.append(((File)evt.getSource()).getName()  + " wurde geladen" + "\n");
			text.setCaretPosition(text.getDocument().getLength());
		}
		
		//Marking wird übergeben
		else if ("addMarking".equals(evt.getCommand())) {
			//falls Startmarkierung übergeben wurde
			if("0".equals(((Marking)evt.getSource()).getMarkingID())) {
				text.append("Startmarkierung " + ((Marking)evt.getSource()).toString() + " wurde hinzugefügt." + "\n");
				text.setCaretPosition(text.getDocument().getLength());
			} else {
				text.append("Markierung " + ((Marking)evt.getSource()).toString() + " wurde hinzugefügt." + "\n");
				text.setCaretPosition(text.getDocument().getLength());
			}
		}
		
		//MarkingEdge wird übergeben
		else if("addEdge".equals(evt.getCommand())) {
			MarkingGraphEdge edge = (MarkingGraphEdge)evt.getSource();
			text.append("Kante von " + edge.getPredMarking().toString() + " nach " + edge.getSuccMakring().toString() + " wurde hinzugefügt." + "\n");
			text.setCaretPosition(text.getDocument().getLength());
		}
	}
}
