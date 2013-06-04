package edu.ucsf.rbvi.setsApp.internal.ui;

import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class PartialDisableComboBox extends JComboBox {
	 private static final long serialVersionUID = -1690671707274328126L;
	 
	 private ArrayList<Boolean> itemsState = new ArrayList<Boolean>();
	 
	 public PartialDisableComboBox() {
	  super();
	  this.setRenderer(new BasicComboBoxRenderer() {
	   private static final long serialVersionUID = -2774241371293899669L;
	   @Override
	   public Component getListCellRendererComponent(JList list, Object value, 
	     int index, boolean isSelected, boolean cellHasFocus) {
	    Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	    boolean disabled = index >= 0 && index < itemsState.size() && !itemsState.get(index);
	    c.setEnabled(!disabled);
	    c.setFocusable(!disabled);
	    return c;
	   }
	  });
	 }
	 
	 @Override
	 public void addItem(Object item) {
	  this.addItem(item, true);
	 }
	 
	 public void addItem(Object item, boolean enabled) {
	  super.addItem(item);
	  itemsState.add(enabled);
	 }
	 
	 @Override
	 public void insertItemAt(Object item, int index) {
	  this.insertItemAt(item, index, true);
	 }

	 public void insertItemAt(Object item, int index, boolean enabled) {
	  super.insertItemAt(item, index);
	  itemsState.add(index, enabled);
	 }
	 
	 @Override
	 public void removeAllItems() {
	  super.removeAllItems();
	  itemsState.clear();
	 }
	 
	 @Override
	 public void removeItemAt(int index) {
	  if (index < 0 || index >= itemsState.size()) throw new IllegalArgumentException("Item Index out of Bounds!");
	  super.removeItemAt(index);
	  itemsState.remove(index);
	 }
	 
	 @Override
	 public void removeItem(Object item) {
	  for (int q = 0; q < this.getItemCount(); q++) {
	   if (this.getItemAt(q) == item) itemsState.remove(q);
	  }
	  super.removeItem(item);
	 }
	 
	 @Override
	 public void setSelectedIndex(int index) {
	  if (index < 0 || index >= itemsState.size()) throw new IllegalArgumentException("Item Index out of Bounds!");
	  if (itemsState.get(index)) super.setSelectedIndex(index);
	 }
	 
	 public void setItemEnabled(int index, boolean enabled) {
	  if (index < 0 || index >= itemsState.size()) throw new IllegalArgumentException("Item Index out of Bounds!");
	  itemsState.set(index, enabled);
	 }
	 
	 public boolean isItemEnabled(int index) {
	  if (index < 0 || index >= itemsState.size()) throw new IllegalArgumentException("Item Index out of Bounds!");
	  return itemsState.get(index);
	 }
}
