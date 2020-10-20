package de.ugoe.cs.tcs.simparameter.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Property;

/**
 * This class references the code entity states of entities and parents touched by
 * one found refactoring. Each commit has its own state. Deltas can be computed
 * between the analyzed commit and its parents.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class RefactoringState {
  @Property("parent_ce_before")
  private ObjectId parentCeBefore;
  @Property("parent_ce_after")
  private ObjectId parentCeAfter;
  @Property("ce_before")
  private ObjectId ceBefore;
  @Property("ce_after")
  private ObjectId ceAfter;

  public ObjectId getParentCeBefore() {
    return parentCeBefore;
  }

  public void setParentCeBefore(ObjectId parentCeBefore) {
    this.parentCeBefore = parentCeBefore;
  }

  public ObjectId getParentCeAfter() {
    return parentCeAfter;
  }

  public void setParentCeAfter(ObjectId parentCeAfter) {
    this.parentCeAfter = parentCeAfter;
  }

  public ObjectId getCeBefore() {
    return ceBefore;
  }

  public void setCeBefore(ObjectId ceBefore) {
    this.ceBefore = ceBefore;
  }

  public ObjectId getCeAfter() {
    return ceAfter;
  }

  public void setCeAfter(ObjectId ceAfter) {
    this.ceAfter = ceAfter;
  }
}
