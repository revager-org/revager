package org.revager.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingWorker;

import org.revager.gui.UI;
import org.revager.gui.presentationView.PresentationFrame;
import org.revager.tools.GUITools;

public class OpenPresentationView extends AbstractAction {

	private static final long serialVersionUID = 6898299494722370833L;

	@Override
	public void actionPerformed(ActionEvent e) {
		GUITools.executeSwingWorker(new OpenPresentationFrameWorker());
	}

	public void performActionDirectly() {
		final PresentationFrame presentationFrame = UI.getInstance().getPresentationFrame();
		// Wait a bit to wait for the scribble view to be loaded. Otherwise the
		// presentation view start flickering because the scribble view is
		// loading data.
		Thread thread = new Thread(() -> {
			try {
				Thread.sleep(700);
			} catch (InterruptedException e) {
			}
			presentationFrame.setVisible(true);
		});
		thread.start();
	}

	private class OpenPresentationFrameWorker extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception {
			performActionDirectly();
			return null;
		}
	}

}
