package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.setsApp.internal.model.Set;
import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class CreateSetFromAttributeTask extends AbstractTask implements ObservableTask {
	@Tunable(description="Select the name of the column:")
	public ListSingleSelection<String> colName;
	@Tunable(description="Enter a prefix for the sets:")
	public String setName;
	private SetsManager setsManager;
	private CyNetwork cyNetwork = null;
	private String newName;
	private	Map<String, Set<? extends CyIdentifiable>> cySet;
	private Class<? extends CyIdentifiable> type;

	public CreateSetFromAttributeTask(SetsManager mgr, CyNetwork network, Class<? extends CyIdentifiable> t) {
		setsManager = mgr;
		cyNetwork = network;
		type = t;
		colName = new ListSingleSelection<String>(getStringAttributes(network, t));
	}
	
	public void run(TaskMonitor taskMonitor) throws Exception {
		String column = colName.getSelectedValue();
		cySet = new HashMap<String, Set<? extends CyIdentifiable>>();

		CyTable cyTable;
		if (type.equals(CyNode.class))
			cyTable = cyNetwork.getDefaultNodeTable();
		else
			cyTable = cyNetwork.getDefaultEdgeTable();

		CyColumn cyIdColumn = cyTable.getPrimaryKey();
		List<Long> cyIdList = cyIdColumn.getValues(Long.class);

		for (Long cyId: cyIdList) {
			String attrName = setName + ":" + cyTable.getRow(cyId).get(column, String.class);
			addSet(attrName, cyId);
		}

		for (String s: cySet.keySet()) {
			setsManager.addSet(cySet.get(s));
		}
		taskMonitor.showMessage(TaskMonitor.Level.INFO, 
			                      "Created "+cySet.keySet().size()+" new sets");
	}

	// Return the updated sets	
	public Object getResults(Class expectedType) {
		if (expectedType.equals(String.class)) {
			String str = "[";
			for (Set s: cySet.values())
				str += s.toString()+",";
			return str.substring(0, str.length()-1)+"]";
		}
		return cySet.values();
	}

	private void addSet(String attrName, Long cyId) {
		if (type.equals(CyNode.class)) {
			if (!cySet.containsKey(attrName)) {
				cySet.put(attrName,new Set<CyNode>(attrName, cyNetwork, CyNode.class));
			}
			cySet.get(attrName).add(cyNetwork.getNode(cyId));
		} else {
			if (!cySet.containsKey(attrName)) {
				cySet.put(attrName,new Set<CyEdge>(attrName, cyNetwork, CyEdge.class));
			}
			cySet.get(attrName).add(cyNetwork.getEdge(cyId));
		}
	}

	private List<String> getStringAttributes(CyNetwork network, Class<? extends CyIdentifiable> t) {
		List<String> attr = new ArrayList<String>();
		CyTable table = null;
		if (t.equals(CyNode.class))
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
