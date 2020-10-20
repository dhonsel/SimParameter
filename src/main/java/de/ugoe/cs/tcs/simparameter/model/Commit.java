package de.ugoe.cs.tcs.simparameter.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This class represents a mapping for the mungodb collection <code>commit</code>.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
@Entity("commit")
public class Commit {
  @Id
  @Property("_id")
  private ObjectId id;
  @Property("vcs_system_id")
  private ObjectId vcSystemId;
  @Property("revision_hash")
  private String revisionHash;
  private List<String> parents;
  private List<String> branches;
  @Property("author_date")
  private Date authorDate;
  @Property("committer_date")
  private Date committerDate;
  @Property("author_id")
  private ObjectId authorId;
  private String message;
  @Property("author_date_offset")
  private int authorDateOffset;
  @Property("committer_date_offset")
  private int committerDateOffset;
  @Property("committer_id")
  private ObjectId committerId;
  @Property("code_entity_states")
  private List<ObjectId> codeEntityStates;
  @Property("labels")
  private Map<String, Boolean> labels;

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public ObjectId getVcSystemId() {
    return vcSystemId;
  }

  public void setVcSystemId(ObjectId vcSystemId) {
    this.vcSystemId = vcSystemId;
  }

  public String getRevisionHash() {
    return revisionHash;
  }

  public void setRevisionHash(String revisionHash) {
    this.revisionHash = revisionHash;
  }

  public List<String> getParents() {
    return parents;
  }

  public void setParents(List<String> parents) {
    this.parents = parents;
  }

  public List<String> getBranches() {
    return branches;
  }

  public void setBranches(List<String> branches) {
    this.branches = branches;
  }

  public Date getAuthorDate() {
    return authorDate;
  }

  public void setAuthorDate(Date authorDate) {
    this.authorDate = authorDate;
  }

  public Date getCommitterDate() {
    return committerDate;
  }

  public void setCommitterDate(Date committerDate) {
    this.committerDate = committerDate;
  }

  public ObjectId getAuthorId() {
    return authorId;
  }

  public void setAuthorId(ObjectId authorId) {
    this.authorId = authorId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getAuthorDateOffset() {
    return authorDateOffset;
  }

  public void setAuthorDateOffset(int authorDateOffset) {
    this.authorDateOffset = authorDateOffset;
  }

  public int getCommitterDateOffset() {
    return committerDateOffset;
  }

  public void setCommitterDateOffset(int committerDateOffset) {
    this.committerDateOffset = committerDateOffset;
  }

  public ObjectId getCommitterId() {
    return committerId;
  }

  public void setCommitterId(ObjectId committerId) {
    this.committerId = committerId;
  }

  public List<ObjectId> getCodeEntityStates() {
    return codeEntityStates;
  }

  public void setCodeEntityStates(List<ObjectId> codeEntityStates) {
    this.codeEntityStates = codeEntityStates;
  }

  public Map<String, Boolean> getLabels() {
    return labels;
  }

  public void setLabels(Map<String, Boolean> labels) {
    this.labels = labels;
  }
}
