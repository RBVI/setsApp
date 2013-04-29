package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.setsApp.internal.CyIdType;
import edu.ucsf.rbvi.setsApp.internal.SetOperations;



public class CreateSetTaskFactory extends AbstractTaskFactory {

	private SetsManager setsManager;
/*	private String setName;
	private List<CyNode> cyNodes;
	private List<CyEdge> cyEdges; */
	
	public CreateSetTaskFactory() {}
	
	public CreateSetTaskFactory(SetsManager mgr) {
		setsManager = mgr;
	}
	
/*	public CreateSetTaskFactory(SetsManager mgr, String name, List<CyNode> nodes, List<CyEdge> edges) {
		setsManager = mgr;
		loadCyIdentifiables(name, nodes, edges);
	} */
	
/*	public void loadCyIdentifiables(String name, List<CyNode> nodes, List<CyEdge> edges) {
		setName = name;
		cyNodes = nodes;
		cyEdges = edges;
	} */
	
	public boolean isReady(CyNetwork network) {
		return true;
	}
	
	public TaskIterator createTaskIterator() {
		return null;
	}
	
	public TaskIterator createTaskIterator(String setName, CyNetworkViewManager networkViewManager, CyIdType t) {
		return new TaskIterator(new CreateSetTask(setsManager, setName, networkViewManager, t));
	}
	
	public TaskIterator createTaskIterator(String setName, CyNetwork cyNetwork, List<CyNode> cyNodes, List<CyEdge> cyEdges) {
		return new TaskIterator(new RestoreSetTask(setsManager, cyNetwork, setName, cyNodes, cyEdges));
	}

	public TaskIterator createTaskIterator(String setName, String set1, String set2, SetOperations operation) {
		return new TaskIterator(new SetOperationsTask(setsManager, setName, set1, set2, operation));
	}
}