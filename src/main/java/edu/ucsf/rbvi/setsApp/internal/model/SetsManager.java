package edu.ucsf.rbvi.setsApp.internal.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;

import edu.ucsf.rbvi.setsApp.internal.model.Set;
import edu.ucsf.rbvi.setsApp.internal.events.SetChangedEvent;
import edu.ucsf.rbvi.setsApp.internal.events.SetChangedListener;

public class SetsManager implements SessionLoadedListener {
	private ConcurrentHashMap<String, Set<? extends CyIdentifiable>> setsMap;
	private ArrayList<SetChangedListener> setChangedListener = new ArrayList<SetChangedListener>();
	private CyNetworkManager netMgr;
	public static final String TABLE_PREFIX = "setsApp:";
	
	public SetsManager(CyNetworkManager netMgr) {
		this.setsMap = new ConcurrentHashMap<String, Set<? extends CyIdentifiable>> ();
		this.netMgr = netMgr;
	}
	
	public int setsCount() {return setsMap.size();}
	
	public void addSetChangedListener(SetChangedListener s) {
		if (s != null)
			this.setChangedListener.add(s);
	}
	
	public boolean isInSet(Long cyId) {
		for (Set<? extends CyIdentifiable> set: setsMap.values()) {
			if (set.hasCyId(cyId))
				return true;
		}
		return false;
	}

	public boolean isInSet(String name, Long cyId) {
		Set<? extends CyIdentifiable> thisSet;
		if ((thisSet = setsMap.get(name)) != null) return  thisSet.hasCyId(cyId);
		else return false;
	}
	
	public CyNetwork getCyNetwork(String name) {
		if (setsMap.containsKey(name))
			return setsMap.get(name).getNetwork();
		return null;
	}
	
	public List<String> getSetNames() {
		List<String> setNames = new ArrayList<String>();
		for (String s: setsMap.keySet()) {
			setNames.add(s);
		}
		return setNames;
	}

	/**
 	 * Returns the names of all sets of a given type (CyNode or CyEdge).
 	 *
 	 * @param type the type of the sets we're interested in
 	 * @return the list of names
 	 */
	public List<String> getSetNames(Class<? extends CyIdentifiable> type) {
		List<String> setNames = new ArrayList<String>();
		for (Set<? extends CyIdentifiable> set: setsMap.values()) {
			if (set.getType() == type)
				setNames.add(set.getName());
		}
		return setNames;
		
	}

	/**
 	 * Returns the names of all sets in a given network
 	 *
 	 * @param network the network we're interested in
 	 * @return the list of names
 	 */
	public List<String> getSetNames(CyNetwork network) {
		List<String> setNames = new ArrayList<String>();
		for (Set<? extends CyIdentifiable> set: setsMap.values()) {
			if (set.getNetwork().equals(network))
				setNames.add(set.getName());
		}
		return setNames;
	}
	
	public boolean isInSetsManager(String name) {
		if (setsMap.get(name) == null)
			return false;
		else
			return true;
	}
	
	public Class<? extends CyIdentifiable> getType(String name) {
		if(setsMap.containsKey(name))
			return setsMap.get(name).getType();
		return null;
	}
	
	public Set<? extends CyIdentifiable> getSet(String setName) {
		if(setsMap.containsKey(setName))
			return setsMap.get(setName);
		return null;
	}

	public String getElementName(CyNetwork network, String setName, CyIdentifiable element) {
		if (getType(setName) == CyNode.class) {
			return network.getDefaultNodeTable().getRow(element.getSUID()).get(CyNetwork.NAME, String.class);
		} else {
			return network.getDefaultEdgeTable().getRow(element.getSUID()).get(CyNetwork.NAME, String.class);
		}
	}
	
	public void reset() {
		this.setsMap = new ConcurrentHashMap<String, Set<? extends CyIdentifiable>> ();
		fireSetsClearedEvent();
	}

