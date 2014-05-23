package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.command.util.EdgeList;
import org.cytoscape.command.util.NodeList;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.setsApp.internal.model.Set;
import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class RemoveFromSetTask extends AbstractTask implements ObservableTask {
	
	// @Tunable(description="Enter a name for the set:")
	// This will require Cytoscape 3.1
	@Tunable(description="Enter a name for the set:",
           tooltip="The set name must be unique and will appear in the 'Sets' panel",
           gravity=1.0)
	public String name;

	@Tunable(description="Network for this set", context="nogui")
	public CyNetwork network;

	public NodeList nodeList = new NodeList(null);
	@Tunable(description="Nodes to be removed from this set", context="nogui")
	public NodeList getnodeList() {
		if (network == null) network = mgr.getCurrentNetwork();
		nodeList.setNetwork(network);
		return nodeList;
	}
	public void setnodeList(NodeList setValue) {}

	public EdgeList edgeList = new EdgeList(null);
	@Tunable(description="Edges to removed from in this set", context="nongui")
	public EdgeList getedgeList() {
		if (network == null) network = mgr.getCurrentNetwork();
		edgeList.setNetwork(network);
		return edgeList;
	}
	public void setedgeList(EdgeList setValue) {}

	private SetsManager mgr;

	public RemoveFromSetTask(SetsManager manager) {
		mgr = manager;
	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		arg0.setTitle("Removing from set");
		if (name == null) {
			arg0.showMessage(TaskMonitor.Level.ERROR,
					"The name of the set must be specified");
			return;
		}

		Set set = mgr.getSet(name);
		if (set == null) {
			arg0.showMessage(TaskMonitor.Level.ERROR,
					"Can't find the set '"+name+"'");
			return;
		}

		List<CyNode> nodes = null;
		List<CyEdge> edges = null;
		if (network != null) {
			nodes = nodeList.getValue();
			edges = edgeList.getValue();
		}

		if (set.getType().equals(CyNode.class)) {
			if(edges != null) {
				arg0.showMessage(TaskMonitor.Level.ERROR,
						"Can't remove edges from node set '"+name+"'");
				return;
			} else if (nodes == null) {
				arg0.showMessage(TaskMonitor.Level.ERROR,
						"No nodes to remove from node set '"+name+"'");
				return;
			}
			mgr.removeFromSet(name, new ArrayList<CyIdentifiable>(nodes));
			arg0.showMessage(TaskMonitor.Level.INFO,
	                 "Removed "+nodes.size()+" nodes from node set '"+name+"'");
			return;
		}

		if(nodes != null) {
			arg0.showMessage(TaskMonitor.Level.ERROR,
					"Can't remove nodes from edge set '"+name+"'");
			return;
		} else if (edges == null) {
			arg0.showMessage(TaskMonitor.Level.ERROR,
					"No edges to remove from edge set '"+name+"'");
			return;
		}
		mgr.removeFromSet(name, new ArrayList<CyIdentifiable>(edges));
		arg0.showMessage(TaskMonitor.Level.INFO,
                 "Removed "+edges.size()+" edges from edge set '"+name+"'");
		return;
	}

	// Return the updated set
	public Object getResults(Class expectedType) {
		if (expectedType.equals(String.class)) {
			return mgr.getSet(name).toString();
		}
		return mgr.getSet(name);
	}

}
