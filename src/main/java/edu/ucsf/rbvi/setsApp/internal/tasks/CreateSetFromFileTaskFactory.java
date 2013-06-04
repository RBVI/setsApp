package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class CreateSetFromFileTaskFactory extends AbstractTaskFactory {
	private SetsManager mgr;
	private CyNetworkManager netMgr;
	
	public CreateSetFromFileTaskFactory(SetsManager manager, CyNetworkManager netMgr) {
		mgr = manager;
		this.netMgr = netMgr;
	}
	
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new CreateSetFromFileTask(mgr, netMgr));
	}

}
