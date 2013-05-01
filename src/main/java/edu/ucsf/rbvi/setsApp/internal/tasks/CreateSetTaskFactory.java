package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.setsApp.internal.CyIdType;
import edu.ucsf.rbvi.setsApp.internal.SetOperations;



public class CreateSetTaskFactory extends AbstractTaskFactory {

	private SetsManager setsManager;
	
	public CreateSetTaskFactory() {}
	
	public CreateSetTaskFactory(SetsManager mgr) {
		setsManager = mgr;
	}
		
	public boolean isReady(CyNetwork network) {
		return true;
	}
	
	public TaskIterator createTaskIterator() {
		return null;
	}
	
	public TaskIterator createTaskIterator(CyNetwork network, CyIdType type) {
		return new TaskIterator(new CreateSetFromAttributeTask(setsManager, network, type));
	}
	
	public TaskIterator createTaskIterator(String setName, CyNetworkViewManager networkViewManager, CyIdType t) {
		return new TaskIterator(new CreateSetTask(setsManager, setName, networkViewManager, t));
	}
	
	public TaskIterator createTaskIterator(String setName, CyNetwork cyNetwork, List<CyNode> cyNodes, List<CyEdge> cyEdges) {
		return new TaskIterator(new RestoreSetTask(setsManager, cyNetwork, setName, cyNodes, cyEdges));
	}

/*	public TaskIterator createTaskIterator(String setName, String set1, String set2, SetOperations operation) {
		return new TaskIterator(new SetOperationsTask(setsManager, setName, set1, set2, operation));
	} */
	
	public TaskIterator createTaskIterator(CyIdType type, SetOperations operation) {
		return new TaskIterator(new SetOperationsTask(setsManager, type, operation));
	}
	
/*	public TaskIterator createTaskIterator(List<String> attributes, SetOperations operation) {
		return new TaskIterator(new SetOperationsTask(setsManager, attributes, operation));
	} */
}