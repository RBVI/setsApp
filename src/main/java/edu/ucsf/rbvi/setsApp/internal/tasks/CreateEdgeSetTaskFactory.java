package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

public class CreateEdgeSetTaskFactory extends AbstractNetworkViewTaskFactory {
	
	private SetsManager mgr;
	
	@Override
	public boolean isReady(CyNetworkView networkView) {
		if (networkView != null)
			if (!CyTableUtil.getEdgesInState(networkView.getModel(), CyNetwork.SELECTED, true).isEmpty())
				return true;
		return false;
	}
	
	public CreateEdgeSetTaskFactory(SetsManager manager) {
		mgr = manager;
	}
	
	public TaskIterator createTaskIterator(CyNetworkView arg0) {
		// TODO Auto-generated method stub
		return new TaskIterator(new CreateEdgeSetTask(mgr,arg0));
	}

}
