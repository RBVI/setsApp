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

import javax.swing.AbstractAction;
import javax.swing.Action;
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
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.osgi.framework.BundleContext;

import edu.ucsf.rbvi.setsApp.internal.events.SetChangedEvent;
import edu.ucsf.rbvi.setsApp.internal.events.SetChangedListener;
import edu.ucsf.rbvi.setsApp.internal.model.Set;
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
import edu.ucsf.rbvi.setsApp.internal.tasks.PartitionNodesTask;
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
public class SetsPane extends JPanel implements CytoPanelComponent, SetChangedListener, SetCurrentNetworkListener {
	private JButton createSet, newSetFromAttribute, union, intersection, difference, partition, removeBtn;
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
	private List<String> setsList;
	
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
		// Button for set union
		ImageIcon unionIcon = new ImageIcon(getClass().getResource("/icons/union.png"));
		union = new JButton(unionIcon);
		union.setToolTipText("Union");
		union.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (setsList != null) {
					taskManager.execute(new TaskIterator(new SetOperationsTask(mySets,setsList,SetOperations.UNION)));
				} else
					taskManager.execute(new TaskIterator(new SetOperationsTask(mySets,set1,set2,SetOperations.UNION)));
			}
		});
		union.setEnabled(false);
		// Button for set intersection
		ImageIcon intersectIcon = new ImageIcon(getClass().getResource("/icons/intersect.png"));
		intersection = new JButton(intersectIcon);
		intersection.setToolTipText("Intersect");
		intersection.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (setsList != null)
					taskManager.execute(new TaskIterator(new SetOperationsTask(mySets,setsList,SetOperations.INTERSECT)));
				else
					taskManager.execute(new TaskIterator(new SetOperationsTask(mySets,set1,set2,SetOperations.INTERSECT)));
			}
		});
		intersection.setEnabled(false);
		// Button for set difference
		ImageIcon differenceIcon = new ImageIcon(getClass().getResource("/icons/difference.png"));
		difference = new JButton(differenceIcon);
		difference.setToolTipText("Difference");
		difference.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (setsList != null)
					taskManager.execute(new TaskIterator(new SetOperationsTask(mySets,setsList,SetOperations.DIFFERENCE)));
				else
					taskManager.execute(new TaskIterator(new SetOperationsTask(mySets,set2,set1,SetOperations.DIFFERENCE)));
			}
		});
		difference.setEnabled(false);

		ImageIcon partitionIcon = new ImageIcon(getClass().getResource("/icons/partition.png"));
		partition = new JButton(partitionIcon);
		partition.setToolTipText("Partition");
		partition.setEnabled(false);
		partition.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				CyApplicationManager appManager = (CyApplicationManager) getService(CyApplicationManager.class);
				taskManager.execute(new TaskIterator(new PartitionNodesTask(mySets, appManager.getCurrentNetwork())));
			}
		});

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
		final Action addSelectedNodes = new AbstractAction("selected nodes") {
			public void actionPerformed(ActionEvent e) {
				CyApplicationManager appManager = (CyApplicationManager) getService(CyApplicationManager.class);
				taskManager.execute(new TaskIterator(new CreateNodeSetTask(mySets, appManager.getCurrentNetwork())));
			}
		};
		final Action addSelectedEdges = new AbstractAction("selected edges") {
			public void actionPerformed(ActionEvent e) {
				CyApplicationManager appManager = (CyApplicationManager) getService(CyApplicationManager.class);
				taskManager.execute(new TaskIterator(new CreateEdgeSetTask(mySets, appManager.getCurrentNetwork())));
			}
		};
		final Action addNodeAttributes = new AbstractAction("node attributes") {
			public void actionPerformed(ActionEvent e) {
				CyApplicationManager appManager = (CyApplicationManager) getService(CyApplicationManager.class);
				taskManager.execute(new TaskIterator(new CreateSetFromAttributeTask(mySets, appManager.getCurrentNetwork(), CyNode.class)));
			}
		};	
		final Action addEdgeAttributes = new AbstractAction("edge attributes") {
			public void actionPerformed(ActionEvent e) {
				CyApplicationManager appManager = (CyApplicationManager) getService(CyApplicationManager.class);
				taskManager.execute(new TaskIterator(new CreateSetFromAttributeTask(mySets, appManager.getCurrentNetwork(), CyEdge.class)));
			}
		};	
		final Action importFromFile = new AbstractAction("import from file") {
			public void actionPerformed(ActionEvent e) {
				taskManager.execute(new TaskIterator(new CreateSetFromFileTask(mySets,networkManager)));
			}
		};
		final JPopupMenu addMenu = new JPopupMenu();
		addMenu.add(addSelectedNodes);
		addMenu.add(addSelectedEdges);
		addMenu.add(addNodeAttributes);
		addMenu.add(addEdgeAttributes);
		addMenu.addSeparator();
		addMenu.add(importFromFile);

		ImageIcon addIcon = new ImageIcon(getClass().getResource("/icons/add.png"));
		final JButton addBtn = new JButton(addIcon);
		addBtn.setToolTipText("Add a set");
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CyApplicationManager appManager = (CyApplicationManager) getService(CyApplicationManager.class);
				CyNetwork curNetwork = appManager.getCurrentNetwork();
				if (curNetwork != null) {
					List<CyNode> nodes = CyTableUtil.getNodesInState(curNetwork, CyNetwork.SELECTED, true);
					List<CyEdge> edges = CyTableUtil.getEdgesInState(curNetwork, CyNetwork.SELECTED, true);
					addSelectedNodes.setEnabled(!nodes.isEmpty());
					addSelectedEdges.setEnabled(!edges.isEmpty());
					CyTable nodesTable = curNetwork.getDefaultNodeTable(),
					        edgesTable = curNetwork.getDefaultEdgeTable();
					addNodeAttributes.setEnabled(!nodesTable.getColumns().isEmpty());
					addEdgeAttributes.setEnabled(!edgesTable.getColumns().isEmpty());
				} else {
					addSelectedNodes.setEnabled(false);
					addSelectedEdges.setEnabled(false);
					addNodeAttributes.setEnabled(false);
					addEdgeAttributes.setEnabled(false);
				}
				addMenu.show(addBtn, 0, addBtn.getHeight());
			}
		});

		ImageIcon removeIcon = new ImageIcon(getClass().getResource("/icons/remove.png"));
		removeBtn = new JButton(removeIcon);
		removeBtn.setToolTipText("Remove selected sets");
		removeBtn.setEnabled(false);
		removeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TreePath[] paths = setsTree.getSelectionPaths();

				// delete set elements first
				for (TreePath path : paths) {
					final DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
					boolean isSetElement = ! node.isRoot() && ((NodeInfo) node.getUserObject()).cyId != null;
					if (isSetElement) {
						final DefaultMutableTreeNode setNode = (DefaultMutableTreeNode) node.getParent();
						final CyIdentifiable selectecCyId = ((NodeInfo) node.getUserObject()).cyId;
						final String thisSetName = ((NodeInfo) setNode.getUserObject()).setName;
						try {
							mySets.removeFromSet(thisSetName, selectecCyId);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}

				// delete sets
				for (TreePath path : paths) {
					final DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
					boolean isSet = ! node.isRoot() && ((NodeInfo) node.getUserObject()).cyId == null;
					if (isSet) {
						taskManager.execute(new TaskIterator(new RemoveSetTask(mySets, ((NodeInfo) node.getUserObject()).setName)));
					}
				}
			}
		});

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, BS, 0));
		topPanel.add(addBtn);
		topPanel.add(removeBtn);
		
		JPanel leftBtmPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JPanel rightBtmPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightBtmPanel.add(partition);
		rightBtmPanel.add(union);
		rightBtmPanel.add(intersection);
		rightBtmPanel.add(difference);

		JPanel btmPanel = new JPanel(new BorderLayout(BS, BS));
		btmPanel.add(leftBtmPanel, BorderLayout.LINE_START);
		btmPanel.add(rightBtmPanel, BorderLayout.LINE_END);

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
	}

	public void enableRemoveButton(boolean b) {
		removeBtn.setEnabled(b);
	}

	/**
	 * Set "set1" variable, usually used internally. Used by the "Set Operations" button to
	 * determine what set to use.
	 * @param set name of a set
	 */
	public void setFirstSet(String set) {
		set1 = set;
		setsList = null;
	}

	/**
	 * Set "set2" variable, usually used internally. Used by the "Set Operations" button to
	 * determine what set to use.
	 * @param set name of a set
	 */
	public void setSecondSet(String set) {
		set2 = set;
		setsList = null;
	}

	/**
	 * Set "setList" variable, usually used internally. Used by the "Set Operations" button to
	 * determine what sets to use.
	 * @param list of set names to use
	 */
	public void setSetList(List<String> sets) {
		set1 = null;
		set2 = null;
		setsList = sets;
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
		Class<? extends CyIdentifiable> type = mySets.getType(event.getSetName());
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
		updatePartitionBtn();
	}

	/**
 	 * {@interitDoc}
 	 */
	public void setRemoved(SetChangedEvent event) {
		treeModel.removeNodeFromParent(setsNode.get(event.getSetName()));
		setsNode.remove(event.getSetName());
		cyIdNode.remove(event.getSetName());
		updatePartitionBtn();
	}

	/**
 	 * {@interitDoc}
 	 */
	public void setsCleared(SetChangedEvent event) {
		while (sets.getChildCount() > 0) {
			treeModel.removeNodeFromParent((MutableTreeNode) sets.getLastChild());
		}
		partition.setEnabled(false);
	}

	/**
 	 * {@interitDoc}
 	 */
	public void setChanged(SetChangedEvent event) {
		CyNetwork cyNetwork = event.getCyNetwork();
		List<CyIdentifiable> added = (List<CyIdentifiable>) event.getCyIdsAdded(),
				removed = (List<CyIdentifiable>) event.getCyIdsRemoved();
		DefaultMutableTreeNode setNode = setsNode.get(event.getSetName());
		Set set = mySets.getSet(event.getSetName());
		if (added != null) {
			for (CyIdentifiable node: added) {
				String cyIdName = mySets.getElementName(cyNetwork, event.getSetName(), node);
				DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(new NodeInfo(cyIdName, node));
				setNode.add(newTreeNode);
			}
		} else if (removed != null) {
			for (CyIdentifiable edge: removed) {
				DefaultMutableTreeNode treeNode = cyIdNode.get(event.getSetName()).get(edge.getSUID());
				if (treeNode != null)
					treeModel.removeNodeFromParent(treeNode);
			}
		}
		setNode.setUserObject(new NodeInfo(event.getSetName() + " (" + set.getElements().size() + ")", event.getSetName()));
		treeModel.nodeChanged(setNode);
	}

	public void handleEvent(SetCurrentNetworkEvent e) {
		updatePartitionBtn();
	}


	/****************************************************
 	 * Private methods
 	 ***************************************************/

	private void updatePartitionBtn() {
		CyApplicationManager appManager = (CyApplicationManager) getService(CyApplicationManager.class);
		partition.setEnabled(mySets.getSetsFor(appManager.getCurrentNetwork(), CyNode.class).size() > 0);
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
