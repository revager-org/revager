package org.revager.gui.findings_list;

import static org.revager.app.model.Data._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.revager.app.Application;
import org.revager.app.FindingManagement;
import org.revager.app.ResiFileFilter;
import org.revager.app.SeverityManagement;
import org.revager.app.model.Data;
import org.revager.app.model.ResiData;
import org.revager.app.model.schema.Aspect;
import org.revager.app.model.schema.Finding;
import org.revager.app.model.schema.Protocol;
import org.revager.gui.UI;
import org.revager.gui.findings_list.AddAspToFindPopupWindow.ButtonClicked;
import org.revager.gui.helpers.DefaultTableHeaderCellRenderer;
import org.revager.gui.helpers.FileChooser;
import org.revager.gui.models.FindAspTableModel;
import org.revager.gui.models.FindExtRefTableModel;
import org.revager.gui.models.FindRefTableModel;
import org.revager.tools.AppTools;
import org.revager.tools.GUITools;

public class FindingPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public enum Type {
		EDIT_VIEW, COMPACT_VIEW
	}

	private static final int CONTROL_BUTTONS_PADDING = 2;

	public static final Dimension EDIT_VIEW_SIZE = new Dimension(100, 280);
	public static final Dimension COMPACT_VIEW_SIZE = new Dimension(100, 45);

	private static final Color EDIT_VIEW_BG = new Color(255, 255, 204);
	private static final Color COMPACT_VIEW_BG = new Color(229, 226, 226);

	private Type type = Type.EDIT_VIEW;

	private Finding finding = null;

	private FindingsTab findingsTab = null;

	private Protocol protocol = null;

	private GridBagLayout layout = new GridBagLayout();

	private FindingManagement findMgmt = Application.getInstance()
			.getFindingMgmt();
	private SeverityManagement sevMgmt = Application.getInstance()
			.getSeverityMgmt();
	private ResiData resiData = Data.getInstance().getResiData();

	private ImageIcon iconAdd = Data.getInstance().getIcon("add_25x25_0.png");
	private ImageIcon iconRemove = Data.getInstance().getIcon(
			"remove_25x25_0.png");
	private ImageIcon iconEdit = Data.getInstance().getIcon("edit_25x25_0.png");
	private ImageIcon iconPushUp = Data.getInstance().getIcon(
			"upArrow_25x25_0.png");
	private ImageIcon iconPushDown = Data.getInstance().getIcon(
			"downArrow_25x25_0.png");
	private ImageIcon iconPushTop = Data.getInstance().getIcon(
			"pushTop_25x25_0.png");
	private ImageIcon iconPushBottom = Data.getInstance().getIcon(
			"pushBottom_25x25_0.png");
	private ImageIcon iconRemoveFinding = Data.getInstance().getIcon(
			"delete_25x25_0.png");
	private ImageIcon iconCloseFinding = Data.getInstance().getIcon(
			"closeFinding_25x25_0.png");
	private ImageIcon iconPaste = Data.getInstance().getIcon(
			"paste_25x25_0.png");

	private ImageIcon iconRolloverAdd = Data.getInstance().getIcon(
			"add_25x25.png");
	private ImageIcon iconRolloverRemove = Data.getInstance().getIcon(
			"remove_25x25.png");
	private ImageIcon iconRolloverEdit = Data.getInstance().getIcon(
			"edit_25x25.png");
	private ImageIcon iconRolloverPushUp = Data.getInstance().getIcon(
			"upArrow_25x25.png");
	private ImageIcon iconRolloverPushDown = Data.getInstance().getIcon(
			"downArrow_25x25.png");
	private ImageIcon iconRolloverPushTop = Data.getInstance().getIcon(
			"pushTop_25x25.png");
	private ImageIcon iconRolloverPushBottom = Data.getInstance().getIcon(
			"pushBottom_25x25.png");
	private ImageIcon iconRolloverRemoveFinding = Data.getInstance().getIcon(
			"delete_25x25.png");
	private ImageIcon iconRolloverCloseFinding = Data.getInstance().getIcon(
			"closeFinding_25x25.png");
	private ImageIcon iconRolloverPaste = Data.getInstance().getIcon(
			"paste_25x25.png");

	private ImageIcon iconBlank = Data.getInstance().getIcon("blank_25x25.png");

	private GridBagLayout layoutEditView = new GridBagLayout();
	private JPanel panelEditView = new JPanel(layoutEditView);

	private GridBagLayout layoutCompactView = new GridBagLayout();
	private JPanel panelCompactView = new JPanel(layoutCompactView);

	private JLabel labelFindingNumber = new JLabel();
	private JLabel labelFindingSeverity = new JLabel();
	private JLabel labelFindingDescription = new JLabel();

	private JLabel labelFindingTitle = new JLabel();

	private JComboBox comboSeverity = new JComboBox();

	private JTextArea textDescription = new JTextArea();

	private FindRefTableModel modelReferences;
	private FindExtRefTableModel modelExtReferences;
	private FindAspTableModel modelAspects;

	private JTable tableReferences;
	private JTable tableExtReferences;
	private JTable tableAspects;

	private JScrollPane scrollDescription;
	private JScrollPane scrollReferences;
	private JScrollPane scrollExtReferences;
	private JScrollPane scrollAspects;

	private JButton buttonAddReference = GUITools.newImageButton(iconAdd,
			iconRolloverAdd);
	private JButton buttonEditReference = GUITools.newImageButton(iconEdit,
			iconRolloverEdit);
	private JButton buttonRemoveReference = GUITools.newImageButton(iconRemove,
			iconRolloverRemove);

	private JButton buttonAddExtReference = GUITools.newImageButton(iconAdd,
			iconRolloverAdd);
	private JButton buttonRemoveExtReference = GUITools.newImageButton(
			iconRemove, iconRolloverRemove);
	private JButton buttonPasteExtReference = GUITools.newImageButton(
			iconPaste, iconRolloverPaste);

	private JButton buttonAddAspect = GUITools.newImageButton(iconAdd,
			iconRolloverAdd);
	private JButton buttonRemoveAspect = GUITools.newImageButton(iconRemove,
			iconRolloverRemove);

	private JButton buttonPushUp = GUITools.newImageButton(iconPushUp,
			iconRolloverPushUp);
	private JButton buttonPushDown = GUITools.newImageButton(iconPushDown,
			iconRolloverPushDown);
	private JButton buttonPushTop = GUITools.newImageButton(iconPushTop,
			iconRolloverPushTop);
	private JButton buttonPushBottom = GUITools.newImageButton(iconPushBottom,
			iconRolloverPushBottom);
	private JButton buttonCloseFinding = GUITools.newImageButton(
			iconCloseFinding, iconRolloverCloseFinding);
	private JButton buttonRemoveFinding = GUITools.newImageButton(
			iconRemoveFinding, iconRolloverRemoveFinding);

	private JButton buttonDummy = GUITools.newImageButton(iconBlank, iconBlank);

	private FocusListener focusListener = new FocusListener() {
		@Override
		public void focusGained(FocusEvent e) {
			updateFocus(e.getSource());

			updateTableButtons();
		}

		@Override
		public void focusLost(FocusEvent e) {
			updateTableButtons();
		}
	};

	private MouseListener mouseListener = new MouseListener() {
		@Override
		public void mouseClicked(MouseEvent e) {
			updateTableButtons();
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

	private MouseListener mouseListenerCompact = new MouseListener() {
		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			findingsTab.editFinding(finding);
		}
	};

	public FindingPanel(final Finding finding, final FindingsTab findingsTab) {
		super();

		this.finding = finding;
		this.findingsTab = findingsTab;
		this.protocol = findingsTab.getProtocol();

		this.setLayout(layout);
		this.setBackground(Color.WHITE);

		/*
		 * Initialize tables and models
		 */
		modelReferences = new FindRefTableModel(finding);
		modelExtReferences = new FindExtRefTableModel(finding);
		modelAspects = new FindAspTableModel(finding);

		/*
		 * Prepare buttons and put them inside separate panels
		 */
		JPanel panelButtonsReferences = new JPanel(new GridLayout(3, 1));
		JPanel panelButtonsExtReferences = new JPanel(new GridLayout(3, 1));
		JPanel panelButtonsAspects = new JPanel(new GridLayout(2, 1));

		panelButtonsReferences.setOpaque(false);
		panelButtonsExtReferences.setOpaque(false);
		panelButtonsAspects.setOpaque(false);

		/*
		 * Set tooltips for buttons
		 */
		buttonCloseFinding.setToolTipText(_("Close Edit View of this Finding"));
		buttonRemoveFinding.setToolTipText(_("Remove this Finding"));
		buttonPushUp.setToolTipText(_("Push up Finding"));
		buttonPushDown.setToolTipText(_("Push down Finding"));
		buttonPushTop.setToolTipText(_("Push Finding to the top"));
		buttonPushBottom.setToolTipText(_("Push Finding to the bottom"));

		/*
		 * Buttons for references table
		 */
		buttonAddReference.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// UI.getInstance().getProtocolFrame().updateFocus(finding,
				// tableReferences);

				if (tableReferences.getCellEditor() != null) {
					tableReferences.getCellEditor().stopCellEditing();
				}

				String ref = _("Please enter a reference");
				findMgmt.addReference(ref, finding);
				modelReferences.fireTableDataChanged();

				int row = tableReferences.getRowCount() - 1;

				tableReferences.scrollRectToVisible(tableReferences
						.getCellRect(row, 0, false));
				tableReferences.editCellAt(row, 0);
			}
		});

		buttonRemoveReference.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selRow = tableReferences.getSelectedRow();

				if (tableReferences.getEditingRow() != -1) {
					selRow = tableReferences.getEditingRow();

					tableReferences.getCellEditor().stopCellEditing();
				}

				if (selRow != -1) {
					String ref = findMgmt.getReferences(finding).get(selRow);
					findMgmt.removeReference(ref, finding);
					modelReferences.fireTableDataChanged();
				}

				updateTableButtons();
			}
		});

		buttonEditReference.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selRow = tableReferences.getSelectedRow();

				if (selRow != -1) {
					tableReferences.editCellAt(selRow, 0);
					modelReferences.fireTableDataChanged();
				}
			}
		});

		buttonAddReference.setToolTipText(_("Add Reference"));
		buttonRemoveReference.setToolTipText(_("Remove Reference"));
		buttonEditReference.setToolTipText(_("Edit Reference"));

		buttonRemoveReference.setEnabled(false);
		buttonEditReference.setEnabled(false);

		panelButtonsReferences.add(buttonAddReference);
		panelButtonsReferences.add(buttonRemoveReference);
		panelButtonsReferences.add(buttonEditReference);

		/*
		 * Buttons for external references table
		 */
		buttonAddExtReference.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFocus(tableExtReferences);

				GUITools.executeSwingWorker(new AddExternalReferenceWorker());
			}
		});

		buttonRemoveExtReference.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selRow = tableExtReferences.getSelectedRow();

				if (selRow != -1) {
					File file = findMgmt.getExtReferences(finding).get(selRow);

					findMgmt.removeExtReference(file, finding);

					modelExtReferences.fireTableDataChanged();

					updateTableButtons();
				}
			}
		});

		buttonPasteExtReference.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFocus(tableExtReferences);

				GUITools.executeSwingWorker(new PasteImageFromClipboardWorker());
			}
		});

		buttonAddExtReference.setToolTipText(_("Add File"));
		buttonRemoveExtReference.setToolTipText(_("Remove file"));
		buttonPasteExtReference.setToolTipText(_("Paste Image from Clipboard"));

		buttonRemoveExtReference.setEnabled(false);

		panelButtonsExtReferences.add(buttonAddExtReference);
		panelButtonsExtReferences.add(buttonRemoveExtReference);
		panelButtonsExtReferences.add(buttonPasteExtReference);

		/*
		 * Buttons for aspects table
		 */
		buttonAddAspect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFocus(tableAspects);

				AddAspToFindPopupWindow popup = new AddAspToFindPopupWindow(UI
						.getInstance().getProtocolFrame());

				popup.setVisible(true);

				if (popup.getButtonClicked() == ButtonClicked.OK) {
					List<Aspect> aspList = popup.getSelAspList();

					for (Aspect asp : aspList) {
						findMgmt.addAspect(asp, finding);
					}

					modelAspects.fireTableDataChanged();

					GUITools.scrollToBottom(scrollAspects);
				}

				updateTableButtons();
			}
		});

		buttonRemoveAspect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selRow = tableAspects.getSelectedRow();

				if (selRow != -1) {
					String asp = findMgmt.getAspects(finding).get(selRow);
					findMgmt.removeAspect(asp, finding);
					modelAspects.fireTableDataChanged();
				}

				updateTableButtons();
			}
		});

		buttonAddAspect.setToolTipText(_("Add Aspect(s)"));
		buttonRemoveAspect.setToolTipText(_("Remove Aspect(s)"));

		buttonRemoveAspect.setEnabled(false);

		panelButtonsAspects.add(buttonAddAspect);
		panelButtonsAspects.add(buttonRemoveAspect);

		/*
		 * Prepare tables
		 */
		tableAspects = GUITools.newStandardTable(modelAspects, true);
		tableReferences = GUITools.newStandardTable(modelReferences, true);
		tableExtReferences = GUITools
				.newStandardTable(modelExtReferences, true);

		tableAspects.getColumnModel().getColumn(0)
				.setHeaderRenderer(new FindingPanelHeadRenderer());
		tableReferences.getColumnModel().getColumn(0)
				.setHeaderRenderer(new FindingPanelHeadRenderer());
		tableExtReferences.getColumnModel().getColumn(0)
				.setHeaderRenderer(new FindingPanelHeadRenderer());

		tableAspects.getColumnModel().getColumn(0)
				.setCellRenderer(new FindingPanelCellRenderer());
		tableReferences.getColumnModel().getColumn(0)
				.setCellRenderer(new FindingPanelCellRenderer());
		tableExtReferences.getColumnModel().getColumn(0)
				.setCellRenderer(new FindingPanelCellRenderer());

		tableAspects.setRowHeight(29);
		tableReferences.setRowHeight(29);
		tableExtReferences.setRowHeight(29);

		tableAspects.addFocusListener(focusListener);
		tableReferences.addFocusListener(focusListener);
		tableExtReferences.addFocusListener(focusListener);

		tableAspects.addMouseListener(mouseListener);
		tableReferences.addMouseListener(mouseListener);
		tableExtReferences.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateTableButtons();

				if (e.getClickCount() == 2) {
					int selRow = tableExtReferences.getSelectedRow();

					if (selRow != -1) {
						File ref = findMgmt.getExtReferences(finding).get(
								tableExtReferences.getSelectedRow());

						if (AppTools.isReadableWritableImageFile(ref)) {
							UI.getInstance().getProtocolFrame()
									.getImageEditor(ref).setVisible(true);
						} else {
							try {
								Desktop.getDesktop().open(ref);
							} catch (Exception exc) {
								JOptionPane.showMessageDialog(UI.getInstance()
										.getProtocolFrame(), GUITools
										.getMessagePane(exc.getMessage()),
										_("Error"), JOptionPane.ERROR_MESSAGE);
							}
						}
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
			}
		});

		scrollAspects = new JScrollPane(tableAspects);
		scrollReferences = new JScrollPane(tableReferences);
		scrollExtReferences = new JScrollPane(tableExtReferences);

		scrollAspects.getViewport().setBackground(Color.WHITE);
		scrollReferences.getViewport().setBackground(Color.WHITE);
		scrollExtReferences.getViewport().setBackground(Color.WHITE);

		/*
		 * Create content panel
		 */
		panelEditView.setBorder(UI.STANDARD_BORDER);
		panelEditView.setBackground(EDIT_VIEW_BG);

		panelCompactView.setBorder(UI.STANDARD_BORDER);
		panelCompactView.setBackground(COMPACT_VIEW_BG);

		labelFindingDescription.setFont(UI.PROTOCOL_FONT);

		labelFindingNumber.setFont(UI.PROTOCOL_FONT_BOLD);

		labelFindingSeverity.setFont(UI.PROTOCOL_FONT);
		labelFindingSeverity.setForeground(Color.DARK_GRAY);

		labelFindingTitle.setFont(UI.PROTOCOL_FONT_BOLD);
		labelFindingTitle.setText(_("Finding") + " " + finding.getId());

		scrollDescription = GUITools.setIntoScrllPn(textDescription);
		GUITools.scrollToTop(scrollDescription);

		textDescription.setText(finding.getDescription());
		textDescription.addFocusListener(focusListener);
		textDescription.setFont(UI.PROTOCOL_FONT);
		textDescription.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				findingsTab.updateTab();

				if (findMgmt.isFindingNotComplete(finding)) {
					scrollDescription.setBorder(UI.MARKED_BORDER);
				} else {
					scrollDescription.setBorder(UI.STANDARD_BORDER);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		comboSeverity.setFont(UI.PROTOCOL_FONT_BOLD);
		for (String sev : sevMgmt.getSeverities()) {
			comboSeverity.addItem(sev);
		}
		comboSeverity.addFocusListener(focusListener);

		comboSeverity.setSelectedItem(findMgmt.getLocalizedSeverity(finding));

		comboSeverity.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					findMgmt.setLocalizedSeverity(finding, comboSeverity
							.getSelectedItem().toString());
					resiData.fireDataChanged();
				}
			}
		});

		buttonCloseFinding.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				findingsTab.closeCurrentFinding();
			}
		});
		buttonRemoveFinding.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(
						UI.getInstance().getProtocolFrame(),
						GUITools.getMessagePane(_("Are you sure you want to remove the selected finding permanently?")),
						_("Question"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					findingsTab.removeCurrentFinding();
				}
			}
		});
		buttonPushUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				findingsTab.pushUpCurrentFinding();
			}
		});
		buttonPushDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				findingsTab.pushDownCurrentFinding();
			}
		});
		buttonPushTop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				findingsTab.pushTopCurrentFinding();
			}
		});
		buttonPushBottom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				findingsTab.pushBottomCurrentFinding();
			}
		});

		/*
		 * Register focus listener
		 */
		textDescription.addFocusListener(focusListener);
		comboSeverity.addFocusListener(focusListener);

		tableAspects.addFocusListener(focusListener);
		tableExtReferences.addFocusListener(focusListener);
		tableReferences.addFocusListener(focusListener);

		/*
		 * Add components to compact view panel
		 */
		GUITools.addComponent(panelCompactView, layoutCompactView,
				labelFindingNumber, 0, 0, 1, 1, 0.0, 0.0, 10, 10, 10, 10,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);

		GUITools.addComponent(panelCompactView, layoutCompactView,
				labelFindingDescription, 1, 0, 1, 1, 1.0, 0.0, 10, 10, 10, 10,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);

		GUITools.addComponent(panelCompactView, layoutCompactView,
				labelFindingSeverity, 2, 0, 1, 1, 0.0, 0.0, 10, 10, 10, 10,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHEAST);

		/*
		 * Add components to edit view panel
		 */
		JPanel panelStrut = new JPanel();
		panelStrut.setBackground(EDIT_VIEW_BG);

		GUITools.addComponent(panelEditView, layoutEditView, labelFindingTitle,
				0, 0, 2, 1, 0.0, 0.0, 10, 10, 0, 10, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelEditView, layoutEditView, comboSeverity, 1,
				0, 2, 1, 0.0, 0.0, 10, 10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHEAST);
		GUITools.addComponent(panelEditView, layoutEditView, scrollDescription,
				0, 1, 1, 1, 1.0, 1.0, 10, 10, 0, 10, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelEditView, layoutEditView, panelStrut, 1, 1,
				1, 1, 0.0, 0.0, 10, 0, 0, 20, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelEditView, layoutEditView, scrollReferences,
				2, 1, 1, 1, 1.0, 0.0, 10, 10, 0, 10, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelEditView, layoutEditView,
				panelButtonsReferences, 3, 1, 1, 1, 0.0, 0.0, 10, 0, 0, 10,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelEditView, layoutEditView, scrollAspects, 0,
				2, 1, 1, 1.0, 0.0, 10, 10, 10, 10, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelEditView, layoutEditView,
				panelButtonsAspects, 1, 2, 1, 1, 0.0, 0.0, 10, 0, 10, 20,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelEditView, layoutEditView,
				scrollExtReferences, 2, 2, 1, 1, 1.0, 0.0, 10, 10, 10, 10,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelEditView, layoutEditView,
				panelButtonsExtReferences, 3, 2, 1, 1, 0.0, 0.0, 10, 0, 10, 10,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);

		setEditView();

		if (findMgmt.isFindingEmpty(finding)) {
			finding.setDescription("");
			findMgmt.setLocalizedSeverity(finding,
					sevMgmt.getSeverities().get(0));
			resiData.fireDataChanged();
		}

		updateTableButtons();
		updateFindingButtons();
	}

	private void setEditView() {
		type = Type.EDIT_VIEW;

		this.setPreferredSize(EDIT_VIEW_SIZE);

		this.setToolTipText(null);

		this.removeAll();

		this.removeMouseListener(mouseListenerCompact);

		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		if (findMgmt.isFindingNotComplete(finding)) {
			scrollDescription.setBorder(UI.MARKED_BORDER);
		} else {
			scrollDescription.setBorder(UI.STANDARD_BORDER);
		}

		updateFindingButtons();

		/*
		 * Create finding control buttons like push etc.
		 */
		GUITools.addComponent(this, layout, buttonPushTop, 0, 0, 1, 1, 0.0,
				0.0, CONTROL_BUTTONS_PADDING, CONTROL_BUTTONS_PADDING,
				CONTROL_BUTTONS_PADDING, CONTROL_BUTTONS_PADDING,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, layout, buttonPushUp, 0, 1, 1, 1, 0.0, 0.0,
				CONTROL_BUTTONS_PADDING, CONTROL_BUTTONS_PADDING,
				CONTROL_BUTTONS_PADDING, CONTROL_BUTTONS_PADDING,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, layout, buttonCloseFinding, 0, 2, 1, 1,
				0.0, 1.0, CONTROL_BUTTONS_PADDING + 35,
				CONTROL_BUTTONS_PADDING, CONTROL_BUTTONS_PADDING,
				CONTROL_BUTTONS_PADDING, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, layout, buttonRemoveFinding, 0, 3, 1, 1,
				0.0, 1.0, CONTROL_BUTTONS_PADDING, CONTROL_BUTTONS_PADDING,
				CONTROL_BUTTONS_PADDING + 35, CONTROL_BUTTONS_PADDING,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, layout, buttonPushDown, 0, 4, 1, 1, 0.0,
				0.0, CONTROL_BUTTONS_PADDING, CONTROL_BUTTONS_PADDING,
				CONTROL_BUTTONS_PADDING, CONTROL_BUTTONS_PADDING,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, layout, buttonPushBottom, 0, 5, 1, 1, 0.0,
				0.0, CONTROL_BUTTONS_PADDING, CONTROL_BUTTONS_PADDING,
				CONTROL_BUTTONS_PADDING, CONTROL_BUTTONS_PADDING,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);

		/*
		 * Add content panel to root panel
		 */
		GUITools.addComponent(this, layout, panelEditView, 1, 0, 1, 6, 1.0,
				0.0, 0, 0, 0, 20, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);

		/*
		 * Get the focus
		 */
		textDescription.requestFocusInWindow();
	}

	private void setCompactView() {
		type = Type.COMPACT_VIEW;

		this.setPreferredSize(COMPACT_VIEW_SIZE);

		this.setToolTipText(finding.getAspects().size() + " " + _("Aspect(s)")
				+ ", " + finding.getReferences().size() + " "
				+ _("Reference(s)") + ", "
				+ finding.getExternalReferences().size() + " " + _("File(s)"));

		this.removeAll();

		this.addMouseListener(mouseListenerCompact);

		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		if (findMgmt.isFindingNotComplete(finding)) {
			panelCompactView.setBorder(UI.MARKED_BORDER);
			panelCompactView.setBackground(UI.MARKED_COLOR.brighter());
		} else {
			panelCompactView.setBorder(UI.STANDARD_BORDER);
			panelCompactView.setBackground(COMPACT_VIEW_BG);
		}

		storeFindingData();

		/*
		 * Update labels
		 */
		labelFindingNumber.setText(_("Finding") + " " + finding.getId());

		labelFindingSeverity.setText(findMgmt.getLocalizedSeverity(finding));

		labelFindingDescription.setText(finding.getDescription());

		/*
		 * Add content panel to the root panel
		 */
		buttonDummy.setEnabled(false);

		GUITools.addComponent(this, layout, buttonDummy, 0, 0, 1, 1, 0.0, 0.0,
				CONTROL_BUTTONS_PADDING, CONTROL_BUTTONS_PADDING,
				CONTROL_BUTTONS_PADDING, CONTROL_BUTTONS_PADDING,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);

		GUITools.addComponent(this, layout, panelCompactView, 1, 0, 1, 1, 1.0,
				0.0, 0, 0, 0, 20, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
	}

	public void updateFindingButtons() {
		if (findMgmt.getNumberOfFindings(protocol) == 1) {
			// buttonRemoveFinding.setEnabled(false);
			buttonPushUp.setEnabled(false);
			buttonPushDown.setEnabled(false);
			buttonPushTop.setEnabled(false);
			buttonPushBottom.setEnabled(false);
		} else {
			// buttonRemoveFinding.setEnabled(true);

			if (findMgmt.isTopFinding(finding, protocol)) {
				buttonPushUp.setEnabled(false);
				buttonPushTop.setEnabled(false);
			} else {
				buttonPushUp.setEnabled(true);
				buttonPushTop.setEnabled(true);
			}

			if (findMgmt.isBottomFinding(finding, protocol)) {
				buttonPushDown.setEnabled(false);
				buttonPushBottom.setEnabled(false);
			} else {
				buttonPushDown.setEnabled(true);
				buttonPushBottom.setEnabled(true);
			}
		}
	}

	private void updateTableButtons() {
		if (tableAspects.getSelectedRow() == -1) {
			buttonRemoveAspect.setEnabled(false);
		} else {
			buttonRemoveAspect.setEnabled(true);
		}

		if (tableReferences.getSelectedRow() == -1) {
			buttonRemoveReference.setEnabled(false);
			buttonEditReference.setEnabled(false);
		} else {
			buttonRemoveReference.setEnabled(true);
			buttonEditReference.setEnabled(true);
		}

		if (tableExtReferences.getSelectedRow() == -1) {
			buttonRemoveExtReference.setEnabled(false);
		} else {
			buttonRemoveExtReference.setEnabled(true);
		}
	}

	public void updateFocus(Object evSource) {
		if (evSource != textDescription) {
			textDescription.select(0, 0);
		}

		if (evSource != tableReferences) {
			if (tableReferences.getCellEditor() != null) {
				tableReferences.getCellEditor().stopCellEditing();
			}

			if (tableReferences.getRowCount() > 0) {
				tableReferences.removeRowSelectionInterval(0,
						tableReferences.getRowCount() - 1);
			}
		}

		if (evSource != tableExtReferences) {
			if (tableExtReferences.getRowCount() > 0) {
				tableExtReferences.removeRowSelectionInterval(0,
						tableExtReferences.getRowCount() - 1);
			}
		}

		if (evSource != tableAspects) {
			if (tableAspects.getRowCount() > 0) {
				tableAspects.removeRowSelectionInterval(0,
						tableAspects.getRowCount() - 1);
			}
		}
	}

	public boolean isCompactView() {
		return type == Type.COMPACT_VIEW;
	}

	public boolean isEditView() {
		return type == Type.EDIT_VIEW;
	}

	public void switchView() {
		if (isEditView()) {
			setCompactView();
		} else {
			setEditView();
		}
	}

	public Finding getFinding() {
		return finding;
	}

	public void storeFindingData() {
		finding.setDescription(textDescription.getText());

		if (tableReferences.isEditing()) {
			tableReferences.getCellEditor().stopCellEditing();
		}
	}

	private class FindingPanelCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Font getFont() {
			return UI.PROTOCOL_FONT;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			setToolTipText(GUITools.getTextAsHtml("<font size=\"5\">"
					+ (String) value + "</font>"));

			return super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
		}
	};

	private class FindingPanelHeadRenderer extends
			DefaultTableHeaderCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Font getFont() {
			return UI.PROTOCOL_FONT;
		}
	};

	private class AddExternalReferenceWorker extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception {
			FileChooser fileChooser = UI.getInstance().getFileChooser();

			fileChooser.setFile(null);

			if (fileChooser.showDialog(UI.getInstance().getEditProductDialog(),
					FileChooser.MODE_OPEN_FILE, ResiFileFilter.TYPE_ALL) == FileChooser.SELECTED_APPROVE) {

				UI.getInstance().getProtocolFrame()
						.notifySwitchToProgressMode();

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						UI.getInstance().getProtocolFrame()
								.switchToProgressMode();
					}
				});

				File file = fileChooser.getFile();

				findMgmt.addExtReference(file, finding);

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						modelExtReferences.fireTableDataChanged();

						GUITools.scrollToBottom(scrollExtReferences);
					}
				});

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateTableButtons();
					}
				});
			}

			UI.getInstance().getProtocolFrame().notifySwitchToEditMode();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					UI.getInstance().getProtocolFrame().switchToEditMode();
				}
			});

			return null;
		}
	};

	private class PasteImageFromClipboardWorker extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception {
			UI.getInstance().getProtocolFrame().notifySwitchToProgressMode();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					UI.getInstance()
							.getProtocolFrame()
							.switchToProgressMode(
									_("Getting image from clipboard ..."));
				}
			});

			Image img = AppTools.getImageFromClipboard();

			if (img == null) {
				JOptionPane
						.showMessageDialog(
								UI.getInstance().getProtocolFrame(),
								GUITools.getMessagePane(_("Unfortunately there isn't any image in the clipboard which can be included as a reference.")),
								_("Information"),
								JOptionPane.INFORMATION_MESSAGE);
			} else {
				Image scalImg = img.getScaledInstance(-1, 250,
						Image.SCALE_SMOOTH);

				JLabel labelImg = new JLabel(new ImageIcon(scalImg));
				labelImg.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 15,
						0), new MatteBorder(1, 1, 1, 1, UI.SEPARATOR_COLOR)));

				JTextField inputField = new JTextField();
				inputField.setText(_("Screenshot"));

				JPanel messagePane = new JPanel(new BorderLayout());
				messagePane.add(labelImg, BorderLayout.NORTH);
				messagePane.add(new JLabel(
						_("Please enter a name for the image:")),
						BorderLayout.CENTER);
				messagePane.add(inputField, BorderLayout.SOUTH);

				Object[] options = { _("Save"), _("Edit"), _("Cancel") };

				int action = JOptionPane.showOptionDialog(UI.getInstance()
						.getProtocolFrame(), messagePane, _("Confirm"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

				String fileName = null;

				if (action != 2) {
					fileName = inputField.getText();
				}

				if (fileName != null && !fileName.trim().equals("")) {
					findMgmt.addExtReference(img, fileName, finding);

					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							modelExtReferences.fireTableDataChanged();

							GUITools.scrollToBottom(scrollExtReferences);

							updateTableButtons();
						}
					});
				}

				if (action == 1) {
					int numberOfExtRefs = findMgmt.getExtReferences(finding)
							.size();

					final File extRef = findMgmt.getExtReferences(finding).get(
							numberOfExtRefs - 1);

					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							UI.getInstance().getProtocolFrame()
									.getImageEditor(extRef).setVisible(true);
						}
					});
				}
			}

			UI.getInstance().getProtocolFrame().notifySwitchToEditMode();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					UI.getInstance().getProtocolFrame().switchToEditMode();
				}
			});

			return null;
		}
	}

}
