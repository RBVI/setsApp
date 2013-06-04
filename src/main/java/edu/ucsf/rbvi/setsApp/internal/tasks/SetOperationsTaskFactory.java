package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;
import edu.ucsf.rbvi.setsApp.internal.model.SetOperations;

public class SetOperationsTaskFactory extends AbstractTaskFactory {
	
	private SetsManager mgr;
	private Class<? extends CyIdentifiable> t;
	private SetOperations s;
	
	public boolean isReady() {
		if (mgr.setsCount() != 0)
			return true;
		else return false;
	}
	public SetOperationsTaskFactory(SetsManager manager, Class<? extends CyIdentifiable> type, SetOperations sets) {
		mgr = manager;
		t = type;
		s = sets;
	}
	
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new SetOperationsTask(mgr, t, s));
	}

}
