package de.ugoe.cs.tcs.simparameter.softgraph;

public class SoftwareMethod extends SoftwareEntity {
  private int unresolvedMethodCalls;
  private String declarationString;

  public SoftwareMethod(String name) {
    super(name);
  }

  public void increaseUnresolvedMethodCalls() {
    unresolvedMethodCalls++;
  }

  public int getUnresolvedMethodCalls() {
    return unresolvedMethodCalls;
  }

  public String getDeclarationString() {
    return declarationString;
  }

  public void setDeclarationString(String declarationString) {
    this.declarationString = declarationString;
  }
}
