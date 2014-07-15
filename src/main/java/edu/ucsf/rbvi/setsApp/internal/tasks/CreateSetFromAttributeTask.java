package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;

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
	static final HashSet<Class<?>> DISCRETE_TYPES = new HashSet<Class<?>>(Arrays.asList(String.class, Integer.class, Long.class));

	@Tunable(description="Select the name of the column:")
	public ListSingleSelection<String> colName;
	@Tunable(description="Enter a prefix for the sets:")
	public String setName;
	private SetsManager setsManager;
	private CyNetwork cyNetwork = null;
	private String newName;
	private	Map<Object, Set<? extends CyIdentifiable>> cySet;
	private Class<? extends CyIdentifiable> type;

	public CreateSetFromAttributeTask(SetsManager mgr, CyNetwork network, Class<? extends CyIdentifiable> t) {
		setsManager = mgr;
		cyNetwork = network;
		type = t;
		colName = new ListSingleSelection<String>(getDiscreteAttributes(network, t));
	}
	
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Creating set from attribute");
		String column = colName.getSelectedValue();
		cySet = new HashMap<Object, Set<? extends CyIdentifiable>>();

		CyTable cyTable;
		if (type.equals(CyNode.class))
			cyTable = cyNetwork.getDefaultNodeTable();
		else
			cyTable = cyNetwork.getDefaultEdgeTable();

		CyColumn cyIdColumn = cyTable.getPrimaryKey();
		List<Long> cyIdList = cyIdColumn.getValues(Long.class);

		for (Long cyId: cyIdList) {
			Object value = cyTable.getRow(cyId).getRaw(column);
			if (value == null)
				continue;
			String attrName = setName + ":" + value.toString();
			addSet(attrName, cyId);
		}

		for (Object s: cySet.keySet()) {
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

	private List<String> getDiscreteAttributes(CyNetwork network, Class<? extends CyIdentifiable> t) {
		List<String> attr = new ArrayList<String>();
		CyTable table = null;
		if (t.equals(CyNode.class))
			table = network.getDefaultNodeTable();
		else
			table = network.getDefaultEdgeTable();
		for (CyColumn column : table.getColumns()) {
			if (DISCRETE_TYPES.contains(column.getType())) {
				attr.add(column.getName());
			}
		}
		return attr;
	}
}
