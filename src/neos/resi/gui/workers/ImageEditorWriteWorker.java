package neos.resi.gui.workers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import neos.resi.app.Application;
import neos.resi.app.FindingManagement;
import neos.resi.app.model.schema.Finding;
import neos.resi.app.model.schema.Protocol;
import neos.resi.gui.UI;

public class ImageEditorWriteWorker extends SwingWorker<Void, Void> {

	private FindingManagement findMgmt = Application.getInstance()
			.getFindingMgmt();

	private Protocol protocol = null;

	public ImageEditorWriteWorker(Protocol prot) {
		super();

		protocol = prot;
	}

	@Override
	protected Void doInBackground() throws Exception {
		List<String> absPaths = new ArrayList<String>();

		for (Finding find : findMgmt.getFindings(protocol)) {
			for (File ref : findMgmt.getExtReferences(find)) {
				absPaths.add(ref.getAbsolutePath());
			}
		}

		for (String filePath : UI.getInstance().getProtocolFrame()
				.getImageEditors().keySet()) {
			if (absPaths.contains(filePath)) {
				UI.getInstance().getProtocolFrame().getImageEditors().get(
						filePath).writeImageToFile();
			}
		}

		return null;
	}

}
