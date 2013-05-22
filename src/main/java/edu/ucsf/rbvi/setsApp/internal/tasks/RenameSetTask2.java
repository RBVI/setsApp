package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class RenameSetTask2 extends AbstractTask {
	@Tunable(description="Enter new name for set:")
	public String newName;
	@Tunable(description="Select set to rename:")
	public ListSingleSelection<String> oldName;
	private SetsManager mgr;
	
	public RenameSetTask2(SetsManager manager) {
		mgr = manager;
		oldName = new ListSingleSelection<String>(mgr.getSetNames());
	}
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		mgr.rename(oldName.getSelectedValue(), newName);
	}

}
