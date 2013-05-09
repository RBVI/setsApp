package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
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

public class CreateSetFromFileTask extends AbstractTask {
	@Tunable(description="Select column to create set from:")
	public ListSingleSelection<String> column = null;
	@Tunable(description="Enter name for new set:")
	public String name;
	private SetsManager mgr;
	private CyNetwork network = null;
	private BufferedReader reader;
	private CyIdType type;
	
	public CreateSetFromFileTask(SetsManager setsManager, CyNetworkManager cnm, BufferedReader stream, CyIdType t) {
		mgr = setsManager;
		reader = stream;
		type = t;
		Set<CyNetwork> networkSet = cnm.getNetworkSet();
		for (CyNetwork net: networkSet) {
			if (net.getRow(net).get(CyNetwork.SELECTED, Boolean.class))
				network = net;
		}
		if (network != null) {
			CyTable table = null;
			if (t == CyIdType.NODE) table = network.getDefaultNodeTable();
			if (t == CyIdType.EDGE) table = network.getDefaultEdgeTable();
			if (table != null) {
				Collection<CyColumn> cyColumns = table.getColumns();
				ArrayList<String> columnNames = new ArrayList<String>();
				for (CyColumn c: cyColumns) 
					if (c.getType() == String.class) columnNames.add(c.getName());
				column = new ListSingleSelection<String>(columnNames);
			}
		}
	}
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		if (network != null && column != null) {
			mgr.createSetFromStream(name, column.getSelectedValue(), network, reader, type);
		}
	}

}
