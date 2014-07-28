package edu.ucsf.rbvi.setsApp.internal.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyRow;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

import edu.ucsf.rbvi.setsApp.internal.model.Set;
import edu.ucsf.rbvi.setsApp.internal.model.SetsManager;
import edu.ucsf.rbvi.setsApp.internal.tasks.CopyCyIdTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.CopySetTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.MoveCyIdTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.RemoveSetTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.RenameSetTask;
import edu.ucsf.rbvi.setsApp.internal.tasks.WriteSetToFileTask2;

/**
 * Creates context menu for the JTree
 * @author Allan Wu
 *
 */
public class SetsMouseAdapter extends MouseAdapter {
	private SetsManager mySets;
	private SetsPane panel;
	private TaskManager taskManager;
	private String set1, set2;

	public SetsMouseAdapter(SetsManager mySets, SetsPane panel, TaskManager taskManager) {
		this.mySets = mySets;
		this.panel = panel;
		this.taskManager = taskManager;
	}

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
		if (! node.isRoot() && ((NodeInfo) node.getUserObject()).cyId != null) {
			final DefaultMutableTreeNode setNode = (DefaultMutableTreeNode) node.getParent();
			final CyIdentifiable selectecCyId = ((NodeInfo) node.getUserObject()).cyId;
			final String thisSetName = ((NodeInfo) setNode.getUserObject()).setName;
			
			JMenuItem copy = new JMenuItem("Copy to...");
			JMenuItem move = new JMenuItem("Move to...");
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
			popup.add(copy);
			popup.add(move);
		}
		else {
			JMenuItem rename = new JMenuItem("Rename");
			JMenuItem move = new JMenuItem("Copy set to different network");
			JMenuItem export = new JMenuItem("Export to file");

			rename.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					taskManager.execute(new TaskIterator(new RenameSetTask(mySets, ((NodeInfo) node.getUserObject()).setName)));
				}
			});
			move.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					CyNetworkManager networkManager = (CyNetworkManager) panel.getService(CyNetworkManager.class);
					taskManager.execute(new TaskIterator(new CopySetTask(mySets, networkManager.getNetworkSet(), ((NodeInfo) node.getUserObject()).setName)));
				}
			});
			export.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final String setName = ((NodeInfo) node.getUserObject()).setName;
					taskManager.execute(new TaskIterator(new WriteSetToFileTask2(mySets,setName)));
				}
			});
			popup.add(rename);
			popup.add(move);
			popup.add(export);
		}
		popup.show(tree, x, y);
	}

	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) popupEvent(e);
		else {
			if (getSetsSelectedFromTree(e))
				panel.enableOperationsButton(true);
			else panel.enableOperationsButton(false);
			if (singleSetSelected(e))
				panel.enableExportButton(true);
			else panel.enableExportButton(false);
			// panel.setFirstSet(set1);
			// panel.setSecondSet(set2);
		}
	}
	public void mouseReleased(MouseEvent e) {

		if (e.isPopupTrigger()) popupEvent(e);
		else {
			if (getSetsSelectedFromTree(e))
				panel.enableOperationsButton(true);
			else panel.enableOperationsButton(false);
			if (singleSetSelected(e))
				panel.enableExportButton(true);
			else panel.enableExportButton(false);
			JTree tree = (JTree)e.getSource();
			panel.enableRemoveButton(tree.getSelectionPath() != null);
			// panel.setFirstSet(set1);
			// panel.setSecondSet(set2);
			updateNetworkSelectionFromSelectedPaths(tree.getSelectionPaths());
		}
	}

	private void updateNetworkSelectionFromSelectedPaths(final TreePath[] paths) {
		final HashSet<CyNetwork> networksSeen = new HashSet<CyNetwork>();
		for (final TreePath path : paths) {
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			final DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
			final Object obj = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
			if (obj == null || !(obj instanceof NodeInfo)) {
				continue;
			}
			final NodeInfo nodeInfo = (NodeInfo) obj;
			final boolean isSet = nodeInfo.cyId == null;
			final String name = isSet ? nodeInfo.setName : ((NodeInfo) parent.getUserObject()).setName;

			final CyNetwork network = mySets.getCyNetwork(name);
			if (!networksSeen.contains(network)) {
				for (final CyTable tbl : Arrays.asList(network.getDefaultNodeTable(), network.getDefaultEdgeTable())) {
					for (final CyRow row : tbl.getAllRows()) {
						row.set(CyNetwork.SELECTED, false);
					}
				}
				networksSeen.add(network);
			}

			final CyTable tbl = mySets.getType(name).equals(CyNode.class) ? network.getDefaultNodeTable() : network.getDefaultEdgeTable();
			if (isSet) {
				for (final CyIdentifiable cyId : mySets.getSet(nodeInfo.setName).getElements()) {
					tbl.getRow(cyId.getSUID()).set(CyNetwork.SELECTED, true);
				}
			} else {
				tbl.getRow(nodeInfo.cyId.getSUID()).set(CyNetwork.SELECTED, true);
			}
		}
	}

	private boolean singleSetSelected(MouseEvent e) {
		JTree tree = (JTree) e.getSource();
		TreePath path[] = tree.getSelectionPaths();
		if (path != null && path.length == 1) {
			DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) path[0].getLastPathComponent();
			if (! node1.isRoot() && ((NodeInfo) node1.getUserObject()).cyId == null) return true;
			return false;
		}
		else return false;
	}

	private boolean getSetsSelectedFromTree(MouseEvent e) {
		JTree tree = (JTree) e.getSource();
		TreePath path[] = tree.getSelectionPaths();
		if (path != null && path.length == 1) {
			DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) path[0].getLastPathComponent();
			if (! node1.isRoot() && ((NodeInfo) node1.getUserObject()).cyId == null) {
				set1 = ((NodeInfo) node1.getUserObject()).setName;
				panel.setFirstSet(set1);
			}
			return false;
		}
		else if (path != null && path.length == 2) {
			DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) path[0].getLastPathComponent();
			DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) path[1].getLastPathComponent();
			if (! node1.isRoot() && ! node2.isRoot() && ((NodeInfo) node1.getUserObject()).cyId == null &&  ((NodeInfo) node2.getUserObject()).cyId == null) {
				if (set1.equals(((NodeInfo) node1.getUserObject()).setName)) {
					set1 = ((NodeInfo) node1.getUserObject()).setName;
					set2 = ((NodeInfo) node2.getUserObject()).setName;
				}
				else {
					set1 = ((NodeInfo) node2.getUserObject()).setName;
					set2 = ((NodeInfo) node1.getUserObject()).setName;
				}
				if (mySets.getType(set1) == mySets.getType(set2)) {
					panel.setFirstSet(set1);
					panel.setSecondSet(set2);
					return true;
				}
				else return false;
			}
			else return false;
		}
		else if (path != null && path.length > 2) {
			Class<? extends CyIdentifiable> type = null;
			List<String> setList = new ArrayList<String>();

			for (int i = 0; i < path.length; i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path[i].getLastPathComponent();
				if (!node.isRoot() && ((NodeInfo) node.getUserObject()).cyId == null) {
					String setName = ((NodeInfo) node.getUserObject()).setName;
					if (type == null) 
						type = mySets.getType(setName);
					else
						if (! mySets.getType(setName).equals(type) )
							return false;
					setList.add(setName);
				}
			}
			panel.setSetList(setList);
			return true;
		}
		else return false;
	}

	private void displaySelectedCyIds(String setName, Long cyId) {
		CyNetwork curNetwork = mySets.getCyNetwork(setName);
		CyTable curTable = null;
		if (mySets.getType(setName) == CyNode.class)
			curTable = curNetwork.getDefaultNodeTable();
		else if (mySets.getType(setName) == CyEdge.class)
			curTable = curNetwork.getDefaultEdgeTable();
		if (curTable != null)
			for (Long suid: curTable.getPrimaryKey().getValues(Long.class))
				curTable.getRow(suid).set(CyNetwork.SELECTED, 
						cyId == null ? mySets.isInSet(setName, suid): suid == cyId);
		CyNetworkViewManager nvm = (CyNetworkViewManager) panel.getService(CyNetworkViewManager.class);
		for (CyNetworkView networkView: nvm.getNetworkViewSet())
			if (networkView.getModel() == curNetwork)
				networkView.updateView();
	}
	private void unDisplaySelectedCyIds(String setName, Long cyId) {
		CyNetwork curNetwork = mySets.getCyNetwork(setName);
		CyTable curTable = null;
		if (mySets.getType(setName) == CyNode.class)
			curTable = curNetwork.getDefaultNodeTable();
		else if (mySets.getType(setName) == CyEdge.class)
			curTable = curNetwork.getDefaultEdgeTable();
		if (curTable != null) {
			if (cyId != null)
				curTable.getRow(cyId).set(CyNetwork.SELECTED, false);
			else
				for (CyIdentifiable suid: mySets.getSet(setName).getElements())
						curTable.getRow(suid.getSUID()).set(CyNetwork.SELECTED, false);
		}
		CyNetworkViewManager nvm = (CyNetworkViewManager) panel.getService(CyNetworkViewManager.class);
		for (CyNetworkView networkView: nvm.getNetworkViewSet())
			if (networkView.getModel() == curNetwork)
				networkView.updateView();
	}
}


