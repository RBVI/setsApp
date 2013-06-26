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

public class AddEdgeTask extends AbstractTask {
	private static Logger messages = LoggerFactory
			.getLogger("CyUserMessages.setsApp");
	
	@Tunable(description="Select set to put nodes in:")
	public ListSingleSelection<String> sets;
	private SetsManager mgr;
	private CyEdge edge;
	
	public AddEdgeTask (SetsManager manager, CyEdge cyEdge) {
		mgr = manager;
		edge = cyEdge;
		sets = new ListSingleSelection<String>(mgr.getSetNames(CyEdge.class));
	}
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		mgr.addToSet(sets.getSelectedValue(), edge);
		messages.info("Added edge "+edge+" to set "+sets.getSelectedValue());
	}

}
