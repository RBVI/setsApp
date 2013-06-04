package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class RemoveSetTask extends AbstractTask {
	private SetsManager mgr;
	@Tunable(description="Select set to remove")
	public ListSingleSelection<String> name;
	public String setName;
	
	public RemoveSetTask(SetsManager manager) {
		mgr = manager;
		name = new ListSingleSelection<String>(mgr.getSetNames());
	}
	
	public RemoveSetTask(SetsManager manager, String name2) {
		mgr = manager;
		name = null;
		setName = name2;
	}
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		if (name != null)
			mgr.removeSet(name.getSelectedValue());
		else
			mgr.removeSet(setName);
	}

}
