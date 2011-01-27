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
package org.revager.gui;

import static org.revager.app.model.Data._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.revager.app.Application;
import org.revager.app.MeetingManagement;
import org.revager.app.ReviewManagement;
import org.revager.app.model.Data;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;
import org.revager.gui.TextPopupWindow.ButtonClicked;
import org.revager.gui.UI.Status;
import org.revager.gui.actions.ActionRegistry;
import org.revager.gui.actions.ExitAction;
import org.revager.gui.actions.LoadReviewAction;
import org.revager.gui.actions.ManageSeveritiesAction;
import org.revager.gui.actions.NewReviewAction;
import org.revager.gui.actions.OpenAspectsManagerAction;
import org.revager.gui.actions.OpenExpCSVDialogAction;
import org.revager.gui.actions.OpenExpPDFDialogAction;
import org.revager.gui.actions.OpenFindingsListAction;
import org.revager.gui.actions.OpenInvitationsDialogAction;
import org.revager.gui.actions.SaveReviewAction;
import org.revager.gui.actions.SaveReviewAsAction;
import org.revager.gui.actions.assistant.OpenAssistantAction;
import org.revager.gui.actions.attendee.AddAttendeeAction;
import org.revager.gui.actions.attendee.EditAttendeeAction;
import org.revager.gui.actions.attendee.RemoveAttendeeAction;
import org.revager.gui.actions.meeting.AddMeetingAction;
import org.revager.gui.actions.meeting.CommentMeetingAction;
import org.revager.gui.actions.meeting.EditMeetingAction;
import org.revager.gui.actions.meeting.RemoveMeetingAction;
import org.revager.gui.helpers.HintItem;
import org.revager.gui.helpers.MeetingsTreeRenderer;
import org.revager.gui.helpers.TreeMeeting;
import org.revager.gui.helpers.TreeProtocol;
import org.revager.gui.models.AttendeeTableModel;
import org.revager.tools.GUITools;
import org.revager.tools.TreeTools;

/**
 * This class represents the main frame.
 */
@SuppressWarnings("serial")
public class MainFrame extends AbstractFrame implements Observer {

	private boolean observingResiData = true;
	private boolean componentMarked = false;

	private boolean assistantMode = true;

	private ReviewManagement revMgmt = Application.getInstance()
			.getReviewMgmt();

	private JPanel splitPanel = new JPanel(new GridLayout(1, 2));
	private GridBagLayout gbl = new GridBagLayout();

	private AttendeeTableModel attendeesTableModel = new AttendeeTableModel();
	private JTable attendeesTable;

	private JButton removeAttendee;
	private JButton editAttendee;

	private DefaultMutableTreeNode nodeMeetingRoot = new DefaultMutableTreeNode(
			_("All meetings"));
	private DefaultTreeModel meetingsTreeModel = new DefaultTreeModel(
			nodeMeetingRoot);
	private JTree meetingsTree;

	private Border labelBorder = new MatteBorder(15, 0, 0, 0, getContentPane()
			.getBackground());
	private int padding = 30;

	/*
	 * menu items
	 */
	private JMenu menuFile = new JMenu();
	private JMenuItem fileSelectModeItem;
	private JMenuItem closeApplicationItem;
	private JMenuItem fileNewReviewItem;
	private JMenuItem fileOpenReviewItem;
	private JMenuItem fileSaveReviewItem;
	private JMenuItem fileSaveReviewAsItem;

	private JMenu menuEdit = new JMenu();
	private JMenuItem manageSeveritiesItem;
	private JMenuItem newMeetingItem;
	private JMenuItem aspectsManagerItem;
	private JMenuItem createInvitationsItem;
	private JMenuItem newAttendeeItem;
	private JMenuItem protocolModeItem;
	private JMenuItem pdfExportItem;
	private JMenuItem csvExportItem;

	private JMenu menuSettings;
	private JMenuItem appSettings;
	private JMenuItem csvProfiles;

	private JMenu menuHelp;
	private JMenuItem openHelp;
	private JMenuItem aboutHelp;

	/*
	 * Toolbar items
	 */
	private JButton tbManageSeverities;
	private JButton tbAspectsManager;
	private JButton tbCreateInvitations;
	private JButton tbNewAttendee;
	private JButton tbProtocolMode;
	private JButton tbPdfExport;
	private JButton tbCsvExport;
	private JButton tbNewMeeting;
	private JButton tbShowAssistant;
	private JButton tbShowHelp;
	private JButton tbSaveReview;
	private JButton tbNewReview;
	private JButton tbOpenReview;

	/*
	 * Content pane elements
	 */
	private JPanel leftPanel = new JPanel(gbl);
	private JLabel product;
	private JLabel reviewName;
	private JLabel reviewDescription;

	private JPanel rightPanel = new JPanel(gbl);
	private JLabel meetings;
	private JPanel meetingButtons;
	private JLabel attendees;
	private JPanel attendeeButtons;
	private JButton addAttendee;
	private JLabel generalImpression;
	private JLabel recommendation;

	/*
	 * Buttons
	 */
	private JButton addMeeting;
	private JButton editMeeting;
	private JButton editProtocol;
	private JButton commentMeeting;
	private JButton removeMeeting;

	/*
	 * Hint items
	 */
	private HintItem hintStart;
	private HintItem hintMeetAtt;
	private HintItem hintOk;
	private HintItem hintListOfFindings;
	private HintItem hintInfoAssistant;

	/*
	 * Content panes
	 */
	private JTextField textProduct = new JTextField();
	private JTextField textRevName = new JTextField();
	private JTextArea textRevDesc = new JTextArea();
	private JScrollPane scrollRevDesc;
	private JScrollPane treeScrollPane;
	private JScrollPane tableScrollBar;
	private JButton editAspects = new JButton();
	private JButton commentReview = new JButton();
	private JTextArea impressionTxtArea = new JTextArea();
	private JScrollPane scrollImpression;
	private JComboBox recommendationBx = new JComboBox();

	private long updateTime = System.currentTimeMillis();