	public void initialize() {
		reset();
		CyTable cyTable;
		Collection<CyColumn> cyColumns;
		for (CyNetwork cyNetwork: netMgr.getNetworkSet()) {
			cyTable = cyNetwork.getTable(CyNode.class, CyNetwork.HIDDEN_ATTRS);
			cyColumns = cyTable.getColumns();
			for (CyColumn c: cyColumns) {
				String colName = c.getName();
				if (colName.length() >= 9 && colName.substring(0, 8).equals(TABLE_PREFIX)) {
					String loadedSetName = colName.substring(8);
					List<CyNode> cyNodes = new ArrayList<CyNode>();
					for (Long suid: cyTable.getPrimaryKey().getValues(Long.class))
						if (cyTable.getRow(suid).get(colName, Boolean.class))
							cyNodes.add(cyNetwork.getNode(suid));
					if (cyNodes != null && cyNodes.size() == 0) cyNodes = null;
					try {
						createSet(loadedSetName, cyNetwork, CyNode.class, cyNodes);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			cyTable = cyNetwork.getTable(CyEdge.class, CyNetwork.HIDDEN_ATTRS);
			cyColumns = cyTable.getColumns();
			for (CyColumn c: cyColumns) {
				String colName = c.getName();
				if (colName.length() >= 9 && colName.substring(0, 8).equals(TABLE_PREFIX)) {
					String loadedSetName = colName.substring(8);
					List<CyEdge> cyEdges = new ArrayList<CyEdge>();
					for (Long suid: cyTable.getPrimaryKey().getValues(Long.class))
						if (cyTable.getRow(suid).get(colName, Boolean.class))
							cyEdges.add(cyNetwork.getEdge(suid));
					if (cyEdges != null && cyEdges.size() == 0) cyEdges = null;
					try {
						createSet(loadedSetName, cyNetwork, CyEdge.class, cyEdges);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void rename(String name, String newName) throws Exception {
		if (! setsMap.containsKey(name)) throw new Exception("Set \"" + name + "\" does not exist.");
		if (setsMap.containsKey(newName)) throw new Exception("Set \"" + newName + "\" already exists. Choose a different name.");
		if (setsMap.containsKey(name) && ! setsMap.containsKey(newName)) {
			Set<? extends CyIdentifiable> oldSet = setsMap.get(name);
			CyNetwork oldNetwork = getCyNetwork(name);

			removeSet(name);

			oldSet.setName(newName);
			addSet(oldSet);
		}
	}
	
	public void addSet(Set<? extends CyIdentifiable> newSet) {
		String name = newSet.getName();
		setsMap.put(name, newSet);
		exportToAttribute(name);
		fireSetCreatedEvent(name);
	}


	public void removeSet(String name) throws Exception {
		if (! setsMap.containsKey(name)) throw new Exception("Set \"" + name + "\" does not exist.");
		String setTableName = TABLE_PREFIX + name;
		CyNetwork cyNetwork = getCyNetwork(name);
		// This needs to be after the call to getCyNetwork
		setsMap.remove(name);
		CyTable nodeTable = cyNetwork.getTable(CyNode.class, CyNetwork.HIDDEN_ATTRS);
		CyTable edgeTable = cyNetwork.getTable(CyEdge.class, CyNetwork.HIDDEN_ATTRS);
		if (nodeTable.getColumn(setTableName) != null)
			nodeTable.deleteColumn(setTableName);
		if (edgeTable.getColumn(setTableName) != null)
			edgeTable.deleteColumn(setTableName);
		fireSetRemovedEvent(name, cyNetwork);
	}
	
	public void union(String newName, String set1, String set2) throws Exception {
		sanityCheck(newName, set1, set2);
		Set<? extends CyIdentifiable> newSet = setsMap.get(set1).union(newName, setsMap.get(set2));
		addSet(newSet);
	}
	
	public void intersection(String newName, String set1, String set2) throws Exception {
		sanityCheck(newName, set1, set2);
		Set<? extends CyIdentifiable> newSet = setsMap.get(set1).intersection(newName, setsMap.get(set2));
		addSet(newSet);
	}
	
	public void difference(String newName, String set1, String set2) throws Exception {
		sanityCheck(newName, set1, set2);
		Set<? extends CyIdentifiable> newSet = setsMap.get(set1).difference(newName, setsMap.get(set2));
		addSet(newSet);
	}
	
	public void addToSet(String name, CyIdentifiable cyId) throws Exception {
		if (!setsMap.containsKey(name)) throw new Exception("Set \"" + name + "\" does not exist.");
		if (getCyNetwork(name).getRow(cyId) == null) throw new Exception(cyId + " is not in the network of set \"" + name + "\". Cannot perform operation.");
		Set<? extends CyIdentifiable> s = setsMap.get(name);
		if (getCyNetwork(name).getRow(cyId) != null && s.add(cyId)) {
			ArrayList<CyIdentifiable> cyIdList = new ArrayList<CyIdentifiable>();
			cyIdList.add(cyId);
			fireSetAddedEvent(name, cyIdList);
		} else 
			throw new Exception("Node/Edge already in the set \"" + name + "\" or not in network.");
	}

	public void moveSet(String name, String newName, CyNetwork cyNetwork) throws Exception {
		if (! setsMap.containsKey(name)) throw new Exception("Set \"" + name + "\" does not exist.");
		if (setsMap.containsKey(newName)) throw new Exception("Set \"" + newName + "\" already exist. Choose a different name.");
		Set<? extends CyIdentifiable> oldSet = setsMap.get(name);
		if (oldSet.getType() == CyNode.class) {
			List<CyNode> cyNodes = new ArrayList<CyNode>();
			CyNode thisNode;
			for (CyIdentifiable cyId: setsMap.get(name).getElements())
				if ((thisNode = cyNetwork.getNode(cyId.getSUID())) != null)
					cyNodes.add(thisNode);
			createSet(newName, cyNetwork, CyNode.class, cyNodes);
		}
		if (oldSet.getType() == CyEdge.class) {
			List<CyEdge> cyEdges = new ArrayList<CyEdge>();
			CyEdge thisEdge;
			for (CyIdentifiable cyId: setsMap.get(name).getElements())
				if ((thisEdge = cyNetwork.getEdge(cyId.getSUID())) != null)
					cyEdges.add(thisEdge);
			createSet(newName, cyNetwork, CyEdge.class, cyEdges);
		}
	}
	
	public void removeFromSet(String name, CyIdentifiable cyId) throws Exception {
		if (! setsMap.containsKey(name)) throw new Exception("Set \"" + name + "\" does not exist.");
		Set<? extends CyIdentifiable> s = setsMap.get(name);
		if (s.remove(cyId)) {
			ArrayList<CyIdentifiable> cyIdList = new ArrayList<CyIdentifiable>();
			cyIdList.add(cyId);
			fireSetRemovedEvent(name, cyIdList);
		}
	}
	
	// Implementation for SessionLoadedListener 
	public void handleEvent(SessionLoadedEvent sle) {
		initialize();
	}

	public void createSet(String name, CyNetwork network, Class<? extends CyIdentifiable> type, 
	                      List<? extends CyIdentifiable> members) {
		Set<? extends CyIdentifiable> newSet;

		if (type.equals(CyNode.class))
			newSet = new Set<CyNode>(name, network, CyNode.class, (List<CyNode>)members);
		else
			newSet = new Set<CyEdge>(name, network, CyEdge.class, (List<CyEdge>)members);

		setsMap.put(name, newSet);
		fireSetCreatedEvent(name);
	}

	private void exportToAttribute(String name) {
		CyTable table = null;
		CyNetwork network = getCyNetwork(name);
		String colName = TABLE_PREFIX + name;
		if (getType(name) == CyNode.class)
			table = network.getTable(CyNode.class, CyNetwork.HIDDEN_ATTRS);
		else if (getType(name) == CyEdge.class)
			table = network.getTable(CyEdge.class, CyNetwork.HIDDEN_ATTRS);

		// Delete the column to clear any old entries
		if (table != null && table.getColumn(colName) != null)
			table.deleteColumn(colName);

		table.createColumn(colName, Boolean.class, false);
		for (CyIdentifiable cyId: getSet(name).getElements())
			table.getRow(cyId.getSUID()).set(colName, true);
	}

	private void sanityCheck(String newName, String set1, String set2) throws Exception {
		if (setsMap.containsKey(newName)) throw new Exception("Set \"" + newName + "\" already exists. Choose a different name.");
		if (! setsMap.containsKey(set1)) throw new Exception("Set \"" + set1 + "\" does not exist.");
		if (! setsMap.containsKey(set2)) throw new Exception("Set \"" + set2 + "\" does not exist.");
	}
	
	/* Event handling */
	private void fireSetCreatedEvent(String setName) {
		SetChangedEvent event = new SetChangedEvent(this);
		event.setSetName(setName);
		Iterator<SetChangedListener> iterator = setChangedListener.iterator();
		while (iterator.hasNext())
			iterator.next().setCreated(event);
	}
	
	private void fireSetAddedEvent(String setName, List<? extends CyIdentifiable> added) {
		SetChangedEvent event = new SetChangedEvent(this);
		event.setSetName(setName);
		event.setCyNetwork(setsMap.get(setName).getNetwork());
		event.setCyIdsAdded(added);
		for (SetChangedListener listener: setChangedListener)
			listener.setChanged(event);
	}
	
	private void fireSetRemovedEvent(String setName, List<? extends CyIdentifiable> removed) {
		SetChangedEvent event = new SetChangedEvent(this);
		event.setSetName(setName);
		event.setCyNetwork(setsMap.get(setName).getNetwork());
		event.setCyIdsRemoved(removed);
		Iterator<SetChangedListener> iterator = setChangedListener.iterator();
		for (SetChangedListener listener: setChangedListener)
			listener.setChanged(event);
	}
	
	private void fireSetRemovedEvent(String setName, CyNetwork c) {
		SetChangedEvent event = new SetChangedEvent(this);
		event.setSetName(setName);
		event.setCyNetwork(c);
		for (SetChangedListener listener: setChangedListener)
			listener.setRemoved(event);
	}

	private void fireSetsClearedEvent() {
		SetChangedEvent event = new SetChangedEvent(this);
		for (SetChangedListener listener: setChangedListener)
			listener.setsCleared(event);
	}
}
