package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

public class CreateSetFromFileTaskFactory extends AbstractNetworkTaskFactory {
	private SetsManager mgr;
	
	public boolean isReady(CyNetwork network) {
		if (super.isReady(network) && mgr.setsCount() != 0)
			return true;
		else return false;
	}
	
	public CreateSetFromFileTaskFactory(SetsManager manager) {
		mgr = manager;
	}
	
	public TaskIterator createTaskIterator(CyNetwork arg0) {
		return new TaskIterator(new CreateSetFromFileTask3(mgr, arg0));
	}

}
