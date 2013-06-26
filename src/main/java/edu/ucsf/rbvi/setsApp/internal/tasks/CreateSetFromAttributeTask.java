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
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ucsf.rbvi.setsApp.internal.model.Set;
import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class CreateSetFromAttributeTask extends AbstractTask {
	@Tunable(description="Select the name of the column:")
	public ListSingleSelection<String> colName;
	@Tunable(description="Enter a name for the set:")
	public String setName;
	private SetsManager setsManager;
	private CyNetwork cyNetwork = null;
	private String newName;
	private	Map<String, Set<? extends CyIdentifiable>> cySet;
	private Class<? extends CyIdentifiable> type;
	private static Logger messages = LoggerFactory
			.getLogger("CyUserMessages.setsApp");

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
		messages.info("Created "+cySet.keySet().size()+" new sets");
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
