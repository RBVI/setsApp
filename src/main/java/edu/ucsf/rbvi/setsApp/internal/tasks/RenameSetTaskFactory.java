package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class RenameSetTaskFactory extends AbstractTaskFactory {
	private SetsManager mgr;
	
	public boolean isReady() {
		if (mgr.setsCount() == 0)
			return false;
		else return true;
	}
	
	public RenameSetTaskFactory(SetsManager manager) {
		mgr = manager;
	}
	
	public TaskIterator createTaskIterator() {
		// TODO Auto-generated method stub
		return new TaskIterator(new RenameSetTask2(mgr));
	}

}
