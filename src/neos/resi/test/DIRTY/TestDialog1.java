/**
 * 
 */
package neos.resi.test.DIRTY;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import neos.resi.app.model.Data;
import neos.resi.gui.AbstractDialog;
import neos.resi.gui.UI;

/**
 * @author jojo
 * 
 */
@SuppressWarnings("serial")
public class TestDialog1 extends AbstractDialog {

	public TestDialog1(Frame parent) {
		super(parent);

		setTitle("Ein Test-Dialog");
		setDescription("Dies ist eine Testbeschreibung. "
				+ "Hier wird beschrieben, "
				+ "was der Benutzer in diesem Dialog tun kann. "
				+ "Und noch ein bisschen Text.");
		setIcon(Data.getInstance().getIcon("assistantDialog_64x64.png"));

		GridLayout gridLay = new GridLayout(2, 2);
		getContentPane().setLayout(gridLay);

		addButton(new JButton("Bestätigen"));
		addButton(new JButton("Abbrechen"));

		add(new JButton("Ein einfacher Button..."));
		add(new JButton("Ein einfacher Button..."));

		JButton buttonTest2 = new JButton("Test");
		buttonTest2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setMessage("Dies ist eine Meldung...");
				switchToProgressMode();
			}
		});
		add(buttonTest2);

		JButton buttonTest = new JButton("Test");
		buttonTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setHint("Bitte geben Sie einen gültigen Wert ein.");
			}
		});
		add(buttonTest);

		// setHelpChapter("example");
		setHelpChapter("example", "test");

		setMinimumSize(new Dimension(600, 600));
		setSize(new Dimension(650, 650));
		setLocationToCenter();
	}

	public static void main(String[] args) {
		new TestDialog1(UI.getInstance().getMainFrame()).setVisible(true);
	}

}
