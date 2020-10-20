package de.ugoe.cs.tcs.simparameter.model;

import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.List;

/**
 * This class represents a mapping for the mungodb collection <code>refactoring</code>.
 * An object will be instantiated for each found refactoring in the commit to be analyzed.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */

@Entity(noClassnameStored = true, value="refactoring")
public class Refactoring {
  @Id
  @Property("_id")
  private ObjectId id;
  @Property("commit_id")
  private ObjectId commitId;
  private String type;
  private String description;
  @Embedded("ce_state")
  private RefactoringState state;
  @Embedded("parent_commit_ce_states")
  private List<RefactoringState> parentStates;

  public Refactoring() {
    parentStates = Lists.newArrayList();
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public ObjectId getCommitId() {
    return commitId;
  }

  public void setCommitId(ObjectId commitId) {
    this.commitId = commitId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public RefactoringState getState() {
    return state;
  }

  public void setState(RefactoringState state) {
    this.state = state;
  }

  public List<RefactoringState> getParentStates() {
    return parentStates;
  }

  public void setParentStates(List<RefactoringState> parentStates) {
    this.parentStates = parentStates;
  }
}
