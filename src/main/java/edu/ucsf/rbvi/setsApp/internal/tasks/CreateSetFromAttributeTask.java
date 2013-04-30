package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.setsApp.internal.CyIdType;

public class CreateSetFromAttributeTask extends AbstractTask {
	@Tunable(description = "Enter the value of the attribute:")
	public String attrName;
	@Tunable(description="Enter the name of the column:")
	public String colName;
	@Tunable(description="Enter the name of a network:")
	public String networkName;
	@Tunable(description="Enter a name for the set:")
	public String setName;
	private SetsManager setsManager;
	private CyNetworkManager cyNetwork = null;
	private List<CyNode> cyNodes = null;
	private List<CyEdge> cyEdges = null;
	private CyIdType type;
	private String newName;

	public CreateSetFromAttributeTask(SetsManager mgr, String name, CyNetworkManager network, CyIdType t) {
		setsManager = mgr;
		newName = name;
		cyNetwork = network;
		type = t;
	}
	
	public void run(TaskMonitor taskMonitor) throws Exception {
		if (newName == null) newName = setName;
		setsManager.createSetFromAttributes(newName, colName, attrName, cyNetwork, networkName, type);
	}

}