package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.Collection;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkCollectionTaskFactory;
import org.cytoscape.work.TaskIterator;

public class MoveSetTaskFactory extends AbstractNetworkCollectionTaskFactory {
	
	private SetsManager mgr;
	
	public boolean isReady(Collection<CyNetwork> networks) {
		if (super.isReady(networks)) {
			if (mgr.setsCount() != 0) return true;
			else return false;
		}
		else return false;
	}
	
	public MoveSetTaskFactory(SetsManager manager) {
		mgr = manager;
	}
	
	public TaskIterator createTaskIterator(Collection<CyNetwork> arg0) {
		return new TaskIterator(new MoveSetTask(mgr, arg0));
	}

}
