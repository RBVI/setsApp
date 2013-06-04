package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;


public class WriteSetToFileTaskFactory extends AbstractNetworkTaskFactory {
	private SetsManager mgr;
	private Class<? extends CyIdentifiable> type;
	
	public WriteSetToFileTaskFactory(SetsManager manager, Class<? extends CyIdentifiable> t) {
		mgr = manager;
		type = t;
	}
	
	public boolean isReady(CyNetwork network) {
		if (super.isReady(network)) {
			if (mgr.setsCount() != 0)
				return true;
			return false;
		}
		else return false;
	}
	
	public TaskIterator createTaskIterator(CyNetwork arg0) {
		return new TaskIterator(new WriteSetToFileTask3(mgr, arg0, type));
	}

}
