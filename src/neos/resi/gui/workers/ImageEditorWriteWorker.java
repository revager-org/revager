package neos.resi.gui.workers;

import javax.swing.SwingWorker;

import neos.resi.gui.UI;
import neos.resi.gui.protocol.graphical_annotations.ImageEditor;

public class ImageEditorWriteWorker extends SwingWorker<Void, Void> {

	@Override
	protected Void doInBackground() throws Exception {
		for (ImageEditor editor : UI.getInstance().getProtocolFrame()
				.getImageEditors().values()) {
			editor.writeImageToFile();
		}

		return null;
	}

}
