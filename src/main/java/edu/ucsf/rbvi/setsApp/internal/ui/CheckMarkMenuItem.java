package edu.ucsf.rbvi.setsApp.internal.ui;

import javax.swing.JMenuItem;

class CheckMarkMenuItem extends JMenuItem {
  static final String CHECKED_STATE_TEXT_FMT = "<html><font size=\"+1\">\u2714</font> %s</html>";
  static final String NORMAL_STATE_TEXT_FMT = "<html>%s</html>";
  final String text;

  public CheckMarkMenuItem(final String text) {
    this(text, false);
  }

  public CheckMarkMenuItem(final String text, final boolean state) {
    this.text = text;
    setSelected(state);
  }

  public void setSelected(final boolean state) {
    super.setSelected(state);
    updateText();
  }

  private void updateText() {
    super.setText(String.format(super.isSelected() ? CHECKED_STATE_TEXT_FMT : NORMAL_STATE_TEXT_FMT, text));
  }
}