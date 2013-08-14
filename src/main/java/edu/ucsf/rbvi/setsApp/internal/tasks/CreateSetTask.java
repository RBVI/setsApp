package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.List;

import org.cytoscape.command.util.EdgeList;
import org.cytoscape.command.util.NodeList;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class CreateSetTask extends AbstractTask implements ObservableTask {
	
	// @Tunable(description="Enter a name for the set:")
	// This will require Cytoscape 3.1
	@Tunable(description="Enter a name for the set:",
           tooltip="The set name must be unique and will appear in the 'Sets' panel",
           gravity=1.0)
	public String name;

	@Tunable(description="Network for this set", context="nogui")
	public CyNetwork network;

	public NodeList nodeList = new NodeList(null);
	@Tunable(description="Nodes to be include in this set", context="nogui")
	public NodeList getnodeList() {
		if (network == null) network = mgr.getCurrentNetwork();
		nodeList.setNetwork(network);
		return nodeList;
	}
	public void setnodeList(NodeList setValue) {}

	public EdgeList edgeList = new EdgeList(null);
	@Tunable(description="Edges to be included in this set", context="nongui")
	public EdgeList getedgeList() {
		if (network == null) network = mgr.getCurrentNetwork();
		edgeList.setNetwork(network);
		return edgeList;
	}
	public void setedgeList(EdgeList setValue) {}
	// This will require Cytoscape 3.1
	// @Tunable(description="Network to create set for",context="nogui",gravity=2.0)
	// public CyNetwork network;
	//
	// @Tunable(description="List of nodes for selection",context="nogui",gravity=3.0)
	// public NodeList nodeList;

	private SetsManager mgr;
	private CyNetwork net;

	public CreateSetTask(SetsManager manager, CyNetwork net) {
		mgr = manager;
		this.net = net;
		// System.out.println("Creating node set task with net: "+net);
	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		List<CyNode> nodes = null;
		List<CyEdge> edges = null;
		if (net == null && network != null) {
			nodes = nodeList.getValue();
			edges = edgeList.getValue();
			net = network;
		}
		if (nodes != null && edges != null)
			arg0.showMessage(TaskMonitor.Level.ERROR,
					"Cannot create set containing both nodes and edges.");
		else if (nodes != null && edges == null) {
			arg0.showMessage(TaskMonitor.Level.INFO,
	                 "Creating set "+name+" with "+nodes.size()+" nodes");
			mgr.createSet(name, net, CyNode.class, nodes);
			arg0.showMessage(TaskMonitor.Level.INFO, 
	                 "Created new node set: "+name+" with "+nodes.size()+" nodes");
		}
		else if (nodes == null && edges != null) {
			arg0.showMessage(TaskMonitor.Level.INFO,
	                 "Creating set "+name+" with "+edges.size()+" edges");
			mgr.createSet(name, net, CyEdge.class, edges);
			arg0.showMessage(TaskMonitor.Level.INFO, 
	                 "Created new edge set: "+name+" with "+edges.size()+" edges");
		}
		else
			arg0.showMessage(TaskMonitor.Level.ERROR,
					"No nodes or edges chosen.");
	/*	if (nodes == null)
			arg0.showMessage(TaskMonitor.Level.INFO, 
			                 "Created new empty node set: "+name);
		else
			arg0.showMessage(TaskMonitor.Level.INFO, 
			                 "Created new node set: "+name+" with "+nodes.size()+" nodes"); */
	}

	// Return the updated set
	public Object getResults(Class expectedType) {
		if (expectedType.equals(String.class)) {
			return mgr.getSet(name).toString();
		}
		return mgr.getSet(name);
	}

}
