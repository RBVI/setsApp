package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.setsApp.internal.model.Set;
import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class AddNodeTask extends AbstractTask implements ObservableTask {
	
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
		arg0.setTitle("Adding node to set "+sets.getSelectedValue());
		mgr.addToSet(sets.getSelectedValue(), node);
		arg0.showMessage(TaskMonitor.Level.INFO, 
		                 "Added node "+node+" to set "+sets.getSelectedValue());
	}

	public Object getResults(Class expectedType) {
		if (expectedType.equals(String.class)) {
			return mgr.getSet(sets.getSelectedValue()).toString();
		}
		return mgr.getSet(sets.getSelectedValue());
	}

}
