package edu.ucsf.rbvi.setsApp.internal;

import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.ENABLE_FOR;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_BEFORE;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.io.BasicCyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ucsf.rbvi.setsApp.internal.tasks.CreateNodeSetTaskFactory;
import edu.ucsf.rbvi.setsApp.internal.tasks.AddEdgeViewTaskFactory;
import edu.ucsf.rbvi.setsApp.internal.tasks.CreateEdgeSetTaskFactory;
import edu.ucsf.rbvi.setsApp.internal.tasks.CreateSetFromAttributesTaskFactory;
import edu.ucsf.rbvi.setsApp.internal.tasks.RemoveSetTaskFactory;
import edu.ucsf.rbvi.setsApp.internal.tasks.RenameSetTaskFactory;
import edu.ucsf.rbvi.setsApp.internal.tasks.SetOperationsTaskFactory;
//import edu.ucsf.rbvi.setsApp.internal.tasks.CreateNodeSetTaskFactory;
import edu.ucsf.rbvi.setsApp.internal.tasks.CreateSetTaskFactory;
import edu.ucsf.rbvi.setsApp.internal.tasks.AddNodeViewTaskFactory;
import edu.ucsf.rbvi.setsApp.internal.tasks.SetsManager;

public class CyActivator extends AbstractCyActivator {
	private static Logger logger = LoggerFactory
			.getLogger(edu.ucsf.rbvi.setsApp.internal.CyActivator.class);
	
	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		SetsManager sets = new SetsManager();
		// See if we have a graphics console or not
		boolean haveGUI = true;
		CySwingApplication cyApplication = getService(bc, CySwingApplication.class);
	/*	CyNetworkViewManager networkViewManager = getService(bc, CyNetworkViewManager.class); */
		if (cyApplication == null) {
			haveGUI = false;
			// Issue error and return
		}
		else {
			NodeViewTaskFactory addNode = new AddNodeViewTaskFactory(sets);
			Properties addNodeToSetProperties = new Properties();
			setStandardProperties(addNodeToSetProperties, "Add node to set", "addNode", "3.0");
			registerService(bc,addNode,NodeViewTaskFactory.class,addNodeToSetProperties);
			
			EdgeViewTaskFactory addEdge = new AddEdgeViewTaskFactory(sets);
			Properties addEdgeToSetProperties = new Properties();
			setStandardProperties(addEdgeToSetProperties, "Add edge to set", "addEdge", "4.0");
			registerService(bc, addEdge, EdgeViewTaskFactory.class, addEdgeToSetProperties);
			
			NetworkViewTaskFactory createNodeSetTaskFactory = new CreateNodeSetTaskFactory(sets);
			Properties createNodeSetProperties = new Properties();
			setStandardProperties(createNodeSetProperties, "Create node set", "createNodeSet", "1.0");
			registerService(bc, createNodeSetTaskFactory, NetworkViewTaskFactory.class, createNodeSetProperties);
			
			NetworkViewTaskFactory createEdgeSetTaskFactory = new CreateEdgeSetTaskFactory(sets);
			Properties createEdgeSetProperties = new Properties();
			setStandardProperties(createEdgeSetProperties, "Create edge set", "createEdgeSet", "2.0");
			registerService(bc, createEdgeSetTaskFactory, NetworkViewTaskFactory.class, createEdgeSetProperties);
			
			SetsPane setsPanel = new SetsPane(bc, sets);
			registerService(bc,setsPanel,CytoPanelComponent.class, new Properties());
			registerService(bc,setsPanel,SessionLoadedListener.class, new Properties());
		}
		NetworkTaskFactory createNodeSetFromAttributes = new CreateSetFromAttributesTaskFactory(sets, CyIdType.NODE);
		Properties createNodeSetFromAttributesProperties = new Properties();
		setStandardProperties(createNodeSetFromAttributesProperties, "Create node set from attributes", "createNodeSetFromAttributes", "1.0");
		registerService(bc, createNodeSetFromAttributes, NetworkTaskFactory.class, createNodeSetFromAttributesProperties);
		
		NetworkTaskFactory createEdgeSetFromAttributes = new CreateSetFromAttributesTaskFactory(sets, CyIdType.EDGE);
		Properties createEdgeSetFromAttributesProperties = new Properties();
		setStandardProperties(createEdgeSetFromAttributesProperties, "Create edge set from attributes", "createEdgeSetFromAttributes", "2.0");
		registerService(bc, createEdgeSetFromAttributes, NetworkTaskFactory.class, createEdgeSetFromAttributesProperties);
		
		TaskFactory nodeUnion = new SetOperationsTaskFactory(sets, CyIdType.NODE, SetOperations.UNION);
		Properties nodeUnionProperties = new Properties();
		setStandardProperties(nodeUnionProperties, "Union", "nodeUnion", "1.0");
		nodeUnionProperties.setProperty(PREFERRED_MENU, "Apps.SetsApp.Operations.Nodes");
		registerService(bc,nodeUnion, TaskFactory.class, nodeUnionProperties);
		
