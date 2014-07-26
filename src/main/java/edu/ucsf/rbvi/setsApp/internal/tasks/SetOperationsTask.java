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
	@Tunable(description="Enter a name for the new set:", gravity=1.0)
	public String name;
	@Tunable(description="Select name of first set:", gravity=2.0)
	public ListSingleSelection<String> set1;
	@Tunable(description="Select name of second set:", gravity=3.0)
	public ListSingleSelection<String> set2;
	private SetsManager sm;
	private SetOperations operation;
	private String s1, s2;
	private List<String> setsList;
	
	public SetOperationsTask(SetsManager setsManager, Class<? extends CyIdentifiable> type, SetOperations s) {
		sm = setsManager;
		if (type == null) {
			// Coming from command-line help?
			type = CyNode.class;
		}

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
		setsList = null;
	}
	
	public SetOperationsTask(SetsManager setsManager, List<String> setList, SetOperations s) {
		sm = setsManager;
		this.set1 = null;
		this.set2 = null;
		this.setsList = setList;
		operation = s;

		final StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < setList.size(); i++) {
			buffer.append(setList.get(i));
			if (i < (setList.size() - 1)) {
				buffer.append(' ');
				buffer.append(s.operator());
				buffer.append(' ');
			}
		}
		this.name = buffer.toString();
	}

	public SetOperationsTask(SetsManager setsManager, String set1, String set2, SetOperations s) {
		sm = setsManager;
		this.set1 = null;
		this.set2 = null;
		s1 = set1;
		s2 = set2;
		operation = s;
		this.name = String.format("%s %s %s", set1, s.operator(), set2);
	}
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		arg0.setTitle("Executing set operation");
		if (setsList == null) {
			setsList = new ArrayList<String>();

			if (this.set1 != null && this.set2 != null) {
				setsList.add(this.set1.getSelectedValue());
				setsList.add(this.set2.getSelectedValue());
			}
			else {
				setsList.add(s1);
				setsList.add(s2);
			}
		}

		switch (operation) {
		case INTERSECT:
			sm.intersection(name, setsList);
			arg0.showMessage(TaskMonitor.Level.INFO,
			                 "Putting the intersection of "+multiSetName(setsList)+" into "+name);
			break;
		case DIFFERENCE:
			sm.difference(name, setsList);
			arg0.showMessage(TaskMonitor.Level.INFO,
			                 "Putting the difference of "+multiSetName(setsList)+" into "+name);
			break;
		case UNION:
			sm.union(name, setsList);
			arg0.showMessage(TaskMonitor.Level.INFO,
			                 "Putting the union of "+multiSetName(setsList)+" into "+name);
			break;
		default:
			break;
		}
	}

	private String multiSetName(List<String> setList) {
		if (setList.size() == 1) 
			return setList.get(0);

		String name = "";
		for (int i = 0; i < (setList.size()-2); i++) {
			name += setList.get(i)+", ";
		}
		name += setList.get(setList.size()-2)+" and "+setList.get(setList.size()-1);
		return name;
	}

	// Return the updated set
	public Object getResults(Class expectedType) {
		if (expectedType.equals(String.class)) {
			return sm.getSet(name).toString();
		}
		return sm.getSet(name);
	}

}
