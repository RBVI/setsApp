package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

public class CreateNodeSetTaskFactory extends AbstractNetworkViewTaskFactory {
	
	private SetsManager mgr;
	
	public CreateNodeSetTaskFactory(SetsManager manager) {
		mgr = manager;
	}
	
	public TaskIterator createTaskIterator(CyNetworkView arg0) {
		// TODO Auto-generated method stub
		return new TaskIterator(new CreateNodeSetTask(mgr,arg0));
	}

}
