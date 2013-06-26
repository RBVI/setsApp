package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class RenameSetTask extends AbstractTask {
	@Tunable(description="Enter new name for set:")
	public String newName;
	private String oldName;
	private SetsManager mgr;
	private static Logger messages = LoggerFactory
			.getLogger("CyUserMessages.setsApp");
	
	public RenameSetTask(SetsManager manager, String name) {
		mgr = manager;
		oldName = name;
	}
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		mgr.rename(oldName, newName);
		messages.info("Renamed set "+oldName+" to "+newName);
	}

}
