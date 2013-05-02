package edu.ucsf.rbvi.setsApp.internal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;

import edu.ucsf.rbvi.setsApp.internal.tasks.CreateSetTaskFactory;
import edu.ucsf.rbvi.setsApp.internal.tasks.SetChangedEvent;
import edu.ucsf.rbvi.setsApp.internal.tasks.SetChangedListener;
import edu.ucsf.rbvi.setsApp.internal.tasks.SetsManager;

public class SetsPane extends JPanel implements CytoPanelComponent, SetChangedListener, SessionLoadedListener {
	private JButton createSet, newSetFromAttribute, union, intersection, difference;
	private ButtonGroup select;
	private JRadioButton selectNodes, selectEdges;
	private JTree setsTree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode sets;
	private JScrollPane scrollPane;
	private BundleContext bundleContext;
	private SetsManager mySets;
	private CyNetworkManager networkManager;
	private CyNetworkViewManager networkViewManager;
	private CreateSetTaskFactory createSetTaskFactory;
	private TaskManager taskManager;
	public static final String tablePrefix = "setsApp:";
	
	public SetsPane(BundleContext bc) {
		bundleContext = bc;
		mySets = new SetsManager(this);
		createSetTaskFactory = new CreateSetTaskFactory(mySets);
		networkManager = (CyNetworkManager) getService(CyNetworkManager.class);
		networkViewManager = (CyNetworkViewManager) getService(CyNetworkViewManager.class);
		taskManager = (TaskManager) getService(TaskManager.class);

		setPreferredSize(new Dimension(500,600));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		select = new ButtonGroup();
		selectNodes = new JRadioButton("Nodes");
		selectNodes.setSelected(true);
		selectEdges = new JRadioButton("Edges");
		select.add(selectNodes);
		select.add(selectEdges);
		createSet = new JButton("Create Set");
		createSet.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (selectNodes.isSelected())
					taskManager.execute(createSetTaskFactory.createTaskIterator(null, networkViewManager, CyIdType.NODE));
				if (selectEdges.isSelected())
					taskManager.execute(createSetTaskFactory.createTaskIterator(null, networkViewManager, CyIdType.EDGE));
			}
		});
		newSetFromAttribute = new JButton("Create Set From Attributes");
		newSetFromAttribute.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				CyNetwork network = null;
				for (CyNetwork n: networkManager.getNetworkSet())
					if (n.getRow(n).get(CyNetwork.SELECTED, Boolean.class)) network = n;
				if (network != null) {
					if (selectNodes.isSelected())
						taskManager.execute(createSetTaskFactory.createTaskIterator(network, CyIdType.NODE));
					if (selectEdges.isSelected())
						taskManager.execute(createSetTaskFactory.createTaskIterator(network, CyIdType.EDGE));
				}
			}
		});
		union = new JButton("Union");
		union.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				taskManager.execute(createSetTaskFactory.createTaskIterator(selectNodes.isSelected() ? CyIdType.NODE : CyIdType.EDGE, SetOperations.UNION));
			}
		});
		intersection = new JButton("Intersection");
		intersection.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				taskManager.execute(createSetTaskFactory.createTaskIterator(selectNodes.isSelected() ? CyIdType.NODE : CyIdType.EDGE, SetOperations.INTERSECT));
			}
		});
		difference = new JButton("Set Difference");
		difference.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				taskManager.execute(createSetTaskFactory.createTaskIterator(selectNodes.isSelected() ? CyIdType.NODE : CyIdType.EDGE, SetOperations.DIFFERENCE));
			}
		});
		
		sets = new DefaultMutableTreeNode("Sets");
		setsTree = new JTree(sets);
		scrollPane = new JScrollPane(setsTree);
		treeModel = (DefaultTreeModel) setsTree.getModel();
		URL myUrl = SetsPane.class.getResource("images/Node.png");
		ImageIcon nodeIcon = new ImageIcon(myUrl);
		myUrl = SetsPane.class.getResource("images/Edge.png");
		ImageIcon edgeIcon = new ImageIcon(myUrl);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(nodeIcon);
		setsTree.setCellRenderer(renderer);
	//	if (nodeIcon != null && edgeIcon != null)
	//		setsTree.setCellRenderer(new SetIconRenderer(nodeIcon, edgeIcon));
		
		add(selectNodes);
		add(selectEdges);
		add(createSet);
		add(newSetFromAttribute);
		add(scrollPane);
		add(union);
		add(intersection);
		add(difference);
	}
	
	private Object getService(Class<?> serviceClass) {
		return bundleContext.getService(bundleContext.getServiceReference(serviceClass.getName()));
    }
	
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
	
	public synchronized void setCreated(SetChangedEvent event) {
		DefaultMutableTreeNode thisSet = new DefaultMutableTreeNode(event.getSetName());
		CyNetwork cyNetwork = mySets.getCyNetwork(event.getSetName());
		CyTable nodeTable = cyNetwork.getDefaultNodeTable();
		CyTable edgeTable = cyNetwork.getDefaultEdgeTable();
		Iterator<? extends CyIdentifiable> iterator = (Iterator<? extends CyIdentifiable>) mySets.getSet(event.getSetName()).getElements();
		while (iterator.hasNext()) {
			CyIdentifiable cyId = iterator.next();
			String cyIdName = "???";
			if (nodeTable.rowExists(cyId.getSUID()))
				cyIdName = nodeTable.getRow(cyId.getSUID()).get("name", String.class);
			if (edgeTable.rowExists(cyId.getSUID()))
				cyIdName = edgeTable.getRow(cyId.getSUID()).get("name", String.class);
			thisSet.add(new DefaultMutableTreeNode(new StringHolder(cyIdName)));
		}
		treeModel.insertNodeInto(thisSet, sets, sets.getChildCount());
	//	sets.add(thisSet);
		CyTable networkTable = cyNetwork.getDefaultNetworkTable();
		if (networkTable.getColumn(tablePrefix + event.getSetName()) == null) {
			networkTable.createListColumn(tablePrefix + event.getSetName(), Long.class, false);
			ArrayList<Long> suidSet = new ArrayList<Long>();
			iterator = (Iterator<? extends CyIdentifiable>) mySets.getSet(event.getSetName()).getElements();
			while (iterator.hasNext())
				suidSet.add(iterator.next().getSUID());
			networkTable.getRow(cyNetwork.getSUID()).set(tablePrefix + event.getSetName(), suidSet);
		}
	}

	public void setRemoved(SetChangedEvent event) {
		
	}

	public void handleEvent(SessionLoadedEvent event) {
		mySets.reset();
		while (sets.getChildCount() > 0) {
			treeModel.removeNodeFromParent((MutableTreeNode) sets.getLastChild());
		}
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
					taskManager.execute(createSetTaskFactory.createTaskIterator(loadedSetName, cyNetwork, cyNodes, cyEdges));
				}
			}
		}
	}
	
	private class SetIconRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = -4782376042373670468L;
		private Icon nodeIcon, edgeIcon;
		
		public SetIconRenderer(Icon icon1, Icon icon2) {
			nodeIcon = icon1;
			edgeIcon = icon2;
		}
		
		public Component getTreeCellRendererComponent(
				JTree tree,
				Object value,
				boolean sel,
				boolean expanded,
				boolean leaf,
				int row,
				boolean hasFocus) {
			super.getTreeCellRendererComponent(
					tree, value, sel,
					expanded, leaf, row,
					hasFocus);
			CyIdType type = getCyIdType(value);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			if (node.getParent() != null) {
				if (type == CyIdType.NODE) setIcon(nodeIcon);
				else if (type == CyIdType.EDGE) setIcon(edgeIcon);
				else setIcon(nodeIcon);
			}
			return this;
		}
		
		private CyIdType getCyIdType(Object o) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
			StringHolder nodeName = (StringHolder) node.getUserObject();
			return mySets.getType(nodeName.s);
		}
	}
	
	private class StringHolder {
		public String s;
		public StringHolder(String string) {
			s = string;
		}
		public String toString(){
			return s;
		}
	}
}
