package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.setsApp.internal.SetOperations;

public class SetOperationsTask extends AbstractTask {
	@Tunable(description="Enter a name for the new set:")
	public String setName;
	@Tunable(description="Enter name of second set:")
	public String seT2;
	@Tunable(description="Enter name of first set:")
	public String seT1;
	private SetsManager sm;
	private String newName, set1, set2;
	private SetOperations operation;
	
	public SetOperationsTask(SetsManager setsManager, String name, String s1, String s2, SetOperations s) {
		sm = setsManager;
		newName = name;
		set1 = s1;
		set2 = s2;
		operation = s;
	}
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		if (newName == null) newName = setName;
		if (set1 == null) set1 = seT1;
		if (set2 == null) set2 = seT2;
		switch (operation) {
		case INTERSECT:
			sm.intersection(newName, set1, set2);
			break;
		case DIFFERENCE:
			sm.intersection(newName, set1, set2);
			break;
		case UNION:
			sm.union(newName, set1, set2);
			break;
		default:
			break;
		}
	}

}
