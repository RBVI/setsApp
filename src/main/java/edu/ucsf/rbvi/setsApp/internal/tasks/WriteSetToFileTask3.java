package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class WriteSetToFileTask3 extends AbstractExportSetTask {
	@Tunable(description="Select column to use for ID:")
	public ListSingleSelection<String> column = null;
	@Tunable(description="Select a set to write:")
	public ListSingleSelection<String> selectSet = null;
	@Tunable(description="File to write set data to", params="input=false;fileCategory=unspecified")
	public File setFile;
	private CyNetwork network = null;
	
	public WriteSetToFileTask3(SetsManager setsManager, CyNetwork n, Class<? extends CyIdentifiable> type) {
		super(setsManager);
		network = n;
		if (network != null) {
			List<String> setNames = setsManager.getSetNames(), networkSetNames = new ArrayList<String>();
			CyTable table = null;
			if (type.equals(CyNode.class)) {
				table = network.getDefaultNodeTable();
				for (String s: setNames)
					if (setsManager.getCyNetwork(s) == network && setsManager.getType(s).equals(CyNode.class)) 
						networkSetNames.add(s);
				selectSet = new ListSingleSelection<String>(networkSetNames);
			}
			if (type.equals(CyEdge.class)) {
				table = network.getDefaultEdgeTable();
				for (String s: setNames)
					if (setsManager.getCyNetwork(s) == network && setsManager.getType(s).equals(CyEdge.class)) 
						networkSetNames.add(s);
				selectSet = new ListSingleSelection<String>(networkSetNames);
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
		if (network != null && column != null && selectSet != null) {
			if (!setFile.exists()) {
				exportSetToStream(selectSet.getSelectedValue(), column.getSelectedValue(), setFile);
			}
			else throw new IOException("File " + setFile.getName() + " already exists.");
		}
	}

}
