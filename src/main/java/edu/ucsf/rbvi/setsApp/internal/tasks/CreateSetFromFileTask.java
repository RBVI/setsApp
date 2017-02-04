package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.setsApp.internal.model.Set;
import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;

public class CreateSetFromFileTask extends AbstractTask implements ObservableTask {

	private ListSingleSelection<String> type = new ListSingleSelection<String>("Node", "Edge");
	enum InputType {NONE, SINGLE_SET, TWO_COLUMN, MULTI_COLUMN};
	int nColumns = 0;

	static String splitString = "[,\t](?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

	@ProvidesTitle
	public String getTitle() { return "Import set from file"; }

	// @Tunable(description="Type of set to import:", gravity=1.0)
	@Tunable(description="Type of set to import:")
	public ListSingleSelection<String> getType() {
		return type;
	}
	public void setType(ListSingleSelection<String>input) {}

	// @Tunable(description="Select column to use for ID:", listenForChange="Type", gravity=2.0)
	@Tunable(description="Select column to use for ID:", listenForChange="Type")
	public ListSingleSelection<String> getColumns() {
		if (type.getSelectedValue().equals("Node")) {
			return nodesColumn;
		} else {
			return edgesColumn;
		}
	}
	public void setColumns(ListSingleSelection<String>input) {}

	public ListSingleSelection<String> nodesColumn = null;

	public ListSingleSelection<String> edgesColumn = null;

	// @Tunable(description="Enter name for new set:", gravity=.5)
	@Tunable(description="Enter name for new set:")
	public String name;

	@Tunable(description="File containing set data", params="input=true;fileCategory=unspecified")
	public File setFile;
	
	private SetsManager mgr;
	private CyNetwork network = null;
	private TaskMonitor monitor = null;
	
