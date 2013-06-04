package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.setsApp.internal.model.Set;
import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class MoveCyIdTask extends AbstractTask {
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
		// TODO Auto-generated method stub
		mgr.addToSet(sets.getSelectedValue(), cyId);
		mgr.removeFromSet(setName, cyId);
	}

}
