package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.setsApp.internal.model.SetOperations;
import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class SetOperationsTask extends AbstractTask {
	@Tunable(description="Enter a name for the new set:")
	public String setName;
	@Tunable(description="Select name of second set:")
	public ListSingleSelection<String> seT2;
	@Tunable(description="Select name of first set:")
	public ListSingleSelection<String> seT1;
	private SetsManager sm;
	private SetOperations operation;
	private String s1, s2;
	
	public SetOperationsTask(SetsManager setsManager, Class<? extends CyIdentifiable> type, SetOperations s) {
		sm = setsManager;
		List<String> attr = sm.getSetNames(type); 
		seT1 = new ListSingleSelection<String>(attr);
		seT2 = new ListSingleSelection<String>(attr);
		operation = s;
	}
	
	public SetOperationsTask(SetsManager setsManager, String set1, String set2, SetOperations s) {
		sm = setsManager;
		seT1 = null;
		seT2 = null;
		s1 = set1;
		s2 = set2;
		operation = s;
	}
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		String set1, set2;
		if (seT1 != null && seT2 != null) {
			set1 = seT1.getSelectedValue();
			set2 = seT2.getSelectedValue();
		}
		else {
			set1 = s1;
			set2 = s2;
		}
		switch (operation) {
		case INTERSECT:
			sm.intersection(setName, set1, set2);
			break;
		case DIFFERENCE:
			sm.difference(setName, set1, set2);
			break;
		case UNION:
			sm.union(setName, set1, set2);
			break;
		default:
			break;
		}
	}

}
