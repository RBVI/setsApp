package edu.ucsf.rbvi.setsApp.internal.layouts;

import org.cytoscape.work.Tunable;

public class GridLayoutContext {
  @Tunable(description="Horizontal space between sets")
  public double setSpaceX = 150.0;

  @Tunable(description="Vertical space between sets")
  public double setSpaceY = 150.0;

  @Tunable(description="Horizontal space between nodes")
  public double nodeSpaceX = 10.0;

  @Tunable(description="Vertical space between nodes")
  public double nodeSpaceY = 10.0;
}
