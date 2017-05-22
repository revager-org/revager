package org.revager.gui.models;

import javax.swing.table.AbstractTableModel;

import org.revager.app.Application;
import org.revager.app.FindingManagement;
import org.revager.app.model.schema.Finding;

public abstract class AbstractFindingTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 8121903111638434547L;

	protected transient FindingManagement findingMgmt = Application.getInstance().getFindingMgmt();
	protected transient Finding localFind;

	public AbstractFindingTableModel(Finding currentFinding) {
		localFind = currentFinding;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	public void setFinding(Finding finding) {
		localFind = finding;
	}

}
