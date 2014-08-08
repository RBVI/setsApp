package edu.ucsf.rbvi.setsApp.internal.ui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JSeparator;
import javax.swing.Icon;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

import java.awt.Dimension;

class SplitButton extends JButton {
  static final int GAP = 5;
  final JLabel mainText;
  volatile boolean actionListenersEnabled = true;
  JPopupMenu menu = null;

  public SplitButton(final Icon icon) {
    mainText = new JLabel(icon);
    final JLabel menuIcon = new JLabel("\u25be");
    super.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    super.add(mainText);
    super.add(Box.createRigidArea(new Dimension(GAP, 0)));
    super.add(new JSeparator(JSeparator.VERTICAL));
    super.add(Box.createRigidArea(new Dimension(GAP, 0)));
    super.add(menuIcon);

    super.addMouseListener(new MouseAdapter() {
      public void mousePressed(final MouseEvent e) {
        if (!SplitButton.this.isEnabled())
          return;
        final int x = e.getX();
        final int w = e.getComponent().getWidth();
        if (x >= (2 * w / 3)) {
          actionListenersEnabled = false;
          if (menu != null) {
            menu.show(e.getComponent(), e.getX(), e.getY());
          }
        } else {
          actionListenersEnabled = true;
        }
      }
    });
  }

  public void setText(final String label) {
    mainText.setText(label);
  }

  protected void fireActionPerformed(final ActionEvent e) {
    if (actionListenersEnabled) {
      super.fireActionPerformed(e);
    }
  }

  public void setMenu(final JPopupMenu menu) {
    this.menu = menu;
  }
}

