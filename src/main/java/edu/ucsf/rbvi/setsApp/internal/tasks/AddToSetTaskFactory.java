package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class AddToSetTaskFactory implements TaskFactory {
	
	private SetsManager mgr;
	
	public AddToSetTaskFactory(SetsManager manager) {
		mgr = manager;
	}
	
	public boolean isReady () {
		if (mgr.getCurrentNetwork() != null)
			return true;
		return false;
	}
	
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new AddToSetTask(mgr));
	}

}
