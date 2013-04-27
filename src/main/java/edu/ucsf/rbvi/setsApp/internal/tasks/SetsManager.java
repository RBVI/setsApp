package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.CyNetworkView;

import edu.ucsf.rbvi.setsApp.internal.Set;

public class SetsManager {
	private HashMap<String, Set<? extends CyIdentifiable>> setsMap;
	private ArrayList<SetChangedListener> setChangedListener = new ArrayList<SetChangedListener>();
/*	private CreateSetTaskFactory createSetTaskFactory; */
	
	public SetsManager() {
		this.setsMap = new HashMap<String, Set<? extends CyIdentifiable>> ();
	/*	createSetTaskFactory = new CreateSetTaskFactory(this); */
	}
	
	public SetsManager(SetChangedListener s) {
		this.setsMap = new HashMap<String, Set<? extends CyIdentifiable>> ();
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
	
	private void fireSetRemovedEvent(String setName) {
		SetChangedEvent event = new SetChangedEvent(null);
		event.setSetName(setName);
		Iterator<SetChangedListener> iterator = setChangedListener.iterator();
		while (iterator.hasNext())
			iterator.next().setRemoved(event);
	}
/*	public void createSetTask(String name, List<CyNode> cyNodes, List<CyEdge> cyEdges) {
		createSetTaskFactory.loadCyIdentifiables(name, cyNodes, cyEdges);
		
	} */
	
	public void createSet(String name, List<CyNode> cyNodes, List<CyEdge> cyEdges) {
		if (cyNodes != null)
			setsMap.put(name, new Set<CyNode>(name,cyNodes));
		else if (cyEdges != null)
			setsMap.put(name, new Set<CyEdge>(name, cyEdges));
		fireSetCreatedEvent(name);
	}
	
	public void createSet(String name, List<CyIdentifiable> cyNodes) {
		setsMap.put(name, new Set<CyIdentifiable>(name,cyNodes));
	}
	
	public void removeSet(String name) {
		setsMap.remove(name);
		fireSetRemovedEvent(name);
	}
	
	public void union(String newName, String set1, String set2) {
		setsMap.put(newName, setsMap.get(set1).unionGeneric(newName, setsMap.get(set2)));
		fireSetCreatedEvent(newName);
	}
	
	public void intersection(String newName, String set1, String set2) {
		setsMap.put(newName, setsMap.get(set1).intersectionGeneric(newName, setsMap.get(set2)));
		fireSetCreatedEvent(newName);
	}
	
	public void difference(String newName, String set1, String set2) {
		setsMap.put(newName, setsMap.get(set1).differenceGeneric(newName, setsMap.get(set2)));
		fireSetCreatedEvent(newName);
	}
	
	public Set<? extends CyIdentifiable> getSet(String setName) {
		return setsMap.get(setName);
	}
}
