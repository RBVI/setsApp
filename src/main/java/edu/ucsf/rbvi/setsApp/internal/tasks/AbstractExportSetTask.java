package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ucsf.rbvi.setsApp.internal.model.Set;
import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

abstract public class AbstractExportSetTask extends AbstractTask {
	private SetsManager mgr;

	public AbstractExportSetTask(SetsManager mgr) {
		this.mgr = mgr;
	}

	public void exportSetToStream(String name, String column, File setFile) throws IOException {
		setFile.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(setFile.getAbsolutePath()));
		Set<? extends CyIdentifiable> set = mgr.getSet(name);
		CyNetwork network = set.getNetwork();
		CyTable table = null;
		if (set.getType() == CyNode.class) 
			table = network.getDefaultNodeTable();
		else
			table = network.getDefaultEdgeTable();

		Collection<? extends CyIdentifiable> cyIds = set.getElements();
		if (table != null) {
			try {
				for (CyIdentifiable cyId: cyIds)
					writer.write(table.getRow(cyId.getSUID()).get(column, String.class) + "\n");
			} catch (IOException e) {
				throw new IOException("Cannot write to file: " + writer.toString() + "["+e.getMessage()+"]");
			}
		}
		try {
			writer.close();
		} catch (IOException e) {
			throw new IOException("Problems writing to stream: " + writer.toString() + "["+e.getMessage()+"]");
		}
	}

}
