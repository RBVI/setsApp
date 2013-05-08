package edu.ucsf.rbvi.setsApp.internal.tasks;

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
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;

import edu.ucsf.rbvi.setsApp.internal.CyIdType;
import edu.ucsf.rbvi.setsApp.internal.Set;

public class SetsManager {
	private ConcurrentHashMap<String, Set<? extends CyIdentifiable>> setsMap;
	private ConcurrentHashMap<String, CyNetwork> networkSetNames;
	private ConcurrentHashMap<String, CyIdType> setType;
	private ArrayList<SetChangedListener> setChangedListener = new ArrayList<SetChangedListener>();
	
	public SetsManager() {
		this.setsMap = new ConcurrentHashMap<String, Set<? extends CyIdentifiable>> ();
		this.networkSetNames = new ConcurrentHashMap<String, CyNetwork>();
	}
	
	public SetsManager(SetChangedListener s) {
		this.setsMap = new ConcurrentHashMap<String, Set<? extends CyIdentifiable>> ();
		this.networkSetNames = new ConcurrentHashMap<String, CyNetwork>();
		if (s != null)
			this.setChangedListener.add(s);
	}
	
	public void addSetChangedListener(SetChangedListener s) {
		if (s != null)
			this.setChangedListener.add(s);
	}
	
	private void fireSetCreatedEvent(String setName) {
		SetChangedEvent event = new SetChangedEvent(setName);
		event.setSetName(setName);
		Iterator<SetChangedListener> iterator = setChangedListener.iterator();
		while (iterator.hasNext())
			iterator.next().setCreated(event);
	}
	
	private void fireSetAddedEvent(String setName, List<? extends CyIdentifiable> added) {
		SetChangedEvent event = new SetChangedEvent(setName);
		event.setSetName(setName);
		event.cyIdsAdded(added);
		Iterator<SetChangedListener> iterator = setChangedListener.iterator();
		while (iterator.hasNext())
			iterator.next().setChanged(event);
	}
	
	private void fireSetRemovedEvent(String setName, List<? extends CyIdentifiable> removed) {
		SetChangedEvent event = new SetChangedEvent(setName);
		event.setSetName(setName);
		event.cyIdsRemoved(removed);
		Iterator<SetChangedListener> iterator = setChangedListener.iterator();
		while (iterator.hasNext())
			iterator.next().setChanged(event);
	}
	
	private void fireSetRenamedEvent(String oldName, String setName) {
		SetChangedEvent event = new SetChangedEvent(setName);
		event.setSetName(setName);
		event.changeSetName(oldName, setName);
		Iterator<SetChangedListener> iterator = setChangedListener.iterator();
		while (iterator.hasNext())
			iterator.next().setRenamed(event);
	}
	
	private void fireSetRemovedEvent(String setName) {
		SetChangedEvent event = new SetChangedEvent(setName);
		event.setSetName(setName);
		Iterator<SetChangedListener> iterator = setChangedListener.iterator();
		while (iterator.hasNext())
			iterator.next().setRemoved(event);
	}
/*	public void createSetTask(String name, List<CyNode> cyNodes, List<CyEdge> cyEdges) {
		createSetTaskFactory.loadCyIdentifiables(name, cyNodes, cyEdges);
		
	} */
	
	public void createSet(String name, CyNetwork cyNetwork, List<CyNode> cyNodes, List<CyEdge> cyEdges) {
		if (! setsMap.containsKey(name))
			if ((cyNodes != null && ! cyNodes.isEmpty()) || (cyEdges != null && ! cyEdges.isEmpty())) {
				if (cyNodes != null && ! cyNodes.isEmpty()) {
					setsMap.put(name, new Set<CyNode>(name,cyNodes));
					setType.put(name, CyIdType.NODE);
				//	System.out.println("Added " + this.getSet(name) + " to set");
				}
				if (cyEdges != null && ! cyEdges.isEmpty()) {
					setsMap.put(name, new Set<CyEdge>(name, cyEdges));
					setType.put(name, CyIdType.EDGE);
				//	System.out.println("Added " + this.getSet(name) + " to set");
				}
				networkSetNames.put(name, cyNetwork);
				fireSetCreatedEvent(name);
			}
	}
	
