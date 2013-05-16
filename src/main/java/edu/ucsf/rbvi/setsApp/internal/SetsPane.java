package edu.ucsf.rbvi.setsApp.internal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.cytoscape.application.CyApplicationManager;
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
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;

import edu.ucsf.rbvi.setsApp.internal.events.SetChangedEvent;
import edu.ucsf.rbvi.setsApp.internal.events.SetChangedListener;
import edu.ucsf.rbvi.setsApp.internal.tasks.CopyCyIdTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.CreateSetTaskFactory;
import edu.ucsf.rbvi.setsApp.internal.tasks.MoveCyIdTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.RenameSetTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.SetsManager;
import edu.ucsf.rbvi.setsApp.internal.tasks.WriteSetToFileTask;

public class SetsPane extends JPanel implements CytoPanelComponent, SetChangedListener, SessionLoadedListener {
	private JButton importSet, createSet, newSetFromAttribute, union, intersection, difference, exportSet;
	private JPanel modePanel, createSetPanel, filePanel, setOpPanel;
	private ButtonGroup select;
	private JRadioButton selectNodes, selectEdges;
	private JTree setsTree, nodesTree, edgesTree;
	private DefaultTreeModel treeModel, nodesTreeModel, edgesTreeModel;
	private DefaultMutableTreeNode sets, nodesSet, edgesSet;
	private JScrollPane scrollPane, nodesPane, edgesPane;
	private BundleContext bundleContext;
	private SetsManager mySets;
	private CyNetworkManager networkManager;
	private CyNetworkViewManager networkViewManager;
	private CreateSetTaskFactory createSetTaskFactory;
	private TaskManager taskManager;
	private HashMap<String, DefaultMutableTreeNode> setsNode;
	private HashMap<String, HashMap<Long, DefaultMutableTreeNode>> cyIdNode;
	private JFileChooser chooseImport;
	private String set1, set2;
	public static final String tablePrefix = "setsApp:";
	