	public CreateSetFromFileTask(SetsManager setsManager, CyNetworkManager cnm) {
		mgr = setsManager;
		java.util.Set<CyNetwork> networkSet = cnm.getNetworkSet();
		for (CyNetwork net: networkSet) {
			if (net.getRow(net).get(CyNetwork.SELECTED, Boolean.class))
				network = net;
		}
		if (network != null) {
			CyTable table = null;
			table = network.getDefaultNodeTable();
			if (table != null) {
				Collection<CyColumn> cyColumns = table.getColumns();
				ArrayList<String> columnNames = new ArrayList<String>();
				columnNames.add("none");
				for (CyColumn c: cyColumns) 
					if (c.getType() == String.class) columnNames.add(c.getName());
				nodesColumn = new ListSingleSelection<String>(columnNames);
				nodesColumn.setSelectedValue("none");
			}
			table = network.getDefaultEdgeTable();
			if (table != null) {
				Collection<CyColumn> cyColumns = table.getColumns();
				ArrayList<String> columnNames = new ArrayList<String>();
				for (CyColumn c: cyColumns) 
					if (c.getType() == String.class) columnNames.add(c.getName());
				columnNames.add("none");
				edgesColumn = new ListSingleSelection<String>(columnNames);
				edgesColumn.setSelectedValue("none");
			}
		}
	}
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		arg0.setTitle("Creating set from file");
		this.monitor = arg0;
		if (network != null && nodesColumn != null) {
			BufferedReader reader = new BufferedReader(new FileReader(setFile));
			if (! nodesColumn.getSelectedValue().equals("none") && 
			    edgesColumn.getSelectedValue().equals("none"))
				createSetFromStream(name, nodesColumn.getSelectedValue(), reader, CyNode.class);
			if (! edgesColumn.getSelectedValue().equals("none") && 
			    nodesColumn.getSelectedValue().equals("none"))
				createSetFromStream(name, edgesColumn.getSelectedValue(), reader, CyEdge.class);
		}
	}

	// Return the updated set
	public Object getResults(Class expectedType) {
		if (expectedType.equals(String.class)) {
			return mgr.getSet(name).toString();
		}
		return mgr.getSet(name);
	}

	private void createSetFromStream(String name, String column, 
	                                 BufferedReader reader, 
	                                 Class<? extends CyIdentifiable> setType) throws Exception {
		if (mgr.getSet(name) != null)
			throw new Exception("Set \"" + name + "\" already exists. Choose a different name.");

		CyTable table;
		// Set<? extends CyIdentifiable> newSet = null;

		if (setType == CyNode.class) {
			table = network.getDefaultNodeTable();
		} else {
			table = network.getDefaultEdgeTable();
		}


		// First, figure out the format.  We support 3 different formats:
		// 	Single column: Each line represents a node or edge of the set
		// 	Two column: First column is set name, second column are nodes or edges.
		// 	            Generally, the first column will be blank unless a new set
		//	Multi-column: First line is column header with the set names
		InputType inputType = getInputType(reader);
		Map<String, List<String>> setsMap = new HashMap<>();
		List<String> setsNames = new ArrayList<>();
		String currentSet = null;
		if (inputType == InputType.SINGLE_SET) {
			setsMap.put(name, new ArrayList<String>());
			setsNames.add(name);
			currentSet = name;
		}

		String curLine;
		while ((curLine = reader.readLine()) != null) {
			currentSet = parseLine(curLine, setsMap, setsNames, inputType, currentSet);
		}

		// setsMap now has one or more sets.  Create them
		for (String setName: setsNames) {
			Set<? extends CyIdentifiable> newSet = createSet(setName, network, setType);
			// Use a HashSet to avoid duplicates
			HashSet<Long> matches = new HashSet<Long>();
			for (String ident: setsMap.get(setName)) {
				Collection<CyRow> rows = table.getMatchingRows(column, ident);
				for (CyRow row: rows) {
					matches.add(row.get(CyIdentifiable.SUID, Long.class));
				}
			}
			if (matches.size() > 0) {
				// Now, add our matches to our set
				newSet.addElementsByID(new ArrayList<Long>(matches));
			}
			mgr.addSet(newSet);

			if (setType == CyNode.class) {
				monitor.showMessage(TaskMonitor.Level.INFO, 
				                    "Created new node set '"+name+"' with "+matches.size()+" nodes ");
			} else {
				monitor.showMessage(TaskMonitor.Level.INFO, 
				                    "Created new edge set '"+name+"' with "+matches.size()+" edges ");
			}
		}
	}

	Set<? extends CyIdentifiable> createSet(String name, CyNetwork network, Class<? extends CyIdentifiable> setType) {
		if (setType == CyNode.class) {
			return new Set<CyNode>(name, network, CyNode.class);
		} else {
			return new Set<CyEdge>(name, network, CyEdge.class);
		}
	}

	InputType getInputType(BufferedReader reader) throws IOException {
		InputType inputType = InputType.NONE;
		reader.mark(4096);
		int lineNumber = 0;
		int headerLength = 0;
		String curLine;
		while ((curLine = reader.readLine()) != null) {
			if (curLine.startsWith("#") || curLine.trim().length() == 0)
				continue;
			String[] tokens = curLine.split(splitString);
			if (lineNumber == 0) {
				headerLength = tokens.length;
				lineNumber++;
				continue;
			}
			int dataLength = tokens.length;
			if (headerLength == 1 && dataLength == 2) {
				inputType = InputType.TWO_COLUMN;
				nColumns = 2;
			} else if (headerLength == 1 && dataLength == 1) {
				inputType = InputType.SINGLE_SET;
				nColumns = 1;
			} else if (headerLength > 1 && dataLength > 1) {
				inputType = InputType.MULTI_COLUMN;
				nColumns = headerLength;
			}
			break;
		}
		reader.reset();
		return inputType;
	}

	private String parseLine(String line, Map<String, List<String>> setsMap, 
	                         List<String> setNames, InputType inputType, String currentSet) {
		if (line.startsWith("#") || line.trim().length() == 0)
			return currentSet;

		if (inputType.equals(InputType.SINGLE_SET)) {
			setsMap.get(currentSet).add(line.trim());
			return currentSet;
		}
		String[] tokens = line.split(splitString);
		if (inputType.equals(InputType.MULTI_COLUMN)) {
			if (currentSet == null) {
				// First line -- these are our set names
				for (String s: tokens) {
					setsMap.put(s.trim(), new ArrayList<String>());
					setNames.add(s.trim());
				}
				return line;
			}
			for (int i = 0; i < tokens.length; i++) {
				String s = tokens[i];
				String setName = setNames.get(i);
				if (!setsMap.containsKey(setName))
					setsMap.put(setName, new ArrayList<String>());

				setsMap.get(setName).add(s.trim());
			}
			return currentSet;
		}
		if (inputType.equals(InputType.TWO_COLUMN)) {
			if (tokens[0].trim().length() > 0) {
				// New set
				currentSet = tokens[0].trim();
				setsMap.put(currentSet, new ArrayList<String>());
				setNames.add(currentSet);
				return currentSet;
			}
			setsMap.get(currentSet).add(tokens[1].trim());
		}
		return currentSet;

	}
}
