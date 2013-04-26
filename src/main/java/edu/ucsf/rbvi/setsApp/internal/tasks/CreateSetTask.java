package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

public class CreateSetTask extends AbstractTask {
	@Tunable(description="Enter a name for the set:")
	public String setName;
	private SetsManager setsManager;
	private List<CyNode> cyNodes = null;
	private List<CyEdge> cyEdges = null;
	private List<CyIdentifiable> cyIds = null;
	private String newName;
	
	public CreateSetTask(SetsManager mgr, String name, List<CyNode> nodes, List<CyEdge> edges) {
		setsManager = mgr;
		newName = name;
		cyNodes = nodes;
		cyEdges = edges;
	}

	public CreateSetTask(SetsManager mgr, String name, List<CyIdentifiable> cyids) {
		setsManager = mgr;
		newName = name;
		cyIds = cyids;
	}
	
	public void run(TaskMonitor taskMonitor) throws Exception {
		if (newName == null) newName = setName;
		if (cyIds != null)
			setsManager.createSet(newName, cyIds);
		else
			setsManager.createSet(newName, cyNodes, cyEdges);
	}

}