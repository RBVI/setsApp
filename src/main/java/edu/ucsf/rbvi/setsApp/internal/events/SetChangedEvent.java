package edu.ucsf.rbvi.setsApp.internal.events;

import java.util.EventObject;
import java.util.List;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;

public class SetChangedEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9100575041268798978L;
	private String setName = null, oldSet = null;
	private List<? extends CyIdentifiable> added = null, removed = null;
	private CyNetwork setNetwork = null;
	
	public SetChangedEvent(Object source) {
		super(source);
	}
	
	public void setSetName(String s) {
		if (setName == null)
			setName = s;
	}
	
	public void changeSetName(String oldName, String newName) {
		if (setName == null) setName = newName;
		if (oldSet == null) oldSet = oldName;
	}
	
	public void cyIdsAdded(List<? extends CyIdentifiable> a) {
		if (added == null) added = a;
	}
	
	public void cyIdsRemoved(List<? extends CyIdentifiable> r) {
		if (removed == null) removed = r;
	}
	
	public void setCyNetwork(CyNetwork c) {
		setNetwork = c;
	}
	
	public String getSetName() {
		return setName;
	}
	
	public String getOldSetName() {
		return oldSet;
	}
	
	public CyNetwork getCyNetwork() {
		return setNetwork;
	}
	
	public List<? extends CyIdentifiable> getCyIdsAdded() {return added;}
	
	public List<? extends CyIdentifiable> getCyIdsRemoved() {return removed;}
}