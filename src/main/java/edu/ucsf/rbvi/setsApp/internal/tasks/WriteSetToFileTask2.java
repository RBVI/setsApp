package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.setsApp.internal.CyIdType;

public class WriteSetToFileTask2 extends AbstractTask {
	@Tunable(description="Select column to write set from:")
	public ListSingleSelection<String> column = null;
	private SetsManager mgr;
	private CyNetwork network = null;
	private BufferedWriter writer;
	private String name;
	private CyIdType type;
	
	public WriteSetToFileTask2(SetsManager setsManager, String setName, BufferedWriter stream) {
		mgr = setsManager;
		writer = stream;
		name = setName;
		network = setsManager.getCyNetwork(setName);
		type = setsManager.getType(setName);
		if (network != null) {
			List<String> setNames = setsManager.getSetNames(), networkSetNames = new ArrayList<String>();
			CyTable table = null;
			if (type == CyIdType.NODE) {
				table = network.getDefaultNodeTable();
			/*	for (String s: setNames)
					if (setsManager.getCyNetwork(s) == network && setsManager.getType(s) == CyIdType.NODE) 
						networkSetNames.add(s);
				selectSet = new ListSingleSelection<String>(networkSetNames); */
			}
			if (type == CyIdType.EDGE) {
				table = network.getDefaultEdgeTable();
			/*	for (String s: setNames)
					if (setsManager.getCyNetwork(s) == network && setsManager.getType(s) == CyIdType.EDGE) 
						networkSetNames.add(s);
				selectSet = new ListSingleSelection<String>(networkSetNames); */
			}
			if (table != null) {
				Collection<CyColumn> cyColumns = table.getColumns();
				ArrayList<String> columnNames = new ArrayList<String>();
				for (CyColumn c: cyColumns) 
					if (c.getType() == String.class) columnNames.add(c.getName());
				column = new ListSingleSelection<String>(columnNames);
				column.setSelectedValue(CyNetwork.NAME);
			}
		}
	}
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		if (network != null && column != null) {
			mgr.exportSetToStream(name, column.getSelectedValue(), writer);
		}
	}

}