	private KeyListener updKeyListener = new KeyListener() {
		@Override
		public void keyPressed(KeyEvent e) {
			Object evSrc = e.getSource();

			if (evSrc instanceof JTextArea && e.getKeyCode() == KeyEvent.VK_TAB) {
				JTextArea txtArea = (JTextArea) evSrc;

				if (e.getModifiers() > 0) {
					txtArea.transferFocusBackward();
				} else {
					txtArea.transferFocus();
					e.consume();
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			updateTime = System.currentTimeMillis();
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	};

	private ItemListener updItemListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			updateTime = System.currentTimeMillis();
		}
	};

	private SwingWorker<Void, Void> updateWorker = new SwingWorker<Void, Void>() {
		@Override
		protected Void doInBackground() throws Exception {
			long change = Long.parseLong(Data.getInstance().getResource(
					"keyTypeChangeInMillis"));

			while (true) {
				try {
					Thread.sleep(change);

					long diff = System.currentTimeMillis() - updateTime;

					if (diff >= change && diff < change * 3) {
						final boolean textRevNameFocus = textRevName
								.isFocusOwner();
						final boolean textRevDescFocus = textRevDesc
								.isFocusOwner();

						final int textRevNamePos = textRevName
								.getCaretPosition();
						final int textRevDescPos = textRevDesc
								.getCaretPosition();

						updateResiData();

						updateHints();

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								if (textRevNameFocus) {
									textRevName.requestFocus();
									textRevName
											.setCaretPosition(textRevNamePos);
								} else if (textRevDescFocus) {
									textRevDesc.requestFocus();
									textRevDesc
											.setCaretPosition(textRevDescPos);
								}
							}
						});
					}
				} catch (Exception e) {
					/*
					 * do nothing and wait for the next run of this worker
					 */
				}
			}
		}
	};

	/**
	 * Gets the review name.
	 * 
	 * @return the review name
	 */
	public String getTextRevName() {
		return textRevName.getText();
	}

	/**
	 * Gets the review description.
	 * 
	 * @return the review description
	 */
	public String getTextRevDesc() {
		return textRevDesc.getText();
	}

	/**
	 * Gets the meetings tree.
	 * 
	 * @return the meetings tree
	 */
	public JTree getMeetingsTree() {
		return meetingsTree;
	}

	/**
	 * Creates the left pane.
	 */
	private void createLeftPane() {
		/*
		 * creating the left panel
		 */
		product = new JLabel(_("Product:"));

		textProduct.setFocusable(false);

		reviewName = new JLabel(_("Title of the review:"));
		reviewName.setBorder(labelBorder);
		textRevName.addKeyListener(updKeyListener);

		reviewDescription = new JLabel(_("Description of the review:"));
		reviewDescription.setBorder(labelBorder);
		textRevDesc.addKeyListener(updKeyListener);

		scrollRevDesc = GUITools.setIntoScrllPn(textRevDesc);

		commentReview.setIcon(Data.getInstance().getIcon("comment_16x16.png"));
		commentReview.setText(_("Comments on the review"));
		commentReview.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String comments = revMgmt.getReviewComments();

				TextPopupWindow popup = new TextPopupWindow(UI.getInstance()
						.getMainFrame(), _("Comments on the review:"),
						comments, true);

				popup.setVisible(true);

				/*
				 * saving comment of the selected meeting
				 */
				if (popup.getButtonClicked() == ButtonClicked.OK) {
					revMgmt.setReviewComments(popup.getInput());
				}
			}
		});

		textProduct.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		textProduct.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				UI.getInstance().getEditProductDialog().setVisible(true);

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
	}

	/**
	 * Updates left pane.
	 */
	private void updateLeftPane() {
		splitPanel.remove(leftPanel);
		splitPanel.add(leftPanel);

		leftPanel.removeAll();

		GUITools.addComponent(leftPanel, gbl, product, 0, 0, 2, 1, 1.0, 0.0,
				10, 20, 0, padding, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(leftPanel, gbl, textProduct, 0, 1, 2, 1, 1.0,
				0.0, 10, 20, 0, padding, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTH);
		GUITools.addComponent(leftPanel, gbl, reviewName, 0, 2, 2, 1, 1.0, 0.0,
				10, 20, 0, padding, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(leftPanel, gbl, textRevName, 0, 3, 2, 1, 1.0,
				0.0, 10, 20, 0, padding, GridBagConstraints.BOTH,
				GridBagConstraints.NORTH);
		GUITools.addComponent(leftPanel, gbl, reviewDescription, 0, 4, 2, 1,
				1.0, 0.0, 10, 20, 0, padding, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(leftPanel, gbl, scrollRevDesc, 0, 5, 2, 2, 1.0,
				1.0, 10, 20, 0, padding, GridBagConstraints.BOTH,
				GridBagConstraints.NORTH);
		GUITools.addComponent(leftPanel, gbl, commentReview, 0, 7, 2, 1, 1.0,
				0.0, 10, 20, 0, padding, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
	}

	/**
	 * Creates the right pane.
	 */
	private void createRightPane() {
		/*
		 * creating the right panel
		 */
		rightPanel.setBorder(new MatteBorder(0, 1, 0, 0, UI.SEPARATOR_COLOR));

		meetings = new JLabel(_("Review Meetings:"));

		MeetingsTreeRenderer renderer = new MeetingsTreeRenderer();

		meetingsTree = new JTree(meetingsTreeModel) {
			@Override
			public void validate() {
				TreePath selMeeting = getPath(getSelectedMeeting());

				meetingsTreeModel.setRoot(nodeMeetingRoot);

				TreeTools.expandAll(meetingsTree,
						new TreePath(nodeMeetingRoot), true);

				meetingsTree.setSelectionPath(selMeeting);

				super.validate();
			}

			@Override
			public String getToolTipText(MouseEvent evt) {
				if (getRowForLocation(evt.getX(), evt.getY()) == -1) {
					return null;
				}

				String tip = null;

				TreePath curPath = getPathForLocation(evt.getX(), evt.getY());

				Object obj = ((DefaultMutableTreeNode) curPath
						.getLastPathComponent()).getUserObject();

				if (obj instanceof TreeMeeting) {
					tip = "<b>" + ((TreeMeeting) obj).toString() + "</b>\n\n"
							+ ((TreeMeeting) obj).getMeeting().getComments();
				} else if (obj instanceof TreeProtocol) {
					tip = "<b>"
							+ ((TreeProtocol) obj).toString()
							+ "</b>\n\n"
							+ ((TreeProtocol) obj).getMeeting().getProtocol()
									.getComments();
				}

				return GUITools.getTextAsHtml(tip);
			}
		};
		meetingsTree.setToolTipText("");
		meetingsTree.setCellRenderer(renderer);

		meetingsTree.setRootVisible(false);
		((BasicTreeUI) meetingsTree.getUI()).setLeftChildIndent(18);
		((BasicTreeUI) meetingsTree.getUI()).setRightChildIndent(25);
		meetingsTree.setSelectionRow(0);
		meetingsTree.setRowHeight(30);
		meetingsTree.addTreeWillExpandListener(new TreeWillExpandListener() {
			public void treeWillExpand(TreeExpansionEvent e) {
			}

			public void treeWillCollapse(TreeExpansionEvent e)
					throws ExpandVetoException {
				throw new ExpandVetoException(e);
			}
		});
		meetingsTree.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (getSelectedMeeting() != null) {
						ActionRegistry.getInstance()
								.get(EditMeetingAction.class.getName())
								.actionPerformed(null);
					} else if (getSelectedProtocol() != null) {
						ActionRegistry.getInstance()
								.get(OpenFindingsListAction.class.getName())
								.actionPerformed(null);
					}
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
				updateButtons();
			}
		});

		/*
		 * Rollover
		 */
		MouseInputAdapter rolloverListener = new MouseInputAdapter() {
			public void mouseMoved(MouseEvent e) {
				int i = meetingsTree.getRowForPath(meetingsTree
						.getPathForLocation(e.getX(), e.getY()));

				MeetingsTreeRenderer.currentRow = i;

				meetingsTree.repaint();
			}
		};
		meetingsTree.addMouseMotionListener(rolloverListener);
		meetingsTree.addMouseListener(rolloverListener);

		meetingsTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		treeScrollPane = new JScrollPane(meetingsTree);

		/*
		 * creating add/edit/remove/comment meeting buttons and adding them to a
		 * separate panel
		 */
		meetingButtons = new JPanel(new GridLayout(5, 1));

		addMeeting = GUITools.newImageButton(
				Data.getInstance().getIcon("add_25x25_0.png"), Data
						.getInstance().getIcon("add_25x25.png"), ActionRegistry
						.getInstance().get(AddMeetingAction.class.getName()));
		meetingButtons.add(addMeeting);

		removeMeeting = GUITools.newImageButton();
		removeMeeting.setIcon(Data.getInstance().getIcon("remove_25x25_0.png"));
		removeMeeting.setRolloverIcon(Data.getInstance().getIcon(
				"remove_25x25.png"));
		removeMeeting.setToolTipText(_("Remove"));
		removeMeeting.addActionListener(ActionRegistry.getInstance().get(
				RemoveMeetingAction.class.getName()));
		meetingButtons.add(removeMeeting);

		editMeeting = GUITools.newImageButton();
		editMeeting.setIcon(Data.getInstance().getIcon("edit_25x25_0.png"));
		editMeeting.setRolloverIcon(Data.getInstance()
				.getIcon("edit_25x25.png"));
		editMeeting.setToolTipText(_("Modify meeting"));
		editMeeting.addActionListener(ActionRegistry.getInstance().get(
				EditMeetingAction.class.getName()));

		meetingButtons.add(editMeeting);

		commentMeeting = GUITools.newImageButton();
		commentMeeting.setIcon(Data.getInstance()
				.getIcon("comment_25x25_0.png"));
		commentMeeting.setRolloverIcon(Data.getInstance().getIcon(
				"comment_25x25.png"));
		commentMeeting.setToolTipText(_("Comment meeting"));
		commentMeeting.addActionListener(ActionRegistry.getInstance().get(
				CommentMeetingAction.class.getName()));
		meetingButtons.add(commentMeeting);

		editProtocol = GUITools.newImageButton();
		editProtocol.setIcon(Data.getInstance().getIcon(
				"protocolFrame_25x25_0.png"));
		editProtocol.setRolloverIcon(Data.getInstance().getIcon(
				"protocolFrame_25x25.png"));
		editProtocol.setToolTipText(_("Open/create list of findings"));
		editProtocol.addActionListener(ActionRegistry.getInstance().get(
				OpenFindingsListAction.class.getName()));

		meetingButtons.add(editProtocol);

		commentMeeting.setEnabled(false);
		editMeeting.setEnabled(false);
		removeMeeting.setEnabled(false);
		editProtocol.setEnabled(false);

		attendees = new JLabel(_("Attendees of the review:"));
		attendees.setBorder(labelBorder);
		attendeesTable = GUITools.newStandardTable(attendeesTableModel, false);
		attendeesTable.getColumnModel().getColumn(2).setPreferredWidth(50);
		TableColumn col = attendeesTable.getColumnModel().getColumn(0);
		col.setCellRenderer(new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object arg1, boolean isSelected, boolean arg3, int row,
					int column) {
				JPanel localPnl = new JPanel();

				if (isSelected) {
					localPnl.setBackground(attendeesTable
							.getSelectionBackground());
				} else {
					int localRow = row;

					while (localRow > 0) {
						localRow = localRow - 2;
					}

					if (localRow == 0) {
						localPnl.setBackground(UI.TABLE_ALT_COLOR);
					} else {
						localPnl.setBackground(attendeesTable.getBackground());
					}
				}
				if (!table.isEnabled()) {
					localPnl.add(new JLabel(Data.getInstance().getIcon(
							"attendeeDisabled_20x20.png")));
				} else {
					localPnl.add(new JLabel(Data.getInstance().getIcon(
							"attendee_20x20.png")));
				}

				return localPnl;
			}
		});
		attendeesTable.setRowHeight(30);
		attendeesTable.getColumnModel().getColumn(0).setMaxWidth(30);
		attendeesTable.getColumnModel().getColumn(2).setMaxWidth(110);
		attendeesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
		attendeesTable.getColumnModel().getColumn(3).setMaxWidth(140);
		attendeesTable.getColumnModel().getColumn(3).setPreferredWidth(140);
		attendeesTable.setShowGrid(true);

		/*
		 * Tooltip
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
		attendeesTable.getColumnModel().getColumn(1).setCellRenderer(cellRend);

		attendeesTable.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					ActionRegistry.getInstance()
							.get(EditAttendeeAction.class.getName())
							.actionPerformed(null);
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
				updateButtons();
			}
		});

		tableScrollBar = new JScrollPane(attendeesTable);
		tableScrollBar.getViewport().setBackground(Color.WHITE);

		/*
		 * creating add/edit/remove/comment meeting buttons and adding them to a
		 * separate panel
		 */
		GridLayout grid = new GridLayout(3, 1);
		grid.setVgap(4);

		attendeeButtons = new JPanel(grid);

		addAttendee = GUITools.newImageButton(
				Data.getInstance().getIcon("addAttendee_25x25_0.png"),
				Data.getInstance().getIcon("addAttendee_25x25.png"),
				ActionRegistry.getInstance().get(
						AddAttendeeAction.class.getName()));
		attendeeButtons.add(addAttendee);

		removeAttendee = GUITools.newImageButton();
		removeAttendee.setIcon(Data.getInstance().getIcon(
				"removeAttendee_25x25_0.png"));
		removeAttendee.setRolloverIcon(Data.getInstance().getIcon(
				"removeAttendee_25x25.png"));
		removeAttendee.setToolTipText(_("Remove attendee"));
		removeAttendee.addActionListener(ActionRegistry.getInstance().get(
				RemoveAttendeeAction.class.getName()));
		attendeeButtons.add(removeAttendee);

		editAttendee = GUITools.newImageButton();
		editAttendee.setIcon(Data.getInstance().getIcon(
				"editAttendee_25x25_0.png"));
		editAttendee.setRolloverIcon(Data.getInstance().getIcon(
				"editAttendee_25x25.png"));
		editAttendee.setToolTipText(_("Modify attendee"));
		editAttendee.addActionListener(ActionRegistry.getInstance().get(
				EditAttendeeAction.class.getName()));
		attendeeButtons.add(editAttendee);

		editAspects.setAction(ActionRegistry.getInstance().get(
				OpenAspectsManagerAction.class.getName()));

		removeAttendee.setEnabled(false);
		editAttendee.setEnabled(false);

		generalImpression = new JLabel(_("General impression of the product:"));
		generalImpression.setBorder(labelBorder);

		scrollImpression = GUITools.setIntoScrllPn(impressionTxtArea);
		impressionTxtArea.addKeyListener(updKeyListener);

		recommendation = new JLabel(_("Final recommendation for the product:"));
		recommendation.setBorder(labelBorder);
		recommendationBx.setEditable(true);
		for (String rec : Data.getStandardRecommendations()) {
			recommendationBx.addItem(rec);
		}
		recommendationBx.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				updateTime = System.currentTimeMillis();
			}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}
		});

		recommendationBx.addItemListener(updItemListener);

	}

	/**
	 * Updates right pane.
	 */
	private void updateRightPane() {
		splitPanel.remove(rightPanel);
		splitPanel.add(rightPanel);

		rightPanel.removeAll();

		GUITools.addComponent(rightPanel, gbl, meetings, 0, 0, 1, 1, 0, 0, 10,
				padding, 0, 20, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(rightPanel, gbl, treeScrollPane, 0, 1, 2, 2, 1.0,
				1.0, 10, padding, 0, 20, GridBagConstraints.BOTH,
				GridBagConstraints.NORTH);
		GUITools.addComponent(rightPanel, gbl, meetingButtons, 2, 1, 1, 2, 0,
				1.0, 10, 0, 0, padding / 2, GridBagConstraints.NONE,
				GridBagConstraints.NORTH);

		GUITools.addComponent(rightPanel, gbl, attendees, 0, 3, 1, 1, 0, 0, 10,
				padding, 0, 20, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(rightPanel, gbl, tableScrollBar, 0, 4, 2, 2, 1.0,
				1.0, 10, padding, 0, 20, GridBagConstraints.BOTH,
				GridBagConstraints.NORTH);
		GUITools.addComponent(rightPanel, gbl, attendeeButtons, 2, 4, 1, 2, 0,
				1.0, 10, 0, 0, padding / 2, GridBagConstraints.NONE,
				GridBagConstraints.NORTH);
		GUITools.addComponent(rightPanel, gbl, editAspects, 1, 6, 1, 2, 0, 0,
				10, 0, 0, 20, GridBagConstraints.NONE,
				GridBagConstraints.NORTHEAST);

		/*
		 * if (Data.getInstance().getModeParam("ableToEditImpression")) {
		 * GUITools.addComponent(rightPanel, gbl, generalImpression, 0, 4, 1, 1,
		 * 0, 0, 10, padding, 0, 20, GridBagConstraints.NONE,
		 * GridBagConstraints.NORTHWEST); GUITools.addComponent(rightPanel, gbl,
		 * scrollImpression, 0, 5, 2, 2, 1.0, 1.0, 10, padding, 0, 20,
		 * GridBagConstraints.BOTH, GridBagConstraints.NORTH); }
		 * 
		 * if (Data.getInstance().getModeParam("ableToEditRecommendation")) {
		 * GUITools.addComponent(rightPanel, gbl, recommendation, 0, 7, 1, 1, 0,
		 * 0, 10, padding, 0, 20, GridBagConstraints.NONE,
		 * GridBagConstraints.NORTHWEST); GUITools.addComponent(rightPanel, gbl,
		 * recommendationBx, 0, 8, 1, 1, 1.0, 0, 10, padding, 0, 20,
		 * GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH); }
		 */

	}

	/**
	 * Creates the tool bar.
	 */
	private void createToolBar() {
		tbShowAssistant = GUITools.newImageButton(
				Data.getInstance().getIcon("tbShowAssistant_50x50_0.png"), Data
						.getInstance().getIcon("tbShowAssistant_50x50.png"));
		tbShowAssistant.setToolTipText(_("Open RevAger Assistant"));
		tbShowAssistant.addActionListener(ActionRegistry.getInstance().get(
				OpenAssistantAction.class.getName()));

		addTopComponent(tbShowAssistant);

		tbNewReview = GUITools.newImageButton(
				Data.getInstance().getIcon("new_50x50_0.png"), Data
						.getInstance().getIcon("new_50x50.png"), ActionRegistry
						.getInstance().get(NewReviewAction.class.getName()));

		addTopComponent(tbNewReview);

		tbOpenReview = GUITools.newImageButton(
				Data.getInstance().getIcon("open_50x50_0.png"),
				Data.getInstance().getIcon("open_50x50.png"),
				ActionRegistry.getInstance().get(
						LoadReviewAction.class.getName()));

		addTopComponent(tbOpenReview);

		tbSaveReview = GUITools.newImageButton(
				Data.getInstance().getIcon("save_50x50_0.png"),
				Data.getInstance().getIcon("save_50x50.png"),
				ActionRegistry.getInstance().get(
						SaveReviewAction.class.getName()));

		addTopComponent(tbSaveReview);

		// TODO HELP IS CURRENTLY DISABLED!

		// tbShowHelp = GUITools.newImageButton(
		// Data.getInstance().getIcon("tbShowHelp_50x50_0.png"), Data
		// .getInstance().getIcon("tbShowHelp_50x50.png"));
		// tbShowHelp.setToolTipText(_("Open RevAger Help"));
		// tbShowHelp.addActionListener(ActionRegistry.getInstance().get(
		// OpenHelpAction.class.getName()));
		//
		// addTopComponent(tbShowHelp);

		tbManageSeverities = GUITools.newImageButton(
				Data.getInstance().getIcon("severities_50x50_0.png"),
				Data.getInstance().getIcon("severities_50x50.png"),
				ActionRegistry.getInstance().get(
						ManageSeveritiesAction.class.getName()));

		addTopRightComp(tbManageSeverities);

		tbAspectsManager = GUITools.newImageButton(
				Data.getInstance().getIcon("aspectsManager_50x50_0.png"),
				Data.getInstance().getIcon("aspectsManager_50x50.png"),
				ActionRegistry.getInstance().get(
						OpenAspectsManagerAction.class.getName()));

		addTopRightComp(tbAspectsManager);

		tbCreateInvitations = GUITools
				.newImageButton(
						Data.getInstance().getIcon(
								"createInvitations_50x50_0.png"),
						Data.getInstance().getIcon(
								"createInvitations_50x50.png"),
						ActionRegistry.getInstance().get(
								OpenInvitationsDialogAction.class.getName()));

		addTopRightComp(tbCreateInvitations);

		tbNewAttendee = GUITools.newImageButton(
				Data.getInstance().getIcon("addAttendee_50x50_0.png"),
				Data.getInstance().getIcon("addAttendee_50x50.png"),
				ActionRegistry.getInstance().get(
						AddAttendeeAction.class.getName()));

		addTopComponent(tbNewAttendee);

		tbNewMeeting = GUITools.newImageButton(
				Data.getInstance().getIcon("addMeeting_50x50_0.png"),
				Data.getInstance().getIcon("addMeeting_50x50.png"),
				ActionRegistry.getInstance().get(
						AddMeetingAction.class.getName()));

		addTopComponent(tbNewMeeting);

		tbProtocolMode = GUITools.newImageButton(
				Data.getInstance().getIcon("protocolFrame_50x50_0.png"),
				Data.getInstance().getIcon("protocolFrame_50x50.png"),
				ActionRegistry.getInstance().get(
						OpenFindingsListAction.class.getName()));

		addTopComponent(tbProtocolMode);

		tbPdfExport = GUITools.newImageButton(
				Data.getInstance().getIcon("PDFExport_50x50_0.png"),
				Data.getInstance().getIcon("PDFExport_50x50.png"),
				ActionRegistry.getInstance().get(
						OpenExpPDFDialogAction.class.getName()));

		addTopRightComp(tbPdfExport);

		tbCsvExport = GUITools.newImageButton(
				Data.getInstance().getIcon("CSVExport_50x50_0.png"),
				Data.getInstance().getIcon("CSVExport_50x50.png"),
				ActionRegistry.getInstance().get(
						OpenExpCSVDialogAction.class.getName()));

		addTopRightComp(tbCsvExport);
	}

	/**
	 * Updates tool bar.
	 */
	private void updateToolBar() {
		tbShowAssistant.setVisible(assistantMode);

		// tbOpenReview.setVisible(!assistantMode);
		tbManageSeverities.setVisible(!assistantMode);
		tbSaveReview.setVisible(!assistantMode);
		// tbNewReview.setVisible(!assistantMode);
		// tbAspectsManager.setVisible(!assistantMode);
		tbCreateInvitations.setVisible(!assistantMode);
		tbNewAttendee.setVisible(!assistantMode);
		tbProtocolMode.setVisible(!assistantMode);
		tbPdfExport.setVisible(!assistantMode);
		tbCsvExport.setVisible(!assistantMode);
		tbNewMeeting.setVisible(!assistantMode);
	}

	/**
	 * Creates the menu.
	 */
	private void createMenu() {
		/*
		 * Create the menu within its components
		 */
		JMenuBar menuBar = new JMenuBar();

		/*
		 * File menu
		 */
		menuFile = new JMenu();
		menuFile.setText(_("File"));

		fileSelectModeItem = new JMenuItem(ActionRegistry.getInstance().get(
				OpenAssistantAction.class.getName()));

		menuFile.add(fileSelectModeItem);

		menuFile.addSeparator();

		fileNewReviewItem = new JMenuItem(ActionRegistry.getInstance().get(
				NewReviewAction.class.getName()));

		menuFile.add(fileNewReviewItem);

		fileOpenReviewItem = new JMenuItem(ActionRegistry.getInstance().get(
				LoadReviewAction.class.getName()));

		menuFile.add(fileOpenReviewItem);

		fileSaveReviewItem = new JMenuItem(ActionRegistry.getInstance().get(
				SaveReviewAction.class.getName()));

		menuFile.add(fileSaveReviewItem);

		fileSaveReviewAsItem = new JMenuItem(ActionRegistry.getInstance().get(
				SaveReviewAsAction.class.getName()));

		menuFile.add(fileSaveReviewAsItem);

		menuFile.addSeparator();

		closeApplicationItem = new JMenuItem(ActionRegistry.getInstance().get(
				ExitAction.class.getName()));

		menuFile.add(closeApplicationItem);

		menuBar.add(menuFile);

		/*
		 * Edit menu
		 */
		menuEdit = new JMenu();
		menuEdit.setText(_("Edit"));

		newMeetingItem = new JMenuItem(ActionRegistry.getInstance().get(
				AddMeetingAction.class.getName()));

		menuEdit.add(newMeetingItem);

		newAttendeeItem = new JMenuItem(ActionRegistry.getInstance().get(
				AddAttendeeAction.class.getName()));

		menuEdit.add(newAttendeeItem);

		aspectsManagerItem = new JMenuItem(ActionRegistry.getInstance().get(
				OpenAspectsManagerAction.class.getName()));

		menuEdit.add(aspectsManagerItem);

		manageSeveritiesItem = new JMenuItem(ActionRegistry.getInstance().get(
				ManageSeveritiesAction.class.getName()));

		menuEdit.add(manageSeveritiesItem);

		protocolModeItem = new JMenuItem(ActionRegistry.getInstance().get(
				OpenFindingsListAction.class.getName()));

		menuEdit.add(protocolModeItem);

		menuEdit.addSeparator();

		createInvitationsItem = new JMenuItem(ActionRegistry.getInstance().get(
				OpenInvitationsDialogAction.class.getName()));

		menuEdit.add(createInvitationsItem);

		pdfExportItem = new JMenuItem(ActionRegistry.getInstance().get(
				OpenExpPDFDialogAction.class.getName()));

		menuEdit.add(pdfExportItem);

		csvExportItem = new JMenuItem(ActionRegistry.getInstance().get(
				OpenExpCSVDialogAction.class.getName()));

		menuEdit.add(csvExportItem);

		menuBar.add(menuEdit);

		/*
		 * Extras menu
		 */
		menuSettings = new JMenu();
		menuSettings.setText(_("Settings"));

		appSettings = new JMenuItem();
		appSettings.setText(_("Application Settings"));
		appSettings.setIcon(Data.getInstance().getIcon(
				"menuAppSettings_16x16.png"));
		appSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UI.getInstance().getSettingsDialog().setVisible(true);
			}
		});

		menuSettings.add(appSettings);

		csvProfiles = new JMenuItem();
		csvProfiles.setText(_("CSV Profiles"));
		csvProfiles.setIcon(Data.getInstance().getIcon(
				"menuCsvSettings_16x16.png"));
		csvProfiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UI.getInstance().getCSVProfilesDialog().setVisible(true);
			}
		});

		menuSettings.add(csvProfiles);

		menuBar.add(menuSettings);

		/*
		 * Help menu
		 */
		menuHelp = new JMenu();
		menuHelp.setText(_("Help"));

		// TODO HELP IS CURRENTLY DISABLED!

		// openHelp = new JMenuItem();
		// openHelp.setText(_("Open Help Browser"));
		// openHelp.setIcon(Data.getInstance().getIcon("menuHelp_16x16.png"));
		// openHelp.addActionListener(ActionRegistry.getInstance().get(
		// OpenHelpAction.class.getName()));
		//
		// menuHelp.add(openHelp);

		aboutHelp = new JMenuItem();
		aboutHelp.setText(_("About RevAger"));
		aboutHelp.setIcon(Data.getInstance().getIcon("menuAbout_16x16.png"));
		aboutHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UI.getInstance().getAboutDialog().setVisible(true);
			}
		});

		menuHelp.add(aboutHelp);

		menuBar.add(menuHelp);

		setJMenuBar(menuBar);
	}

	/**
	 * Updates the menu.
	 */
	private void updateMenu() {
		boolean itemVisible = !assistantMode;

		// fileSelectModeItem.setEnabled(!itemVisible);

		// fileNewReviewItem.setEnabled(itemVisible);
		// fileOpenReviewItem.setEnabled(itemVisible);
		fileSaveReviewItem.setEnabled(itemVisible);
		fileSaveReviewAsItem.setEnabled(itemVisible);

		// menuEdit.setEnabled(itemVisible);
		// menuSettings.setEnabled(itemVisible);

		manageSeveritiesItem.setEnabled(itemVisible);
		newMeetingItem.setEnabled(itemVisible);

		// aspectsManagerItem.setEnabled(itemVisible);
		createInvitationsItem.setEnabled(itemVisible);
		newAttendeeItem.setEnabled(itemVisible);
		protocolModeItem.setEnabled(itemVisible);
		pdfExportItem.setEnabled(itemVisible);
		csvExportItem.setEnabled(itemVisible);
	}

	/**
	 * Updates the meetings tree.
	 */
	public void updateMeetingsTree() {
		MeetingManagement meetingMgmt = Application.getInstance()
				.getMeetingMgmt();
		ReviewManagement revMgmt = Application.getInstance().getReviewMgmt();

		/*
		 * Currently selected items
		 */
		Meeting selMeet = getSelectedMeeting();
		Protocol selProt = getSelectedProtocol();

		nodeMeetingRoot.removeAllChildren();

		for (int nodeCnt = 0; nodeCnt < revMgmt.getNumberOfMeetings(); nodeCnt++) {
			TreeMeeting treeMeet = new TreeMeeting();
			treeMeet.setMeeting(meetingMgmt.getMeetings().get(nodeCnt));
			DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(treeMeet);
			nodeMeetingRoot.add(dmtn);

			if (treeMeet.getMeeting().getProtocol() != null) {
				TreeProtocol treeProt = new TreeProtocol();
				treeProt.setMeeting(treeMeet.getMeeting());
				DefaultMutableTreeNode protocolNode = new DefaultMutableTreeNode(
						treeProt);

				dmtn.add(protocolNode);
			}
		}

		meetingsTreeModel.setRoot(nodeMeetingRoot);

		TreeTools.expandAll(meetingsTree, new TreePath(nodeMeetingRoot), true);

		/*
		 * Restore selection
		 */
		if (selMeet != null) {
			meetingsTree.setSelectionPath(getPath(selMeet));
		} else if (selProt != null) {
			meetingsTree.setSelectionPath(getPath(selProt));
		}

		updateButtons();
	}

	/**
	 * Update attendees table.
	 */
	public void updateAttendeesTable(boolean rememberSelection) {
		Attendee selAtt = getSelectedAttendee();

		attendeesTableModel.fireTableDataChanged();

		int i = 0;

		if (rememberSelection && selAtt != null) {
			for (Attendee att : Application.getInstance().getAttendeeMgmt()
					.getAttendees()) {
				if (att == selAtt) {
					attendeesTable.setRowSelectionInterval(i, i);
				}

				i++;
			}
		}
	}

	/**
	 * Update content pane.
	 */
	private void updateContentPane() {
		String productName = Application.getInstance().getReviewMgmt()
				.getProductName();
		String productVersion = Application.getInstance().getReviewMgmt()
				.getProductVersion();

		String product = productName;

		if (!productVersion.trim().equals("") && !productName.trim().equals("")) {
			product += " (" + _("Version:") + " " + productVersion + ")";
		}

		textProduct.setText(product);

		recommendationBx.setSelectedItem(Application.getInstance()
				.getReviewMgmt().getRecommendation());

		if (!textRevName.hasFocus()) {
			textRevName.setText(Application.getInstance().getReviewMgmt()
					.getReviewName());
		}

		if (!textRevDesc.hasFocus()) { // ODOT
			Rectangle visRect = scrollRevDesc.getViewport().getVisibleRect();
			textRevDesc.setText(Application.getInstance().getReviewMgmt()
					.getReviewDescription());
			scrollRevDesc.scrollRectToVisible(visRect);
		}

		if (!impressionTxtArea.hasFocus()) {
			Rectangle visRect = scrollImpression.getViewport().getVisibleRect();
			impressionTxtArea.setText(Application.getInstance().getReviewMgmt()
					.getImpression());
			impressionTxtArea.scrollRectToVisible(visRect);
		}
	}

	/**
	 * Creates the hints.
	 */
	private void createHints() {
		hintStart = new HintItem(
				_("First of all we recommend to specify the product and the review's title."),
				HintItem.WARNING);

		hintMeetAtt = new HintItem(
				_("You should create a meeting and add the attendees of the review by using the corresponding buttons."),
				HintItem.WARNING);

		hintOk = new HintItem(
				_("All required information for the review are present."),
				HintItem.OK);

		hintListOfFindings = new HintItem(
				_("You can open the list of findings by selecting the corresponding meeting and clicking the 'Open findings list' button."),
				HintItem.INFO);

		hintInfoAssistant = new HintItem(
				_("You can open the RevAger Assistant by clicking on the 'Open Assistant' button."),
				HintItem.INFO);
	}

	/**
	 * Update hints.
	 */
	private void updateHints() {
		boolean warningErrorHints = false;

		List<HintItem> hints = new ArrayList<HintItem>();

		unmarkAllComponents();

		if (!assistantMode) {
			if (revMgmt.getProductName().trim().equals("")
					|| revMgmt.getReviewName().trim().equals("")) {
				hints.add(hintStart);

				warningErrorHints = true;

				if (revMgmt.getProductName().trim().equals("")) {
					markComponent(textProduct);
				} else if (revMgmt.getReviewName().trim().equals("")) {
					markComponent(textRevName);
				}
			}

			if (revMgmt.getNumberOfMeetings() == 0
					|| revMgmt.getNumberOfAttendees() == 0) {
				hints.add(hintMeetAtt);

				warningErrorHints = true;

				if (revMgmt.getNumberOfMeetings() == 0) {
					markComponent(treeScrollPane);
				} else if (revMgmt.getNumberOfAttendees() == 0) {
					markComponent(tableScrollBar);
				}
			}

			if (!warningErrorHints) {
				hints.add(hintOk);
			}

			if (revMgmt.getNumberOfMeetings() > 0) {
				hints.add(hintListOfFindings);
			}
		} else {
			hints.add(hintInfoAssistant);
		}

		setHints(hints);
	}

	/**
	 * Instantiates a new main frame.
	 */
	public MainFrame() {
		super();

		getContentPane().setLayout(new BorderLayout());

		// setIcon(Data.getInstance().getIcon("RevAger_300x50.png"));

		createMenu();
		updateMenu();

		createToolBar();
		updateToolBar();

		createLeftPane();
		createRightPane();

		createHints();

		update(null, null);

		observeResiData(false);

		splitPanel.setBorder(new MatteBorder(0, 0, padding / 2, 0,
				getContentPane().getBackground()));
		add(splitPanel, BorderLayout.CENTER);

		addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				ActionRegistry.getInstance().get(ExitAction.class.getName())
						.actionPerformed(null);
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowOpened(WindowEvent e) {
			}
		});

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		pack();

		setLocationToCenter();

		setMinimumSize(new Dimension(950, 700));

		setExtendedState(Frame.MAXIMIZED_BOTH);

		setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable obs, Object obj) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateMenu();
				updateToolBar();

				if (Data.getInstance().getResiData().getReview() != null) {
					if (!assistantMode) {
						splitPanel.removeAll();

						updateLeftPane();
						updateRightPane();

						GUITools.executeSwingWorker(updateWorker);

						splitPanel.revalidate();
					}

					// updateMeetingsTree();
					meetingsTree.repaint();

					updateAttendeesTable(true);

					updateButtons();

					updateContentPane();

					if (Application.getInstance().getProtocolMgmt()
							.getProtocolsWithFindings().isEmpty()) {
						tbCsvExport.setEnabled(false);
						tbPdfExport.setEnabled(false);
						csvExportItem.setEnabled(false);
						pdfExportItem.setEnabled(false);
					} else {
						tbCsvExport.setEnabled(true);
						tbPdfExport.setEnabled(true);
						csvExportItem.setEnabled(true);
						pdfExportItem.setEnabled(true);
					}

					boolean attendeesEmpty = Application.getInstance()
							.getAttendeeMgmt().getAttendees().isEmpty();
					boolean meetingsEmpty = Application.getInstance()
							.getMeetingMgmt().getMeetings().isEmpty();

					if (!attendeesEmpty && !meetingsEmpty) {
						tbCreateInvitations.setEnabled(true);
						createInvitationsItem.setEnabled(true);
					} else {
						tbCreateInvitations.setEnabled(false);
						createInvitationsItem.setEnabled(false);
					}
				}

				updateHints();

				updateTitle();
			}
		});
	}

	/**
	 * Update resi data.
	 */
	public void updateResiData() {
		observeResiData(false);

		Application.getInstance().getReviewMgmt()
				.setReviewName(getTextRevName());

		Application.getInstance().getReviewMgmt()
				.setReviewDescription(getTextRevDesc());

		Application.getInstance().getReviewMgmt()
				.setImpression(impressionTxtArea.getText());

		Application.getInstance().getReviewMgmt()
				.setRecommendation((String) recommendationBx.getSelectedItem());

		observeResiData(true);
	}

	/**
	 * Update the title of the main frame.
	 */
	public void updateTitle() {
		if (observingResiData) {
			String title = "";

			if (assistantMode) {
				title = Data.getInstance().getResource("appName");
			} else {
				String revName = Data.getInstance().getResiData().getReview()
						.getName();

				if (UI.getInstance().getStatus() != Status.NO_FILE_LOADED) {
					if (revName != null) {
						if (revName.trim().equals("")) {
							title = _("New Review");
						} else {
							title = revName;
						}
					} else {
						title = _("New Review");
					}

					title += " - ";
				}

				title += Data.getInstance().getResource("appName");
			}

			if (UI.getInstance().getStatus() == UI.Status.UNSAVED_CHANGES) {
				if (UI.getInstance().getPlatform() != UI.Platform.MAC) {
					title += " *";
				}

				this.getRootPane().putClientProperty("Window.documentModified",
						Boolean.TRUE);
			} else {
				this.getRootPane().putClientProperty("Window.documentModified",
						Boolean.FALSE);
			}

			setTitle(title);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.revager.gui.AbstractFrame#switchToProgressMode()
	 */
	@Override
	public void switchToProgressMode() {
		super.switchToProgressMode();

		splitPanel.setVisible(false);

		observeResiData(false);

		updateLeftPane();
		updateRightPane();

		splitPanel.repaint();
		getContentPane().repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.revager.gui.AbstractFrame#switchToEditMode()
	 */
	@Override
	public void switchToEditMode() {
		super.switchToEditMode();

		observeResiData(true);

		updateLeftPane();
		updateRightPane();

		updateMeetingsTree();

		splitPanel.setVisible(true);

		splitPanel.revalidate();
		splitPanel.repaint();
		getContentPane().validate();
		getContentPane().repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.revager.gui.AbstractFrame#switchToClearMode()
	 */
	@Override
	public void switchToClearMode() {
		super.switchToClearMode();

		splitPanel.setVisible(false);
		splitPanel.removeAll();

		observeResiData(false);

		UI.getInstance().setStatus(Status.NO_FILE_LOADED);

		updateHints();
		updateTitle();

		splitPanel.repaint();
		getContentPane().repaint();
	}

	/**
	 * Observe resi data.
	 * 
	 * @param obs
	 *            the obs
	 */
	private void observeResiData(boolean obs) {
		observingResiData = obs;

		if (obs) {
			Data.getInstance().getResiData().addObserver(this);
			update(null, null);
		} else {
			Data.getInstance().getResiData().deleteObserver(this);
		}
	}

	/**
	 * Unmark all components.
	 */
	private void unmarkAllComponents() {
		textProduct.setBorder(UI.STANDARD_BORDER_INLINE);
		textRevName.setBorder(UI.STANDARD_BORDER_INLINE);
		scrollRevDesc.setBorder(UI.STANDARD_BORDER);
		treeScrollPane.setBorder(UI.STANDARD_BORDER);
		tableScrollBar.setBorder(UI.STANDARD_BORDER);
		scrollImpression.setBorder(UI.STANDARD_BORDER);
		recommendationBx.setBorder(new JComboBox().getBorder());

		componentMarked = false;
	}

	/**
	 * Mark component.
	 * 
	 * @param comp
	 *            the comp
	 */
	private void markComponent(JComponent comp) {
		if (!componentMarked) {
			if (comp instanceof JTextField) {
				comp.setBorder(UI.MARKED_BORDER_INLINE);
			} else {
				comp.setBorder(UI.MARKED_BORDER);
			}

			componentMarked = true;
		}
	}

	/**
	 * Update buttons.
	 */
	public void updateButtons() {
		if (getSelectedAttendee() != null) {
			removeAttendee.setEnabled(true);
			editAttendee.setEnabled(true);
		} else {
			removeAttendee.setEnabled(false);
			editAttendee.setEnabled(false);
		}

		if (getSelectedMeeting() != null) {
			commentMeeting.setEnabled(true);
			editMeeting.setEnabled(true);
			removeMeeting.setEnabled(true);

			protocolModeItem.setEnabled(true);
			tbProtocolMode.setEnabled(true);
			editProtocol.setEnabled(true);

			removeMeeting.setToolTipText(_("Remove meeting"));
		} else if (getSelectedProtocol() != null) {
			commentMeeting.setEnabled(false);
			editMeeting.setEnabled(false);
			removeMeeting.setEnabled(true);

			protocolModeItem.setEnabled(true);
			tbProtocolMode.setEnabled(true);
			editProtocol.setEnabled(true);

			removeMeeting.setToolTipText(_("Remove list of findings"));
		} else {
			commentMeeting.setEnabled(false);
			editMeeting.setEnabled(false);
			removeMeeting.setEnabled(false);

			protocolModeItem.setEnabled(false);
			tbProtocolMode.setEnabled(false);
			editProtocol.setEnabled(false);

			removeMeeting.setToolTipText(_("Remove"));
		}
	}

	/**
	 * Gets the selected meeting.
	 * 
	 * @return the selected meeting
	 */
	public Meeting getSelectedMeeting() {
		if (meetingsTree.getSelectionPath() != null) {
			Object obj = ((DefaultMutableTreeNode) meetingsTree
					.getSelectionPath().getLastPathComponent()).getUserObject();

			if (obj instanceof TreeMeeting) {
				return ((TreeMeeting) obj).getMeeting();
			}
		}

		return null;
	}

	/**
	 * Gets the selected protocol.
	 * 
	 * @return the selected protocol
	 */
	public Protocol getSelectedProtocol() {
		if (meetingsTree.getSelectionPath() != null) {
			Object obj = ((DefaultMutableTreeNode) meetingsTree
					.getSelectionPath().getLastPathComponent()).getUserObject();

			if (obj instanceof TreeProtocol) {
				return ((TreeProtocol) obj).getMeeting().getProtocol();
			}
		}

		return null;
	}

	/**
	 * Gets the selected attendee.
	 * 
	 * @return the selected attendee
	 */
	public Attendee getSelectedAttendee() {
		int selRow = attendeesTable.getSelectedRow();

		if (selRow != -1
				&& selRow < Application.getInstance().getReviewMgmt()
						.getNumberOfAttendees()) {
			return Application.getInstance().getAttendeeMgmt().getAttendees()
					.get(selRow);
		}

		return null;
	}

	/**
	 * Gets the path.
	 * 
	 * @param meet
	 *            the meet
	 * 
	 * @return the path
	 */
	private TreePath getPath(Meeting meet) {
		DefaultMutableTreeNode node = nodeMeetingRoot;

		while (node.getNextNode() != null) {
			node = (DefaultMutableTreeNode) node.getNextNode();

			Object curNode = node.getUserObject();

			if (curNode instanceof TreeMeeting
					&& ((TreeMeeting) curNode).getMeeting().equals(meet)) {
				return new TreePath(node.getPath());
			}
		}

		return null;
	}

	/**
	 * Gets the path.
	 * 
	 * @param prot
	 *            the prot
	 * 
	 * @return the path
	 */
	private TreePath getPath(Protocol prot) {
		DefaultMutableTreeNode node = nodeMeetingRoot;

		while (node.getNextNode() != null) {
			node = (DefaultMutableTreeNode) node.getNextNode();

			Object curNode = node.getUserObject();

			if (curNode instanceof TreeProtocol
					&& ((TreeProtocol) curNode).getMeeting().getProtocol()
							.equals(prot)) {
				return new TreePath(node.getPath());
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Window#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean vis) {
		if (vis) {
			UI.getInstance().getAutoBackupWorker().addObserverFrame(this);
			UI.getInstance().getAutoSaveWorker().addObserverFrame(this);
		} else {
			UI.getInstance().getAutoBackupWorker().removeObserverFrame(this);
			UI.getInstance().getAutoSaveWorker().removeObserverFrame(this);
		}

		super.setVisible(vis);
	}

	/**
	 * @return the assistantMode
	 */
	public boolean isAssistantMode() {
		return assistantMode;
	}

	/**
	 * @param assistantMode
	 *            the assistantMode to set
	 */
	public void setAssistantMode(boolean assistantMode) {
		this.assistantMode = assistantMode;

		if (assistantMode) {
			updateHints();
			updateToolBar();
			updateMenu();
		}
	}

}
