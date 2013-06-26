package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ucsf.rbvi.setsApp.internal.model.Set;
import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class AddNodeTask extends AbstractTask {
	private static Logger messages = LoggerFactory
			.getLogger("CyUserMessages.setsApp");
	
	@Tunable(description="Select set to put nodes in:")
	public ListSingleSelection<String> sets;
	private SetsManager mgr;
	private CyNode node;
	
	public AddNodeTask (SetsManager manager, CyNode cyNode) {
		mgr = manager;
		node = cyNode;
		sets = new ListSingleSelection<String>(mgr.getSetNames(CyNode.class));
	}
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		mgr.addToSet(sets.getSelectedValue(), node);
		messages.info("Added node "+node+" to set "+sets.getSelectedValue());
	}

}
