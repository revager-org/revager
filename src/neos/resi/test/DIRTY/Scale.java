package neos.resi.test.DIRTY;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Scale extends JFrame {

  Image image;

  Insets insets;

  public Scale() {
    super();
    ImageIcon icon = new ImageIcon("/home/jojo/Files/Media/Grafik/smiley.png");
    image = icon.getImage();
  }

  public void paint(Graphics g) {
    super.paint(g);
    if (insets == null) {
      insets = getInsets();
    }
    g.drawImage(image, insets.left, insets.top, this);
  }

  public void go() {
    // Sleep first to see original
    rest();
    Image original = image;
    // Down fast
    image = original.getScaledInstance(200, -1, Image.SCALE_FAST);
    repaint();
    rest();
    // Down slow
    image = original.getScaledInstance(200, -1, Image.SCALE_SMOOTH);
    repaint();
    rest();
    // Up fast
    image = original.getScaledInstance(400, -1, Image.SCALE_FAST);
    repaint();
    rest();
    // Up slow
    image = original.getScaledInstance(400, -1, Image.SCALE_SMOOTH);
    repaint();
    rest();
    System.exit(0);
  }

  private void rest() {
    try {
      Thread.sleep(5000);
    } catch (InterruptedException ignored) {
    }
  }

  public static void main(String args[]) {
    Scale f = new Scale();
    f.setSize(400, 400);
    f.show();
    f.go();
  }
}