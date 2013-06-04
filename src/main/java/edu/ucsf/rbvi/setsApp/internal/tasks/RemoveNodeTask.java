package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyNode;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class RemoveNodeTask extends AbstractTask {
	@Tunable(description="Select set to remove nodes from:")
	public ListSingleSelection<String> sets;
	private SetsManager mgr;
	private CyNode node;
	
	public RemoveNodeTask (SetsManager manager, CyNode cyNode) {
		mgr = manager;
		node = cyNode;
		sets = new ListSingleSelection<String>(mgr.getSetNames(CyNode.class));
	}
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		mgr.removeFromSet(sets.getSelectedValue(), node);
	}

}
