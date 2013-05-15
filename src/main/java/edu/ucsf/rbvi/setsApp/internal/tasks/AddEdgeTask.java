package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.setsApp.internal.CyIdType;
import edu.ucsf.rbvi.setsApp.internal.Set;

public class AddEdgeTask extends AbstractTask {
	
	@Tunable(description="Select set to put nodes in:")
	public ListSingleSelection<String> sets;
	private SetsManager mgr;
	private CyEdge edge;
	
	public AddEdgeTask (SetsManager manager, CyEdge cyEdge) {
		mgr = manager;
		edge = cyEdge;
		sets = new ListSingleSelection<String>(getSelectedSets(CyIdType.EDGE));
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
		mgr.addToSet(sets.getSelectedValue(), edge);
	}

}