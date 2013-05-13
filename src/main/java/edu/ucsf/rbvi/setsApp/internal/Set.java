package edu.ucsf.rbvi.setsApp.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.model.CyIdentifiable;

public class Set <T extends CyIdentifiable> {
	private String name;
	private HashMap<Long, T> set;
	
	public Set(String localName) {
		initMethod(localName);
	}
	
	public Set(String localName, List<T> cyIds) {
		initMethod(localName);
		addList(cyIds);
	}
	
	private void initMethod(String localName) {
		name = localName;
		set = new HashMap<Long, T>();
	}
	
	public void addList(List<T> cyIds) {
		Iterator<T> iterator = cyIds.iterator();
		while (iterator.hasNext()) {
			T cyId = iterator.next();
			set.put(cyId.getSUID(), cyId);
		}
	}
	
	public boolean add(T cyId) {
		if (! set.containsKey(cyId.getSUID())) {
			set.put(cyId.getSUID(), cyId);
			return true;
		}
		else
			return false;
	}
	
	public boolean addCyId(CyIdentifiable cyId) {
		if (! set.containsKey(cyId.getSUID())) {
			set.put(cyId.getSUID(), (T) cyId);
			return true;
		}
		else
			return false;
	}
	
	public boolean remove(T cyId) {
		if (set.containsKey(cyId.getSUID())) {
			set.remove(cyId.getSUID());
			return true;
		}
		else return false;
	}
	
	public boolean removeCyId(CyIdentifiable cyId) {
		if (set.containsKey(cyId.getSUID())) {
			set.remove(cyId.getSUID());
			return true;
		}
		else return false;
	}
	
	public void rename(String newName) {
		name = newName;
	}
	
	public boolean hasCyId(Long cyId) {
		if (set.get(cyId) != null) return true;
		else return false;
	}
	
	public Set<T> intersection(String newName, Set<T> s) {
		Set<T> newSet = new Set<T>(newName);
		Collection<T> sValues = s.getElements();
		for (T curValue: sValues) {
			if (set.containsKey(curValue.getSUID()))
				newSet.add(curValue);
		}
		return newSet;
	}
	
	public Set<T> union(String newName, Set<T> s) {
		Set<T> newSet = new Set<T>(newName);
		Collection<T> sValues = s.getElements(), thisValue = getElements();
		for (T value: sValues)
			newSet.add(value);
		for (T value: thisValue)
			newSet.add(value);
		return newSet;
	}
	
	public Set<T> difference(String newName, Set<T> s) {
		Set<T> newSet = new Set<T>(newName);
		Collection<T> sValues = s.getElements();
		for (T curValue: sValues) {
			if (! set.containsKey(((CyIdentifiable) curValue).getSUID()))
				newSet.add(curValue);
		}
		return newSet;
	}
	
	public Collection<T> getElements() {
		return set.values();
	}

	public Set<? extends CyIdentifiable> unionGeneric(String newName,
			Set<? extends CyIdentifiable> set2) {
		return union(newName, (Set<T>) set2);
	}

	public Set<? extends CyIdentifiable> intersectionGeneric(String newName,
			Set<? extends CyIdentifiable> set2) {
		return intersection(newName, (Set<T>) set2);
	}

	public Set<? extends CyIdentifiable> differenceGeneric(String newName,
			Set<? extends CyIdentifiable> set2) {
		return difference(newName, (Set<T>) set2);
	}
	
	
}
