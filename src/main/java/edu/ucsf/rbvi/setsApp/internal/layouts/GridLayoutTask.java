package edu.ucsf.rbvi.setsApp.internal.layouts;

import java.util.List;
import java.util.Collection;

import  java.awt.geom.Point2D;
import  java.awt.geom.Rectangle2D;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;
import edu.ucsf.rbvi.setsApp.internal.model.Set;

class GridLayoutTask extends AbstractTask {
  final CyNetworkView netView;
  final SetsManager setsMgr;
  final GridLayoutContext settings;

  public GridLayoutTask(
      final CyNetworkView netView,
      final SetsManager setsMgr,
      final GridLayoutContext settings) {
    this.netView = netView;
    this.setsMgr = setsMgr;
    this.settings = settings;
  }

  public void run(final TaskMonitor monitor) {
    final List<Set<CyNode>> sets = setsMgr.getSetsFor(netView.getModel(), CyNode.class);
    final int cols = numOfCols(sets.size());
    final Point2D.Double pos = new Point2D.Double(0.0, 0.0);
    final Rectangle2D.Double size = new Rectangle2D.Double();
    double maxRowH = 0.0;
    int col = 0;
    for (final Set<CyNode> set : sets) {
      layoutSet(set, pos, size);

      col++;
      if (col >= cols) {
        pos.x = 0.0;
        pos.y += maxRowH + settings.setSpaceY;
        maxRowH = 0.0;
        col = 0;
      } else {
        pos.x += settings.setSpaceX + size.width;
        maxRowH = Math.max(maxRowH, size.height);
      }
    }

    netView.fitContent();
    netView.updateView();
  }

  private void layoutSet(final Set<CyNode> set, final Point2D.Double pos, final Rectangle2D.Double size) {
    final Collection<CyNode> nodes = set.getElements();
    double maxRowH = 0.0;
    double maxX = 0.0;
    double x = pos.x;
    double y = pos.y;
    final int cols = numOfCols(nodes.size());
    int col = 0;


    for (final CyNode node : nodes) {
      final View<CyNode> view = netView.getNodeView(node);
      view.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
      view.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y);

      final double w = view.getVisualProperty(BasicVisualLexicon.NODE_WIDTH);
      final double h = view.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT);

      maxX = Math.max(maxX, x);
      col++;
      if (col >= cols) {
        y += maxRowH + settings.nodeSpaceX;
        maxRowH = 0.0;
        x = pos.x;
        col = 0;
      } else {
        x += w + settings.nodeSpaceY;
        maxRowH = Math.max(maxRowH, h);
      }
    }
    size.setRect(pos.x, pos.y, maxX - pos.x, y - pos.y);
  }

  static int numOfCols(final int n) {
    return (int) Math.ceil(Math.sqrt(n));
  }
}