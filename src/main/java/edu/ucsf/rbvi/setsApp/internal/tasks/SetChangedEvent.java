package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.EventObject;

public class SetChangedEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9100575041268798978L;
	private String setName = null;
	
	public SetChangedEvent(Object source) {
		super(source);
	}
	
	public void setSetName(String s) {
		if (setName == null)
			setName = s;
	}
	
	public String getSetName() {
		return setName;
	}
}