package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.ArrayList;
import java.util.Collection;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class CopySetTask extends AbstractTask implements ObservableTask {

	private SetsManager mgr;
	@Tunable(description="Enter name of new set:")
	public String newSet;
	@Tunable(description="Select new network for set:")
	public ListSingleSelection<CyNetwork> networks;
	@Tunable(description="Select set to copy:")
	public ListSingleSelection<String> selectSet;
	private String movingSet;
	
	public CopySetTask(SetsManager manager, Collection<CyNetwork> networkManager) {
		mgr = manager;
		selectSet = new ListSingleSelection<String>(mgr.getSetNames());
		networks = new ListSingleSelection<CyNetwork>(new ArrayList<CyNetwork>(networkManager));
		movingSet = null;
	}
	
	public CopySetTask(SetsManager manager, Collection<CyNetwork> networkManager, String movingSetName) {
		mgr = manager;
		selectSet = null;
		networks = new ListSingleSelection<CyNetwork>(new ArrayList<CyNetwork>(networkManager));
		movingSet = movingSetName;
	}
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		if (selectSet != null) {
			mgr.moveSet(selectSet.getSelectedValue(), newSet, networks.getSelectedValue());
			arg0.showMessage(TaskMonitor.Level.INFO,
			                 "Copied set "+selectSet.getSelectedValue()+
			                 " to "+newSet+" in network "+networks.getSelectedValue());
		} else {
			mgr.moveSet(movingSet, newSet, networks.getSelectedValue());
			arg0.showMessage(TaskMonitor.Level.INFO,
			                 "Copied set "+movingSet+
			                 " to "+newSet+" in network "+networks.getSelectedValue());
		}
	}

	// Return the updated set
	public Object getResults(Class expectedType) {
		String targetSet;
		if (selectSet != null)
			targetSet = selectSet.getSelectedValue();
		else
			targetSet = movingSet;

		if (expectedType.equals(String.class)) {
			return mgr.getSet(targetSet).toString();
		}
		return mgr.getSet(targetSet);
	}

}