	public SetsPane(BundleContext bc, SetsManager thisSet) {
		bundleContext = bc;
		mySets = thisSet;
		mySets.addSetChangedListener(this);
		createSetTaskFactory = new CreateSetTaskFactory(mySets);
		networkManager = (CyNetworkManager) getService(CyNetworkManager.class);
		networkViewManager = (CyNetworkViewManager) getService(CyNetworkViewManager.class);
		taskManager = (TaskManager) getService(TaskManager.class);
		chooseImport = new JFileChooser();
		
		setPreferredSize(new Dimension(500,600));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		select = new ButtonGroup();
		selectNodes = new JRadioButton("Nodes");
		selectNodes.setSelected(true);
		selectEdges = new JRadioButton("Edges");
		select.add(selectNodes);
		select.add(selectEdges);
		importSet = new JButton("Import Set From File");
		importSet.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
		importSet.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (JFileChooser.APPROVE_OPTION == chooseImport.showOpenDialog(SetsPane.this)){
					BufferedReader reader;
					try {
						reader = new BufferedReader(new FileReader(chooseImport.getSelectedFile()));
						if (selectNodes.isSelected())
							taskManager.execute(createSetTaskFactory.createTaskIterator(networkManager, reader, CyIdType.NODE));
						if (selectEdges.isSelected())
							taskManager.execute(createSetTaskFactory.createTaskIterator(networkManager, reader, CyIdType.EDGE));
					} catch (FileNotFoundException e1) {
						System.err.println("Couldn't open file: " + chooseImport.getSelectedFile().getName());
						e1.printStackTrace();
					}
				}
			}
		});
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
		union.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
		union.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				mySets.union(set1 + " union " + set2, set1, set2);
			//	taskManager.execute(createSetTaskFactory.createTaskIterator(selectNodes.isSelected() ? CyIdType.NODE : CyIdType.EDGE, SetOperations.UNION));
			}
		});
		union.setEnabled(false);
		intersection = new JButton("Intersection");
		intersection.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
		intersection.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				mySets.intersection(set1 + " intersection " + set2, set1, set2);
			//	taskManager.execute(createSetTaskFactory.createTaskIterator(selectNodes.isSelected() ? CyIdType.NODE : CyIdType.EDGE, SetOperations.INTERSECT));
			}
		});
		intersection.setEnabled(false);
		difference = new JButton("Set Difference");
		difference.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
		difference.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				mySets.difference(set1 + " difference " + set2, set2, set1);
			//	taskManager.execute(createSetTaskFactory.createTaskIterator(selectNodes.isSelected() ? CyIdType.NODE : CyIdType.EDGE, SetOperations.DIFFERENCE));
			}
		});
		difference.setEnabled(false);
		exportSet = new JButton("Export Set to File");
		exportSet.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
		exportSet.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (JFileChooser.APPROVE_OPTION == chooseImport.showSaveDialog(SetsPane.this)) {
					File f = chooseImport.getSelectedFile();
					if (!f.exists()) {
						try {
							f.createNewFile();
							if (selectNodes.isSelected())
								taskManager.execute(new TaskIterator(new WriteSetToFileTask(mySets, networkManager, new BufferedWriter(new FileWriter(f.getAbsolutePath())), CyIdType.NODE)));
							if (selectEdges.isSelected())
								taskManager.execute(new TaskIterator(new WriteSetToFileTask(mySets, networkManager, new BufferedWriter(new FileWriter(f.getAbsolutePath())), CyIdType.EDGE)));
						} catch (IOException e1) {
							System.err.println("Unable to create file: " + f.getName());
							e1.printStackTrace();
						}
					}
					else
						System.out.println("File already exist, choose another file name/directory.");
				}
			}
		});
		
		sets = new DefaultMutableTreeNode("Sets");
		setsTree = new JTree(sets);
		setsTree.addMouseListener(new MouseAdapter() {
			private void popupEvent(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				JTree tree = (JTree)e.getSource();
				TreePath path = tree.getPathForLocation(x, y);
				if (path == null)
					return;

				tree.setSelectionPath(path);
				path.getLastPathComponent();
				final DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();

				JPopupMenu popup = new JPopupMenu();
				if (node.isLeaf()) {
					final DefaultMutableTreeNode setNode = (DefaultMutableTreeNode) node.getParent();
					final CyIdentifiable selectecCyId = ((NodeInfo) node.getUserObject()).cyId;
					final String thisSetName = setNode.getUserObject().toString();
					
					JMenuItem select = new JMenuItem("Select");
					JMenuItem copy = new JMenuItem("Copy to...");
					JMenuItem move = new JMenuItem("Move to...");
					JMenuItem delete = new JMenuItem("Remove from Set");
					select.addActionListener(new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							displaySelectedCyIds(thisSetName, selectecCyId.getSUID());
						}
					});
					copy.addActionListener(new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							taskManager.execute(new TaskIterator(new CopyCyIdTask(mySets, selectecCyId)));
						}
					});
					move.addActionListener(new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							taskManager.execute(new TaskIterator(new MoveCyIdTask(mySets, thisSetName, selectecCyId)));
						}
					});
					delete.addActionListener(new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							mySets.removeFromSet(thisSetName, selectecCyId);
						}
					});
					popup.add(select);
					popup.add(copy);
					popup.add(move);
					popup.add(delete);
				}
				else if (!node.isRoot()) {
					JMenuItem select = new JMenuItem("Select");
					JMenuItem delete = new JMenuItem("Remove Set");
					JMenuItem rename = new JMenuItem("Rename");

					select.addActionListener(new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							String setName = node.getUserObject().toString();
							displaySelectedCyIds(setName, null);
						}
					});
					rename.addActionListener(new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							taskManager.execute(new TaskIterator(new RenameSetTask(mySets, (String) node.getUserObject())));
						}
					});
					delete.addActionListener(new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							mySets.removeSet(node.getUserObject().toString());
						}
					});
					popup.add(select);
					popup.add(delete);
					popup.add(rename);
				}
				popup.show(tree, x, y);
			}
			public void enableOperationsButton(boolean b) {
				intersection.setEnabled(b);
				union.setEnabled(b);
				difference.setEnabled(b);
			}
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) popupEvent(e);
				else {
					if (getSetsSelectedFromTree(e))
						enableOperationsButton(true);
					else enableOperationsButton(false);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) popupEvent(e);
				else {
					if (getSetsSelectedFromTree(e))
						enableOperationsButton(true);
					else enableOperationsButton(false);
				}
			}
			private boolean getSetsSelectedFromTree(MouseEvent e) {
				JTree tree = (JTree) e.getSource();
				TreePath path[] = tree.getSelectionPaths();
				if (path != null && path.length == 1) {
					DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) path[0].getLastPathComponent();
					if (! node1.isLeaf() && ! node1.isRoot())
						set1 = (String) node1.getUserObject();
					return false;
				}
				else if (path != null && path.length == 2) {
					DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) path[0].getLastPathComponent();
					DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) path[1].getLastPathComponent();
					if (! node1.isLeaf() && ! node2.isLeaf() && ! node1.isRoot() && ! node2.isRoot()) {
						if (set1.equals((String) node1.getUserObject())) {
							set1 = (String) node1.getUserObject();
							set2 = (String) node2.getUserObject();
						}
						else {
							set1 = (String) node2.getUserObject();
							set2 = (String) node1.getUserObject();
						}
						if (mySets.getType(set1) == mySets.getType(set2))
							return true;
						else return false;
					}
					else return false;
				}
				else return false;
			}
			private void displaySelectedCyIds(String setName, Long cyId) {
				CyNetwork curNetwork = mySets.getCyNetwork(setName);
				CyTable curTable = null;
				if (mySets.getType(setName) == CyIdType.NODE)
					curTable = curNetwork.getDefaultNodeTable();
				if (mySets.getType(setName) == CyIdType.EDGE)
					curTable = curNetwork.getDefaultEdgeTable();
				if (curTable != null)
					for (Long suid: curTable.getPrimaryKey().getValues(Long.class))
						curTable.getRow(suid).set(CyNetwork.SELECTED, 
								cyId == null ? mySets.isInSet(setName, suid): suid == cyId);
				CyNetworkViewManager nvm = (CyNetworkViewManager) getService(CyNetworkViewManager.class);
				for (CyNetworkView networkView: nvm.getNetworkViewSet())
					if (networkView.getModel() == curNetwork)
						networkView.updateView();
			}
		});
		treeModel = (DefaultTreeModel) setsTree.getModel();
		setsTree.setCellRenderer(new SetIconRenderer());
		scrollPane = new JScrollPane(setsTree);
		setsNode = new HashMap<String, DefaultMutableTreeNode>();
		cyIdNode = new HashMap<String, HashMap<Long, DefaultMutableTreeNode>>();
		
		nodesSet = new DefaultMutableTreeNode("Nodes");
		edgesSet = new DefaultMutableTreeNode("Edges");
		nodesTree = new JTree(nodesSet);
		nodesTree.setCellRenderer(new SetIconRenderer());
		edgesTree = new JTree(edgesSet);
		edgesTree.setCellRenderer(new SetIconRenderer());
		nodesTreeModel = (DefaultTreeModel) nodesTree.getModel();
		edgesTreeModel = (DefaultTreeModel) edgesTree.getModel();
		nodesPane = new JScrollPane(nodesTree);
		edgesPane = new JScrollPane(edgesTree);