	public void createSetFromAttributes(String name, String column, Object attribute, CyNetworkManager networkManager, String networkName, CyIdType type) {
		if (! setsMap.containsKey(name)) {
			CyNetwork network = null;
			java.util.Set<CyNetwork> networkSet = networkManager.getNetworkSet();
			for (CyNetwork net: networkSet) {
				CyTable cyTable = net.getDefaultNetworkTable();
				String netName = cyTable.getRow(net.getSUID()).get("name", String.class);
				if (networkName.equals(netName))
					network = net;
			}
			if (network != null) {
				if (type == CyIdType.NODE) {
					CyTable cyTable = network.getDefaultNodeTable();
					final ArrayList<CyNode> nodes = new ArrayList<CyNode>();
					final Collection<CyRow> selectedRows = cyTable.getMatchingRows(column, attribute);
					String primaryKey = cyTable.getPrimaryKey().getName();
					for (CyRow row: selectedRows) {
						final Long nodeId = row.get(primaryKey, Long.class);
						if (nodeId == null) continue;
						final CyNode node = network.getNode(nodeId);
						if (node == null) continue;
						nodes.add(node);
					}
					setsMap.put(name, new Set<CyNode>(name, nodes));
				}
				if (type == CyIdType.EDGE) {
					CyTable cyTable = network.getDefaultEdgeTable();
					final ArrayList<CyEdge> edges = new ArrayList<CyEdge>();
					final Collection<CyRow> selectedRows = cyTable.getMatchingRows(column, attribute);
					String primaryKey = cyTable.getPrimaryKey().getName();
					for (CyRow row: selectedRows) {
						final Long edgeId = row.get(primaryKey, Long.class);
						if (edgeId == null) continue;
						final CyEdge edge = network.getEdge(edgeId);
						if (edge == null) continue;
						edges.add(edge);
					}
					setsMap.put(name, new Set<CyEdge>(name, edges));		
				}
				networkSetNames.put(name, network);
				setType.put(name, type);
				fireSetCreatedEvent(name);
			}
		}
	}
	
	public void createSetsFromAttributes(String name, String column, CyNetwork network, CyIdType type) {
		if (! setsMap.containsKey(name)) {
			if (network != null) {
				if (type == CyIdType.NODE) {
					CyTable cyTable = network.getDefaultNodeTable();
					CyColumn cyIdColumn = cyTable.getPrimaryKey();
					List<Long> cyIdList = cyIdColumn.getValues(Long.class);
					HashMap<String, Set<CyNode>> cyNodeSet = new HashMap<String, Set<CyNode>>();
					for (Long cyId: cyIdList) {
						String attrName = name + ":" + cyTable.getRow(cyId).get(column, String.class);
						if (!cyNodeSet.containsKey(attrName)) cyNodeSet.put(attrName, new Set<CyNode>(attrName));
						cyNodeSet.get(attrName).add(network.getNode(cyId));
					}
					for (String s: cyNodeSet.keySet()) {
						setsMap.put(s, cyNodeSet.get(s));
						networkSetNames.put(s, network);
						setType.put(s, CyIdType.NODE);
						fireSetCreatedEvent(s);
					}
				}
				if (type == CyIdType.EDGE) {
					CyTable cyTable = network.getDefaultEdgeTable();
					CyColumn cyIdColumn = cyTable.getPrimaryKey();
					List<Long> cyIdList = cyIdColumn.getValues(Long.class);
					HashMap<String, Set<CyEdge>> cyNodeSet = new HashMap<String, Set<CyEdge>>();
					for (Long cyId: cyIdList) {
						String attrName = name + ":" + cyTable.getRow(cyId).get(column, String.class);
						if (!cyNodeSet.containsKey(attrName)) cyNodeSet.put(attrName, new Set<CyEdge>(attrName));
						cyNodeSet.get(attrName).add(network.getEdge(cyId));
					}
					for (String s: cyNodeSet.keySet()) {
						setsMap.put(s, cyNodeSet.get(s));
						networkSetNames.put(s, network);
						setType.put(s, CyIdType.EDGE);
						fireSetCreatedEvent(s);
					}
				}
			}
		}
	}
	
	public void createSetFromSelectedNetwork(String name, CyNetworkViewManager networkViewManager, CyIdType type) {
		if (! setsMap.containsKey(name)) {
			List<CyNode> cyNodes = null;
			List<CyEdge> cyEdges = null;
			Iterator<CyNetworkView> networkViewSet = networkViewManager.getNetworkViewSet().iterator();
			CyNetwork cyNetwork = null;
			while (networkViewSet.hasNext()) {
				cyNetwork = networkViewSet.next().getModel();
				if (cyNetwork != null && cyNetwork.getRow(cyNetwork).get(CyNetwork.SELECTED, Boolean.class)) {
					if (type == CyIdType.NODE) {
						cyNodes = CyTableUtil.getNodesInState(cyNetwork, CyNetwork.SELECTED, true);
						if (! cyNodes.isEmpty()) {
							setsMap.put(name, new Set<CyNode>(name,cyNodes));
							networkSetNames.put(name, cyNetwork);
							setType.put(name, type);
							fireSetCreatedEvent(name);
						}
					}
					else if (type == CyIdType.EDGE) {
						cyEdges = CyTableUtil.getEdgesInState(cyNetwork, CyNetwork.SELECTED, true);
						if (! cyEdges.isEmpty()) {
							setsMap.put(name, new Set<CyEdge>(name,cyEdges));
							networkSetNames.put(name, cyNetwork);
							setType.put(name, type);
							fireSetCreatedEvent(name);
						}
					}
				}
			}
		}
	/*	if (cyNodes != null && ! cyNodes.isEmpty())
			setsMap.put(name, new Set<CyNode>(name,cyNodes));
		else if (cyEdges != null && ! cyEdges.isEmpty())
			setsMap.put(name, new Set<CyEdge>(name,cyEdges));
		if ((cyNodes != null && ! cyNodes.isEmpty()) || (cyEdges != null && ! cyEdges.isEmpty())) {
			networkSetNames.put(name, cyNetwork);
			setType.put(name, type);
			fireSetCreatedEvent(name);
		} */
	}
	