		TaskFactory nodeIntersect = new SetOperationsTaskFactory(sets, CyIdType.NODE, SetOperations.INTERSECT);
		Properties nodeIntersectProperties = new Properties();
		setStandardProperties(nodeIntersectProperties, "Intersect", "nodeIntersect", "2.0");
		nodeIntersectProperties.setProperty(PREFERRED_MENU, "Apps.SetsApp.Operations.Nodes");
		registerService(bc,nodeIntersect, TaskFactory.class, nodeIntersectProperties);
		
		TaskFactory nodeDifference = new SetOperationsTaskFactory(sets, CyIdType.NODE, SetOperations.DIFFERENCE);
		Properties nodeDifferenceProperties = new Properties();
		setStandardProperties(nodeDifferenceProperties, "Difference", "nodeDifference", "3.0");
		nodeDifferenceProperties.setProperty(PREFERRED_MENU, "Apps.SetsApp.Operations.Nodes");
		registerService(bc,nodeDifference, TaskFactory.class, nodeDifferenceProperties);
		
		TaskFactory edgeUnion = new SetOperationsTaskFactory(sets, CyIdType.EDGE, SetOperations.UNION);
		Properties edgeUnionProperties = new Properties();
		setStandardProperties(edgeUnionProperties, "Union", "edgeUnion", "1.0");
		edgeUnionProperties.setProperty(PREFERRED_MENU, "Apps.SetsApp.Operations.Edge");
		registerService(bc,edgeUnion, TaskFactory.class, edgeUnionProperties);
		
		TaskFactory edgeIntersection = new SetOperationsTaskFactory(sets, CyIdType.EDGE, SetOperations.INTERSECT);
		Properties edgeIntersectProperties = new Properties();
		setStandardProperties(edgeIntersectProperties, "Intersection", "edgeIntersect", "2.0");
		edgeIntersectProperties.setProperty(PREFERRED_MENU, "Apps.SetsApp.Operations.Edge");
		registerService(bc,edgeIntersection, TaskFactory.class, edgeIntersectProperties);
		
		TaskFactory edgeDifference = new SetOperationsTaskFactory(sets, CyIdType.EDGE, SetOperations.DIFFERENCE);
		Properties edgeDifferenceProperties = new Properties();
		setStandardProperties(edgeDifferenceProperties, "Difference", "edgeDifference", "3.0");
		edgeDifferenceProperties.setProperty(PREFERRED_MENU, "Apps.SetsApp.Operations.Edge");
		registerService(bc,edgeDifference, TaskFactory.class, edgeDifferenceProperties);
		
		TaskFactory renameSet = new RenameSetTaskFactory(sets);
		Properties renameSetProperties = new Properties();
		setStandardProperties(renameSetProperties, "Rename Set", "renameSet", "3.0");
		registerService(bc, renameSet, TaskFactory.class, renameSetProperties);
		
		TaskFactory removeSet = new RemoveSetTaskFactory(sets);
		Properties removeSetProperties = new Properties();
		setStandardProperties(removeSetProperties, "Remove Set", "removeSet", "4.0");
		registerService(bc, removeSet, TaskFactory.class, removeSetProperties);
	/*	TaskFactory createSetTaskFactory = new CreateSetTaskFactory();
		Properties createSetTaskProps = new Properties();
		// These are just example properties for placing the factory in the apps menu
		createSetTaskProps.setProperty(PREFERRED_MENU, "Apps.SetsApp");
		createSetTaskProps.setProperty(TITLE, "Create set");
		createSetTaskProps.setProperty(COMMAND, "createSet");
		createSetTaskProps.setProperty(COMMAND_NAMESPACE, "setsApp");
		createSetTaskProps.setProperty(ENABLE_FOR, "network");
		createSetTaskProps.setProperty(IN_MENU_BAR, "true");
		createSetTaskProps.setProperty(MENU_GRAVITY, "1.0");
		
		Iterator<CyNetworkView> networkViewSet = networkViewManager.getNetworkViewSet().iterator();
		CyNetwork cyNetwork = networkViewSet.next().getModel(); */
	/*	TaskManager task = getService(bc, TaskManager.class);
		task.execute(createSetTaskFactory.createTaskIterator()); */
		
	/*	registerService(bc, createSetTaskFactory, NetworkTaskFactory.class, createSetTaskProps); */
	}
	
	private void setStandardProperties(Properties p, String title, String command, String gravity) {
		p.setProperty(TITLE, title);
		p.setProperty(PREFERRED_MENU, "Apps.SetsApp");
		p.setProperty(COMMAND,command);
		p.setProperty(COMMAND_NAMESPACE,"setsApp");
		p.setProperty(IN_MENU_BAR,"false");
		p.setProperty(MENU_GRAVITY, gravity);
	}
}
