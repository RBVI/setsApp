package edu.ucsf.rbvi.setsApp.internal.events;

import java.util.EventObject;
import java.util.List;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;

/**
 * This is the event object created for all SetChangedListener
 * calls.
 */
public class SetChangedEvent extends EventObject {

	private static final long serialVersionUID = -9100575041268798978L;
	private String setName = null, oldSet = null;
	private List<? extends CyIdentifiable> added = null, removed = null;
	private CyNetwork setNetwork = null;
	
	/**
 	 * Create a SetChangedEvent object.  The source is almost
 	 * always the SetsManager.
 	 *
 	 * @param source the SetsManager calling the listener
 	 */
	public SetChangedEvent(Object source) {
		super(source);
	}
	
	/**
 	 * Returns the name of the set that is being effected by the
 	 * change.  This not relevant for the setsCleared method.
 	 *
 	 * @return the name of the set that is effected.
 	 */
	public String getSetName() {
		return setName;
	}
	
	/**
 	 * Sets the name of the set that is being effected by the
 	 * change.
 	 *
 	 * @param s the name of the set that is effected.
 	 */
	public void setSetName(String s) {
		if (setName == null)
			setName = s;
	}
	
	/**
 	 * Returns the CyNetwork of the set that is being effected by the
 	 * change.  This not relevant for the setsCleared method.
 	 *
 	 * @return the network of the set that is effected.
 	 */
	public CyNetwork getCyNetwork() {
		return setNetwork;
	}
	
	/**
 	 * Sets the CyNetwork of the set that is being effected by the
 	 * change.
 	 *
 	 * @param network the CyNetwork of the set that is effected.
 	 */
	public void setCyNetwork(CyNetwork network) {
		setNetwork = network;
	}

	/**
 	 * Returns the list of members that have been added
 	 * to the set.
 	 *
 	 * @return the list of added members
 	 */
	public List<? extends CyIdentifiable> getCyIdsAdded() {return added;}
	
	/**
 	 * Sets the list of members that have been added
 	 * to the set.
 	 *
 	 * @param a the list of added members
 	 */
	public void setCyIdsAdded(List<? extends CyIdentifiable> a) {
		if (added == null) added = a;
	}
	
	/**
 	 * Returns the list of members that have been removed
 	 * from the set.
 	 *
 	 * @return the list of removed members
 	 */
	public List<? extends CyIdentifiable> getCyIdsRemoved() {return removed;}
	
	/**
 	 * Sets the list of members that have been removed
 	 * from the set.
 	 *
 	 * @param r the list of removed members
 	 */
	public void setCyIdsRemoved(List<? extends CyIdentifiable> r) {
		if (removed == null) removed = r;
	}
}
