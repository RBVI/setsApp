package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

public class AddNodeViewTaskFactory extends AbstractNodeViewTaskFactory {
	
	private SetsManager mgr;
	public AddNodeViewTaskFactory (SetsManager manager) {
		mgr = manager;
	}
	
	public TaskIterator createTaskIterator(View<CyNode> arg0, CyNetworkView arg1) {
		return new TaskIterator(new AddNodeTask(mgr, arg0.getModel()));
	}

}
