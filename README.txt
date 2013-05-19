SetsApp 0.0.1

Introduction:
SetsApp is a Cytoscape 3 app allowing the user to create and manipulate sets of nodes
or edges (but not sets with both nodes and edges, at least in the current version) in
Cytoscape. Sets can be created from different networks. In order to create a set, the
user must select a network (usually when a user imports a network or opens up a
session there is a network selected by default), and the program will create sets of
nodes or edges based on user input.

The "Sets" Panel:
The "Sets" panel is the main graphical user interface through which the user creates,
manipulates and destroys sets. In the center is a tree which contains all the sets
that has been created.

Creating sets:
There are three methods to create sets. 

Creating sets from selected nodes/edges:
The first method is to create a set by selecting a set of nodes or edges in a network.
First, select nodes and/or edges with the mouse, then go to the sets panel to the
section "New Sets." Select from the drop-down menu
"Create sets from selected nodes" or "Create sets from selected edges." A dialog box
opens up to allow the user to enter the name for the set. A new set should appear in
the "Sets" panel.

Otherwise, a set can also be created using the network context menu. Select nodes/
edges using the mouse, then right-click on a network and select Apps->setsApp->
Create node set or Create edge set.

Creating sets from attributes:
Go to "New Sets" section on the "Sets" panel, and select "Create set from node attributes"
or "Create set from edge attributes." A dialog box pops up
to allow the user to choose which attribute column to create the set from. The program
only will create a set for nodes/edges with the same attribute value. The current
version of the program only uses String attributes, but support for other data types
might appear in the future.

Creating sets from file:
Click the button "Import Set from File." Select file using the file chooser, then enter
into the dialog box the name of the set. To create a node set, select from the drop-
down menu "Select column to create node set" or the other drop-down menu to create an
edge set. The file is assumed to be in the format of a list of names of the nodes/edges,
and new set will be created by merging this list with the column chosen by the user
using the drop-down menu.

Set Operations:
All set operations are in the "Set Operations" section of the "Sets" panel. The three
operations allowed are Union, Intersection and Difference. All these operations take
two sets, then creates a new set based on the operation. To perform set operations,
select two sets from the "Sets" panel, (this can be done by doing left-click-ctrl-z on
most operating systems), then press any of the three buttons. A new set will be
created called "X union Y", "X intersection y" or "X difference Y" for sets named "X"
and "Y" based on operation done (in the future an option maybe added for users to
choose the name of the set created). For set difference, the order the operation is
done is based on the order the user selects the sets.

Set manipulations:
There are other manipulations that can be performed on the sets, which are moving a
node/edge from one set to another, copying a node/edge from one set to another,
removing a node/edge from a set, adding a node/edge to a set, renaming a set, and
removing a set.

Moving/Copying a node/edge:
Right-click on a node/edge in the "Sets" panel to bring up the context menu, and
select "Move to.." or "Copy to.." to bring up a dialog box containing the sets this
node/edge can be moved to. Select the target set. (Note: only nodes that belong to
the same network can be moved.)

Adding a node/edge to a set:
Adding a node/edge to a set can be done through the node/edge context menu. Right-
click on a node/edge to bring up the context menu, then select Apps->setsApp->
Add a node/edge. A dialog box brings up a list of choices of sets to add the node/
edge to (Note: this is also subject to the restriction of only allowing nodes/
edges of the same network to be added.)

Removing a node/edge from the set or removing a set:
Right-click on a node/edge in the "Sets" panel to bring up the context menu, and
select "Remove from set." The operation for removing a set is very similar to this
one.

Saving the nodes in the current session:
The sets are automatically saved to the current session upon creation, and
automatically removed from the session when removed. Any changes made to the set
are also saved in the current session. To save a set in the current session all
the user needs to do is to save the current save current session, and the sets
will be automatically restored when the session is loaded/reloaded.

Exporting sets to file:
To export a set, select a set by clicking on it, then click on the button at the
bottom of the "Sets" pane. Enter the name of the file to save the set in, then
click "Save," then choose the column to output the file in. The file will be saved
as a list of identifiers.