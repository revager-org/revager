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
package org.revager.gui.aspects_manager;

import static org.revager.app.model.Data._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.revager.app.Application;
import org.revager.app.AspectManagement;
import org.revager.app.AttendeeManagement;
import org.revager.app.ResiFileFilter;
import org.revager.app.ReviewManagement;
import org.revager.app.model.ApplicationData;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppAspect;
import org.revager.app.model.appdata.AppCatalog;
import org.revager.app.model.schema.Aspect;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Review;
import org.revager.app.model.schema.Role;
import org.revager.gui.AbstractFrame;
import org.revager.gui.TextPopupWindow;
import org.revager.gui.TextPopupWindow.ButtonClicked;
import org.revager.gui.UI;
import org.revager.gui.actions.ActionRegistry;
import org.revager.gui.actions.attendee.AddAttendeeAction;
import org.revager.gui.helpers.ExtendedFlowLayout;
import org.revager.gui.helpers.FileChooser;
import org.revager.gui.helpers.HintItem;
import org.revager.gui.models.AspectTableModel;
import org.revager.gui.workers.AutoAspAllocWorker;
import org.revager.gui.workers.ExportAspectsWorker;
import org.revager.gui.workers.ExportCatalogWorker;
import org.revager.gui.workers.ImportAspectsWorker;
import org.revager.gui.workers.ImportCatalogWorker;
import org.revager.gui.workers.LoadDefCatalogsWorker;
import org.revager.tools.GUITools;
import org.revager.tools.TreeTools;

import com.lowagie.text.Font;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel;

/**
 * The Class AspectsManagerFrame.
 */
@SuppressWarnings("serial")
public class AspectsManagerFrame extends AbstractFrame implements Observer {

	private AttendeeManagement attMgmt = Application.getInstance()
			.getAttendeeMgmt();
	private AspectManagement aspMgmt = Application.getInstance()
			.getAspectMgmt();
	private ReviewManagement revMgmt = Application.getInstance()
			.getReviewMgmt();
	private ApplicationData appData = Data.getInstance().getAppData();

	private JSplitPane splitPane = new JSplitPane();
	private int dividerLocation = 0;

	private CheckboxTree tree;
	private CheckNode root = new CheckNode(_("All catalogs"));
	private DefaultTreeModel dtm = new DefaultTreeModel(root);

	private AspectTableModel aspTabModel;

	private Attendee selectedReviewer;
	private ReviewerPanel reviewerPanel;

	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel panelRevButtons;
	private JPanel panelRevTable;
	private JPanel panelReviewers;
	private JPanel panelAllAspects;

	private JButton pushTop;
	private JButton pushUp;
	private JButton pushDown;
	private JButton pushBottom;

	private JButton tbNewAttendee;
	private JButton tbRemove;
	private JButton tbRemoveAll;
	private JButton tbEditItem;
	private JButton tbAddAspect;
	private JButton tbAutoAllocation;
	private JButton tbImportCatalog;
	private JButton tbExportCatalog;
	private JButton tbImportAspects;
	private JButton tbExportAspects;
	private JButton tbAddCatalog;
	private JButton tbCopyCatalog;
	private JButton tbLoadStdCatalogs;

	private JButton buttonAlloc;
	private JButton buttonAllocAll;

	private JRadioButton rbReviewersView;
	private JRadioButton rbAspectsView;

	private HintItem hintMinOneAsp;
	private HintItem hintInfoAlloc;
	private HintItem hintInfoImpExp;
	private HintItem hintMinOneRev;

