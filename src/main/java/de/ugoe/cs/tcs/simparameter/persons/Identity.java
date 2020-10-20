package de.ugoe.cs.tcs.simparameter.persons;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@JsonIgnoreProperties(value = {"people"})
public class Identity implements Comparable<Identity> {
  private final UUID objectID;
  private String name;
  private List<ObjectId> people;
  private int numberOfCommits;
  private double percent;
  private DeveloperType type;
  private DeveloperRole role;
  private boolean maintainer;
  private int numberOfFixes;
  private int numberOfTests;
  private int numberOfFeatures;
  private int numberOfMaintenance;
  private int numberOfRefactorings;
  private int numberOfDocumentation;

  public Identity() {
    objectID = UUID.randomUUID();
    this.people = Lists.newArrayList();
  }

  public UUID getObjectID() {
    return objectID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<ObjectId> getPeople() {
    return people;
  }

  public int getNumberOfCommits() {
    return numberOfCommits;
  }

  public void setNumberOfCommits(int numberOfCommits) {
    this.numberOfCommits = numberOfCommits;
  }

  public double getPercent() {
    return percent;
  }

  public void setPercent(double percent) {
    this.percent = percent;
  }

  public int getNumberOfFixes() {
    return numberOfFixes;
  }

  public void incrementNumberOfFixes() {
    this.numberOfFixes++;
  }

  public DeveloperType getType() {
    return type;
  }

  public void setType(DeveloperType type) {
    this.type = type;
  }

  public DeveloperRole getRole() {
    return role;
  }

  public void setRole(DeveloperRole role) {
    this.role = role;
  }

  public boolean isMaintainer() {
    return maintainer;
  }

  public void setMaintainer(boolean maintainer) {
    this.maintainer = maintainer;
  }

  public int getNumberOfTests() {
    return numberOfTests;
  }

  public void incrementNumberOfTests() {
    this.numberOfTests++;
  }

  public int getNumberOfFeatures() {
    return numberOfFeatures;
  }

  public void incrementNumberOfFeatures() {
    this.numberOfFeatures++;
  }

  public int getNumberOfMaintenance() {
    return numberOfMaintenance;
  }

  public void incrementNumberOfMaintenance() {
    this.numberOfMaintenance++;
  }

  public int getNumberOfRefactorings() {
    return numberOfRefactorings;
  }

  public void incrementNumberOfRefactorings() {
    this.numberOfRefactorings++;
  }

  public int getNumberOfDocumentation() {
    return numberOfDocumentation;
  }

  public void incrementNumberOfDocumentation() {
    this.numberOfDocumentation++;
  }

  @Override
  public int compareTo(Identity o) {
    return Integer.compare(this.numberOfCommits, o.numberOfCommits);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Identity identity = (Identity) o;
    return Objects.equals(objectID, identity.objectID);
  }

  @Override
  public int hashCode() {
    return Objects.hash(objectID);
  }
}
