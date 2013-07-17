package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class RenameSetTask extends AbstractTask implements ObservableTask {
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
		mgr.rename(oldName, newName);
		arg0.showMessage(TaskMonitor.Level.INFO, 
		                 "Renamed set "+oldName+" to "+newName);
	}

	// Return the updated set
	public Object getResults(Class expectedType) {
		if (expectedType.equals(String.class)) {
			return mgr.getSet(newName).toString();
		}
		return mgr.getSet(newName);
	}

}
