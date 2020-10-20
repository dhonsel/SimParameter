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
public class SmallCommit {
  @Id
  @Property("_id")
  private ObjectId id;
  @Property("vcs_system_id")
  private ObjectId vcSystemId;
  @Property("revision_hash")
  private String revisionHash;
  @Property("author_date")
  private Date authorDate;
  @Property("author_id")
  private ObjectId authorId;

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

  public Date getAuthorDate() {
    return authorDate;
  }

  public void setAuthorDate(Date authorDate) {
    this.authorDate = authorDate;
  }

  public ObjectId getAuthorId() {
    return authorId;
  }

  public void setAuthorId(ObjectId authorId) {
    this.authorId = authorId;
  }

}