//		modePanel = new JPanel();
//		modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.X_AXIS));
//		modePanel.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));
//		modePanel.add(selectNodes);
//		modePanel.add(selectEdges);
//		
//		add(selectNodes);
//		add(selectEdges);
//		add(importSet);
//		add(exportSet);
//		add(newSetFromAttribute);
//		add(scrollPane);
//		add(union);
//		add(intersection);
//		add(difference);

		final int BS = 8;
		modePanel = new JPanel(new BorderLayout(BS, BS));
		//modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.X_AXIS));
		modePanel.setBorder(BorderFactory.createEmptyBorder(BS, BS, BS, BS));
		
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, BS, 0));
		topPanel.setBorder(BorderFactory.createTitledBorder("Create Set from Selected Nodes/Edges"));
		final String noneSelected = "none", 
				selectNodes = "Create node set", 
				selectEdges = "Create edge set",
				attrNodes = "Create node set from attributes",
				attrEdges = "Create node set from edges";
		JComboBox createSetsFromSelected = new JComboBox(new String[] {noneSelected, selectNodes, selectEdges, attrNodes, attrEdges});
		createSetsFromSelected.setSelectedIndex(0);
		createSetsFromSelected.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				JComboBox selectedStuff = (JComboBox) e.getSource();
				String selectedType = (String) selectedStuff.getSelectedItem();
				selectedStuff.setPopupVisible(false);
				CyApplicationManager appManager = (CyApplicationManager) getService(CyApplicationManager.class);
				if (selectedType.equals(selectNodes))
					taskManager.execute(createSetTaskFactory.createTaskIterator(null, networkViewManager, CyIdType.NODE));
				if (selectedType.equals(selectEdges))
					taskManager.execute(createSetTaskFactory.createTaskIterator(null, networkViewManager, CyIdType.EDGE));
				if (selectedType.equals(attrNodes))
					taskManager.execute(createSetTaskFactory.createTaskIterator(appManager.getCurrentNetwork(), CyIdType.NODE));
				if (selectedType.equals(attrEdges))
					taskManager.execute(createSetTaskFactory.createTaskIterator(appManager.getCurrentNetwork(), CyIdType.EDGE));
				if (selectedType.equals(attrNodes))
					taskManager.execute(createSetTaskFactory.createTaskIterator(appManager.getCurrentNetwork(), CyIdType.NODE));
				if (selectedType.equals(attrEdges))
					taskManager.execute(createSetTaskFactory.createTaskIterator(appManager.getCurrentNetwork(), CyIdType.EDGE));
			}
		});
		topPanel.add(new Label("Select:"));
		topPanel.add(createSetsFromSelected);
	//	topPanel.add(createSet);
		
		JPanel btmPanel = new JPanel(new BorderLayout(BS, BS));

		JPanel buttons1 = new JPanel(new FlowLayout(FlowLayout.CENTER, BS, 0));
		buttons1.setBorder(BorderFactory.createTitledBorder("Set Operations"));
		adjustWidth(new JButton[] {union, intersection, difference});
		buttons1.add(union);
		buttons1.add(intersection);
		buttons1.add(difference);

		JPanel buttons2 = new JPanel(new FlowLayout(FlowLayout.CENTER, BS, 0));
		buttons2.setBorder(BorderFactory.createTitledBorder("Import/Export Sets to File"));
		adjustWidth(new JButton[] {importSet, exportSet});
		buttons2.add(importSet);
		buttons2.add(exportSet);

		btmPanel.add(buttons1, BorderLayout.NORTH);
		btmPanel.add(buttons2, BorderLayout.SOUTH);

		modePanel.add(topPanel, BorderLayout.NORTH);
		modePanel.add(scrollPane, BorderLayout.CENTER);
		modePanel.add(btmPanel, BorderLayout.SOUTH);
		add(modePanel);

	}

	private void adjustWidth(JComponent[] components) {
		Dimension dim = components[0].getPreferredSize();
		int width = dim.width;
		for (int i = 1; i < components.length; i++) {
			dim = components[i].getPreferredSize();
			if (dim.width > width) {
				width = dim.width;
			}
		}
		for (final JComponent cbx : components) {
			dim = cbx.getPreferredSize();
			dim.width = width;
			cbx.setPreferredSize(dim);
		}
	}

	
	
	public SetsManager getSetsManager() {return mySets;}
	
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
	
	private void exportToAttribute(String name) {
		CyTable table = null;
		CyNetwork network = mySets.getCyNetwork(name);
		String colName = tablePrefix + name;
		if (mySets.getType(name) == CyIdType.NODE)
			table = network.getTable(CyNode.class, CyNetwork.HIDDEN_ATTRS);
		if (mySets.getType(name) == CyIdType.EDGE)
			table = network.getTable(CyEdge.class, CyNetwork.HIDDEN_ATTRS);
		if (table != null && table.getColumn(colName) == null) {
			table.createColumn(colName, Boolean.class, false);
			for (CyIdentifiable cyId: mySets.getSet(name).getElements())
				table.getRow(cyId.getSUID()).set(colName, true);
		}
	}
	
	private void importFromAttribute(String loadedSetName) {
		CyNetwork cyNetwork = mySets.getCyNetwork(loadedSetName);
		List<CyNode> cyNodes = null;
		List<CyEdge> cyEdges = null;
		cyNodes = CyTableUtil.getNodesInState(cyNetwork, loadedSetName, true);
		cyEdges = CyTableUtil.getEdgesInState(cyNetwork, loadedSetName, true);
		if (cyNodes != null && cyNodes.size() == 0) cyNodes = null;
		if (cyEdges != null && cyEdges.size() == 0) cyEdges = null;
		mySets.createSet(loadedSetName, cyNetwork, cyNodes, cyEdges);
	}
	
	public synchronized void setCreated(SetChangedEvent event) {
		DefaultMutableTreeNode thisSet = new DefaultMutableTreeNode(event.getSetName());
		HashMap<Long, DefaultMutableTreeNode> setNodesMap = new HashMap<Long, DefaultMutableTreeNode>();
		CyNetwork cyNetwork = mySets.getCyNetwork(event.getSetName());
		CyTable nodeTable = cyNetwork.getDefaultNodeTable();
		CyTable edgeTable = cyNetwork.getDefaultEdgeTable();
		Collection<? extends CyIdentifiable> cyIds = mySets.getSet(event.getSetName()).getElements();
		for (CyIdentifiable cyId: cyIds) {
			String cyIdName = "???";
			if (nodeTable.rowExists(cyId.getSUID()))
				cyIdName = nodeTable.getRow(cyId.getSUID()).get(CyNetwork.NAME, String.class);
			if (edgeTable.rowExists(cyId.getSUID()))
				cyIdName = edgeTable.getRow(cyId.getSUID()).get(CyNetwork.NAME, String.class);
			DefaultMutableTreeNode thisNode = new DefaultMutableTreeNode(new NodeInfo(cyIdName, cyId));
			thisSet.add(thisNode);
			setNodesMap.put(cyId.getSUID(), thisNode);
		}
		setsNode.put(event.getSetName(), thisSet);
		cyIdNode.put(event.getSetName(), setNodesMap);
		treeModel.insertNodeInto(thisSet, sets, sets.getChildCount());
		exportToAttribute(event.getSetName());
	}

	public void setRemoved(SetChangedEvent event) {
	/*	CyNetworkManager manager = (CyNetworkManager) getService(CyNetworkManager.class);
		for (CyNetwork cyNetwork: manager.getNetworkSet()) {
			CyTable networkTable = cyNetwork.getDefaultNetworkTable();
			if (networkTable != null && networkTable.getColumn(tablePrefix + event.getSetName()) != null)
				networkTable.deleteColumn(tablePrefix + event.getSetName());
		} */
		String setTableName = tablePrefix + event.getSetName();
	//	CyNetwork cyNetwork = mySets.getCyNetwork(event.getSetName());
		CyNetwork cyNetwork = event.getCyNetwork();
		CyTable nodeTable = cyNetwork.getTable(CyNode.class, CyNetwork.HIDDEN_ATTRS);
		CyTable edgeTable = cyNetwork.getTable(CyEdge.class, CyNetwork.HIDDEN_ATTRS);
		if (nodeTable.getColumn(setTableName) != null)
			nodeTable.deleteColumn(setTableName);
		if (edgeTable.getColumn(setTableName) != null)
			edgeTable.deleteColumn(setTableName);
		treeModel.removeNodeFromParent(setsNode.get(event.getSetName()));
		setsNode.remove(event.getSetName());
		cyIdNode.remove(event.getSetName());
	}

	public void handleEvent(SessionLoadedEvent event) {
		mySets.reset();
		while (sets.getChildCount() > 0) {
			treeModel.removeNodeFromParent((MutableTreeNode) sets.getLastChild());
		}
		CyNetworkManager nm = (CyNetworkManager) getService(CyNetworkManager.class);
		java.util.Set<CyNetwork> networks = nm.getNetworkSet();
		CyTable cyTable;
		Collection<CyColumn> cyColumns;
		for (CyNetwork cyNetwork: networks) {
			cyTable = cyNetwork.getTable(CyNode.class, CyNetwork.HIDDEN_ATTRS);
			cyColumns = cyTable.getColumns();
			for (CyColumn c: cyColumns) {
				String colName = c.getName();
				if (colName.length() >= 9 && colName.substring(0, 8).equals(tablePrefix)) {
					String loadedSetName = colName.substring(8);
					List<CyNode> cyNodes = new ArrayList<CyNode>();
					for (Long suid: cyTable.getPrimaryKey().getValues(Long.class))
						if (cyTable.getRow(suid).get(colName, Boolean.class))
							cyNodes.add(cyNetwork.getNode(suid));
					if (cyNodes != null && cyNodes.size() == 0) cyNodes = null;
					mySets.createSet(loadedSetName, cyNetwork, cyNodes, null);
				}
			}
			cyTable = cyNetwork.getTable(CyEdge.class, CyNetwork.HIDDEN_ATTRS);
			cyColumns = cyTable.getColumns();
			for (CyColumn c: cyColumns) {
				String colName = c.getName();
				if (colName.length() >= 9 && colName.substring(0, 8).equals(tablePrefix)) {
					String loadedSetName = colName.substring(8);
					List<CyEdge> cyEdges = new ArrayList<CyEdge>();
					for (Long suid: cyTable.getPrimaryKey().getValues(Long.class))
						if (cyTable.getRow(suid).get(colName, Boolean.class))
							cyEdges.add(cyNetwork.getEdge(suid));
					if (cyEdges != null && cyEdges.size() == 0) cyEdges = null;
					mySets.createSet(loadedSetName, cyNetwork, null, cyEdges);
				}
			}
		}
	}
	
	private class SetIconRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = -4782376042373670468L;
		private boolean iconsOk = false;
		private Icon setsIcon = null, nodeSetIcon = null, edgeSetIcon = null, nodeIcon = null, edgeIcon = null;
		
		public SetIconRenderer() {
			URL myUrl = SetsPane.class.getResource("/images/node_set.png");
			if (myUrl != null) nodeSetIcon = new ImageIcon(myUrl);
			myUrl = SetsPane.class.getResource("/images/edge_set.png");
			if (myUrl != null) edgeSetIcon = new ImageIcon(myUrl);
			myUrl = SetsPane.class.getResource("/images/sets.png");
			if (myUrl != null) setsIcon = new ImageIcon(myUrl);
			myUrl = SetsPane.class.getResource("/images/edge.png");
			if (myUrl != null) edgeIcon = new ImageIcon(myUrl);
			myUrl = SetsPane.class.getResource("/images/node.png");
			if (myUrl != null) nodeIcon = new ImageIcon(myUrl);
			if (nodeSetIcon != null && edgeSetIcon != null && setsIcon != null && edgeIcon != null && nodeIcon != null)
				iconsOk = true;
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
			if (iconsOk) {
				CyIdType type = getCyIdType(value);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
				if (node.isRoot()) {setIcon(setsIcon);}
				else if (leaf) {
					DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
					if (parent != null) {
						CyIdType parentType = getCyIdType(parent);
						if (parentType == CyIdType.NODE) setIcon(nodeIcon);
						else if (parentType == CyIdType.EDGE) setIcon(edgeIcon);
					}
				}
				else {
					if (type == CyIdType.NODE) setIcon(nodeSetIcon);
					else if (type == CyIdType.EDGE) setIcon(edgeSetIcon);
				}
			}
			return this;
		}
		
		private CyIdType getCyIdType(Object o) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
			String nodeName = node.getUserObject().toString();
			if (mySets.isInSetsManager(nodeName))
				return mySets.getType(nodeName);
			else
				return null;
		}
	}

	public void setChanged(SetChangedEvent event) {
		CyNetwork cyNetwork = mySets.getCyNetwork(event.getSetName());
		List<CyIdentifiable> added = (List<CyIdentifiable>) event.getCyIdsAdded(),
				removed = (List<CyIdentifiable>) event.getCyIdsRemoved();
		DefaultMutableTreeNode setNode = setsNode.get(event.getSetName());
		CyTable nodeTable = cyNetwork.getDefaultNodeTable(),
				edgeTable = cyNetwork.getDefaultEdgeTable(),
				nodeHiddenTable = cyNetwork.getTable(CyNode.class, CyNetwork.HIDDEN_ATTRS),
				edgeHiddenTable = cyNetwork.getTable(CyEdge.class, CyNetwork.HIDDEN_ATTRS);
		String setTableName = tablePrefix + event.getSetName();
		if (added != null)
			for (CyIdentifiable node: added) {
				String cyIdName = null;
				if (cyNetwork.getNode(node.getSUID()) != null) {
					cyIdName = nodeTable.getRow(node.getSUID()).get(CyNetwork.NAME, String.class);
					nodeHiddenTable.getRow(node.getSUID()).set(setTableName, true);
				}
				if (cyNetwork.getEdge(node.getSUID()) != null) {
					cyIdName = edgeTable.getRow(node.getSUID()).get(CyNetwork.NAME, String.class);
					edgeHiddenTable.getRow(node.getSUID()).set(setTableName, true);
				}
				DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(new NodeInfo(cyIdName, node));
				cyIdNode.get(event.getSetName()).put(node.getSUID(), newTreeNode);
				treeModel.insertNodeInto(newTreeNode, setNode, setNode.getChildCount());
			}
		if (removed != null)
			for (CyIdentifiable node: removed) {
				if (cyNetwork.getNode(node.getSUID()) != null)
					nodeHiddenTable.getRow(node.getSUID()).set(setTableName, false);
				if (cyNetwork.getEdge(node.getSUID()) != null)
					edgeHiddenTable.getRow(node.getSUID()).set(setTableName, false);
				treeModel.removeNodeFromParent(cyIdNode.get(event.getSetName()).get(node.getSUID()));
				cyIdNode.get(event.getSetName()).remove(node.getSUID());
			}
	/*	CyTable networkTable = cyNetwork.getDefaultNetworkTable();
		networkTable.deleteColumn(tablePrefix + event.getSetName());
		networkTable.createListColumn(tablePrefix + event.getSetName(), Long.class, false);
		ArrayList<Long> suidSet = new ArrayList<Long>();
		iterator = (Iterator<? extends CyIdentifiable>) mySets.getSet(event.getSetName()).getElements();
		while (iterator.hasNext())
			suidSet.add(iterator.next().getSUID());
		networkTable.getRow(cyNetwork.getSUID()).set(tablePrefix + event.getSetName(), suidSet); */
	}
	
	private class NodeInfo {
		public String label;
		public CyIdentifiable cyId;
		
		public NodeInfo(String name, CyIdentifiable s) {
			label = name;
			cyId = s;
		}
		public String toString() {
			return label;
		}
	}

	public void setRenamed(SetChangedEvent event) {
		setsNode.put(event.getSetName(), setsNode.get(event.getOldSetName()));
		setsNode.remove(event.getOldSetName());
		cyIdNode.put(event.getSetName(), cyIdNode.get(event.getOldSetName()));
		cyIdNode.remove(event.getOldSetName());
		setsNode.get(event.getSetName()).setUserObject(event.getSetName());
		
		CyNetwork cyNetwork = mySets.getCyNetwork(event.getSetName());
		CyTable networkTable = null;
		if (mySets.getType(event.getSetName()) == CyIdType.NODE)
			networkTable = cyNetwork.getTable(CyNode.class, CyNetwork.HIDDEN_ATTRS);
		if (mySets.getType(event.getSetName()) == CyIdType.EDGE)
			networkTable = cyNetwork.getTable(CyEdge.class, CyNetwork.HIDDEN_ATTRS);
		if (networkTable != null) {
			networkTable.deleteColumn(tablePrefix + event.getOldSetName());
			networkTable.createColumn(tablePrefix + event.getSetName(), Boolean.class, false);
		/*	ArrayList<Long> suidSet = new ArrayList<Long>();
			Iterator<? extends CyIdentifiable> iterator = (Iterator<? extends CyIdentifiable>) mySets.getSet(event.getSetName()).getElements();
			while (iterator.hasNext())
				suidSet.add(iterator.next().getSUID());
			networkTable.getRow(cyNetwork.getSUID()).set(tablePrefix + event.getSetName(), suidSet); */
			for (Long suid: networkTable.getPrimaryKey().getValues(Long.class))
				networkTable.getRow(suid).set(tablePrefix + event.getSetName(), mySets.isInSet(event.getSetName(), suid));
		}
	}
}
