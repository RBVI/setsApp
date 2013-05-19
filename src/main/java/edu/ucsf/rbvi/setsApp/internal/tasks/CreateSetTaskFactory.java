package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import edu.ucsf.rbvi.setsApp.internal.tasks.WriteSetToFileTask2;



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
	
	public TaskIterator createTaskIterator(CyIdType type, SetOperations operation) {
		return new TaskIterator(new SetOperationsTask(setsManager, type, operation));
	}
	
	public TaskIterator createTaskIterator(CyNetworkManager cnm, BufferedReader stream, CyIdType t) {
		return new TaskIterator(new CreateSetFromFileTask(setsManager, cnm, stream, t));
	}
	
	public TaskIterator createTaskIterator(CyNetworkManager cnm, File file) {
		return new TaskIterator(new CreateSetFromFileTask2(setsManager, cnm, file));
	}
	
	public TaskIterator createTaskIterator(String name, File file) {
		return new TaskIterator(new WriteSetToFileTask2(setsManager,name,file));
	}
}