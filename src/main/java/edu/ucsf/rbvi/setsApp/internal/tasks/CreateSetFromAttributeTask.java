package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.setsApp.internal.CyIdType;

public class CreateSetFromAttributeTask extends AbstractTask {
	@Tunable(description="Select the name of the column:")
	public ListSingleSelection<String> colName;
	@Tunable(description="Enter a name for the set:")
	public String setName;
	private SetsManager setsManager;
	private CyNetwork cyNetwork = null;
	private CyIdType type;
	private String newName;

	public CreateSetFromAttributeTask(SetsManager mgr, CyNetwork network, CyIdType t) {
		setsManager = mgr;
		cyNetwork = network;
		type = t;
		colName = new ListSingleSelection<String>(getStringAttributes(network, t));
	}
	
	public void run(TaskMonitor taskMonitor) throws Exception {
		setsManager.createSetsFromAttributes(setName, colName.getSelectedValue(), cyNetwork, type);
	}

	private List<String> getStringAttributes(CyNetwork network, CyIdType t) {
		List<String> attr = new ArrayList<String>();
		CyTable table = null;
		if (t == CyIdType.NODE)
			table = network.getDefaultNodeTable();
		else
			table = network.getDefaultEdgeTable();
		for (CyColumn column : table.getColumns()) {
			if (column.getType() == String.class) {
				attr.add(column.getName());
			}
		}
		return attr;
	}
}