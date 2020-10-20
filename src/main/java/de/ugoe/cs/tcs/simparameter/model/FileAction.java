package de.ugoe.cs.tcs.simparameter.model;


import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

/**
 * This class represents a mapping for the mungodb collection <code>file_action</code>.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
@Entity("file_action")
public class FileAction {
  @Id
  @Property("_id")
  private ObjectId id;
  @Property("file_id")
  private ObjectId fileId;
  @Property("commit_id")
  private ObjectId commitId;
  private String mode;
  @Property("size_at_commit")
  private int sizeAtCommit;
  @Property("lines_added")
  private int linesAdded;
  @Property("is_binary")
  private boolean isBinary;
  @Property("old_file_id")
  private ObjectId oldFileId;

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public ObjectId getFileId() {
    return fileId;
  }

  public void setFileId(ObjectId fileId) {
    this.fileId = fileId;
  }

  public ObjectId getCommitId() {
    return commitId;
  }

  public void setCommitId(ObjectId commitId) {
    this.commitId = commitId;
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public int getSizeAtCommit() {
    return sizeAtCommit;
  }

  public void setSizeAtCommit(int sizeAtCommit) {
    this.sizeAtCommit = sizeAtCommit;
  }

  public int getLinesAdded() {
    return linesAdded;
  }

  public void setLinesAdded(int linesAdded) {
    this.linesAdded = linesAdded;
  }

  public boolean isBinary() {
    return isBinary;
  }

  public void setBinary(boolean binary) {
    isBinary = binary;
  }

  public ObjectId getOldFileId() {
    return oldFileId;
  }

  public void setOldFileId(ObjectId oldFileId) {
    this.oldFileId = oldFileId;
  }
}
