package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class WriteSetToFileTask2 extends AbstractExportSetTask {
	@ProvidesTitle
	public String getTitle() { return "Export set to file"; }
	@Tunable(description="Select column to use for ID:")
	public ListSingleSelection<String> column = null;
	@Tunable(description="File to write set data to", params="input=false;fileCategory=unspecified")
	public File setFile;
	private String name;
	CyNetwork network;
	
	public WriteSetToFileTask2(SetsManager setsManager, String setName) {
		super(setsManager);
		name = setName;
		network = setsManager.getCyNetwork(setName);
		Class<? extends CyIdentifiable> type = setsManager.getType(setName);
		if (network != null) {
			List<String> setNames = setsManager.getSetNames(), networkSetNames = new ArrayList<String>();
			CyTable table = null;
			if (type.equals(CyNode.class)) {
				table = network.getDefaultNodeTable();
			} else if (type.equals(CyEdge.class)) {
				table = network.getDefaultEdgeTable();
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
			if (!setFile.exists()) {
				exportSetToStream(name, column.getSelectedValue(), setFile);
			}
			else throw new IOException("File " + setFile.getName() + " already exists.");
		}
	}
}
