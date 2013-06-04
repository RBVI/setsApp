package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class AddNodeViewTaskFactory extends AbstractNodeViewTaskFactory {
	
	private SetsManager mgr;
	public AddNodeViewTaskFactory (SetsManager manager) {
		mgr = manager;
	}
	
	public boolean isReady(View<CyNode> nodeView, CyNetworkView networkView) {
		if (super.isReady(nodeView, networkView) && mgr.setsCount() == 0) return false;
		else return true;
	}
	
	public TaskIterator createTaskIterator(View<CyNode> arg0, CyNetworkView arg1) {
		return new TaskIterator(new AddNodeTask(mgr, arg0.getModel()));
	}

}
