package edu.ucsf.rbvi.setsApp.internal.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
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
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;

import edu.ucsf.rbvi.setsApp.internal.events.SetChangedEvent;
import edu.ucsf.rbvi.setsApp.internal.events.SetChangedListener;
import edu.ucsf.rbvi.setsApp.internal.model.SetOperations;
import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;
import edu.ucsf.rbvi.setsApp.internal.tasks.CopyCyIdTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.CopySetTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.CreateSetFromAttributeTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.CreateSetFromFileTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.CreateEdgeSetTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.CreateNodeSetTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.MoveCyIdTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.RemoveSetTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.RenameSetTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.SetOperationsTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.WriteSetToFileTask2;

/**
 * The user interface of setsApp. Consists of four sub-panels, "New Sets" which creates sets, a
 * JTree that displays all the sets in SetsManager, "Sets Operations" that allows user to perform
 * set operations and "Import/Export Sets to File" to allow users to import/export sets to file.
 * @author Allan Wu
 *
 */
public class SetsPane extends JPanel implements CytoPanelComponent, SetChangedListener {
	private JButton importSet, createSet, newSetFromAttribute, union, intersection, difference, exportSet;
	private JPanel modePanel, createSetPanel, filePanel, setOpPanel;
	private JTree setsTree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode sets;
	private JScrollPane scrollPane;
	private JPopupMenu selectSetCreation;
	private JMenuItem setsFNodes, setsFEdges, setsFNodeA, setsFEdgeA;
	private BundleContext bundleContext;
	private SetsManager mySets;
	private CyNetworkManager networkManager;
	private CyNetworkViewManager networkViewManager;
	private TaskManager taskManager;
	private HashMap<String, DefaultMutableTreeNode> setsNode;
	private HashMap<String, HashMap<Long, DefaultMutableTreeNode>> cyIdNode;
	private JFileChooser chooseImport;
	private String set1, set2;
	
