package neos.resi.test.DIRTY;

//Example from http://www.crionics.com/products/opensource/faq/swing_ex/SwingExamples.html

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import neos.resi.app.model.ApplicationData;
import neos.resi.app.model.Data;
import neos.resi.app.model.appdata.AppAspect;
import neos.resi.app.model.appdata.AppCatalog;

/**
 * @version 1.1 01/15/99
 */
public class CheckNodeTreeExample extends JFrame {

	private ApplicationData appData = Data.getInstance().getAppData();

	public CheckNodeTreeExample() {
		super("CheckNode TreeExample");

		/*
		 * String[] strs = {"swing", // 0 "platf", // 1 "basic", // 2 "metal",
		 * // 3 "JTree"}; // 4
		 * 
		 * CheckNode[] nodes = new CheckNode[strs.length]; for (int
		 * i=0;i<strs.length;i++) { nodes[i] = new CheckNode(strs[i]); }
		 * nodes[0].add(nodes[1]); nodes[1].add(nodes[2]);
		 * nodes[1].add(nodes[3]); nodes[0].add(nodes[4]);
		 * nodes[3].setSelected(true);
		 * 
		 * JTree tree = new JTree( nodes[0] );
		 */

		try {
			appData.initialize();

			AppCatalog catalog = appData.newCatalog("Testkatalog 1");

			catalog.newAspect("Direktive", "Beschr-", "TestKategore");
		} catch (Exception e) {
			e.printStackTrace();
		}

		CheckNode root = new CheckNode("Fragenkataloge");

		try {
			for (AppCatalog ac : appData.getCatalogs()) {
				CheckNode catalog = new CheckNode(ac);

				root.add(catalog);

				for (String cat : ac.getCategories()) {
					CheckNode category = new CheckNode(cat);

					catalog.add(category);

					for (AppAspect asp : ac.getAspects(cat)) {
						CheckNode aspect = new CheckNode(asp);

						category.add(aspect);
					}
				}
			}
		} catch (Exception e) {
			// TODO JoptionPane Fehlermeldung
		}

		JTree tree = new JTree(root);

		tree.setCellRenderer(new CheckRenderer());
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addMouseListener(new NodeSelectionListener(tree));
		JScrollPane sp = new JScrollPane(tree);

		getContentPane().add(sp, BorderLayout.CENTER);
	}

	class NodeSelectionListener implements MouseListener {
		JTree tree;

		NodeSelectionListener(JTree tree) {
			this.tree = tree;
		}

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
			int x = e.getX();
			int y = e.getY();
			int row = tree.getRowForLocation(x, y);
			TreePath path = tree.getPathForRow(row);
			// TreePath path = tree.getSelectionPath();
			if (path != null) {
				CheckNode node = (CheckNode) path.getLastPathComponent();
				boolean isSelected = !(node.isSelected());
				node.setSelected(isSelected);
				if (isSelected) {
					tree.expandPath(path);
				} else {
					tree.collapsePath(path);
				}
				((DefaultTreeModel) tree.getModel()).nodeChanged(node);
				// I need revalidate if node is root. but why?
				if (row == 0) {
					tree.revalidate();
					tree.repaint();
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}

	public static void main(String args[]) {
		CheckNodeTreeExample frame = new CheckNodeTreeExample();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setSize(300, 200);
		frame.setVisible(true);
	}
}

class CheckRenderer extends JPanel implements TreeCellRenderer {
	protected JCheckBox check;

	protected TreeLabel label;

	public CheckRenderer() {
		setLayout(null);
		check = new JCheckBox();
		add(check);
		label = new TreeLabel();
		add(label);
		check.setBackground(UIManager.getColor("Tree.textBackground"));
		label.setForeground(UIManager.getColor("Tree.textForeground"));
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		String stringValue = tree.convertValueToText(value, isSelected,
				expanded, leaf, row, hasFocus);
		setEnabled(tree.isEnabled());
		check.setSelected(((CheckNode) value).isSelected());
		label.setFont(tree.getFont());
		label.setText(stringValue);
		label.setSelected(isSelected);
		label.setFocus(hasFocus);
		if (leaf) {
			label.setIcon(UIManager.getIcon("Tree.leafIcon"));
		} else if (expanded) {
			label.setIcon(UIManager.getIcon("Tree.openIcon"));
		} else {
			label.setIcon(UIManager.getIcon("Tree.closedIcon"));
		}
		return this;
	}

