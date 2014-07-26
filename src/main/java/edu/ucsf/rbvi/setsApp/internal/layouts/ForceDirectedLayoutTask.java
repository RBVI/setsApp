package edu.ucsf.rbvi.setsApp.internal.layouts;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import  java.awt.geom.Point2D;
import  java.awt.geom.Rectangle2D;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import prefuse.util.force.DragForce;
import prefuse.util.force.ForceItem;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;
import edu.ucsf.rbvi.setsApp.internal.model.Set;

public class ForceDirectedLayoutTask extends AbstractTask {
  final CyNetworkView netView;
  final SetsManager setsMgr;
  final ForceDirectedLayoutContext settings;

  public ForceDirectedLayoutTask(
      final CyNetworkView netView,
      final SetsManager setsMgr,
      final ForceDirectedLayoutContext settings) {
    this.netView = netView;
    this.setsMgr = setsMgr;
    this.settings = settings;
  }

  public void run(final TaskMonitor monitor) {
    final ForceSimulator sim = new ForceSimulator();
    sim.addForce(new NBodyForce());
    sim.addForce(new SpringForce());
    sim.addForce(new DragForce());

    final CyNetwork network = netView.getModel();
    final List<Set<CyNode>> sets = setsMgr.getSetsFor(netView.getModel(), CyNode.class);

    // create force items for each node
    final Map<CyNode,ForceItem> items = new HashMap<CyNode,ForceItem>();
    for (final Set<CyNode> set : sets) {
      for (final CyNode node : set.getElements()) {
        if (items.containsKey(node)) {
          continue;
        }
        final ForceItem item = new ForceItem();
        item.mass = (float) settings.nodeMass;
        item.location[0] = 0f;
        item.location[1] = 0f;
        sim.addItem(item);
        items.put(node, item);
      }
    }

    final float edgeSpringCoeff = (float) Math.pow(10, -settings.edgeSpringCoeffNLog);
    final float groupSpringCoeff = (float) Math.pow(10, -settings.groupSpringCoeffNLog);

    // create springs for each edge
    for (final CyEdge edge : network.getEdgeList()) {
      final CyNode src = edge.getSource();
      final CyNode trg = edge.getTarget();
      final ForceItem srcItem = items.get(src);
      final ForceItem trgItem = items.get(trg);
      if (srcItem == null || trgItem == null) {
        continue;
      }
      sim.addSpring(srcItem, trgItem, (float) edgeSpringCoeff, (float) settings.edgeSpringLength);
    }

    // create springs between all intra-group nodes
    for (final Set<CyNode> set : sets) {
      final Collection<CyNode> nodes = set.getElements();
      for (final CyNode src : nodes) {
        final ForceItem srcItem = items.get(src);
        for (final CyNode trg : nodes) {
          final ForceItem trgItem = items.get(trg);
          sim.addSpring(srcItem, trgItem, (float) groupSpringCoeff, (float) settings.groupSpringLength);
        }
      }
    }

    // run force simulation
    final int iters = settings.iterations;
    long timestep = 1000L;
    for (int i = 0; i < iters && !cancelled; i++) {
      timestep *= (1.0 - i/(double)iters);
      long step = timestep+50;
      sim.runSimulator(step);
      monitor.setProgress(i / ((double) iters));
    }

    // update node locations
    for (final CyNode node : items.keySet()) {
      final ForceItem item = items.get(node);
      final double x = item.location[0];
      final double y = item.location[1];
      final View<CyNode> nodeView = netView.getNodeView(node);
      nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
      nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y);
    }

    netView.fitContent();
    netView.updateView();
  }
}