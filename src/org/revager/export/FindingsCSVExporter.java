/* 
 * Copyright 2009 Davide Casciato, Sandra Reich, Johannes Wettinger
 * 
 * This file is part of Resi.
 *
 * Resi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Resi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resi. If not, see <http://www.gnu.org/licenses/>.
 */
package org.revager.export;

import static org.revager.app.model.Data._;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.revager.app.Application;
import org.revager.app.FindingManagement;
import org.revager.app.ReviewManagement;
import org.revager.app.model.appdata.AppCSVColumnName;
import org.revager.app.model.appdata.AppCSVProfile;
import org.revager.app.model.schema.Finding;

/**
 * This class is a concrete exporter class to export findings.
 */
public class FindingsCSVExporter extends CSVExporter {

	/**
	 * Reference to the review management.
	 */
	private ReviewManagement revMgmt = Application.getInstance()
			.getReviewMgmt();

	/**
	 * Reference to the findings management.
	 */
	private FindingManagement findMgmt = Application.getInstance()
			.getFindingMgmt();

	/**
	 * The column order.
	 */
	private List<AppCSVColumnName> columnOrder = null;

	/**
	 * The severity mappings.
	 */
	private Map<String, String> severityMappings = null;

	/**
	 * The findings to export.
	 */
	private List<Finding> findings = null;

	/**
	 * The bug reporter.
	 */
	private String reporter = null;

	/**
	 * Instantiates a new findings CSV exporter.
	 * 
	 * @param csvProfile
	 *            the CSV profile to use for export
	 * @param severityMappings
	 *            the severity mappings
	 * @param findings
	 *            the findings to export
	 * @param reporter
	 *            the bug reporter
	 */
	public FindingsCSVExporter(AppCSVProfile csvProfile,
			Map<String, String> severityMappings, List<Finding> findings,
			String reporter) {
		super();

		try {
			this.columnOrder = csvProfile.getColumnOrder();
			this.severityMappings = severityMappings;
			this.findings = findings;
			this.reporter = reporter;

			if (csvProfile.isEncapsulateContent()) {
				setEncapsulator("'");
			}

			setColumns(columnOrder.size());

			if (csvProfile.isColsInFirstLine()) {
				csvHead = new String[getColumns()];

				int index = 0;
				for (AppCSVColumnName col : columnOrder) {
					csvHead[index] = csvProfile.getColumnMapping(col);

					index++;
				}
			}
		} catch (Exception e) {
			/*
			 * Not part of unit testing because this exception is only thrown if
			 * an internal error occurs.
			 */
			new ExportException(e.getMessage());
		}
	}

	/**
	 * Adds the given severity mapping.
	 * 
	 * @param sevName
	 *            the original severity name in the review
	 * @param sevMapping
	 *            the severity mapping for the CSV file
	 */
	public void addSeverityMapping(String sevName, String sevMapping) {
		if (severityMappings == null) {
			severityMappings = new HashMap<String, String>();
		}

		severityMappings.put(sevName, sevMapping);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.revager.export.CSVExporter#writeContent()
	 */
	@Override
	protected void writeContent() {
		for (Finding f : findings) {
			String[] csvLine = newLine();
			int index = 0;

			/*
			 * Write a line with each column
			 */
			for (AppCSVColumnName col : columnOrder) {
				switch (col) {
				case DESCRIPTION:
					csvLine[index] = f.getDescription() + " ("
							+ _("Review Findings") + " " + f.getId() + ")";
					break;

				case REFERENCE:
					String refSep = "";
					String refs = "";
					for (String ref : findMgmt.getReferences(f)) {
						refs = refs + refSep + ref;
						refSep = " ** ";
					}

					if (refs.equals("")) {
						refs = "--";
					}

					String references = MessageFormat
							.format(_("Finding {0} of Review \"{1}\" ** Product: {2} (Version: {3}) **** {4}"),
									Integer.toString(f.getId()),
									revMgmt.getReviewName(),
									revMgmt.getProductName(),
									revMgmt.getProductVersion(), refs);

					csvLine[index] = references;
					break;

				case REPORTER:
					csvLine[index] = reporter;
					break;

				case SEVERITY:
					if (severityMappings != null
							&& severityMappings.get(f.getSeverity()) != null) {
						csvLine[index] = severityMappings.get(f.getSeverity());
					} else {
						/*
						 * Not part of unit testing because the Resi XML schema
						 * says that each finding has a severity.
						 */
						csvLine[index] = f.getSeverity();
					}
					break;

				default:
					break;
				}

				index++;
			}

			csvDoc.add(csvLine);
		}
	}

}
