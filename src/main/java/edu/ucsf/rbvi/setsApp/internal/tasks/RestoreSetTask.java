package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.setsApp.internal.CyIdType;

public class RestoreSetTask extends AbstractTask {
	private SetsManager setsManager;
	private CyNetworkViewManager nvm = null;
	private CyNetwork cyNetwork = null;
	private List<CyNode> cyNodes = null;
	private List<CyEdge> cyEdges = null;
	private CyIdType type;
	private String newName;
	
	public RestoreSetTask(SetsManager mgr, CyNetwork network, String name, List<CyNode> nodes, List<CyEdge> edges) {
		setsManager = mgr;
		newName = name;
		cyNodes = nodes;
		cyEdges = edges;
		cyNetwork = network;
	}
	
	public void run(TaskMonitor taskMonitor) throws Exception {
		if (nvm != null)
			setsManager.createSetFromSelectedNetwork(newName, nvm, type);
		else
			setsManager.createSet(newName, cyNetwork, cyNodes, cyEdges);
	}

}