	public void rename(String name, String newName) {
		if (setsMap.containsKey(name)) {
			Set<? extends CyIdentifiable> oldSet = setsMap.get(name);
			CyNetwork oldNetwork = networkSetNames.get(name);
			CyIdType oldType = setType.get(name);
			
			setsMap.remove(name);
			networkSetNames.remove(name);
			setType.remove(name);
			
			setsMap.put(newName, oldSet);
			networkSetNames.put(newName, oldNetwork);
			setType.put(newName, oldType);
			oldSet.rename(newName);
			fireSetRenamedEvent(name, newName);
		}
	}
	
	public void removeSet(String name) {
		setsMap.remove(name);
		networkSetNames.remove(name);
		setType.remove(name);
		fireSetRemovedEvent(name);
	}
	
	public void union(String newName, String set1, String set2) {
		if (! setsMap.containsKey(newName)) {
			setsMap.put(newName, setsMap.get(set1).unionGeneric(newName, setsMap.get(set2)));
			networkSetNames.put(newName, networkSetNames.get(set1));
			if (setType.get(set1) != null && setType.get(set2) != null && setType.get(set1) == setType.get(set2))
				setType.put(newName, setType.get(set1));
			fireSetCreatedEvent(newName);
		}
	}
	
	public void intersection(String newName, String set1, String set2) {
		if (! setsMap.containsKey(newName)) {
			setsMap.put(newName, setsMap.get(set1).intersectionGeneric(newName, setsMap.get(set2)));
			networkSetNames.put(newName, networkSetNames.get(set1));
			if (setType.get(set1) != null && setType.get(set2) != null && setType.get(set1) == setType.get(set2))
				setType.put(newName, setType.get(set1));
			fireSetCreatedEvent(newName);
		}
	}
	
	public void difference(String newName, String set1, String set2) {
		if (! setsMap.containsKey(newName)) {
			setsMap.put(newName, setsMap.get(set1).differenceGeneric(newName, setsMap.get(set2)));
			networkSetNames.put(newName, networkSetNames.get(set1));
			if (setType.get(set1) != null && setType.get(set2) != null && setType.get(set1) == setType.get(set2))
				setType.put(newName, setType.get(set1));
			fireSetCreatedEvent(newName);
		}
	}
	
	public void addToSet(String name, CyIdentifiable cyId) {
		Set<? extends CyIdentifiable> s = setsMap.get(name);
		if (s.addCyId(cyId)) {
			ArrayList<CyIdentifiable> cyIdList = new ArrayList<CyIdentifiable>();
			cyIdList.add(cyId);
			fireSetAddedEvent(name, cyIdList);
		}
	}
	
	public void removeFromSet(String name, CyIdentifiable cyId) {
		Set<? extends CyIdentifiable> s = setsMap.get(name);
		if (s.removeCyId(cyId)) {
			ArrayList<CyIdentifiable> cyIdList = new ArrayList<CyIdentifiable>();
			cyIdList.add(cyId);
			fireSetRemovedEvent(name, cyIdList);
		}
	}
	
	public CyNetwork getCyNetwork(String name) {
		return networkSetNames.get(name);
	}
	
	public List<String> getSetNames() {
		List<String> setNames = new ArrayList<String>();
		java.util.Set<String> set = setsMap.keySet();
		for (String s: set) {
			setNames.add(s);
		}
		return setNames;
	}
	
	public boolean isInSetsManager(String name) {
		if (setsMap.get(name) == null)
			return false;
		else
			return true;
	}
	
	public CyIdType getType(String name) {
		return setType.get(name);
	}
	
	public void reset() {
		this.setsMap = new ConcurrentHashMap<String, Set<? extends CyIdentifiable>> ();
		this.networkSetNames = new ConcurrentHashMap<String, CyNetwork>();
		this.setType = new ConcurrentHashMap<String, CyIdType>();
	}
	
	public Set<? extends CyIdentifiable> getSet(String setName) {
		return setsMap.get(setName);
	}
}
