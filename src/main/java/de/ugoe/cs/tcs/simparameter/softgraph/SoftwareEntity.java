package de.ugoe.cs.tcs.simparameter.softgraph;

import java.util.Objects;

public abstract class SoftwareEntity {
  protected String name;
  protected double LOC;


  public SoftwareEntity(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SoftwareEntity that = (SoftwareEntity) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  public double getLOC() {
    return LOC;
  }

  public void setLOC(double LOC) {
    this.LOC = LOC;
  }
}
