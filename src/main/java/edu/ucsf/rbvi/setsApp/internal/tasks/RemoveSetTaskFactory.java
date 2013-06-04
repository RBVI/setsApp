package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class RemoveSetTaskFactory extends AbstractTaskFactory {
	private SetsManager mgr;
	
	public RemoveSetTaskFactory(SetsManager manager) {
		mgr = manager;
	}
	
	public boolean isReady() {
		if (mgr.setsCount() == 0)
			return false;
		else return true;
	}
	
	public TaskIterator createTaskIterator() {
		// TODO Auto-generated method stub
		return new TaskIterator(new RemoveSetTask(mgr));
	}

}
