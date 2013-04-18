package edu.ucsf.rbvi.setsApp.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;



public class CreateSetTaskFactory extends AbstractTaskFactory implements NetworkTaskFactory {

  public CreateSetTaskFactory() {}

  public TaskIterator createTaskIterator() {
	  return null;
  }

  public boolean isReady(CyNetwork network) {
	  return true;
  }

  public TaskIterator createTaskIterator(CyNetwork network) {
	  return new TaskIterator(new CreateSetTask());
  }


}