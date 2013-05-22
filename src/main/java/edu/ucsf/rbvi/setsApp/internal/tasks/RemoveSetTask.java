package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class RemoveSetTask extends AbstractTask {
	private SetsManager mgr;
	@Tunable(description="Select set to remove")
	public ListSingleSelection<String> name;
	
	public RemoveSetTask(SetsManager manager) {
		mgr = manager;
		name = new ListSingleSelection<String>(mgr.getSetNames());
	}
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		mgr.removeSet(name.getSelectedValue());
	}

}
