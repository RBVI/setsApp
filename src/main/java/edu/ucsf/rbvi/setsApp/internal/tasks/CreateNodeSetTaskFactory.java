package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class CreateNodeSetTaskFactory extends AbstractNetworkViewTaskFactory implements TaskFactory {
	
	private SetsManager mgr;
	
	public CreateNodeSetTaskFactory(SetsManager manager) {
		mgr = manager;
	}
	
	@Override
	public boolean isReady(CyNetworkView networkView) {
		if (super.isReady(networkView))
			if (!CyTableUtil.getNodesInState(networkView.getModel(), CyNetwork.SELECTED, true).isEmpty())
				return true;
		return false;
	}

	public boolean isReady () {
		return isReady(mgr.getCurrentNetworkView());
	}
	
	public TaskIterator createTaskIterator(CyNetworkView arg0) {
		System.out.println("NetworkViewTaskFactory createNodeSet");
		return new TaskIterator(new CreateNodeSetTask(mgr,arg0));
	}

	public TaskIterator createTaskIterator() {
		System.out.println("TaskFactory createNodeSet");
		return createTaskIterator(mgr.getCurrentNetworkView());
	}

}
