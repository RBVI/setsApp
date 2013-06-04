package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.model.CyEdge;
import org.cytoscape.task.AbstractEdgeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class RemoveEdgeViewTaskFactory extends AbstractEdgeViewTaskFactory {
	
	private SetsManager mgr;
	public RemoveEdgeViewTaskFactory (SetsManager manager) {
		mgr = manager;
	}
	
	public boolean isReady(View<CyEdge> edgeView, CyNetworkView networkView) {
		if (super.isReady(edgeView, networkView) && mgr.setsCount() == 0) return false;
		if (mgr.isInSet(edgeView.getModel().getSUID()))
			return true;
		return false;
	}
	
	public TaskIterator createTaskIterator(View<CyEdge> arg0, CyNetworkView arg1) {
		return new TaskIterator(new RemoveEdgeTask(mgr, arg0.getModel()));
	}

}
