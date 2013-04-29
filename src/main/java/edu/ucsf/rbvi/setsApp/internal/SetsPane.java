package edu.ucsf.rbvi.setsApp.internal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;

import edu.ucsf.rbvi.setsApp.internal.tasks.CreateSetTaskFactory;
import edu.ucsf.rbvi.setsApp.internal.tasks.SetChangedEvent;
import edu.ucsf.rbvi.setsApp.internal.tasks.SetChangedListener;
import edu.ucsf.rbvi.setsApp.internal.tasks.SetsManager;

public class SetsPane extends JPanel implements CytoPanelComponent, SetChangedListener, SessionLoadedListener {
	private JButton createNodeSet, createEdgeSet, importSet, exportSet, newSetFromAttribute, union, intersection, difference;
	private JTree setsTree;
	private DefaultMutableTreeNode sets;
	private JScrollPane scrollPane;
	private BundleContext bundleContext;
	private SetsManager mySets;
	private CyNetworkViewManager networkViewManager;
	private CreateSetTaskFactory createSetTaskFactory;
	private TaskManager taskManager;
	
	public SetsPane(BundleContext bc) {
		bundleContext = bc;
		mySets = new SetsManager(this);
		createSetTaskFactory = new CreateSetTaskFactory(mySets);
		networkViewManager = (CyNetworkViewManager) getService(CyNetworkViewManager.class);
		taskManager = (TaskManager) getService(TaskManager.class);

		setPreferredSize(new Dimension(500,600));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		createNodeSet = new JButton("Create Node Set");
		createNodeSet.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
			//	taskManager.execute(createSetTaskFactory.createTaskIterator(null, getSelectedNodes(networkViewManager), null));
				taskManager.execute(createSetTaskFactory.createTaskIterator(null, networkViewManager, CyIdType.NODE));
			}
		});
		createEdgeSet = new JButton("Create Edge Set");
		createEdgeSet.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
			//	taskManager.execute(createSetTaskFactory.createTaskIterator(null, null, getSelectedEdges(networkViewManager)));
				taskManager.execute(createSetTaskFactory.createTaskIterator(null, networkViewManager, CyIdType.EDGE));
			}
		});
		importSet = new JButton("Import Set");
		exportSet = new JButton("Export Set");
		newSetFromAttribute = new JButton("New Set From Attributes");
		union = new JButton("Union");
		union.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				taskManager.execute(createSetTaskFactory.createTaskIterator(null, null, null, SetOperations.UNION));
			}
		});
		intersection = new JButton("Intersection");
		intersection.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				taskManager.execute(createSetTaskFactory.createTaskIterator(null, null, null, SetOperations.INTERSECT));
			}
		});
		difference = new JButton("Set Difference");
		difference.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				taskManager.execute(createSetTaskFactory.createTaskIterator(null, null, null, SetOperations.DIFFERENCE));
			}
		});
		
		sets = new DefaultMutableTreeNode("Sets");
		setsTree = new JTree(sets);
		scrollPane = new JScrollPane(setsTree);
		
		add(createNodeSet);
		add(createEdgeSet);
		add(importSet);
		add(exportSet);
		add(newSetFromAttribute);
		add(scrollPane);
		add(union);
		add(intersection);
		add(difference);
	}
	
	private Object getService(Class<?> serviceClass) {
		return bundleContext.getService(bundleContext.getServiceReference(serviceClass.getName()));
    }
	
