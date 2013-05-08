package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyNode;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.setsApp.internal.CyIdType;

public class RemoveNodeTask extends AbstractTask {
	@Tunable(description="Select set to remove nodes from:")
	public ListSingleSelection<String> sets;
	private SetsManager mgr;
	private CyNode node;
	
	public RemoveNodeTask (SetsManager manager, CyNode cyNode) {
		mgr = manager;
		node = cyNode;
		sets = new ListSingleSelection<String>(getSelectedSets(CyIdType.NODE));
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
		
	}

}
