package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class RemoveEdgeTask extends AbstractTask {
	@Tunable(description="Select set to remove edges from:")
	public ListSingleSelection<String> sets;
	private SetsManager mgr;
	private CyEdge edge;
	private static Logger messages = LoggerFactory
			.getLogger("CyUserMessages.setsApp");
	
	public RemoveEdgeTask (SetsManager manager, CyEdge cyEdge) {
		mgr = manager;
		edge = cyEdge;
		sets = new ListSingleSelection<String>(mgr.getSetNames(CyEdge.class));
	}
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		mgr.removeFromSet(sets.getSelectedValue(), edge);
		messages.info("Removed edge "+edge+" from set "+sets.getSelectedValue());
	}

}
