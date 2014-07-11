package edu.ucsf.rbvi.setsApp.internal.layouts;

import org.cytoscape.work.Tunable;

public class ForceDirectedLayoutContext {
  @Tunable(description="The number of iterations to run the algorithm -- the higher the number, the better the accuracy yet longer run-time")
  public int iterations = 100;

  @Tunable(description="The mass of nodes -- the higher the number, the less nodes move around the network")
  public double nodeMass = 3.0f;

  @Tunable(description="The spring coefficient for springs between connected nodes")
  public double edgeSpringCoeff = 1e-4f;

  @Tunable(description="The spring length for springs between connected nodes")
  public double edgeSpringLength = 50.0f;

  @Tunable(description="The spring coefficient for nodes in the same group")
  public double groupSpringCoeff = 1e-7f;

  @Tunable(description="The spring length for nodes in the same group")
  public double groupSpringLength = 300.0f;
}