	/**
	 * Creates the tool bar.
	 */
	private void createToolBar() {
		/*
		 * creating the JToolBar within its components
		 */
		JButton tbConfirm = GUITools.newImageButton();
		tbConfirm.setIcon(Data.getInstance().getIcon("confirm_50x50_0.png"));
		tbConfirm.setRolloverIcon(Data.getInstance().getIcon(
				"confirm_50x50.png"));
		tbConfirm.setToolTipText(_("Confirm and close Aspects Manager"));
		tbConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UI.getInstance().getAspectsManagerFrame().setVisible(false);
			}
		});
		addTopComponent(tbConfirm);

		JButton sepBttn = GUITools.newImageButton();
		sepBttn.setIcon(Data.getInstance().getIcon("sep_50x50.png"));
		sepBttn.setEnabled(false);
		addTopComponent(sepBttn);

		tbAddCatalog = GUITools.newImageButton();
		tbAddCatalog.setIcon(Data.getInstance().getIcon(
				"addCatalog_50x50_0.png"));
		tbAddCatalog.setRolloverIcon(Data.getInstance().getIcon(
				"addCatalog_50x50.png"));
		tbAddCatalog.setToolTipText(_("Add Catalog"));
		tbAddCatalog.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					AppCatalog catalog = appData.newCatalog("");

					EditAspectPopupWindow popup = new EditAspectPopupWindow(UI
							.getInstance().getAspectsManagerFrame(), catalog);

					popup.setVisible(true);

					if (popup.getButtonClicked() == org.revager.gui.aspects_manager.EditAspectPopupWindow.ButtonClicked.ABORT) {
						appData.removeCatalog("");
					}
				} catch (DataException exc) {
					JOptionPane.showMessageDialog(UI.getInstance()
							.getAspectsManagerFrame(), GUITools
							.getMessagePane(exc.getMessage()), _("Error"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		addTopComponent(tbAddCatalog);

		tbCopyCatalog = GUITools.newImageButton();
		tbCopyCatalog.setIcon(Data.getInstance().getIcon(
				"copyCatalog_50x50_0.png"));
		tbCopyCatalog.setRolloverIcon(Data.getInstance().getIcon(
				"copyCatalog_50x50.png"));
		tbCopyCatalog.setToolTipText(_("Copy selected Catalog"));
		tbCopyCatalog.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String name = getSelectedCatalog().getName() + " - Kopie";
					int suffix = 1;

					while (appData.getCatalog(name) != null) {
						name = name + " " + suffix;
						suffix++;
					}

					AppCatalog catalog = appData.copyCatalog(
							getSelectedCatalog(), name);

					updateTree(catalog, null, null);
				} catch (DataException exc) {
					JOptionPane.showMessageDialog(UI.getInstance()
							.getAspectsManagerFrame(), GUITools
							.getMessagePane(exc.getMessage()), _("Error"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		addTopComponent(tbCopyCatalog);

		tbAddAspect = GUITools.newImageButton();
		tbAddAspect
				.setIcon(Data.getInstance().getIcon("addAspect_50x50_0.png"));
		tbAddAspect.setRolloverIcon(Data.getInstance().getIcon(
				"addAspect_50x50.png"));
		tbAddAspect
				.setToolTipText(_("Add a new Aspect to the selected Catalog"));
		tbAddAspect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AppCatalog catalog = getSelectedCatalog();
				AppAspect selAsp = getSelectedAspect();

				String category = _("(No Category)");

				try {
					if (catalog == null && selAsp != null) {
						catalog = selAsp.getCatalog();
						category = selAsp.getCategory();
					}

					if (catalog != null) {
						AppAspect aspect = catalog.newAspect("", "", category);

						EditAspectPopupWindow popup = new EditAspectPopupWindow(
								UI.getInstance().getAspectsManagerFrame(),
								aspect);

						popup.setVisible(true);

						if (popup.getButtonClicked() == org.revager.gui.aspects_manager.EditAspectPopupWindow.ButtonClicked.ABORT) {
							catalog.removeAspect(aspect);
						}
					}
				} catch (DataException exc) {
					JOptionPane.showMessageDialog(UI.getInstance()
							.getAspectsManagerFrame(), GUITools
							.getMessagePane(exc.getMessage()), _("Error"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		addTopComponent(tbAddAspect);

		tbNewAttendee = GUITools.newImageButton(
				Data.getInstance().getIcon("addAttendee_50x50_0.png"), Data
						.getInstance().getIcon("addAttendee_50x50.png"));
		tbNewAttendee.setToolTipText(_("Add Attendee"));
		tbNewAttendee.addActionListener(ActionRegistry.getInstance().get(
				AddAttendeeAction.class.getName()));
		tbNewAttendee.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);

				UI.getInstance().getAttendeeDialog()
						.setCalledByAspectsManager(true);
			}
		});

		addTopComponent(tbNewAttendee);

		tbRemove = GUITools.newImageButton();
		tbRemove.setIcon(Data.getInstance().getIcon("remove_50x50_0.png"));
		tbRemove.setRolloverIcon(Data.getInstance().getIcon("remove_50x50.png"));
		tbRemove.setToolTipText(_("Remove selected Item"));
		tbRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AppAspect selAspect = getSelectedAspect();
				AppCatalog selCatalog = getSelectedCatalog();
				String selCategory = getSelectedCategory();

				try {
					if (selAspect != null) {
						selAspect.getCatalog().removeAspect(selAspect);
					} else if (selCategory != null) {
						selCatalog.removeCategory(selCategory);
					} else if (selCatalog != null) {
						Data.getInstance().getAppData()
								.removeCatalog(selCatalog);
					}
				} catch (DataException exc) {
					JOptionPane.showMessageDialog(UI.getInstance()
							.getAspectsManagerFrame(), GUITools
							.getMessagePane(exc.getMessage()), _("Error"),
							JOptionPane.ERROR_MESSAGE);
				}

				updateTree();
			}
		});
		addTopComponent(tbRemove);

		tbRemoveAll = GUITools.newImageButton();
		tbRemoveAll
				.setIcon(Data.getInstance().getIcon("removeAll_50x50_0.png"));
		tbRemoveAll.setRolloverIcon(Data.getInstance().getIcon(
				"removeAll_50x50.png"));
		tbRemoveAll.setToolTipText(_("Remove selected Items"));
		tbRemoveAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> removeWorker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						notifySwitchToProgressMode();

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								switchToProgressMode();
							}
						});

						List<AppCatalog> chkCatalogs = getCheckedCatalogs();
						List<AppAspect> chkAspects = getCheckedAspects();

						try {
							for (AppAspect asp : chkAspects) {
								asp.getCatalog().removeAspect(asp);
							}

							for (AppCatalog cat : chkCatalogs) {
								appData.removeCatalog(cat);
							}
						} catch (DataException exc) {
							JOptionPane.showMessageDialog(UI.getInstance()
									.getAspectsManagerFrame(), GUITools
									.getMessagePane(exc.getMessage()),
									_("Error"), JOptionPane.ERROR_MESSAGE);
						}

						notifySwitchToEditMode();

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								updateTree();

								switchToEditMode();
							}
						});

						return null;
					}
				};

				GUITools.executeSwingWorker(removeWorker);
			}
		});
		addTopComponent(tbRemoveAll);

		tbEditItem = GUITools.newImageButton();
		tbEditItem.setIcon(Data.getInstance().getIcon("editItem_50x50_0.png"));
		tbEditItem.setRolloverIcon(Data.getInstance().getIcon(
				"editItem_50x50.png"));
		tbEditItem.setToolTipText(_("Edit selected Item"));
		tbEditItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AspectsManagerFrame aspMan = UI.getInstance()
						.getAspectsManagerFrame();

				AppAspect selAspect = getSelectedAspect();
				AppCatalog selCatalog = getSelectedCatalog();
				String selCategory = getSelectedCategory();

				if (selAspect != null) {
					EditAspectPopupWindow popup = new EditAspectPopupWindow(
							aspMan, selAspect);

					popup.setVisible(true);
				} else if (selCategory != null) {
					TextPopupWindow popup = new TextPopupWindow(aspMan,
							_("Please enter a name for the category."),
							selCategory, false);

					popup.setVisible(true);

					if (popup.getButtonClicked() == ButtonClicked.OK
							&& !popup.getInput().trim().equals("")) {
						try {
							String newCategory = popup.getInput().trim();

							selCatalog.editCategory(selCategory, newCategory);

							updateTree(selCatalog, newCategory, null,
									selCategory);
						} catch (DataException exc) {
							JOptionPane.showMessageDialog(aspMan,
									GUITools.getMessagePane(exc.getMessage()),
									_("Error"), JOptionPane.ERROR_MESSAGE);
						}
					}
				} else if (selCatalog != null) {
					EditAspectPopupWindow popup = new EditAspectPopupWindow(
							aspMan, selCatalog);

					popup.setVisible(true);
				}
			}
		});
		addTopComponent(tbEditItem);

		tbAutoAllocation = GUITools.newImageButton(
				Data.getInstance().getIcon("autoAllocation_50x50_0.png"), Data
						.getInstance().getIcon("autoAllocation_50x50.png"));
		tbAutoAllocation
				.setToolTipText(_("Assign selected Aspects to the Reviewers automatically"));
		tbAutoAllocation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUITools.executeSwingWorker(new AutoAspAllocWorker(
						getCheckedAspects()));
			}
		});

		addTopComponent(tbAutoAllocation);

		tbImportCatalog = GUITools.newImageButton(
				Data.getInstance().getIcon("importCatalog_50x50_0.png"), Data
						.getInstance().getIcon("importCatalog_50x50.png"));
		tbImportCatalog.setToolTipText(_("Import Catalog"));
		tbImportCatalog.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileChooser fc = UI.getInstance().getFileChooser();

				fc.setFile(null);

				if (fc.showDialog(UI.getInstance().getAspectsManagerFrame(),
						FileChooser.MODE_OPEN_FILE, ResiFileFilter.TYPE_CATALOG) == FileChooser.SELECTED_APPROVE) {
					GUITools.executeSwingWorker(new ImportCatalogWorker(fc
							.getFile().getAbsolutePath()));
				}
			}
		});

		addTopRightComp(tbImportCatalog);

		tbExportCatalog = GUITools.newImageButton(
				Data.getInstance().getIcon("exportCatalog_50x50_0.png"), Data
						.getInstance().getIcon("exportCatalog_50x50.png"));
		tbExportCatalog.setToolTipText(_("Export selected Catalog"));
		tbExportCatalog.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedCatalog() != null) {
					FileChooser fc = UI.getInstance().getFileChooser();

					fc.setFile(new File(getSelectedCatalog().getName()));

					if (fc.showDialog(
							UI.getInstance().getAspectsManagerFrame(),
							FileChooser.MODE_SAVE_FILE,
							ResiFileFilter.TYPE_CATALOG) == FileChooser.SELECTED_APPROVE) {
						GUITools.executeSwingWorker(new ExportCatalogWorker(fc
								.getFile().getAbsolutePath(),
								getSelectedCatalog()));
					}
				}
			}
		});

		addTopRightComp(tbExportCatalog);

		tbImportAspects = GUITools.newImageButton(
				Data.getInstance().getIcon("importAspects_50x50_0.png"), Data
						.getInstance().getIcon("importAspects_50x50.png"));
		tbImportAspects
				.setToolTipText(_("Import Aspects into the selected Catalog"));
		tbImportAspects.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AppCatalog catalog = getSelectedCatalog();
				FileChooser fc = UI.getInstance().getFileChooser();

				fc.setFile(null);

				if (catalog != null) {
					if (fc.showDialog(
							UI.getInstance().getAspectsManagerFrame(),
							FileChooser.MODE_OPEN_FILE,
							ResiFileFilter.TYPE_ASPECTS) == FileChooser.SELECTED_APPROVE) {
						GUITools.executeSwingWorker(new ImportAspectsWorker(fc
								.getFile().getAbsolutePath(),
								getSelectedCatalog()));
					}
				}
			}
		});

		addTopRightComp(tbImportAspects);

		tbExportAspects = GUITools.newImageButton(
				Data.getInstance().getIcon("exportAspects_50x50_0.png"), Data
						.getInstance().getIcon("exportAspects_50x50.png"));
		tbExportAspects.setToolTipText(_("Export selected Aspects"));
		tbExportAspects.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileChooser fc = UI.getInstance().getFileChooser();

				fc.setFile(null);

				if (getCheckedAspects().size() > 0) {
					if (fc.showDialog(
							UI.getInstance().getAspectsManagerFrame(),
							FileChooser.MODE_SAVE_FILE,
							ResiFileFilter.TYPE_ASPECTS) == FileChooser.SELECTED_APPROVE) {
						GUITools.executeSwingWorker(new ExportAspectsWorker(fc
								.getFile().getAbsolutePath(),
								getCheckedAspects()));
					}
				}
			}
		});

		addTopRightComp(tbExportAspects);

		tbLoadStdCatalogs = GUITools.newImageButton(
				Data.getInstance().getIcon("loadStdCatalogs_50x50_0.png"), Data
						.getInstance().getIcon("loadStdCatalogs_50x50.png"));
		tbLoadStdCatalogs.setToolTipText(_("Load Standard Catalogs"));
		tbLoadStdCatalogs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUITools.executeSwingWorker(new LoadDefCatalogsWorker());
			}
		});

		addTopRightComp(tbLoadStdCatalogs);
	}

	/**
	 * Creates the left pane.
	 */
	private void createLeftPane() {
		leftPanel = new JPanel(new BorderLayout());

		root.removeAllChildren();
		root.setSelected(false);
		tree = new CheckboxTree(dtm) {
			@Override
			public String getToolTipText(MouseEvent evt) {
				if (getRowForLocation(evt.getX(), evt.getY()) == -1) {
					return null;
				}

				TreePath curPath = getPathForLocation(evt.getX(), evt.getY());

				Object obj = ((DefaultMutableTreeNode) curPath
						.getLastPathComponent()).getUserObject();

				if (obj instanceof AppAspect) {
					AppAspect asp = ((AppAspect) obj);

					try {
						return GUITools.getTextAsHtml("<b>"
								+ asp.getDirective() + "</b>" + "\n\n"
								+ asp.getDescription());
					} catch (DataException e) {
						return null;
					}
				} else if (obj instanceof AppCatalog) {
					AppCatalog cat = ((AppCatalog) obj);

					try {
						return GUITools.getTextAsHtml("<b>" + cat.getName()
								+ "</b>\n(" + cat.getNumberOfAspects() + " "
								+ _("Aspects") + ")" + "\n\n"
								+ cat.getDescription());
					} catch (DataException e) {
						return null;
					}
				}

				return null;
			}
		};
		tree.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				updateToolBar();
			}
		});
		((BasicTreeUI) tree.getUI()).setLeftChildIndent(10);
		((BasicTreeUI) tree.getUI()).setRightChildIndent(13);
		tree.setRowHeight(25);
		tree.setCellRenderer(new CustomTreeCellRenderer());
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		//tree.addMouseListener(new NodeSelectionListener(tree));
		JScrollPane scrollPane = new JScrollPane(tree,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(new MatteBorder(0, 0, 1, 0, UI.SEPARATOR_COLOR));
		scrollPane.setMinimumSize(new Dimension(400, 400));
		scrollPane.setPreferredSize(new Dimension(400, 400));

		// updateTree();
		tree.expandRow(0);
		tree.setToolTipText("");
		
		tree.addTreeCheckingListener(new CheckBoxListener(tree));
		
		tree.getCheckingModel().setCheckingMode(TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_CHECK);

		leftPanel.add(scrollPane, BorderLayout.CENTER);
		JPanel leftBottomPanel = new JPanel(new BorderLayout());

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

		pushTop = GUITools.newImageButton(
				Data.getInstance().getIcon("pushTop_25x25_0.png"), Data
						.getInstance().getIcon("pushTop_25x25.png"));
		pushTop.setToolTipText(_("Push Item to the top"));
		pushTop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AppAspect selAspect = getSelectedAspect();
				AppCatalog selCatalog = getSelectedCatalog();
				String selCategory = getSelectedCategory();

				try {
					if (selAspect != null) {
						selAspect.pushTop();
					} else if (selCategory != null) {
						selCatalog.pushTopCategory(selCategory);
					} else if (selCatalog != null) {
						selCatalog.pushTop();
					}
				} catch (DataException exc) {
					JOptionPane.showMessageDialog(UI.getInstance()
							.getAspectsManagerFrame(), GUITools
							.getMessagePane(exc.getMessage()), _("Error"),
							JOptionPane.ERROR_MESSAGE);
				}

				updateTree();
			}
		});
		buttonPanel.add(pushTop);

		pushUp = GUITools.newImageButton(
				Data.getInstance().getIcon("upArrow_25x25_0.png"), Data
						.getInstance().getIcon("upArrow_25x25.png"));
		pushUp.setToolTipText(_("Push Item up"));
		pushUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AppAspect selAspect = getSelectedAspect();
				AppCatalog selCatalog = getSelectedCatalog();
				String selCategory = getSelectedCategory();

				try {
					if (selAspect != null) {
						selAspect.pushUp();
					} else if (selCategory != null) {
						selCatalog.pushUpCategory(selCategory);
					} else if (selCatalog != null) {
						selCatalog.pushUp();
					}
				} catch (DataException exc) {
					JOptionPane.showMessageDialog(UI.getInstance()
							.getAspectsManagerFrame(), GUITools
							.getMessagePane(exc.getMessage()), _("Error"),
							JOptionPane.ERROR_MESSAGE);
				}

				updateTree();
			}
		});
		buttonPanel.add(pushUp);

		pushDown = GUITools.newImageButton(
				Data.getInstance().getIcon("downArrow_25x25_0.png"), Data
						.getInstance().getIcon("downArrow_25x25.png"));
		pushDown.setToolTipText(_("Push Item down"));
		pushDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AppAspect selAspect = getSelectedAspect();
				AppCatalog selCatalog = getSelectedCatalog();
				String selCategory = getSelectedCategory();

				try {
					if (selAspect != null) {
						selAspect.pushDown();
					} else if (selCategory != null) {
						selCatalog.pushDownCategory(selCategory);
					} else if (selCatalog != null) {
						selCatalog.pushDown();
					}
				} catch (DataException exc) {
					JOptionPane.showMessageDialog(UI.getInstance()
							.getAspectsManagerFrame(), GUITools
							.getMessagePane(exc.getMessage()), _("Error"),
							JOptionPane.ERROR_MESSAGE);
				}

				updateTree();
			}
		});
		buttonPanel.add(pushDown);

		pushBottom = GUITools.newImageButton(
				Data.getInstance().getIcon("pushBottom_25x25_0.png"), Data
						.getInstance().getIcon("pushBottom_25x25.png"));
		pushBottom.setToolTipText(_("Push Item to the bottom"));
		pushBottom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AppAspect selAspect = getSelectedAspect();
				AppCatalog selCatalog = getSelectedCatalog();
				String selCategory = getSelectedCategory();

				try {
					if (selAspect != null) {
						selAspect.pushBottom();
					} else if (selCategory != null) {
						selCatalog.pushBottomCategory(selCategory);
					} else if (selCatalog != null) {
						selCatalog.pushBottom();
					}
				} catch (DataException exc) {
					JOptionPane.showMessageDialog(UI.getInstance()
							.getAspectsManagerFrame(), GUITools
							.getMessagePane(exc.getMessage()), _("Error"),
							JOptionPane.ERROR_MESSAGE);
				}

				updateTree();
			}
		});
		buttonPanel.add(pushBottom);

		/*
		 * Strut button
		 */
		JButton strutButton = GUITools.newImageButton(Data.getInstance()
				.getIcon("blank_25x25.png"),
				Data.getInstance().getIcon("blank_25x25.png"));
		strutButton.setEnabled(false);
		buttonPanel.add(strutButton);

		/*
		 * Button for sorting the tree
		 */
		JButton buttonSort = GUITools.newImageButton(Data.getInstance()
				.getIcon("sort_22x22_0.png"),
				Data.getInstance().getIcon("sort_22x22.png"));
		buttonSort.setToolTipText(_("Sort Catalogs in ABC order"));
		buttonSort.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Data.getInstance().getAppData().sortCatalogsAlphabetical();
				} catch (DataException exc) {
					JOptionPane.showMessageDialog(null,
							GUITools.getMessagePane(exc.getMessage()),
							_("Error"), JOptionPane.ERROR_MESSAGE);
				}

				updateTree();
			}
		});
		buttonPanel.add(buttonSort);

		/*
		 * Buttons for collapsing and expanding the whole tree
		 */
		JButton buttonExpandAll = GUITools.newImageButton(Data.getInstance()
				.getIcon("expandTree_25x25_0.png"),
				Data.getInstance().getIcon("expandTree_25x25.png"));
		buttonExpandAll.setToolTipText(_("Expand whole tree"));
		buttonExpandAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreeTools.expandAll(tree, new TreePath(root), true);
			}
		});
		buttonPanel.add(buttonExpandAll);

		JButton buttonCollapseAll = GUITools.newImageButton(Data.getInstance()
				.getIcon("collapseTree_25x25_0.png"), Data.getInstance()
				.getIcon("collapseTree_25x25.png"));
		buttonCollapseAll.setToolTipText(_("Collapse whole tree"));
		buttonCollapseAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreeTools.expandAll(tree, new TreePath(root), false);

				updateTree();
			}
		});
		buttonPanel.add(buttonCollapseAll);

		buttonPanel.setBorder(new EmptyBorder(4, 4, 4, 4));

		leftBottomPanel.add(buttonPanel, BorderLayout.NORTH);
		leftPanel.add(leftBottomPanel, BorderLayout.SOUTH);
		leftPanel.setBorder(new MatteBorder(0, 0, 0, 1, UI.SEPARATOR_COLOR));

		splitPane.setLeftComponent(leftPanel);
	}

	/**
	 * Creates the right pane.
	 */
	private void createRightPane() {
		/*
		 * Radio buttons to change the view
		 */
		ButtonGroup buttonsView = new ButtonGroup();
		GridBagLayout gblView = new GridBagLayout();
		final JPanel panelView = new JPanel(gblView);

		rbReviewersView = new JRadioButton(_("Show Reviewers"), Data
				.getInstance().getIcon("reviewers_16x16.png"));

		rbAspectsView = new JRadioButton(_("Show assigned Aspects"), Data
				.getInstance().getIcon("aspects_16x16.png"));

		rbReviewersView.setFont(UI.STANDARD_FONT.deriveFont(Font.BOLD));
		rbReviewersView.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));
		rbReviewersView.setFocusPainted(false);
		rbReviewersView.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				rbReviewersView.setFont(UI.STANDARD_FONT.deriveFont(Font.BOLD));
				rbAspectsView.setFont(UI.STANDARD_FONT);

				rightPanel.remove(panelAllAspects);
				rightPanel.add(panelReviewers, BorderLayout.CENTER);

				rightPanel.remove(panelView);
				rightPanel.add(panelView, BorderLayout.NORTH);

				updateReviewersView();

				rightPanel.revalidate();
				rightPanel.repaint();
			}
		});
		buttonsView.add(rbReviewersView);

		rbAspectsView.setFont(UI.STANDARD_FONT);
		rbAspectsView.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		rbAspectsView.setFocusPainted(false);
		rbAspectsView.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				rbReviewersView.setFont(UI.STANDARD_FONT);
				rbAspectsView.setFont(UI.STANDARD_FONT.deriveFont(Font.BOLD));

				rightPanel.remove(panelReviewers);
				rightPanel.add(panelAllAspects, BorderLayout.CENTER);

				rightPanel.remove(panelView);
				rightPanel.add(panelView, BorderLayout.NORTH);

				updateAspectsView();

				rightPanel.revalidate();
				rightPanel.repaint();
			}
		});
		buttonsView.add(rbAspectsView);

		GUITools.addComponent(panelView, gblView, rbReviewersView, 0, 1, 1, 1,
				1.0, 0.0, 0, 10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.EAST);
		GUITools.addComponent(panelView, gblView, rbAspectsView, 1, 1, 1, 1,
				1.0, 0.0, 0, 10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(panelView, gblView, new JSeparator(
				SwingConstants.HORIZONTAL), 0, 2, 2, 1, 1.0, 0.0, 0, 0, 10, 0,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);

		/*
		 * ****** Prepare the reviewers view ******
		 */
		GridBagLayout gblReviewers = new GridBagLayout();
		panelReviewers = new JPanel(gblReviewers);
		panelReviewers.setBorder(new EmptyBorder(30, 30, 30, 30));

		/*
		 * Button to allocate aspects to all reviewers
		 */
		buttonAllocAll = GUITools.newImageButton(
				Data.getInstance().getIcon("allocAspAll_32x32_0.png"), Data
						.getInstance().getIcon("allocAspAll_32x32.png"));
		buttonAllocAll
				.setToolTipText(_("Assign selected Aspects to all Reviewers"));
		buttonAllocAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> allocWorker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						notifySwitchToProgressMode();

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								switchToProgressMode(_("Allocating aspects ..."));
							}
						});

						observeResiData(false);

						try {
							for (Attendee att : attMgmt.getAttendees()) {
								if (att.getRole() == Role.REVIEWER) {
									for (AppAspect asp : getCheckedAspects()) {
										attMgmt.addAspect(
												asp.getAsResiAspect(), att);
									}
								}
							}
						} catch (DataException exc) {
							JOptionPane.showMessageDialog(null,
									GUITools.getMessagePane(exc.getMessage()),
									_("Error"), JOptionPane.ERROR_MESSAGE);
						}

						observeResiData(true);

						notifySwitchToEditMode();

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								setStatusMessage(
										_("Aspects allocated successfully."),
										false);

								switchToEditMode();
							}
						});

						return null;
					}
				};

				GUITools.executeSwingWorker(allocWorker);
			}

		});

		GUITools.addComponent(panelReviewers, gblReviewers, buttonAllocAll, 0,
				0, 1, 1, 0.0, 0.0, 0, 0, 0, 5, GridBagConstraints.NONE,
				GridBagConstraints.WEST);

		/*
		 * Prepare the panel for the reviewer buttons
		 */
		panelRevButtons = new JPanel(new ExtendedFlowLayout(FlowLayout.LEFT, 7,
				7));

		GUITools.addComponent(panelReviewers, gblReviewers, panelRevButtons, 1,
				0, 1, 1, 1.0, 0.0, 0, 0, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);

		/*
		 * Button to allocate aspects to the selected reviewer
		 */
		buttonAlloc = GUITools.newImageButton(
				Data.getInstance().getIcon("allocAsp_32x32_0.png"), Data
						.getInstance().getIcon("allocAsp_32x32.png"));
		buttonAlloc
				.setToolTipText(_("Assign selected Aspects to the displayed Reviewer"));
		buttonAlloc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> allocWorker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						notifySwitchToProgressMode();

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								switchToProgressMode(_("Allocating aspects ..."));
							}
						});

						observeResiData(false);

						try {
							for (AppAspect asp : getCheckedAspects()) {
								attMgmt.addAspect(asp.getAsResiAspect(),
										selectedReviewer);
							}
						} catch (DataException exc) {
							JOptionPane.showMessageDialog(null,
									GUITools.getMessagePane(exc.getMessage()),
									_("Error"), JOptionPane.ERROR_MESSAGE);
						}

						observeResiData(true);

						notifySwitchToEditMode();

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								setStatusMessage(
										_("Aspects allocated successfully."),
										false);

								switchToEditMode();
							}
						});

						return null;
					}
				};

				GUITools.executeSwingWorker(allocWorker);
			}
		});

		GUITools.addComponent(panelReviewers, gblReviewers, buttonAlloc, 0, 1,
				1, 1, 0.0, 0.0, 45, 0, 0, 5, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);

		/*
		 * Prepare the panel for the reviewer table
		 */
		panelRevTable = new JPanel(new BorderLayout());

		GUITools.addComponent(panelReviewers, gblReviewers, panelRevTable, 1,
				1, 1, 1, 1.0, 1.0, 20, 7, 7, 7, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);

		/*
		 * ****** Prepare the aspects view ******
		 */
		panelAllAspects = new JPanel(new BorderLayout());
		panelAllAspects.setBorder(new EmptyBorder(30, 40, 30, 40));

		aspTabModel = new AspectTableModel();
		final JTable tableAspects = GUITools
				.newStandardTable(aspTabModel, true);
		tableAspects.setShowGrid(true);
		tableAspects.getColumnModel().getColumn(1).setMaxWidth(200);
		tableAspects.getColumnModel().getColumn(1).setPreferredWidth(150);
		tableAspects.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int selRow = tableAspects.getSelectedRow();

				if (e.getClickCount() == 2 && selRow != -1) {
					Aspect aspect = aspTabModel.getAspect(selRow);

					EditAspectPopupWindow popup = new EditAspectPopupWindow(UI
							.getInstance().getAspectsManagerFrame(), aspect);

					popup.setVisible(true);

					aspMgmt.editAspect(aspect, aspect);

					tableAspects.setRowSelectionInterval(selRow, selRow);
					tableAspects.repaint();

					aspTabModel.fireTableDataChanged();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});

		/*
		 * Tooltips
		 */
		DefaultTableCellRenderer cellRend = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				String content = (String) value;

				setToolTipText(GUITools.getTextAsHtml(content));

				content = content.split("\n")[0];

				return super.getTableCellRendererComponent(table, content,
						isSelected, hasFocus, row, column);
			}
		};
		tableAspects.getColumnModel().getColumn(0).setCellRenderer(cellRend);
		tableAspects.getColumnModel().getColumn(1).setCellRenderer(cellRend);

		panelAllAspects.add(GUITools.setIntoScrollPane(tableAspects),
				BorderLayout.CENTER);

		/*
		 * ****** Add right panel to split panel ******
		 */
		rightPanel = new JPanel(new BorderLayout()) {
			@Override
			public Dimension getMinimumSize() {
				return new Dimension(450, 400);
			}
		};
		splitPane.setRightComponent(rightPanel);
	}

	/**
	 * Update reviewers view.
	 */
	private void updateReviewersView() {
		/*
		 * Check selected reviewer
		 */
		if (!attMgmt.isAttendee(selectedReviewer)
				|| selectedReviewer.getRole() != Role.REVIEWER) {
			selectedReviewer = null;

			for (Attendee att : attMgmt.getAttendees()) {
				if (att.getRole() == Role.REVIEWER) {
					selectedReviewer = att;
					break;
				}
			}
		}

		/*
		 * Buttons for the reviewers
		 */
		panelRevButtons.removeAll();

		for (Attendee att : attMgmt.getAttendees()) {
			if (att.getRole().equals(Role.REVIEWER)) {
				final Attendee rev = att;

				JToggleButton button = new JToggleButton(rev.getName());
				button.setToolTipText(GUITools.getTextAsHtml("<b>"
						+ rev.getName() + "</b>\n\n" + rev.getContact()));
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ev) {
						for (Component butt : panelRevButtons.getComponents()) {
							if (butt != ev.getSource()) {
								((JToggleButton) butt).setSelected(false);
							}
						}

						setSelectedReviewer(rev);
						updateReviewersView();
					}
				});

				if (selectedReviewer == rev) {
					button.setSelected(true);
				}

				panelRevButtons.add(button);
			}
		}

		/*
		 * Table for the aspects of the reviewer
		 */
		panelRevTable.removeAll();

		if (selectedReviewer != null) {
			reviewerPanel = new ReviewerPanel(selectedReviewer);

			panelRevTable.add(reviewerPanel, BorderLayout.CENTER);
		}

		panelReviewers.validate();
		panelReviewers.revalidate();
		panelReviewers.repaint();
	}

	/**
	 * Update aspects view.
	 */
	private void updateAspectsView() {
		aspTabModel.fireTableDataChanged();
	}

	/**
	 * Sets the selected reviewer.
	 * 
	 * @param rev
	 *            the new selected reviewer
	 */
	public void setSelectedReviewer(Attendee rev) {
		this.selectedReviewer = rev;
	}

	/**
	 * Instantiates a new aspects manager frame.
	 * 
	 * @param parent
	 *            the parent
	 */
	public AspectsManagerFrame(Frame parent) {
		super();

		setTitle(_("Aspects Manager"));
		getContentPane().setLayout(new BorderLayout());

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		if (Data.getInstance().getResiData().getReview() == null) {
			Data.getInstance().getResiData().setReview(new Review());
		}

		createToolBar();

		splitPane.setDividerSize(7);
		splitPane.setDividerLocation(400);
		splitPane.setOneTouchExpandable(true);
		splitPane.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				dividerLocation = splitPane.getDividerLocation();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (dividerLocation != splitPane.getDividerLocation()) {
					updateViews();

					rightPanel.setVisible(false);
					rightPanel.setVisible(true);

					System.out.println("!");
				}
			}

		});

		createLeftPane();
		createRightPane();

		// splitPane.validate();
		// splitPane.repaint();

		updateToolBar();

		updateViews();

		createHints();

		// setAlwaysOnTop(true);
		toFront();

		setLocationToCenter();

		setStatusMessage(_("Aspects Manager loaded successfully."), false);

		setLayout(new BorderLayout());

		setNumberOfHints(2);

		setMinimumSize(new Dimension(1000, 660));

		pack();

		add(splitPane, BorderLayout.CENTER);

		Data.getInstance().getResiData().addObserver(this);

		addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// UI.getInstance().getAspectsManagerFrame().toFront();
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// UI.getInstance().getMainFrame().setExtendedState(ICONIFIED);
			}

			@Override
			public void windowOpened(WindowEvent e) {
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Window#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean vis) {
		if (vis) {
			/*
			 * Load standard catalogs if no catalogs exist
			 */
			try {
				if (appData.getNumberOfCatalogs() == 0) {
					GUITools.executeSwingWorker(new LoadDefCatalogsWorker());
				}
			} catch (DataException e) {
				JOptionPane.showMessageDialog(this,
						GUITools.getMessagePane(e.getMessage()), _("Error"),
						JOptionPane.ERROR_MESSAGE);
			}

			rbReviewersView.setSelected(true);

			updateTree();

			updateToolBar();

			updateViews();
		} else {
			setSelectedReviewer(null);

			UI.getInstance().getMainFrame().updateAttendeesTable(true);
		}

		super.setVisible(vis);
	}

	/**
	 * Update tree.
	 */
	public void updateTree() {
		updateTree(null, null, null, null);
	}

	/**
	 * Update tree.
	 * 
	 * @param selCatalog
	 *            the sel catalog
	 * @param selCategory
	 *            the sel category
	 * @param selAspect
	 *            the sel aspect
	 */
	public void updateTree(AppCatalog selCatalog, String selCategory,
			AppAspect selAspect) {
		updateTree(selCatalog, selCategory, selAspect, null);
	}

	/**
	 * Update tree.
	 * 
	 * @param selCatalog
	 *            the sel catalog
	 * @param selCategory
	 *            the sel category
	 * @param selAspect
	 *            the sel aspect
	 * @param oldCategory
	 *            the old category
	 */
	public void updateTree(AppCatalog selCatalog, String selCategory,
			AppAspect selAspect, String oldCategory) {
		if (selAspect == null) {
			selAspect = getSelectedAspect();
		}

		if (selCatalog == null) {
			selCatalog = getSelectedCatalog();
		}

		if (selCategory == null) {
			selCategory = getSelectedCategory();
		}

		List<AppAspect> chkAspects = getCheckedAspects();
		List<AppCatalog> chkCatalogs = getCheckedCatalogs();
		Map<String, List<String>> chkCategories = getCheckedCategories();

		List<AppCatalog> expCatalogs = getExpandedCatalogs();
		Map<String, List<String>> expCategories = getExpandedCategories();

		/*
		 * Correct checked and expanded category
		 */
		if (selCategory != null && oldCategory != null) {
			String catalogName = selCatalog.getName();

			if (chkCategories.get(catalogName).contains(oldCategory)) {
				chkCategories.get(catalogName).remove(oldCategory);
				chkCategories.get(catalogName).add(selCategory);
			}

			if (expCategories.get(catalogName).contains(oldCategory)) {
				expCategories.get(catalogName).remove(oldCategory);
				expCategories.get(catalogName).add(selCategory);
			}
		}

		try {
			root.removeAllChildren();

			/*
			 * Build the tree...
			 */
			for (AppCatalog ac : appData.getCatalogs()) {
				CheckNode catalog = new CheckNode(ac);

				root.add(catalog);

				if (isInList(ac, chkCatalogs)) {
					catalog.setSelected(true);
				}

				for (String cat : ac.getCategories()) {
					CheckNode category = new CheckNode(cat);

					if (isInList(cat, ac.getName(), chkCategories)) {
						category.setSelected(true);
					}

					for (AppAspect asp : ac.getAspects(cat)) {
						CheckNode aspect = new CheckNode(asp);
						category.add(aspect);

						if (isInList(asp, chkAspects)) {
							aspect.setSelected(true);
						}
					}

					if (!category.isLeaf()) {
						catalog.add(category);
					}
				}
			}

			dtm.setRoot(root);
			tree.repaint();
			tree.revalidate();

			/*
			 * Restore expansion states
			 */
			for (AppCatalog catalog : expCatalogs) {
				if (isInList(catalog, expCatalogs)) {
					tree.expandPath(getPath(catalog));
				}

				for (String category : expCategories.get(catalog.getName())) {
					tree.expandPath(getPath(catalog, category));
				}
			}

			/*
			 * Restore selection
			 */
			if (selAspect != null) {
				tree.setSelectionPath(getPath(selAspect));
			} else if (selCatalog != null && selCategory != null) {
				tree.setSelectionPath(getPath(selCatalog, selCategory));
			} else if (selCatalog != null) {
				tree.setSelectionPath(getPath(selCatalog));
			} else {
				tree.setSelectionPath(new TreePath(root));
			}

			/*
			 * Scroll to selection
			 */
			Rectangle visRect = tree.getVisibleRect();
			Rectangle pathRect = tree.getPathBounds(tree.getSelectionPath());

			if (pathRect != null) {
				pathRect.x = visRect.x;
			} else {
				pathRect = visRect;
			}

			tree.scrollRectToVisible(pathRect);

			/*
			 * Update the toolbar buttons
			 */
			updateToolBar();
		} catch (DataException e) {
			JOptionPane.showMessageDialog(this,
					GUITools.getMessagePane(e.getMessage()), _("Error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Update tool bar.
	 */
	private void updateToolBar() {
		if (UI.getInstance().getStatus() == UI.Status.NO_FILE_LOADED) {
			tbNewAttendee.setVisible(false);
			tbAutoAllocation.setVisible(false);
		} else {
			tbNewAttendee.setVisible(true);
			tbAutoAllocation.setVisible(true);
		}

		if (getSelectedCatalog() != null) {
			tbAddAspect.setEnabled(true);
		} else {
			tbAddAspect.setEnabled(false);
		}

		if (getSelectedAspect() != null || getSelectedCatalog() != null
				|| getSelectedCategory() != null) {
			tbRemove.setEnabled(true);
			tbEditItem.setEnabled(true);
			tbAddAspect.setEnabled(true);
		} else {
			tbRemove.setEnabled(false);
			tbEditItem.setEnabled(false);
			tbAddAspect.setEnabled(false);
		}

		if (getSelectedCatalog() != null || getSelectedCategory() != null) {
			tbImportAspects.setEnabled(true);
		} else {
			tbImportAspects.setEnabled(false);
		}

		if (getSelectedCatalog() != null && getSelectedCategory() == null) {
			tbExportCatalog.setEnabled(true);
			tbCopyCatalog.setEnabled(true);
		} else {
			tbExportCatalog.setEnabled(false);
			tbCopyCatalog.setEnabled(false);
		}

		if (getCheckedAspects().size() > 0) {
			tbExportAspects.setEnabled(true);
		} else {
			tbExportAspects.setEnabled(false);
		}

		if (existReviewers() && getCheckedAspects().size() > 0) {
			tbAutoAllocation.setEnabled(true);
		} else {
			tbAutoAllocation.setEnabled(false);
		}

		if (getCheckedAspects().size() > 0 || getCheckedCatalogs().size() > 0) {
			tbRemoveAll.setEnabled(true);
		} else {
			tbRemoveAll.setEnabled(false);
		}

		if (getCheckedAspects().size() > 0) {
			buttonAlloc.setEnabled(true);
			buttonAllocAll.setEnabled(true);
		} else {
			buttonAlloc.setEnabled(false);
			buttonAllocAll.setEnabled(false);
		}
	}

	/**
	 * Creates the hints.
	 */
	private void createHints() {
		hintMinOneAsp = new HintItem(
				_("You have to assign at least one aspect to a reviewer."),
				HintItem.WARNING, "aspects_management");

		hintMinOneRev = new HintItem(
				_("In order to assign aspects to reviewers you have to add at least one reviewer to the review."),
				HintItem.WARNING, "aspects_management");

		hintInfoAlloc = new HintItem(
				_("You can assign catalogs, categories or aspects to the reviewers after selecting them."),
				HintItem.INFO, "aspects_management");

		hintInfoImpExp = new HintItem(
				_("You can import complete catalogs or selected aspects by using the provided buttons in the tool bar."),
				HintItem.INFO, "aspects_management");
	}

	/**
	 * Update hints.
	 */
	private void updateHints() {
		List<HintItem> hints = new ArrayList<HintItem>();

		/*
		 * Show warning
		 */
		if (UI.getInstance().getStatus() != UI.Status.NO_FILE_LOADED) {
			if (!existReviewers()) {
				hints.add(hintMinOneRev);
			} else if (revMgmt.getNumberOfAspects() == 0) {
				hints.add(hintMinOneAsp);
			}

			hints.add(hintInfoAlloc);
		}

		hints.add(hintInfoImpExp);

		setHints(hints);
	}

	/**
	 * Update views.
	 */
	public void updateViews() {
		if (UI.getInstance().getStatus() == UI.Status.NO_FILE_LOADED) {
			rightPanel.removeAll();

			JLabel labelDesc = GUITools
					.getMessagePane(_("No review is loaded. Therefore you can only work in the library with its catalogs and aspects."));
			labelDesc.setHorizontalAlignment(SwingConstants.CENTER);
			labelDesc.setForeground(Color.GRAY);

			rightPanel.add(labelDesc, BorderLayout.CENTER);
		} else if (!existReviewers()) {
			rightPanel.removeAll();

			JLabel labelDesc = GUITools
					.getMessagePane(_("There aren't any reviewers existing in the review. After adding at least one reviewer by using the 'Add Attendee' button you can assign them aspects from the existing catalogs."));
			labelDesc.setHorizontalAlignment(SwingConstants.CENTER);
			labelDesc.setForeground(Color.GRAY);

			rightPanel.add(labelDesc, BorderLayout.CENTER);
		} else {
			rightPanel.removeAll();

			rbReviewersView.setSelected(true);
			for (ItemListener il : rbReviewersView.getItemListeners()) {
				il.itemStateChanged(null);
			}
		}

		updateReviewersView();
		updateAspectsView();
	}

	/**
	 * Observe resi data.
	 * 
	 * @param obs
	 *            the obs
	 */
	public void observeResiData(boolean obs) {
		if (obs) {
			Data.getInstance().getResiData().addObserver(this);

			update(null, null);
		} else {
			Data.getInstance().getResiData().deleteObserver(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateHints();
				updateToolBar();
			}
		});
	}

	/**
	 * Exist reviewers.
	 * 
	 * @return true, if successful
	 */
	private boolean existReviewers() {
		for (Attendee att : attMgmt.getAttendees()) {
			if (att.getRole() == Role.REVIEWER) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the expanded catalogs.
	 * 
	 * @return the expanded catalogs
	 */
	public List<AppCatalog> getExpandedCatalogs() {
		List<AppCatalog> catalogs = new ArrayList<AppCatalog>();

		CheckNode node = root;

		while (node.getNextNode() != null) {
			node = (CheckNode) node.getNextNode();

			Object obj = node.getUserObject();

			if (obj instanceof AppCatalog
					&& tree.isExpanded(getPath((AppCatalog) obj))) {
				catalogs.add((AppCatalog) obj);
			}
		}

		return catalogs;
	}

	/**
	 * Gets the expanded categories.
	 * 
	 * @return the expanded categories
	 */
	public Map<String, List<String>> getExpandedCategories() {
		Map<String, List<String>> categories = new HashMap<String, List<String>>();

		CheckNode node = root;

		AppCatalog currentCatalog = null;

		while (node.getNextNode() != null) {
			node = (CheckNode) node.getNextNode();

			Object obj = node.getUserObject();

			if (obj instanceof AppCatalog) {
				currentCatalog = (AppCatalog) obj;

				categories.put(currentCatalog.getName(),
						new ArrayList<String>());
			} else if (!(obj instanceof AppAspect)
					&& obj != root.getUserObject()
					&& tree.isExpanded(getPath(currentCatalog, (String) obj))) {
				categories.get(currentCatalog.getName()).add((String) obj);
			}
		}

		return categories;
	}

	/**
	 * Gets the selected catalog.
	 * 
	 * @return the selected catalog
	 */
	public AppCatalog getSelectedCatalog() {
		if (tree.getSelectionPath() != null) {
			Object obj = ((CheckNode) tree.getSelectionPath()
					.getLastPathComponent()).getUserObject();

			Object rootObj = root.getUserObject();

			/*
			 * Case: Catalog is selected
			 */
			if (obj instanceof AppCatalog) {
				return (AppCatalog) obj;
			}

			/*
			 * Case: A category is selected
			 */
			if (!(obj instanceof AppAspect) && !(obj instanceof AppAspect)
					&& obj != rootObj) {
				CheckNode catalog = (CheckNode) ((CheckNode) tree
						.getSelectionPath().getLastPathComponent()).getParent();

				return (AppCatalog) catalog.getUserObject();
			}
		}

		return null;
	}

	/**
	 * Gets the selected aspect.
	 * 
	 * @return the selected aspect
	 */
	public AppAspect getSelectedAspect() {
		if (tree.getSelectionPath() != null) {
			Object obj = ((CheckNode) tree.getSelectionPath()
					.getLastPathComponent()).getUserObject();

			if (obj instanceof AppAspect) {
				return (AppAspect) obj;
			}
		}

		return null;
	}

	/**
	 * Gets the selected category.
	 * 
	 * @return the selected category
	 */
	public String getSelectedCategory() {
		if (tree.getSelectionPath() != null) {
			Object obj = ((CheckNode) tree.getSelectionPath()
					.getLastPathComponent()).getUserObject();

			Object rootObj = root.getUserObject();

			if (!(obj instanceof AppAspect) && !(obj instanceof AppCatalog)
					&& obj != rootObj) {
				return (String) obj;
			}
		}

		return null;
	}

	/**
	 * Gets the checked aspects.
	 * 
	 * @return the checked aspects
	 */
	public List<AppAspect> getCheckedAspects() {
		List<AppAspect> aspects = new ArrayList<AppAspect>();

		CheckNode node = root;

		while (node.getNextNode() != null) {
			node = (CheckNode) node.getNextNode();

			if (node.isSelected() && node.getUserObject() instanceof AppAspect) {
				aspects.add((AppAspect) node.getUserObject());
			}
		}

		return aspects;
	}

	/**
	 * Gets the checked catalogs.
	 * 
	 * @return the checked catalogs
	 */
	public List<AppCatalog> getCheckedCatalogs() {
		List<AppCatalog> catalogs = new ArrayList<AppCatalog>();

		CheckNode node = root;

		while (node.getNextNode() != null) {
			node = (CheckNode) node.getNextNode();

			if (node.isSelected() && node.getUserObject() instanceof AppCatalog) {
				catalogs.add((AppCatalog) node.getUserObject());
			}
		}

		return catalogs;
	}

	/**
	 * Gets the checked categories.
	 * 
	 * @return the checked categories
	 */
	public Map<String, List<String>> getCheckedCategories() {
		Map<String, List<String>> categories = new HashMap<String, List<String>>();

		CheckNode node = root;

		AppCatalog currentCatalog = null;

		while (node.getNextNode() != null) {
			node = (CheckNode) node.getNextNode();

			Object obj = node.getUserObject();

			if (obj instanceof AppCatalog) {
				currentCatalog = (AppCatalog) obj;

				categories.put(currentCatalog.getName(),
						new ArrayList<String>());
			} else if (node.isSelected() && !(obj instanceof AppAspect)
					&& obj != root.getUserObject()) {
				categories.get(currentCatalog.getName()).add((String) obj);
			}
		}

		return categories;
	}

	/**
	 * Checks if is in list.
	 * 
	 * @param aspect
	 *            the aspect
	 * @param aspList
	 *            the asp list
	 * 
	 * @return true, if is in list
	 */
	private boolean isInList(AppAspect aspect, List<AppAspect> aspList) {
		for (AppAspect asp : aspList) {
			if (aspect.equals(asp)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if is in list.
	 * 
	 * @param catalog
	 *            the catalog
	 * @param catList
	 *            the cat list
	 * 
	 * @return true, if is in list
	 */
	private boolean isInList(AppCatalog catalog, List<AppCatalog> catList) {
		for (AppCatalog cat : catList) {
			if (catalog.equals(cat)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if is in list.
	 * 
	 * @param category
	 *            the category
	 * @param catalogName
	 *            the catalog name
	 * @param categoryList
	 *            the category list
	 * 
	 * @return true, if is in list
	 */
	private boolean isInList(String category, String catalogName,
			Map<String, List<String>> categoryList) {
		List<String> categories = categoryList.get(catalogName);

		if (categories != null) {
			for (String cat : categories) {
				if (category.equals(cat)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Gets the path.
	 * 
	 * @param asp
	 *            the asp
	 * 
	 * @return the path
	 */
	private TreePath getPath(AppAspect asp) {
		CheckNode node = root;

		while (node.getNextNode() != null) {
			node = (CheckNode) node.getNextNode();

			Object curNode = node.getUserObject();

			if (curNode instanceof AppAspect
					&& ((AppAspect) curNode).equals(asp)) {
				return new TreePath(node.getPath());
			}
		}

		return null;
	}

	/**
	 * Gets the path.
	 * 
	 * @param catalog
	 *            the catalog
	 * 
	 * @return the path
	 */
	private TreePath getPath(AppCatalog catalog) {
		CheckNode node = root;

		while (node.getNextNode() != null) {
			node = (CheckNode) node.getNextNode();

			Object curNode = node.getUserObject();

			if (curNode instanceof AppCatalog
					&& ((AppCatalog) curNode).equals(catalog)) {
				return new TreePath(node.getPath());
			}
		}

		return null;
	}

	/**
	 * Gets the path.
	 * 
	 * @param catalog
	 *            the catalog
	 * @param category
	 *            the category
	 * 
	 * @return the path
	 */
	private TreePath getPath(AppCatalog catalog, String category) {
		TreePath catalogPath = getPath(catalog);

		if (catalogPath == null) {
			return null;
		}

		CheckNode catalogNode = (CheckNode) catalogPath.getLastPathComponent();

		for (int i = 0; i < catalogNode.getChildCount(); i++) {
			CheckNode categoryNode = (CheckNode) catalogNode.getChildAt(i);

			rightPanel.validate();
			String currCategory = (String) categoryNode.getUserObject();

			if (category.equals(currCategory)) {
				return catalogPath.pathByAddingChild(categoryNode);
			}
		}

		return null;
	}

}