	/**
	 * Constructor for SetsPane
	 * @param bc BundleContext of this instance of Cytoscape
	 * @param thisSet SetsManager instance for this instance of Cytoscape
	 */
	public SetsPane(BundleContext bc, SetsManager thisSet) {
		bundleContext = bc;
		mySets = thisSet;
		mySets.addSetChangedListener(this);
		networkManager = (CyNetworkManager) getService(CyNetworkManager.class);
		networkViewManager = (CyNetworkViewManager) getService(CyNetworkViewManager.class);
		taskManager = (TaskManager) getService(TaskManager.class);
		chooseImport = new JFileChooser();
		
		setPreferredSize(new Dimension(500,600));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// Button for importing sets from file
		importSet = new JButton("Import Set From File");
		importSet.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
		importSet.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				taskManager.execute(new TaskIterator(new CreateSetFromFileTask(mySets,networkManager)));
			}
		});
		// Button for set union
		union = new JButton("Union");
		union.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
		union.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				taskManager.execute(new TaskIterator(new SetOperationsTask(mySets,set1,set2,SetOperations.UNION)));
			}
		});
		union.setEnabled(false);
		// Button for set intersection
		intersection = new JButton("Intersection");
		intersection.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
		intersection.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				taskManager.execute(new TaskIterator(new SetOperationsTask(mySets,set1,set2,SetOperations.INTERSECT)));
			}
		});
		intersection.setEnabled(false);
		// Button for set difference
		difference = new JButton("Difference");
		difference.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
		difference.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				taskManager.execute(new TaskIterator(new SetOperationsTask(mySets,set2,set1,SetOperations.DIFFERENCE)));
			}
		});
		difference.setEnabled(false);
		// Button for exporting set to file
		exportSet = new JButton("Export Set to File");
		exportSet.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
		exportSet.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				taskManager.execute(new TaskIterator(new WriteSetToFileTask2(mySets,set1)));
			}
		});
		exportSet.setEnabled(false);
		
		// Create sets tree inside a scroll pane
		sets = new DefaultMutableTreeNode("Sets"/* new NodeInfo("Sets","Sets") */);
		setsTree = new JTree(sets);
		setsTree.setRootVisible(false);
		setsTree.setShowsRootHandles(true);
		setsTree.addMouseListener(new SetsMouseAdapter(mySets, this, taskManager));
		treeModel = (DefaultTreeModel) setsTree.getModel();
		setsTree.setCellRenderer(new SetIconRenderer());
		scrollPane = new JScrollPane(setsTree);
		setsNode = new HashMap<String, DefaultMutableTreeNode>();
		cyIdNode = new HashMap<String, HashMap<Long, DefaultMutableTreeNode>>();
		
		final int BS = 8;
		modePanel = new JPanel(new BorderLayout(BS, BS));
		modePanel.setBorder(BorderFactory.createEmptyBorder(BS, BS, BS, BS));
		
		// Create "New Sets" panel
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, BS, 0));
		topPanel.setBorder(BorderFactory.createTitledBorder("New Sets"));
		final String noneSelected = "",
				selectNodes = "selected nodes",
				selectEdges = "selected edges",
				attrNodes = "node attributes",
				attrEdges = "edge attributes";
		final String [] selectOptions = {noneSelected, selectNodes, selectEdges, attrNodes, attrEdges};
		final PartialDisableComboBox createSetsFromSelected = new PartialDisableComboBox();
		createSetsFromSelected.addItem(selectOptions[0], false);
		createSetsFromSelected.addItem(selectOptions[1], false);
		createSetsFromSelected.addItem(selectOptions[2], false);
		createSetsFromSelected.addItem(selectOptions[3], false);
		createSetsFromSelected.addItem(selectOptions[4], false);
		createSetsFromSelected.setSelectedIndex(0);
		createSetsFromSelected.addPopupMenuListener(new PopupMenuListener() {
			
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				CyApplicationManager appManager = (CyApplicationManager) getService(CyApplicationManager.class);
				CyNetwork curNetwork = appManager.getCurrentNetwork();
				createSetsFromSelected.setItemEnabled(0, true);
				if (curNetwork != null) {
					List<CyNode> nodes = CyTableUtil.getNodesInState(curNetwork, CyNetwork.SELECTED, true);
					List<CyEdge> edges = CyTableUtil.getEdgesInState(curNetwork, CyNetwork.SELECTED, true);
					if (!nodes.isEmpty())
						createSetsFromSelected.setItemEnabled(1, true);
					else
						createSetsFromSelected.setItemEnabled(1, false);
					if (!edges.isEmpty())
						createSetsFromSelected.setItemEnabled(2, true);
					else
						createSetsFromSelected.setItemEnabled(2, false);
					CyTable nodesTable = curNetwork.getDefaultNodeTable(),
							edgesTable = curNetwork.getDefaultEdgeTable();
					if (!nodesTable.getColumns().isEmpty())
						createSetsFromSelected.setItemEnabled(3, true);
					if (!edgesTable.getColumns().isEmpty())
						createSetsFromSelected.setItemEnabled(4, true);
				}
				else {
					for (int i = 1; i < selectOptions.length; i++)
						createSetsFromSelected.setItemEnabled(i, false);
				}
			}
			
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// TODO Auto-generated method stub
			}
			
			public void popupMenuCanceled(PopupMenuEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		createSetsFromSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox selectedStuff = (JComboBox) e.getSource();
				String selectedType = (String) selectedStuff.getSelectedItem();
				selectedStuff.setPopupVisible(false);
				CyApplicationManager appManager = (CyApplicationManager) getService(CyApplicationManager.class);
				if (selectedType.equals(selectNodes))
					taskManager.execute(new TaskIterator(new CreateNodeSetTask(mySets, appManager.getCurrentNetwork())));
				if (selectedType.equals(selectEdges))
					taskManager.execute(new TaskIterator(new CreateEdgeSetTask(mySets, appManager.getCurrentNetwork())));
				if (selectedType.equals(attrNodes))
					taskManager.execute(new TaskIterator(new CreateSetFromAttributeTask(mySets, appManager.getCurrentNetwork(), CyNode.class)));
				if (selectedType.equals(attrEdges))
					taskManager.execute(new TaskIterator(new CreateSetFromAttributeTask(mySets, appManager.getCurrentNetwork(), CyEdge.class)));

				selectedStuff.setSelectedIndex(0);
			}
		});
		
		topPanel.add(new Label("Create set from:"));
		topPanel.add(createSetsFromSelected);
	//	topPanel.add(createSet);
		
		JPanel btmPanel = new JPanel(new BorderLayout(BS, BS));
		// Create "Set Operations" panel
		JPanel buttons1 = new JPanel(new FlowLayout(FlowLayout.CENTER, BS, 0));
		buttons1.setBorder(BorderFactory.createTitledBorder("Set Operations"));
		adjustWidth(new JButton[] {union, intersection, difference});
		buttons1.add(union);
		buttons1.add(intersection);
		buttons1.add(difference);
		// Create "Import/Export Sets to File"
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

	/**
	 * Enable buttons in "Set Operations" Panel
	 * @param b "true" to enable buttons, "false" to disable buttons
	 */
	public void enableOperationsButton(boolean b) {
		intersection.setEnabled(b);
		union.setEnabled(b);
		difference.setEnabled(b);
	}

	/**
	 * Enable "export" button
	 * @param b "true" to enable buttons, "false" to disable buttons
	 */
	public void enableExportButton(boolean b) {
		exportSet.setEnabled(b);
	}

	/**
	 * Set "set1" variable, usually used internally. Used by the "Set Operations" button to
	 * determine what set to use.
	 * @param set name of a set
	 */
	public void setFirstSet(String set) {
		set1 = set;
	}

	/**
	 * Set "set2" variable, usually used internally. Used by the "Set Operations" button to
	 * determine what set to use.
	 * @param set name of a set
	 */
	public void setSecondSet(String set) {
		set2 = set;
	}

	public SetsManager getSetsManager() {return mySets;}
	
	public Object getService(Class<?> serviceClass) {
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
	
	/**
 	 * {@interitDoc}
 	 */
	public synchronized void setCreated(SetChangedEvent event) {
		DefaultMutableTreeNode thisSet = new DefaultMutableTreeNode(event.getSetName());
		HashMap<Long, DefaultMutableTreeNode> setNodesMap = new HashMap<Long, DefaultMutableTreeNode>();

		// Move this functionality to Set??
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
		thisSet.setUserObject(new NodeInfo(event.getSetName() + " (" + thisSet.getChildCount() + ")", event.getSetName()));
		treeModel.insertNodeInto(thisSet, sets, sets.getChildCount());
		setsTree.expandPath(new TreePath(sets.getPath()));
	}

	/**
 	 * {@interitDoc}
 	 */
	public void setRemoved(SetChangedEvent event) {
		treeModel.removeNodeFromParent(setsNode.get(event.getSetName()));
		setsNode.remove(event.getSetName());
		cyIdNode.remove(event.getSetName());
	}

	/**
 	 * {@interitDoc}
 	 */
	public void setsCleared(SetChangedEvent event) {
		while (sets.getChildCount() > 0) {
			treeModel.removeNodeFromParent((MutableTreeNode) sets.getLastChild());
		}
		exportSet.setEnabled(false);
	}

	/**
 	 * {@interitDoc}
 	 */
	public void setChanged(SetChangedEvent event) {
		CyNetwork cyNetwork = event.getCyNetwork();
		List<CyIdentifiable> added = (List<CyIdentifiable>) event.getCyIdsAdded(),
				removed = (List<CyIdentifiable>) event.getCyIdsRemoved();
		DefaultMutableTreeNode setNode = setsNode.get(event.getSetName());
		if (added != null) {
			for (CyIdentifiable node: added) {
				String cyIdName = mySets.getElementName(cyNetwork, event.getSetName(), node);
				DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(new NodeInfo(cyIdName, node));
				setNode.add(newTreeNode);
			}
		} else if (removed != null) {
			for (CyIdentifiable edge: removed) {
				treeModel.removeNodeFromParent(cyIdNode.get(event.getSetName()).get(edge.getSUID()));
			}
		}
		setNode.setUserObject(new NodeInfo(event.getSetName() + " (" + setNode.getChildCount() + ")", event.getSetName()));
		treeModel.nodeChanged(setNode);
	}


	/****************************************************
 	 * Private methods
 	 ***************************************************/
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

	
	private class SetIconRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = -4782376042373670468L;
		private boolean iconsOk = false;
		private Icon setsIcon = null, nodeSetIcon = null, edgeSetIcon = null, nodeIcon = null, edgeIcon = null;
		
		public SetIconRenderer() {
			URL myUrl = SetsPane.class.getResource("/images/Node2.png");
			if (myUrl != null) nodeSetIcon = new ImageIcon(myUrl);
			myUrl = SetsPane.class.getResource("/images/Edge2.png");
			if (myUrl != null) edgeSetIcon = new ImageIcon(myUrl);
			myUrl = SetsPane.class.getResource("/images/Node2.png");
			if (myUrl != null) setsIcon = new ImageIcon(myUrl);
			myUrl = SetsPane.class.getResource("/images/Edge2.png");
			if (myUrl != null) edgeIcon = new ImageIcon(myUrl);
			myUrl = SetsPane.class.getResource("/images/Node2.png");
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
			Class<? extends CyIdentifiable> type = getType(value);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			if (iconsOk) {
				if (node.isRoot()) {setIcon(setsIcon);}
				else if (((NodeInfo) node.getUserObject()).cyId != null) {
					DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
					if (parent != null) {
						Class<? extends CyIdentifiable> parentType = getType(parent);
						if (parentType == CyNode.class) setIcon(nodeIcon);
						else if (parentType == CyEdge.class) setIcon(edgeIcon);
					}
				}
				else {
					if (type == CyNode.class) setIcon(nodeSetIcon);
					else if (type == CyEdge.class) setIcon(edgeSetIcon);
				}
			}
			setPreferredSize(new Dimension(600, getPreferredSize().height));
			validate();
			repaint();
			return this;
		}
		
		private Class<? extends CyIdentifiable> getType(Object o) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
			String nodeName = null;
			if (node.isRoot());
			else if (((NodeInfo) node.getUserObject()).cyId != null)
				nodeName = ((NodeInfo) node.getUserObject()).label;
			else
				nodeName = ((NodeInfo) node.getUserObject()).setName;
			if (nodeName != null && mySets.isInSetsManager(nodeName))
				return mySets.getType(nodeName);
			else
				return null;
		}
	}
}
