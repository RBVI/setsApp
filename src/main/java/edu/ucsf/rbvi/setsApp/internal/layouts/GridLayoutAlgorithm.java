package edu.ucsf.rbvi.setsApp.internal.layouts;

import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class GridLayoutAlgorithm extends AbstractLayoutAlgorithm {
  final SetsManager setsMgr;
  public GridLayoutAlgorithm(final SetsManager setsMgr, final UndoSupport undoSupport) {
    super("sets-based-grid-layout", "Sets-based Grid Layout", undoSupport);
    this.setsMgr = setsMgr;
  }

  public Object createLayoutContext() {
    return new GridLayoutContext();
  }

  public boolean isReady(CyNetworkView view, Object tunableContext, Set<View<CyNode>> nodesToLayout, String attributeName) {
    return setsMgr.getSetsFor(view.getModel(), CyNode.class).size() > 0;
  }

  public boolean getSupportsSelectedOnly() {
    return false;
  }

  public TaskIterator createTaskIterator(CyNetworkView networkView, Object layoutContext, Set<View<CyNode>> nodesToLayOut, String layoutAttribute) {
    GridLayoutContext settings = null;
    if (layoutContext != null && (layoutContext instanceof GridLayoutContext)) {
      settings = (GridLayoutContext) layoutContext;
    }
    if (settings == null) {
      settings = new GridLayoutContext();
    }
    return new TaskIterator(new GridLayoutTask(networkView, setsMgr, settings));
  }
}