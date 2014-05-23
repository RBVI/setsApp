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

import edu.ucsf.rbvi.setsApp.internal.model.Set;
import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class MoveCyIdTask extends AbstractTask implements ObservableTask {
	@Tunable(description="Select set to move to:")
	public ListSingleSelection<String> sets;
	private SetsManager mgr;
	private CyIdentifiable cyId;
	private String setName;
	
	public MoveCyIdTask (SetsManager manager, String s, CyIdentifiable cyid) {
		mgr = manager;
		cyId = cyid;
		if (cyid instanceof CyNode)
			sets = new ListSingleSelection<String>(mgr.getSetNames(CyNode.class));
		if (cyid instanceof CyEdge)
			sets = new ListSingleSelection<String>(mgr.getSetNames(CyEdge.class));
		setName = s;
	}
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		arg0.setTitle("Moving set");
		mgr.addToSet(sets.getSelectedValue(), cyId);
		mgr.removeFromSet(setName, cyId);
		arg0.showMessage(TaskMonitor.Level.INFO, 
			               "Moved "+cyId+" from set "+sets.getSelectedValue()+" to set "+setName);
	}

	// Return the updated set
	public Object getResults(Class expectedType) {
		if (expectedType.equals(String.class)) {
			return mgr.getSet(setName).toString();
		}
		return mgr.getSet(setName);
	}

}
