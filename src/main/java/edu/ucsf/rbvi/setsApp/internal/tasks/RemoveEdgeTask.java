package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class RemoveEdgeTask extends AbstractTask implements ObservableTask {
	@Tunable(description="Select set to remove edges from:")
	public ListSingleSelection<String> sets;
	private SetsManager mgr;
	private CyEdge edge;
	
	public RemoveEdgeTask (SetsManager manager, CyEdge cyEdge) {
		mgr = manager;
		edge = cyEdge;
		sets = new ListSingleSelection<String>(mgr.getSetNames(CyEdge.class));
	}
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		arg0.setTitle("Removing edge from set");
		mgr.removeFromSet(sets.getSelectedValue(), edge);
		arg0.showMessage(TaskMonitor.Level.INFO, 
		                 "Removed edge "+edge+" from set "+sets.getSelectedValue());
	}

	// Return the updated set
	public Object getResults(Class expectedType) {
		if (expectedType.equals(String.class)) {
			return mgr.getSet(sets.getSelectedValue()).toString();
		}
		return mgr.getSet(sets.getSelectedValue());
	}

}
