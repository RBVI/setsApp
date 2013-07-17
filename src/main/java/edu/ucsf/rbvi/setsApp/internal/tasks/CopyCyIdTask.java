package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class CopyCyIdTask extends AbstractTask implements ObservableTask {
	@Tunable(description="Select set to copy to:")
	public ListSingleSelection<String> sets;
	private SetsManager mgr;
	private CyIdentifiable cyId;
	
	public CopyCyIdTask (SetsManager manager, CyIdentifiable cyid) {
		mgr = manager;
		cyId = cyid;
		if (cyid instanceof CyNode)
			sets = new ListSingleSelection<String>(mgr.getSetNames(CyNode.class));
		else if (cyid instanceof CyEdge)
			sets = new ListSingleSelection<String>(mgr.getSetNames(CyEdge.class));
	}
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		mgr.addToSet(sets.getSelectedValue(), cyId);
		if (cyId instanceof CyNode)
			arg0.showMessage(TaskMonitor.Level.INFO, 
			                 "Added node to set "+sets.getSelectedValue());
		else if (cyId instanceof CyEdge)
			arg0.showMessage(TaskMonitor.Level.INFO, 
			                 "Added edge to set "+sets.getSelectedValue());
	}

	// Return the updated set
	public Object getResults(Class expectedType) {
		if (expectedType.equals(String.class)) {
			return mgr.getSet(sets.getSelectedValue()).toString();
		}
		return mgr.getSet(sets.getSelectedValue());
	}
}
