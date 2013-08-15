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

import edu.ucsf.rbvi.setsApp.internal.model.SetOperations;
import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class SetOperationsTask extends AbstractTask implements ObservableTask {
	@Tunable(description="Enter a name for the new set:")
	public String name;
	@Tunable(description="Select name of second set:")
	public ListSingleSelection<String> set2;
	@Tunable(description="Select name of first set:")
	public ListSingleSelection<String> set1;
	private SetsManager sm;
	private SetOperations operation;
	private String s1, s2;
	
	public SetOperationsTask(SetsManager setsManager, Class<? extends CyIdentifiable> type, SetOperations s) {
		sm = setsManager;
		if (type.equals(CyNode.class) || type.equals(CyEdge.class)) {
			List<String> attr = sm.getSetNames(type); 
			this.set1 = new ListSingleSelection<String>(attr);
			this.set2 = new ListSingleSelection<String>(attr);
		}
		else {
			List<String> attr = new ArrayList<String>();
			for (String string: sm.getSetNames(CyNode.class)) attr.add(string);
			for (String string: sm.getSetNames(CyEdge.class)) attr.add(string);
			this.set1 = new ListSingleSelection<String>(attr);
			this.set2 = new ListSingleSelection<String>(attr);
		}
		operation = s;
	}
	
	public SetOperationsTask(SetsManager setsManager, String set1, String set2, SetOperations s) {
		sm = setsManager;
		this.set1 = null;
		this.set2 = null;
		s1 = set1;
		s2 = set2;
		operation = s;
	}
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		String set1, set2;
		if (this.set1 != null && this.set2 != null) {
			set1 = this.set1.getSelectedValue();
			set2 = this.set2.getSelectedValue();
		}
		else {
			set1 = s1;
			set2 = s2;
		}
		switch (operation) {
		case INTERSECT:
			sm.intersection(name, set1, set2);
			arg0.showMessage(TaskMonitor.Level.INFO,
			                 "Putting the intersection of "+set1+" and "+set2+" into "+name);
			break;
		case DIFFERENCE:
			sm.difference(name, set1, set2);
			arg0.showMessage(TaskMonitor.Level.INFO,
			                 "Putting the difference of "+set1+" and "+set2+" into "+name);
			break;
		case UNION:
			sm.union(name, set1, set2);
			arg0.showMessage(TaskMonitor.Level.INFO,
			                 "Putting the union of "+set1+" and "+set2+" into "+name);
			break;
		default:
			break;
		}
	}

	// Return the updated set
	public Object getResults(Class expectedType) {
		if (expectedType.equals(String.class)) {
			return sm.getSet(name).toString();
		}
		return sm.getSet(name);
	}

}
