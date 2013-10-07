package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class CreateEdgeSetTaskFactory extends AbstractNetworkViewTaskFactory implements TaskFactory {
	
	private SetsManager mgr;
	
	@Override
	public boolean isReady(CyNetworkView networkView) {
		if (super.isReady(networkView))
			if (!CyTableUtil.getEdgesInState(networkView.getModel(), CyNetwork.SELECTED, true).isEmpty())
				return true;
		return false;
	}
	
	public CreateEdgeSetTaskFactory(SetsManager manager) {
		mgr = manager;
	}
	
	public TaskIterator createTaskIterator(CyNetworkView arg0) {
		// TODO Auto-generated method stub
		return new TaskIterator(new CreateEdgeSetTask(mgr,arg0.getModel()));
	}

	public TaskIterator createTaskIterator() {
		return new TaskIterator(new CreateEdgeSetTask(mgr,null));
	}

	public boolean isReady() {
		// TODO Auto-generated method stub
		if (mgr.getCurrentNetwork() != null && !CyTableUtil.getEdgesInState(mgr.getCurrentNetwork(), CyNetwork.SELECTED, true).isEmpty())
			return true;
		return false;
	}

}
