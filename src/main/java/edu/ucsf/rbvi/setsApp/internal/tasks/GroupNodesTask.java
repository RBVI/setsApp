package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class GroupNodesTask extends AbstractTask {
	
	@Tunable(description="Network for this set", context="nogui")
	public CyNetwork network;

	private SetsManager mgr;
	private CyNetwork net;

	public GroupNodesTask(SetsManager manager, CyNetwork net) {
		mgr = manager;
		this.net = net;
	}

	@Override
	public void run(TaskMonitor monitor) throws Exception {
		if (net == null && network != null) {
			net = network;
		}
		mgr.group(net);
	}
}
