package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

public class WriteSetToFileTask3 extends AbstractTask {
	@Tunable(description="Select column to use for ID:")
	public ListSingleSelection<String> column = null;
	@Tunable(description="Select a set to write:")
	public ListSingleSelection<String> selectSet = null;
	@Tunable(description="Enter file name:")
	public String fileName;
	private SetsManager mgr;
	private CyNetwork network = null;
	private BufferedWriter writer;
	private File f;
	
	public WriteSetToFileTask3(SetsManager setsManager, CyNetwork n, CyIdType type) {
		mgr = setsManager;
		network = n;
		if (network != null) {
			List<String> setNames = setsManager.getSetNames(), networkSetNames = new ArrayList<String>();
			CyTable table = null;
			if (type == CyIdType.NODE) {
				table = network.getDefaultNodeTable();
				for (String s: setNames)
					if (setsManager.getCyNetwork(s) == network && setsManager.getType(s) == CyIdType.NODE) 
						networkSetNames.add(s);
				selectSet = new ListSingleSelection<String>(networkSetNames);
			}
			if (type == CyIdType.EDGE) {
				table = network.getDefaultEdgeTable();
				for (String s: setNames)
					if (setsManager.getCyNetwork(s) == network && setsManager.getType(s) == CyIdType.EDGE) 
						networkSetNames.add(s);
				selectSet = new ListSingleSelection<String>(networkSetNames);
			}
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
		if (network != null && column != null && selectSet != null) {
			f = new File(fileName);
			if (!f.exists()) {
				f.createNewFile();
				writer = new BufferedWriter(new FileWriter(f.getAbsolutePath()));
				mgr.exportSetToStream(selectSet.getSelectedValue(), column.getSelectedValue(), writer);
			}
			else throw new IOException("File " + f.getName() + " already exists.");
		}
	}

}
