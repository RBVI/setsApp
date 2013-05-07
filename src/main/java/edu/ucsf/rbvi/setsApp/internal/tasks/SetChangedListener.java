package edu.ucsf.rbvi.setsApp.internal.tasks;

import java.util.EventListener;

public interface SetChangedListener extends EventListener {
	public void setCreated(SetChangedEvent event);
	public void setChanged(SetChangedEvent event);
	public void setRemoved(SetChangedEvent event);
}
