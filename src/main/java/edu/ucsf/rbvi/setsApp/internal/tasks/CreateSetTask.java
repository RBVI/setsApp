package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.setsApp.internal.CyIdType;

public class CreateSetTask extends AbstractTask {
	@Tunable(description="Enter a name for the set:")
	public String setName;
	private SetsManager setsManager;
	private CyNetworkViewManager nvm = null;
	private List<CyNode> cyNodes = null;
	private List<CyEdge> cyEdges = null;
	private CyIdType type;
	private String newName;
	
	public CreateSetTask(SetsManager mgr, String name, List<CyNode> nodes, List<CyEdge> edges) {
		setsManager = mgr;
		newName = name;
		cyNodes = nodes;
		cyEdges = edges;
	}

	public CreateSetTask(SetsManager mgr, String name, CyNetworkViewManager networkViewManager, CyIdType t) {
		setsManager = mgr;
		newName = name;
		nvm = networkViewManager;
		type = t;
	}
	
	public void run(TaskMonitor taskMonitor) throws Exception {
		if (newName == null) newName = setName;
		if (nvm != null)
			setsManager.createSetFromSelectedNetwork(newName, nvm, type);
		else
			setsManager.createSet(newName, cyNodes, cyEdges);
	}

}