package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.setsApp.internal.CyIdType;

public class CreateSetFromAttributesTaskFactory extends
		AbstractNetworkTaskFactory {
	private SetsManager mgr;
	private CyIdType t;
	
	public CreateSetFromAttributesTaskFactory(SetsManager manager, CyIdType type) {
		mgr = manager;
		t = type;
	}
	
	public TaskIterator createTaskIterator(CyNetwork arg0) {
		return new TaskIterator(new CreateSetFromAttributeTask(mgr,arg0,t));
	}

}
