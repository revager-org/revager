package neos.resi.test.DIRTY;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class GrayOnDialogDisplay {
	   
	   JFrame frame= new JFrame();
	   CardLayout layout = new CardLayout();
	         
	   public static void main(String[] args) {
	      SwingUtilities.invokeLater(new Runnable() {
	         
	         @Override
	         public void run() {
	            new GrayOnDialogDisplay().makeUI();
	         }
	      });
	   }
	 
	   public void makeUI() {
	      JPanel panel = new JPanel(new GridBagLayout());
	      GridBagConstraints gbc = new GridBagConstraints();
	      gbc.insets = new Insets(5, 5, 5, 5);
	      for (int i = 0; i < 10; i++) {
	         gbc.gridy = i;
	         gbc.gridx = 0;
	         panel.add(new JLabel("Label " + i), gbc);
	         gbc.gridx++;
	         panel.add(new JTextField("Text Field " + i), gbc);
	         gbc.gridx++;
	         JButton button = new JButton("Button " + i);
	         button.addActionListener(new ActionListener() {
	 
	            @Override
	            public void actionPerformed(ActionEvent e) {
	               layout.last(frame.getContentPane());
	               JOptionPane.showMessageDialog(frame, "The frame is gray!");
	               layout.first(frame.getContentPane());
	            }
	         });
	         panel.add(button, gbc);
	      }
	      JPanel grayPanel = new JPanel();
	      
	      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      frame.setLayout(layout);
	      frame.add(panel, "panel");
	      frame.add(grayPanel, "grayPanel");
	      frame.pack();
	      frame.setLocationRelativeTo(null);
	      frame.setVisible(true);
	   }
	}