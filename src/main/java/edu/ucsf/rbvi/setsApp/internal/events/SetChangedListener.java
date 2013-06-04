package edu.ucsf.rbvi.setsApp.internal.events;

import java.util.EventListener;

/**
 * This interface is for classes to implement that are
 * interested in changes to Sets.  In order to receive
 * these events, the class must register itself using
 * the {@link edu.ucsf.rbvi.setsApp.internal.ui.SetManager#addListener}
 * call.
 */
public interface SetChangedListener extends EventListener {
	/**
 	 * This method is called when a set is created.  It is also
 	 * called as the second step of a rename operation.
 	 *
 	 * @param event the SetChangeEvent that triggered this call
 	 */
	public void setCreated(SetChangedEvent event);

	/**
 	 * This method is called after a set is removed.  It is also
 	 * called as the first step of a rename operation.
 	 *
 	 * @param event the SetChangeEvent that triggered this call
 	 */
	public void setRemoved(SetChangedEvent event);

	/**
 	 * This method is called when a set is changed.  This is
 	 * always caused by an add or removal of a member of
 	 * the set.
 	 *
 	 * @param event the SetChangeEvent that triggered this call
 	 */
	public void setChanged(SetChangedEvent event);

	/**
 	 * This method is called when the all of the set information
 	 * in the SetManager is cleared (reset).  This occurs, for
 	 * example when the a session is loaded and at initialization
 	 * time.
 	 *
 	 * @param event the SetChangeEvent that triggered this call
 	 */
	public void setsCleared(SetChangedEvent event);
}
