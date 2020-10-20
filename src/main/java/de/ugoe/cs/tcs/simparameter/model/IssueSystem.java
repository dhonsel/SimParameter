package de.ugoe.cs.tcs.simparameter.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.util.Date;

/**
 * This class represents a mapping for the mungodb collection <code>issue_system</code>.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
@Entity("issue_system")
public class IssueSystem {
  @Id
  @Property("_id")
  private ObjectId id;
  @Property("project_id")
  private ObjectId projectId;
  private String url;
  @Property("last_updated")
  private Date lastUpdated;

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public ObjectId getProjectId() {
    return projectId;
  }

  public void setProjectId(ObjectId projectId) {
    this.projectId = projectId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }
}
