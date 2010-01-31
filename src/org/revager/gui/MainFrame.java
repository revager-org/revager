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

import javax.swing.ImageIcon;
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
import org.revager.gui.UI.Status;
import org.revager.gui.actions.ActionRegistry;
import org.revager.gui.actions.ExitAction;
import org.revager.gui.actions.LoadReviewAction;
import org.revager.gui.actions.ManageSeveritiesAction;
import org.revager.gui.actions.NewReviewAction;
import org.revager.gui.actions.OpenAspectsManagerAction;
import org.revager.gui.actions.OpenExpCSVDialogAction;
import org.revager.gui.actions.OpenExpPDFDialogAction;
import org.revager.gui.actions.OpenInvitationsDialogAction;
import org.revager.gui.actions.OpenProtocolFrameAction;
import org.revager.gui.actions.SaveReviewAction;
import org.revager.gui.actions.SaveReviewAsAction;
import org.revager.gui.actions.SelectModeAction;
import org.revager.gui.actions.attendee.AddAttendeeAction;
import org.revager.gui.actions.attendee.EditAttendeeAction;
import org.revager.gui.actions.attendee.RemoveAttendeeAction;
import org.revager.gui.actions.help.OpenHelpAction;
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

	private ReviewManagement revMgmt = Application.getInstance()
			.getReviewMgmt();

	private JPanel splitPanel = new JPanel(new GridLayout(1, 2));
	private GridBagLayout gbl = new GridBagLayout();

	private AttendeeTableModel attendeesTableModel = new AttendeeTableModel();
	private JTable attendeesTable;

	private JButton removeAttendee;
	private JButton editAttendee;

	private DefaultMutableTreeNode nodeMeetingRoot = new DefaultMutableTreeNode(
			Data.getInstance().getLocaleStr("mainFrame.meetings.tree.node"));
	private DefaultTreeModel meetingsTreeModel = new DefaultTreeModel(
			nodeMeetingRoot);
	private JTree meetingsTree;

	private String mode = Data.getInstance().getMode();

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
	private JButton tbBlank;
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
	private JLabel reviewComment;

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
	private HintItem hintProduct;
	private HintItem hintRevName;
	private HintItem hintRevDesc;
	private HintItem hintMeet;
	private HintItem hintAtt;
	private HintItem hintAsp;
	private HintItem hintImpr;
	private HintItem hintRec;
	private HintItem hintOk;
	private HintItem hintRev;
	private HintItem hintRevConf;
	private HintItem hintInfoNewMeeting;
	private HintItem hintInfoProtocol;
	private HintItem hintInfoNewAttendee;
	private HintItem hintInfoAssistant;

	/*
	 * Content panes
	 */
	private JTextField textProduct = new JTextField();
	private JTextField textRevName = new JTextField();
	private JTextArea textRevDesc = new JTextArea();
	private JScrollPane scrollRevDesc;
	private JScrollPane scrollRevComm;
	private JTextArea textRevComm = new JTextArea();
	private JScrollPane treeScrollPane;
	private JScrollPane tableScrollBar;
	private JButton editAspects = new JButton();
	private JTextArea impressionTxtArea = new JTextArea();
	private JScrollPane scrollImpression;
	private JComboBox recommendationBx = new JComboBox();
	private JButton clearRec;

	private long updateTime = System.currentTimeMillis();
	private MouseListener productMouseListener = new MouseListener() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (revMgmt.getRecommendation().trim().equals("")) {
				UI.getInstance().getEditProductDialog().setVisible(true);
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
	};

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
						updateResiData();

						updateHints();
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
	 * Gets the review comment.
	 * 
	 * @return the review comment
	 */
	public String getTextRevComm() {
		return textRevComm.getText();
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
		product = new JLabel(Data.getInstance().getLocaleStr(
				"mainFrame.product"));

		textProduct.setFocusable(false);

		reviewName = new JLabel(Data.getInstance().getLocaleStr(
				"mainFrame.review.name"));
		reviewName.setBorder(labelBorder);
		textRevName.addKeyListener(updKeyListener);

		reviewDescription = new JLabel(Data.getInstance().getLocaleStr(
				"mainFrame.review.description"));
		reviewDescription.setBorder(labelBorder);
		textRevDesc.addKeyListener(updKeyListener);

		scrollRevDesc = GUITools.setIntoScrllPn(textRevDesc);
		scrollRevComm = GUITools.setIntoScrllPn(textRevComm);

		reviewComment = new JLabel(Data.getInstance().getLocaleStr(
				"mainFrame.review.comments"));
		reviewComment.setBorder(labelBorder);
		textRevComm.addKeyListener(updKeyListener);
		textRevComm.setRows(5);

		scrollRevComm.setMinimumSize(scrollRevComm.getPreferredSize());
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
		GUITools.addComponent(leftPanel, gbl, reviewComment, 0, 7, 2, 1, 1.0,
				0.0, 10, 20, 0, padding, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(leftPanel, gbl, scrollRevComm, 0, 8, 2, 2, 1.0,
				0.0, 10, 20, 0, padding, GridBagConstraints.BOTH,
				GridBagConstraints.NORTH);

		for (MouseListener ml : textProduct.getMouseListeners()) {
			textProduct.removeMouseListener(ml);
		}

		if (Data.getInstance().getModeParam("ableToEditProduct")) {
			textProduct.setCursor(Cursor
					.getPredefinedCursor(Cursor.HAND_CURSOR));
			textProduct.addMouseListener(productMouseListener);
		} else {
			textProduct.setCursor(Cursor
					.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			textProduct.setForeground(Color.GRAY);
		}

		if (!Data.getInstance().getModeParam("ableToEditReviewName")) {
			textRevName.setFocusable(false);
			textRevName.setForeground(Color.GRAY);
		}

		if (!Data.getInstance().getModeParam("ableToEditReviewDescription")) {
			textRevDesc.setFocusable(false);
			textRevDesc.setForeground(Color.GRAY);
		}

		if (!Data.getInstance().getModeParam("ableToEditReviewComment")) {
			textRevComm.setFocusable(false);
			textRevComm.setForeground(Color.GRAY);
		}
	}

	/**
	 * Creates the right pane.
	 */
	private void createRightPane() {
		/*
		 * creating the right panel
		 */
		rightPanel.setBorder(new MatteBorder(0, 1, 0, 0, UI.SEPARATOR_COLOR));

		meetings = new JLabel(Data.getInstance().getLocaleStr(
				"mainFrame.meetings"));

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
						ActionRegistry.getInstance().get(
								EditMeetingAction.class.getName())
								.actionPerformed(null);
					} else if (getSelectedProtocol() != null
							&& Data.getInstance().getModeParam(
									"ableToUseProtocolMode")) {
						ActionRegistry.getInstance().get(
								OpenProtocolFrameAction.class.getName())
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

		addMeeting = GUITools.newImageButton(Data.getInstance().getIcon(
				"add_25x25_0.png"),
				Data.getInstance().getIcon("add_25x25.png"), ActionRegistry
						.getInstance().get(AddMeetingAction.class.getName()));
		meetingButtons.add(addMeeting);

		removeMeeting = GUITools.newImageButton();
		removeMeeting.setIcon(Data.getInstance().getIcon("remove_25x25_0.png"));
		removeMeeting.setRolloverIcon(Data.getInstance().getIcon(
				"remove_25x25.png"));
		removeMeeting.setToolTipText(Data.getInstance().getLocaleStr(
				"button.remove"));
		removeMeeting.addActionListener(ActionRegistry.getInstance().get(
				RemoveMeetingAction.class.getName()));
		meetingButtons.add(removeMeeting);

		editMeeting = GUITools.newImageButton();
		editMeeting.setIcon(Data.getInstance().getIcon("edit_25x25_0.png"));
		editMeeting.setRolloverIcon(Data.getInstance()
				.getIcon("edit_25x25.png"));
		editMeeting.setToolTipText(Data.getInstance().getLocaleStr(
				"meeting.edit"));
		editMeeting.addActionListener(ActionRegistry.getInstance().get(
				EditMeetingAction.class.getName()));
		
		meetingButtons.add(editMeeting);

		commentMeeting = GUITools.newImageButton();
		commentMeeting.setIcon(Data.getInstance()
				.getIcon("comment_25x25_0.png"));
		commentMeeting.setRolloverIcon(Data.getInstance().getIcon(
				"comment_25x25.png"));
		commentMeeting.setToolTipText(Data.getInstance().getLocaleStr(
				"meeting.comment"));
		commentMeeting.addActionListener(ActionRegistry.getInstance().get(
				CommentMeetingAction.class.getName()));
		meetingButtons.add(commentMeeting);

		editProtocol = GUITools.newImageButton();
		editProtocol.setIcon(Data.getInstance().getIcon("protocolFrame_25x25_0.png"));
		editProtocol.setRolloverIcon(Data.getInstance()
				.getIcon("protocolFrame_25x25.png"));
		editProtocol.setToolTipText(Data.getInstance().getLocaleStr(
				"mainFrame.protocolMode"));
		editProtocol.addActionListener(ActionRegistry.getInstance().get(
				OpenProtocolFrameAction.class.getName()));
		
		meetingButtons.add(editProtocol);
		
		commentMeeting.setEnabled(false);
		editMeeting.setEnabled(false);
		removeMeeting.setEnabled(false);
		editProtocol.setEnabled(false);
		
		attendees = new JLabel(Data.getInstance().getLocaleStr(
				"mainFrame.meetings.attendees"));
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
					ActionRegistry.getInstance().get(
							EditAttendeeAction.class.getName())
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

		addAttendee = GUITools.newImageButton(Data.getInstance().getIcon(
				"addAttendee_25x25_0.png"), Data.getInstance().getIcon(
				"addAttendee_25x25.png"), ActionRegistry.getInstance().get(
				AddAttendeeAction.class.getName()));
		attendeeButtons.add(addAttendee);

		removeAttendee = GUITools.newImageButton();
		removeAttendee.setIcon(Data.getInstance().getIcon(
				"removeAttendee_25x25_0.png"));
		removeAttendee.setRolloverIcon(Data.getInstance().getIcon(
				"removeAttendee_25x25.png"));
		removeAttendee.setToolTipText(Data.getInstance().getLocaleStr(
				"attendee.remove"));
		removeAttendee.addActionListener(ActionRegistry.getInstance().get(
				RemoveAttendeeAction.class.getName()));
		attendeeButtons.add(removeAttendee);

		editAttendee = GUITools.newImageButton();
		editAttendee.setIcon(Data.getInstance().getIcon(
				"editAttendee_25x25_0.png"));
		editAttendee.setRolloverIcon(Data.getInstance().getIcon(
				"editAttendee_25x25.png"));
		editAttendee.setToolTipText(Data.getInstance().getLocaleStr(
				"attendee.edit"));
		editAttendee.addActionListener(ActionRegistry.getInstance().get(
				EditAttendeeAction.class.getName()));
		attendeeButtons.add(editAttendee);

		editAspects.setAction(ActionRegistry.getInstance().get(
				OpenAspectsManagerAction.class.getName()));

		removeAttendee.setEnabled(false);
		editAttendee.setEnabled(false);

		generalImpression = new JLabel(Data.getInstance().getLocaleStr(
				"mainFrame.impression"));
		generalImpression.setBorder(labelBorder);

		scrollImpression = GUITools.setIntoScrllPn(impressionTxtArea);
		impressionTxtArea.addKeyListener(updKeyListener);

		recommendation = new JLabel(Data.getInstance().getLocaleStr(
				"mainFrame.recommendation"));
		recommendation.setBorder(labelBorder);
		recommendationBx.setEditable(true);
		for (String rec : Data.getInstance()
				.getLocaleStr("standardImpressions").split(",")) {
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
		recommendationBx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED
						&& !recommendationBx.getSelectedItem().toString()
								.trim().equals("")) {

					setEnabledAllComp(false);
				} else {
					setEnabledAllComp(true);
				}
			}
		});

		ImageIcon clearRecIcon = Data.getInstance()
				.getIcon("clear_22x22_0.png");
		ImageIcon clearRecRollOverIcon = Data.getInstance().getIcon(
				"clear_22x22.png");

		clearRec = GUITools.newImageButton(clearRecIcon, clearRecRollOverIcon);
		clearRec.setToolTipText(Data.getInstance().getLocaleStr(
				"mainFrame.clearRec"));
		clearRec.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				recommendationBx.setSelectedItem(null);
				revMgmt.setRecommendation("");
			}
		});
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

		if (Data.getInstance().getModeParam("ableToManageAttendees")) {
			GUITools.addComponent(rightPanel, gbl, attendees, 0, 3, 1, 1, 0, 0,
					10, padding, 0, 20, GridBagConstraints.NONE,
					GridBagConstraints.NORTHWEST);
			GUITools.addComponent(rightPanel, gbl, tableScrollBar, 0, 4, 2, 2,
					1.0, 1.0, 10, padding, 0, 20, GridBagConstraints.BOTH,
					GridBagConstraints.NORTH);
			GUITools.addComponent(rightPanel, gbl, attendeeButtons, 2, 4, 1, 2,
					0, 1.0, 10, 0, 0, padding / 2, GridBagConstraints.NONE,
					GridBagConstraints.NORTH);
			GUITools.addComponent(rightPanel, gbl, editAspects, 1, 6, 1, 2, 0,
					0, 10, 0, 0, 20, GridBagConstraints.NONE,
					GridBagConstraints.NORTHEAST);
		}

		if (Data.getInstance().getModeParam("ableToEditImpression")) {
			GUITools.addComponent(rightPanel, gbl, generalImpression, 0, 4, 1,
					1, 0, 0, 10, padding, 0, 20, GridBagConstraints.NONE,
					GridBagConstraints.NORTHWEST);
			GUITools.addComponent(rightPanel, gbl, scrollImpression, 0, 5, 2,
					2, 1.0, 1.0, 10, padding, 0, 20, GridBagConstraints.BOTH,
					GridBagConstraints.NORTH);
		}

		if (Data.getInstance().getModeParam("ableToEditRecommendation")) {
			GUITools.addComponent(rightPanel, gbl, recommendation, 0, 7, 1, 1,
					0, 0, 10, padding, 0, 20, GridBagConstraints.NONE,
					GridBagConstraints.NORTHWEST);
			GUITools.addComponent(rightPanel, gbl, recommendationBx, 0, 8, 1,
					1, 1.0, 0, 10, padding, 0, 20,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);
			GUITools.addComponent(rightPanel, gbl, clearRec, 2, 8, 1, 1, 0, 0,
					10, 0, 0, 20, GridBagConstraints.HORIZONTAL,
					GridBagConstraints.NORTH);
		}

	}

	/**
	 * Creates the tool bar.
	 */
	private void createToolBar() {
		/*
		 * Create the toolbar and its components
		 */
		tbNewReview = GUITools.newImageButton(Data.getInstance().getIcon(
				"new_50x50_0.png"),
				Data.getInstance().getIcon("new_50x50.png"), ActionRegistry
						.getInstance().get(NewReviewAction.class.getName()));

		addTopComponent(tbNewReview);

		tbOpenReview = GUITools.newImageButton(Data.getInstance().getIcon(
				"open_50x50_0.png"), Data.getInstance().getIcon(
				"open_50x50.png"), ActionRegistry.getInstance().get(
				LoadReviewAction.class.getName()));

		addTopComponent(tbOpenReview);

		tbSaveReview = GUITools.newImageButton(Data.getInstance().getIcon(
				"save_50x50_0.png"), Data.getInstance().getIcon(
				"save_50x50.png"), ActionRegistry.getInstance().get(
				SaveReviewAction.class.getName()));

		addTopComponent(tbSaveReview);

		tbShowAssistant = GUITools.newImageButton(Data.getInstance().getIcon(
				"tbShowAssistant_50x50_0.png"), Data.getInstance().getIcon(
				"tbShowAssistant_50x50.png"));
		tbShowAssistant.setToolTipText(Data.getInstance().getLocaleStr(
				"menu.showAssistant"));
		tbShowAssistant.addActionListener(ActionRegistry.getInstance().get(
				SelectModeAction.class.getName()));

		addTopComponent(tbShowAssistant);

		tbShowHelp = GUITools.newImageButton(Data.getInstance().getIcon(
				"tbShowHelp_50x50_0.png"), Data.getInstance().getIcon(
				"tbShowHelp_50x50.png"));
		tbShowHelp.setToolTipText(Data.getInstance().getLocaleStr(
				"menu.showHelp"));
		tbShowHelp.addActionListener(ActionRegistry.getInstance().get(
				OpenHelpAction.class.getName()));

		addTopComponent(tbShowHelp);

		tbManageSeverities = GUITools.newImageButton(Data.getInstance()
				.getIcon("severities_50x50_0.png"), Data.getInstance().getIcon(
				"severities_50x50.png"), ActionRegistry.getInstance().get(
				ManageSeveritiesAction.class.getName()));

		addTopComponent(tbManageSeverities);

		tbAspectsManager = GUITools.newImageButton(Data.getInstance().getIcon(
				"aspectsManager_50x50_0.png"), Data.getInstance().getIcon(
				"aspectsManager_50x50.png"), ActionRegistry.getInstance().get(
				OpenAspectsManagerAction.class.getName()));

		addTopComponent(tbAspectsManager);

		tbCreateInvitations = GUITools
				.newImageButton(Data.getInstance().getIcon(
						"createInvitations_50x50_0.png"), Data.getInstance()
						.getIcon("createInvitations_50x50.png"), ActionRegistry
						.getInstance().get(
								OpenInvitationsDialogAction.class.getName()));

		addTopComponent(tbCreateInvitations);

		tbNewAttendee = GUITools.newImageButton(Data.getInstance().getIcon(
				"addAttendee_50x50_0.png"), Data.getInstance().getIcon(
				"addAttendee_50x50.png"), ActionRegistry.getInstance().get(
				AddAttendeeAction.class.getName()));

		addTopComponent(tbNewAttendee);
		
		tbNewMeeting = GUITools.newImageButton(Data.getInstance().getIcon(
		"addMeeting_50x50_0.png"), Data.getInstance().getIcon(
		"addMeeting_50x50.png"), ActionRegistry.getInstance().get(
		AddMeetingAction.class.getName()));

		addTopComponent(tbNewMeeting);

		tbProtocolMode = GUITools.newImageButton(Data.getInstance().getIcon(
				"protocolFrame_50x50_0.png"), Data.getInstance().getIcon(
				"protocolFrame_50x50.png"), ActionRegistry.getInstance().get(
				OpenProtocolFrameAction.class.getName()));

		addTopComponent(tbProtocolMode);

		tbPdfExport = GUITools.newImageButton(Data.getInstance().getIcon(
				"PDFExport_50x50_0.png"), Data.getInstance().getIcon(
				"PDFExport_50x50.png"), ActionRegistry.getInstance().get(
				OpenExpPDFDialogAction.class.getName()));

		addTopComponent(tbPdfExport);

		tbCsvExport = GUITools.newImageButton(Data.getInstance().getIcon(
				"CSVExport_50x50_0.png"), Data.getInstance().getIcon(
				"CSVExport_50x50.png"), ActionRegistry.getInstance().get(
				OpenExpCSVDialogAction.class.getName()));

		addTopComponent(tbCsvExport);

		tbBlank = GUITools.newImageButton(Data.getInstance().getIcon(
				"blank_50x50.png"), Data.getInstance().getIcon(
				"blank_50x50.png"));
		tbBlank.setEnabled(false);

		addTopComponent(tbBlank);
	}

	/**
	 * Updates tool bar.
	 */
	private void updateToolBar() {
		/*
		 * Enable and disable toolbar items by mode parameters
		 */
		if (Data.getInstance().getMode().equals("default")) {
			tbShowAssistant.setVisible(true);
			tbOpenReview.setVisible(false);
			tbShowHelp.setVisible(true);
			tbManageSeverities.setVisible(false);
			tbSaveReview.setVisible(false);
			tbNewReview.setVisible(false);
			tbAspectsManager.setVisible(false);
			tbCreateInvitations.setVisible(false);
			tbNewAttendee.setVisible(false);
			tbProtocolMode.setVisible(false);
			tbPdfExport.setVisible(false);
			tbCsvExport.setVisible(false);
			tbNewMeeting.setVisible(false);
		} else {
			tbShowAssistant.setVisible(false);
			tbShowHelp.setVisible(false);

			tbOpenReview.setVisible(Data.getInstance().getModeParam(
					"ableToOpenReview"));
			tbNewReview.setVisible(Data.getInstance().getModeParam(
					"ableToCreateNewReview"));
			tbSaveReview.setVisible(Data.getInstance().getModeParam(
					"ableToSaveReview"));
			tbManageSeverities.setVisible(Data.getInstance().getModeParam(
					"ableToManageSeverities"));
			tbAspectsManager.setVisible(Data.getInstance().getModeParam(
					"ableToUseAspectsManager"));
			tbCreateInvitations.setVisible(Data.getInstance().getModeParam(
					"ableToCreateInvitations"));
			tbNewAttendee.setVisible(Data.getInstance().getModeParam(
					"ableToManageAttendees"));
			tbProtocolMode.setVisible(Data.getInstance().getModeParam(
					"ableToUseProtocolMode"));
			tbPdfExport.setVisible(Data.getInstance().getModeParam(
					"ableToExportProtocolPDF"));
			tbCsvExport.setVisible(Data.getInstance().getModeParam(
					"ableToExportFindingsCSV"));
			tbNewMeeting.setVisible(Data.getInstance().getModeParam(
					"ableToManageMeetings"));
		}
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
		menuFile.setText(Data.getInstance().getLocaleStr("menu.file"));

		fileSelectModeItem = new JMenuItem(ActionRegistry.getInstance().get(
				SelectModeAction.class.getName()));

		menuFile.add(fileSelectModeItem);

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
		menuEdit.setText(Data.getInstance().getLocaleStr("menu.edit"));

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
				OpenProtocolFrameAction.class.getName()));

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
		menuSettings.setText(Data.getInstance().getLocaleStr("menu.settings"));

		appSettings = new JMenuItem();
		appSettings
				.setText(Data.getInstance().getLocaleStr("menu.appSettings"));
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
		csvProfiles
				.setText(Data.getInstance().getLocaleStr("menu.csvProfiles"));
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
		menuHelp.setText(Data.getInstance().getLocaleStr("menu.help"));

		openHelp = new JMenuItem();
		openHelp.setText(Data.getInstance().getLocaleStr("menu.help.openHelp"));
		openHelp.setIcon(Data.getInstance().getIcon("menuHelp_16x16.png"));
		openHelp.addActionListener(ActionRegistry.getInstance().get(
				OpenHelpAction.class.getName()));

		menuHelp.add(openHelp);

		aboutHelp = new JMenuItem();
		aboutHelp
				.setText(Data.getInstance().getLocaleStr("menu.help.infoHelp"));
		aboutHelp.setIcon(Data.getInstance().getIcon("menuAbout_16x16.png"));
		aboutHelp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
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
		boolean itemVisible;

		if (Data.getInstance().getMode().equals("default")) {
			itemVisible = false;
		} else {
			itemVisible = true;
		}

		/*
		 * If no mode is selected, disable the following menu items
		 */
		fileNewReviewItem.setVisible(itemVisible);
		fileOpenReviewItem.setVisible(itemVisible);
		fileSaveReviewItem.setVisible(itemVisible);
		fileSaveReviewAsItem.setVisible(itemVisible);

		menuEdit.setVisible(itemVisible);
		menuSettings.setVisible(itemVisible);

		/*
		 * Enable and disable menu items by mode parameters
		 */
		if (!Data.getInstance().getMode().equals("default")) {
			fileSelectModeItem.setVisible(Data.getInstance().getModeParam(
					"ableToSelectMode"));
			fileNewReviewItem.setVisible(Data.getInstance().getModeParam(
					"ableToCreateNewReview"));
			aspectsManagerItem.setVisible(Data.getInstance().getModeParam(
					"ableToUseAspectsManager"));
			createInvitationsItem.setVisible(Data.getInstance().getModeParam(
					"ableToCreateInvitations"));
			newAttendeeItem.setVisible(Data.getInstance().getModeParam(
					"ableToManageAttendees"));
			protocolModeItem.setVisible(Data.getInstance().getModeParam(
					"ableToUseProtocolMode"));
			pdfExportItem.setVisible(Data.getInstance().getModeParam(
					"ableToExportProtocolPDF"));
			csvExportItem.setVisible(Data.getInstance().getModeParam(
					"ableToExportFindingsCSV"));
		}
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
			product += " ("
					+ Data.getInstance().getLocaleStr("mainFrame.version")
					+ ": " + productVersion + ")";
		}

		textProduct.setText(product);

		recommendationBx.setSelectedItem(Application.getInstance()
				.getReviewMgmt().getRecommendation());
		if (revMgmt.isReviewConfirmable()) {
			recommendationBx.setEnabled(true);
		} else {
			recommendationBx.setEnabled(false);

		}
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

		if (!textRevComm.hasFocus()) { // ODOT
			Rectangle visRect = scrollRevComm.getViewport().getVisibleRect();
			textRevComm.setText(Application.getInstance().getReviewMgmt()
					.getReviewComments());
			scrollRevComm.scrollRectToVisible(visRect);
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
		hintProduct = new HintItem(Data.getInstance().getLocaleStr(
				"mainFrame.hintProduct"), HintItem.WARNING,
				"review_management", "7");

		hintRevName = new HintItem(Data.getInstance().getLocaleStr(
				"mainFrame.hintRevName"), HintItem.WARNING,
				"review_management", "4");

		hintRevDesc = new HintItem(Data.getInstance().getLocaleStr(
				"mainFrame.hintRevDesc"), HintItem.WARNING,
				"review_management", "5");

		hintMeet = new HintItem(Data.getInstance().getLocaleStr(
				"mainFrame.hintMeet"), HintItem.WARNING, "meetings_management",
				"1");

		hintAtt = new HintItem(Data.getInstance().getLocaleStr(
				"mainFrame.hintAtt"), HintItem.WARNING, "attendees_management",
				"1");

		hintAsp = new HintItem(Data.getInstance().getLocaleStr(
				"mainFrame.hintAsp"), HintItem.WARNING, "aspects_management",
				"1");

		hintRevConf = new HintItem(Data.getInstance().getLocaleStr(
				"mainFrame.hintRevConf"), HintItem.WARNING);

		hintImpr = new HintItem(Data.getInstance().getLocaleStr(
				"mainFrame.hintImpr"), HintItem.WARNING, "protocol", "4");

		hintRec = new HintItem(Data.getInstance().getLocaleStr(
				"mainFrame.hintRec"), HintItem.WARNING, "protocol", "5");

		hintOk = new HintItem(Data.getInstance().getLocaleStr(
				"mainFrame.hintOk"), HintItem.OK);

		hintRev = new HintItem(Data.getInstance().getLocaleStr(
				"mainFrame.hintRev"), HintItem.OK);

		hintInfoNewMeeting = new HintItem(Data.getInstance().getLocaleStr(
				"mainFrame.hintInfoNewMeeting"), HintItem.INFO,
				"meetings_management", "1");

		hintInfoProtocol = new HintItem(Data.getInstance().getLocaleStr(
				"mainFrame.hintInfoProtocol"), HintItem.INFO);

		hintInfoNewAttendee = new HintItem(Data.getInstance().getLocaleStr(
				"mainFrame.hintInfoNewAttendee"), HintItem.INFO,
				"attendees_management", "1");

		hintInfoAssistant = new HintItem(Data.getInstance().getLocaleStr(
				"mainFrame.hintInfoAssistant"), HintItem.INFO);
	}

	/**
	 * Update hints.
	 */
	private void updateHints() {
		boolean warningErrorHints = false;

		List<HintItem> hints = new ArrayList<HintItem>();

		unmarkAllComponents();

		/*
		 * Current mode params
		 */
		boolean ableToEditReviewName = Data.getInstance().getModeParam(
				"ableToEditReviewName");
		boolean ableToEditReviewDescription = Data.getInstance().getModeParam(
				"ableToEditReviewDescription");
		boolean ableToEditProduct = Data.getInstance().getModeParam(
				"ableToEditProduct");
		boolean ableToManageMeetings = Data.getInstance().getModeParam(
				"ableToManageMeetings");
		boolean ableToManageAttendees = Data.getInstance().getModeParam(
				"ableToManageAttendees");
		boolean ableToUseAspectsManager = Data.getInstance().getModeParam(
				"ableToUseAspectsManager");
		boolean ableToEditImpression = Data.getInstance().getModeParam(
				"ableToEditImpression");
		boolean ableToEditRecommendation = Data.getInstance().getModeParam(
				"ableToEditRecommendation");
		boolean ableToUseProtocolMode = Data.getInstance().getModeParam(
				"ableToUseProtocolMode");

		if (!mode.equals("default")) {
			if (((JTextField) recommendationBx.getEditor().getEditorComponent())
					.getText().equals("")) {
				if (ableToEditProduct
						&& (revMgmt.getProductName().trim().equals("")
								|| revMgmt.getProductVersion().trim()
										.equals("") || revMgmt
								.getNumberOfProdRefs() == 0)) {
					hints.add(hintProduct);

					warningErrorHints = true;

					markComponent(textProduct);
				}

				if (revMgmt.getReviewName().trim().equals("")
						&& ableToEditReviewName) {
					hints.add(hintRevName);

					warningErrorHints = true;

					markComponent(textRevName);
				}

				if (revMgmt.getReviewDescription().trim().equals("")
						&& ableToEditReviewDescription) {
					hints.add(hintRevDesc);

					warningErrorHints = true;

					markComponent(scrollRevDesc);
				}

				if (revMgmt.getNumberOfMeetings() == 0 && ableToManageMeetings) {
					hints.add(hintMeet);

					warningErrorHints = true;

					markComponent(treeScrollPane);
				}

				if (revMgmt.getNumberOfAttendees() == 0
						&& ableToManageAttendees) {
					hints.add(hintAtt);

					warningErrorHints = true;

					markComponent(tableScrollBar);
				}

				if (revMgmt.getNumberOfAspects() == 0
						&& ableToUseAspectsManager) {
					hints.add(hintAsp);

					warningErrorHints = true;
				}

				if (revMgmt.getImpression().trim().equals("")
						&& ableToEditImpression) {
					hints.add(hintImpr);

					warningErrorHints = true;

					markComponent(scrollImpression);
				}
			}

			if (!revMgmt.isReviewConfirmable() && ableToEditRecommendation) {
				hints.add(hintRevConf);

				warningErrorHints = true;

				markComponent(treeScrollPane);
			} else if (revMgmt.getRecommendation().trim().equals("")
					&& ableToEditRecommendation) {
				hints.add(hintRec);

				warningErrorHints = true;

				markComponent(recommendationBx);
			}

			if (!revMgmt.getRecommendation().trim().equals("")
					&& ableToEditRecommendation) {
				hints.add(hintRev);

				warningErrorHints = true;
			}

			if (!warningErrorHints) {
				hints.add(hintOk);
			}

			if (ableToUseProtocolMode) {
				hints.add(hintInfoProtocol);
			}

			if (ableToManageMeetings) {
				hints.add(hintInfoNewMeeting);
			}

			if (ableToManageAttendees) {
				hints.add(hintInfoNewAttendee);
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

		setIcon(Data.getInstance().getIcon("RevAger_300x50.png"));

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
		if (Data.getInstance().getResiData().getReview() != null) {
			if (!mode.equals(Data.getInstance().getMode())) {
				mode = Data.getInstance().getMode();

				splitPanel.removeAll();

				updateLeftPane();
				updateRightPane();

				updateMenu();
				updateToolBar();

				updateWorker.execute();

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

			boolean prodNameNotEmpty = !Application.getInstance()
					.getReviewMgmt().getProductName().trim().equals("");
			boolean prodVersionNotEmpty = !Application.getInstance()
					.getReviewMgmt().getProductVersion().trim().equals("");
			boolean prodRefsNotEmpty = !Application.getInstance()
					.getReviewMgmt().getProductReferences().isEmpty();
			boolean revNameNotEmpty = !Application.getInstance()
					.getReviewMgmt().getReviewName().trim().equals("");
			boolean revDescNotEmpty = !Application.getInstance()
					.getReviewMgmt().getReviewDescription().trim().equals("");
			boolean attendeesNotEmpty = !Application.getInstance()
					.getAttendeeMgmt().getAttendees().isEmpty();
			boolean meetingsNotEmpty = !Application.getInstance()
					.getMeetingMgmt().getMeetings().isEmpty();
			boolean aspectsNotEmpty = !Application.getInstance()
					.getAspectMgmt().getAspects().isEmpty();

			if (prodNameNotEmpty && prodVersionNotEmpty && prodRefsNotEmpty
					&& revNameNotEmpty && revDescNotEmpty && attendeesNotEmpty
					&& meetingsNotEmpty && aspectsNotEmpty) {
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

	/**
	 * Update resi data.
	 */
	public void updateResiData() {
		observeResiData(false);

		Application.getInstance().getReviewMgmt().setReviewName(
				getTextRevName());

		Application.getInstance().getReviewMgmt().setReviewDescription(
				getTextRevDesc());

		Application.getInstance().getReviewMgmt().setReviewComments(
				getTextRevComm());

		Application.getInstance().getReviewMgmt().setImpression(
				impressionTxtArea.getText());

		Application.getInstance().getReviewMgmt().setRecommendation(
				(String) recommendationBx.getSelectedItem());

		observeResiData(true);
	}

	/**
	 * Update the title of the main frame.
	 */
	public void updateTitle() {
		if (observingResiData) {
			String title = "";

			if (Data.getInstance().getMode().equals("default")) {
				title = Data.getInstance().getResource("appName");
			} else {
				String revName = Data.getInstance().getResiData().getReview()
						.getName();

				if (UI.getInstance().getStatus() != Status.NO_FILE_LOADED) {
					if (revName != null) {
						if (revName.trim().equals("")) {
							title = Data.getInstance().getLocaleStr(
									"unnamedReview");
						} else {
							title = revName;
						}
					} else {
						title = Data.getInstance().getLocaleStr("newReview");
					}

					title += " - ";
				}

				title += Data.getInstance().getResource("appName")
						+ " ("
						+ Data.getInstance().getLocaleStr(
								"mode.".concat(Data.getInstance().getMode()
										.toString().toLowerCase())) + ")";
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

		UI.getInstance().setStatus(Status.NO_FILE_LOADED);

		splitPanel.setVisible(false);
		splitPanel.removeAll();

		observeResiData(false);

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
	 * Enable protocol mode.
	 * 
	 * @param enable
	 *            the enable
	 */
	private void enableProtocolMode(boolean enable) {
		protocolModeItem.setEnabled(enable);
		tbProtocolMode.setEnabled(enable);
		editProtocol.setEnabled(enable);
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

		if(Data.getInstance().getMode().equals("instant")){
			List<Meeting> meetingsList=Application.getInstance().getMeetingMgmt().getMeetings();
			for(Meeting meet:meetingsList){
				if(meet.isSetProtocol()==false && meetingsList.size()!=0){
					addMeeting.setEnabled(false);
					tbNewMeeting.setEnabled(false);
					newMeetingItem.setEnabled(false);
				}else{
					addMeeting.setEnabled(true);
					tbNewMeeting.setEnabled(true);
					newMeetingItem.setEnabled(true);
				}
			}
		}
			
		if (getSelectedMeeting() != null) {
			commentMeeting.setEnabled(true);
			editMeeting.setEnabled(true);
			removeMeeting.setEnabled(true);

			if (Data.getInstance().getModeParam("ableToUseProtocolMode")) {
				enableProtocolMode(true);
			}

			removeMeeting.setToolTipText(Data.getInstance().getLocaleStr(
					"meeting.remove"));
		} else if (getSelectedProtocol() != null) {
			commentMeeting.setEnabled(false);
			editMeeting.setEnabled(false);
			removeMeeting.setEnabled(true);

			if (Data.getInstance().getModeParam("ableToUseProtocolMode")) {
				enableProtocolMode(true);
			}

			removeMeeting.setToolTipText(Data.getInstance().getLocaleStr(
					"protocol.remove"));
		} else {
			commentMeeting.setEnabled(false);
			editMeeting.setEnabled(false);
			removeMeeting.setEnabled(false);

			if (Data.getInstance().getModeParam("ableToUseProtocolMode")) {
				enableProtocolMode(false);
			}

			removeMeeting.setToolTipText(Data.getInstance().getLocaleStr(
					"button.remove"));
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

	/**
	 * Sets the enabled all comp.
	 * 
	 * @param enabled
	 *            the new enabled all comp
	 */
	private void setEnabledAllComp(boolean enabled) {
		Color textColor = new JTextField().getForeground();
		Cursor productCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

		if (!enabled) {
			textColor = Color.GRAY;
			productCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		}

		addMeeting.setEnabled(enabled);
		meetingsTree.setEnabled(enabled);
		if (!enabled) {
			meetingsTree.setSelectionPath(null);
		}
		addAttendee.setEnabled(enabled);
		attendeesTable.setEnabled(enabled);
		editAspects.setEnabled(enabled);
		
		tbNewMeeting.setEnabled(enabled);
		tbNewAttendee.setEnabled(enabled);
		tbManageSeverities.setEnabled(enabled);
		tbCreateInvitations.setEnabled(enabled);
		tbAspectsManager.setEnabled(enabled);
		tbProtocolMode.setEnabled(enabled);

		newMeetingItem.setEnabled(enabled);
		newAttendeeItem.setEnabled(enabled);
		protocolModeItem.setEnabled(enabled);
		createInvitationsItem.setEnabled(enabled);
		manageSeveritiesItem.setEnabled(enabled);
		aspectsManagerItem.setEnabled(enabled);

		textProduct.setCursor(productCursor);
		textProduct.setForeground(textColor);

		textRevName.setFocusable(enabled);
		textRevName.setForeground(textColor);

		textRevDesc.setFocusable(enabled);
		textRevDesc.setForeground(textColor);

		textRevComm.setFocusable(enabled);
		textRevComm.setForeground(textColor);

		impressionTxtArea.setFocusable(enabled);
		impressionTxtArea.setForeground(textColor);

		clearRec.setEnabled(!enabled);

		if (enabled) {
			updateLeftPane();
			updateRightPane();
		}
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

}
