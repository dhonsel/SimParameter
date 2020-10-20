package de.ugoe.cs.tcs.simparameter.changecoupling;

import org.jgrapht.graph.DefaultWeightedEdge;

public class CCEdge extends DefaultWeightedEdge {
  private CCFile file1;
  private CCFile file2;
  private double weight;

  public CCEdge(CCFile file1, CCFile file2) {
    this.file1 = file1;
    this.file2 = file2;
    this.weight = 1.0;
  }

  @Override
  public double getWeight() {
    return weight;
  }

  public void increaseWeight() {
    weight++;
  }
}
