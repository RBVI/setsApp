package edu.ucsf.rbvi.setsApp.internal.layouts;

import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class ForceDirectedLayoutAlgorithm extends AbstractLayoutAlgorithm {
  final SetsManager setsMgr;
  public ForceDirectedLayoutAlgorithm(final SetsManager setsMgr, final UndoSupport undoSupport) {
    super("sets-based-force-directed-layout", "Sets-based Force Directed Layout", undoSupport);
    this.setsMgr = setsMgr;
  }

  public Object createLayoutContext() {
    return new ForceDirectedLayoutContext();
  }

  public boolean isReady(CyNetworkView view, Object tunableContext, Set<View<CyNode>> nodesToLayout, String attributeName) {
    return setsMgr.getSetsFor(view.getModel(), CyNode.class).size() > 0;
  }

  public boolean getSupportsSelectedOnly() {
    return false;
  }

  public TaskIterator createTaskIterator(CyNetworkView networkView, Object layoutContext, Set<View<CyNode>> nodesToLayOut, String layoutAttribute) {
    ForceDirectedLayoutContext settings = null;
    if (layoutContext != null && (layoutContext instanceof ForceDirectedLayoutContext)) {
      settings = (ForceDirectedLayoutContext) layoutContext;
    }
    if (settings == null) {
      settings = new ForceDirectedLayoutContext();
    }
    return new TaskIterator(new ForceDirectedLayoutTask(networkView, setsMgr, settings));
  }
}