	public Dimension getPreferredSize() {
		Dimension d_check = check.getPreferredSize();
		Dimension d_label = label.getPreferredSize();
		return new Dimension(d_check.width + d_label.width,
				(d_check.height < d_label.height ? d_label.height
						: d_check.height));
	}

	public void doLayout() {
		Dimension d_check = check.getPreferredSize();
		Dimension d_label = label.getPreferredSize();
		int y_check = 0;
		int y_label = 0;
		if (d_check.height < d_label.height) {
			y_check = (d_label.height - d_check.height) / 2;
		} else {
			y_label = (d_check.height - d_label.height) / 2;
		}
		check.setLocation(0, y_check);
		check.setBounds(0, y_check, d_check.width, d_check.height);
		label.setLocation(d_check.width, y_label);
		label.setBounds(d_check.width, y_label, d_label.width, d_label.height);
	}

	public void setBackground(Color color) {
		if (color instanceof ColorUIResource)
			color = null;
		super.setBackground(color);
	}

	public class TreeLabel extends JLabel {
		boolean isSelected;

		boolean hasFocus;

		public TreeLabel() {
		}

		public void setBackground(Color color) {
			if (color instanceof ColorUIResource)
				color = null;
			super.setBackground(color);
		}

		public void paint(Graphics g) {
			String str;
			if ((str = getText()) != null) {
				if (0 < str.length()) {
					if (isSelected) {
						g.setColor(UIManager
								.getColor("Tree.selectionBackground"));
					} else {
						g.setColor(UIManager.getColor("Tree.textBackground"));
					}
					Dimension d = getPreferredSize();
					int imageOffset = 0;
					Icon currentI = getIcon();
					if (currentI != null) {
						imageOffset = currentI.getIconWidth()
								+ Math.max(0, getIconTextGap() - 1);
					}
					g.fillRect(imageOffset, 0, d.width - 1 - imageOffset,
							d.height);
					if (hasFocus) {
						g.setColor(UIManager
								.getColor("Tree.selectionBorderColor"));
						g.drawRect(imageOffset, 0, d.width - 1 - imageOffset,
								d.height - 1);
					}
				}
			}
			super.paint(g);
		}

		public Dimension getPreferredSize() {
			Dimension retDimension = super.getPreferredSize();
			if (retDimension != null) {
				retDimension = new Dimension(retDimension.width + 3,
						retDimension.height);
			}
			return retDimension;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		public void setFocus(boolean hasFocus) {
			this.hasFocus = hasFocus;
		}
	}
}

class CheckNode extends DefaultMutableTreeNode {

	protected boolean isSelected;

	public CheckNode() {
		this(null);
	}

	public CheckNode(Object userObject) {
		this(userObject, true, false);
	}

	public CheckNode(Object userObject, boolean allowsChildren,
			boolean isSelected) {
		super(userObject, allowsChildren);
		this.isSelected = isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;

		if (children != null) {
			Enumeration<Object> e = children.elements();
			// Iterator<Object> e1 = children.iterator();
			while (e.hasMoreElements()) {
				CheckNode node = (CheckNode) e.nextElement();
				node.setSelected(isSelected);
			}
		}
	}

	public boolean isSelected() {
		return isSelected;
	}

	// If you want to change "isSelected" by CellEditor,
	/*
	 * public void setUserObject(Object obj) { if (obj instanceof Boolean) {
	 * setSelected(((Boolean)obj).booleanValue()); } else {
	 * super.setUserObject(obj); } }
	 */

}
