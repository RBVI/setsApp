package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.setsApp.internal.CyIdType;

public class CreateEdgeSetTask extends AbstractTask {
	
	@Tunable(description="Enter a name for the set:")
	public String name;
	private SetsManager mgr;
	private CyNetworkView cnv;
	
	public CreateEdgeSetTask(SetsManager manager, CyNetworkView view) {
		mgr = manager;
		cnv = view;
	}
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		// TODO Auto-generated method stub
		mgr.createSetFromNetworkView(name, cnv, CyIdType.EDGE);
	}

}
