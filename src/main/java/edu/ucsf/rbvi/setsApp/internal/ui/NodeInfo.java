package edu.ucsf.rbvi.setsApp.internal.ui;

import org.cytoscape.model.CyIdentifiable;

public class NodeInfo {
	public String label;
	public CyIdentifiable cyId;
	public String setName;
	
	public NodeInfo(String name, CyIdentifiable s) {
		label = name;
		cyId = s;
		setName = null;
	}
	public NodeInfo(String name, String setName) {
		label = name;
		this.setName = setName;
		cyId = null;
	}
	public String toString() {
		return label;
	}
}
