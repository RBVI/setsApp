package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.setsApp.internal.CyIdType;
import edu.ucsf.rbvi.setsApp.internal.SetOperations;

public class SetOperationsTaskFactory extends AbstractTaskFactory {
	
	private SetsManager mgr;
	private CyIdType t;
	private SetOperations s;
	
	public boolean isReady() {
		if (mgr.setsCount() != 0)
			return true;
		else return false;
	}
	public SetOperationsTaskFactory(SetsManager manager, CyIdType type, SetOperations sets) {
		mgr = manager;
		t = type;
		s = sets;
	}
	
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new SetOperationsTask(mgr, t, s));
	}

}
