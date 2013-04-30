package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class RestoreSetTask extends AbstractTask {
	private SetsManager setsManager;
	private CyNetwork cyNetwork = null;
	private List<CyNode> cyNodes = null;
	private List<CyEdge> cyEdges = null;
	private String newName;
	
	public RestoreSetTask(SetsManager mgr, CyNetwork network, String name, List<CyNode> nodes, List<CyEdge> edges) {
		setsManager = mgr;
		newName = name;
		cyNodes = nodes;
		cyEdges = edges;
		cyNetwork = network;
	}
	
	public void run(TaskMonitor taskMonitor) throws Exception {
		setsManager.createSet(newName, cyNetwork, cyNodes, cyEdges);
	}

}