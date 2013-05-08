package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

public class RenameSetTask extends AbstractTask {
	@Tunable(description="Enter new name for set:")
	public String newName;
	private String oldName;
	private SetsManager mgr;
	
	public RenameSetTask(SetsManager manager, String name) {
		mgr = manager;
		oldName = name;
	}
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		// TODO Auto-generated method stub
		mgr.rename(oldName, newName);
	}

}
