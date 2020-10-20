package de.ugoe.cs.tcs.simparameter.softgraph;

import org.jgrapht.graph.DefaultEdge;

public class MethodMember extends DefaultEdge {
  private SoftwareClass c;
  private SoftwareMethod m;

  public MethodMember(SoftwareClass c, SoftwareMethod m) {
    this.c = c;
    this.m = m;
  }

  public SoftwareClass getSoftwareClass() {
    return c;
  }

  public SoftwareMethod getSoftwareMethod() {
    return m;
  }
}
