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

import edu.ucsf.rbvi.setsApp.internal.CyIdType;

public class MoveCyIdTask extends AbstractTask {
	@Tunable(description="Select set to move/copy to:")
	public ListSingleSelection<String> sets;
	private SetsManager mgr;
	private CyIdentifiable cyId;
	private String setName;
	
	public MoveCyIdTask (SetsManager manager, String s, CyIdentifiable cyid) {
		mgr = manager;
		cyId = cyid;
		if (cyid instanceof CyNode)
			sets = new ListSingleSelection<String>(getSelectedSets(CyIdType.NODE));
		if (cyid instanceof CyEdge)
			sets = new ListSingleSelection<String>(getSelectedSets(CyIdType.EDGE));
		setName = s;
	}
	
	private List<String> getSelectedSets(CyIdType type) {
		ArrayList<String> selectSet = new ArrayList<String>();
		List<String> mySetNames = mgr.getSetNames();
		if (type == CyIdType.NODE) {
			for (String s: mySetNames)
				if (mgr.getType(s) == CyIdType.NODE)
					selectSet.add(s);
		}
		if (type == CyIdType.EDGE) {
			for (String s: mySetNames)
				if (mgr.getType(s) == CyIdType.EDGE)
					selectSet.add(s);
		}
		return selectSet;
	}
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		// TODO Auto-generated method stub
		if (mgr.addToSet(sets.getSelectedValue(), cyId))
			mgr.removeFromSet(setName, cyId);
	}

}
