package edu.ucsf.rbvi.setsApp.internal.layouts;

import org.cytoscape.work.Tunable;

public class ForceDirectedLayoutContext {
  @Tunable(description="Iterations",
           tooltip="The number of iterations to run the algorithm. The higher the number, the better the accuracy yet longer run-time.")
  public int iterations = 100;

  @Tunable(description="Node mass",
           tooltip="The higher the node mass, the less nodes move around the network")
  public double nodeMass = 3.0;

  @Tunable(description="Spring coefficient for connected nodes",
           tooltip="The higher this number is, the more that network topology affects the layout. The effective coefficient is the negative log of this value.")
  public double edgeSpringCoeffNLog = 4;

  @Tunable(description="Spring length for connected nodes")
  public double edgeSpringLength = 50.0;

  @Tunable(description="Spring coefficient for nodes in the same set",
           tooltip="The higher this number is, the more that set membership affects the layout. The effective coefficient is the negative log of this value.")
  public double groupSpringCoeffNLog = 7;

  @Tunable(description="Spring length for nodes in the same set")
  public double groupSpringLength = 50.0;
}
