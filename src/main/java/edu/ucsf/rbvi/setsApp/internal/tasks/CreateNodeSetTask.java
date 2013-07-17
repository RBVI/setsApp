package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.List;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class CreateNodeSetTask extends AbstractTask implements ObservableTask {
	
	// This will require Cytoscape 3.1
	// @Tunable(description="Enter a name for the set:",tooltip="The set name must be unique and will appear in the 'Sets' panel",gravity=1.0)
	@Tunable(description="Enter a name for the set:")
	public String name;

	// This will require Cytoscape 3.1
	// @Tunable(description="List of nodes for selection",context="nogui",gravity=2.0)
	// public String nodeList;

	private SetsManager mgr;
	private CyNetworkView cnv;

	public CreateNodeSetTask(SetsManager manager, CyNetworkView view) {
		mgr = manager;
		cnv = view;
	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		List<CyNode> nodes = CyTableUtil.getNodesInState(cnv.getModel(), CyNetwork.SELECTED, true);
		arg0.showMessage(TaskMonitor.Level.INFO,
		                 "Creating set "+name+" with "+nodes.size()+" nodes");
		mgr.createSet(name, cnv.getModel(), CyNode.class, nodes);
	
		if (nodes == null)
			arg0.showMessage(TaskMonitor.Level.INFO, 
			                 "Created new empty node set: "+name);
		else
			arg0.showMessage(TaskMonitor.Level.INFO, 
			                 "Created new node set: "+name+" with "+nodes.size()+" nodes");
	}

	// Return the updated set
	public Object getResults(Class expectedType) {
		if (expectedType.equals(String.class)) {
			return mgr.getSet(name).toString();
		}
		return mgr.getSet(name);
	}

}
