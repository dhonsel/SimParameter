package de.ugoe.cs.tcs.simparameter.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

/**
 * This class represents a mapping for the mungodb collection <code>file</code>.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
@Entity("file")
public class File {
  @Id
  @Property("_id")
  private ObjectId id;
  @Property("vcs_system_id")
  private ObjectId vcSystemId;
  private String path;

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

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
