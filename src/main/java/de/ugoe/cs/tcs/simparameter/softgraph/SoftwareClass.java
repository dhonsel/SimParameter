package de.ugoe.cs.tcs.simparameter.softgraph;

public class SoftwareClass extends SoftwareEntity {
  private int numOfExternalCalls;

  public SoftwareClass(String name) {
    super(name);
  }

  public int getNumOfExternalCalls() {
    return numOfExternalCalls;
  }

  public void setNumOfExternalCalls(int numOfExternalCalls) {
    this.numOfExternalCalls = numOfExternalCalls;
  }
}
