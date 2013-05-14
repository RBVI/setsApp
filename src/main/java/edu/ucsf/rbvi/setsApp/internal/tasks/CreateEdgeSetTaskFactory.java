package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

public class CreateEdgeSetTaskFactory extends AbstractNetworkViewTaskFactory {
	
	private SetsManager mgr;
	
	public CreateEdgeSetTaskFactory(SetsManager manager) {
		mgr = manager;
	}
	
	public TaskIterator createTaskIterator(CyNetworkView arg0) {
		// TODO Auto-generated method stub
		return new TaskIterator(new CreateEdgeSetTask(mgr,arg0));
	}

}
