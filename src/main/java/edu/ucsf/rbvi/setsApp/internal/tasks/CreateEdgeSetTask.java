package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

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
		List<CyEdge> edges = CyTableUtil.getEdgesInState(cnv.getModel(), CyNetwork.SELECTED, true);
		mgr.createSet(name, cnv.getModel(), CyEdge.class, edges);
	}

}