/*	private List<CyNode> getSelectedNodes(CyNetworkViewManager networkViewManager) {
		List<CyNode> cyNodes = null;
		Iterator<CyNetworkView> networkViewSet = networkViewManager.getNetworkViewSet().iterator();
		CyNetwork cyNetwork = null;
		while (networkViewSet.hasNext()) {
			cyNetwork = networkViewSet.next().getModel();
			if (cyNetwork.getRow(cyNetwork).get(CyNetwork.SELECTED, Boolean.class))
				cyNodes = CyTableUtil.getNodesInState(cyNetwork, CyNetwork.SELECTED, true);
		}
		return cyNodes;
	}
	
	private List<CyEdge> getSelectedEdges(CyNetworkViewManager networkViewManager) {
		List<CyEdge> cyEdges = null;
		Iterator<CyNetworkView> networkViewSet = networkViewManager.getNetworkViewSet().iterator();
		CyNetwork cyNetwork = null;
		while (networkViewSet.hasNext()) {
			cyNetwork = networkViewSet.next().getModel();
			if (cyNetwork.getRow(cyNetwork).get(CyNetwork.SELECTED, Boolean.class))
				cyEdges = CyTableUtil.getEdgesInState(cyNetwork, CyNetwork.SELECTED, true);
		}
		return cyEdges;
	} */
	
	private static final long serialVersionUID = -3152025163466058952L;

	public Component getComponent() {
		return this;
	}

	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}

	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {
		return "Sets";
	}
	
	private void addSetToTree(String name) {
		DefaultMutableTreeNode thisSet = new DefaultMutableTreeNode(name);
		Iterator<? extends CyIdentifiable> cyIds = (Iterator<? extends CyIdentifiable>) mySets.getSet(name).getElements();
		while (cyIds.hasNext()) {
			CyIdentifiable cyId = cyIds.next();
			thisSet.add(new DefaultMutableTreeNode(cyId.toString()));
		}
		sets.add(thisSet);
	}

	public synchronized void setCreated(SetChangedEvent event) {
	//	System.out.println("Adding " + event.getSetName() + " to tree");
		DefaultMutableTreeNode thisSet = new DefaultMutableTreeNode(event.getSetName());
		System.out.println(mySets.getSet("Set " + event.getSetName() + ":" +event.getSetName()));
		Iterator<? extends CyIdentifiable> iterator = (Iterator<? extends CyIdentifiable>) mySets.getSet(event.getSetName()).getElements();
		while (iterator.hasNext()) {
			CyIdentifiable cyId = iterator.next();
			thisSet.add(new DefaultMutableTreeNode(cyId.toString()));
		}
		sets.add(thisSet);
	//	System.out.println("Added " + event.getSetName() + " to tree");
	//	System.out.println("Adding " + event.getSetName() + " to table");
		CyNetwork cyNetwork = mySets.getCyNetwork(event.getSetName());
		CyTable networkTable = cyNetwork.getDefaultNetworkTable();
		if (networkTable.getColumn("setsApp:" + event.getSetName()) == null) {
			networkTable.createListColumn("setsApp:" + event.getSetName(), Long.class, false);
			ArrayList<Long> suidSet = new ArrayList<Long>();
			iterator = (Iterator<? extends CyIdentifiable>) mySets.getSet(event.getSetName()).getElements();
			while (iterator.hasNext())
				suidSet.add(iterator.next().getSUID());
			networkTable.getRow(cyNetwork.getSUID()).set("setsApp:" + event.getSetName(), suidSet);
		}
	//	else
	//		System.out.println(event.getSetName() + " already in table");
	}

	public void setRemoved(SetChangedEvent event) {
		Iterator<? extends CyIdentifiable> iterator = (Iterator<? extends CyIdentifiable>) mySets.getSet(event.getSetName()).getElements();
		while (iterator.hasNext()) {
			System.out.println(iterator.next().toString());
		}
	}

	public void handleEvent(SessionLoadedEvent event) {
		mySets.reset();
		CyNetworkManager nm = (CyNetworkManager) getService(CyNetworkManager.class);
		Iterator<CyNetwork> networks = nm.getNetworkSet().iterator();
		while (networks.hasNext()) {
			CyNetwork cyNetwork = networks.next();
			CyTable cyTable = cyNetwork.getDefaultNetworkTable();
			Iterator<CyColumn> cyColumns = cyTable.getColumns().iterator();
			while (cyColumns.hasNext()) {
				CyColumn c = cyColumns.next();
				String colName = c.getName();
				if (colName.length() >= 9 && colName.substring(0, 8).equals("setsApp:")) {
					ArrayList<CyNode> cyNodes = null;
					ArrayList<CyEdge> cyEdges = null;
					String loadedSetName = colName.substring(8);
					Iterator<List> suidIterator = c.getValues(List.class).iterator();
					Iterator<Long> suids = suidIterator.next().iterator();
				//	System.out.println(loadedSetName);
					while (suids.hasNext()) {
						long suid = suids.next();
						CyNode thisNode = cyNetwork.getNode(suid);
						CyEdge thisEdge = cyNetwork.getEdge(suid);
						if (thisNode != null) {
							if (cyNodes != null) 
								cyNodes.add(thisNode);
							else {
								cyNodes = new ArrayList<CyNode>();
								cyNodes.add(thisNode);
							}
						}
						if (thisEdge != null) {
							if (cyEdges != null)
								cyEdges.add(thisEdge);
							else {
								cyEdges = new ArrayList<CyEdge>();
								cyEdges.add(thisEdge);
							}
						}
					}
				//	System.out.println("cyNodes to be added: " + cyNodes);
				//	System.out.println("cyEdges to be added: " + cyEdges);
					taskManager.execute(createSetTaskFactory.createTaskIterator(loadedSetName, cyNetwork, cyNodes, cyEdges));
				}
			}
		}
	}

}
