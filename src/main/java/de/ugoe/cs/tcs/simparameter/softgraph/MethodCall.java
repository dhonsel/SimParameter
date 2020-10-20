package de.ugoe.cs.tcs.simparameter.softgraph;

import org.jgrapht.graph.DefaultEdge;

public class MethodCall extends DefaultEdge {
  private SoftwareClass callerClass;
  private SoftwareMethod callerMethod;
  private SoftwareClass calleeClass;
  private SoftwareMethod calleeMethod;

  public MethodCall(SoftwareClass callerClass, SoftwareMethod callerMethod, SoftwareClass calleeClass, SoftwareMethod calleeMethod) {
    this.callerClass = callerClass;
    this.callerMethod = callerMethod;
    this.calleeClass = calleeClass;
    this.calleeMethod = calleeMethod;
  }

  public SoftwareClass getCallerClass() {
    return callerClass;
  }

  public SoftwareMethod getCallerMethod() {
    return callerMethod;
  }

  public SoftwareClass getCalleeClass() {
    return calleeClass;
  }

  public SoftwareMethod getCalleeMethod() {
    return calleeMethod;
  }
}
