package de.ugoe.cs.tcs.simparameter.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This class represents a mapping for the mungodb collection <code>issue</code>.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
@Entity("issue")
public class Issue {
  @Id
  @Property("_id")
  private ObjectId id;
  @Property("external_id")
  private String externalId;
  @Property("issue_system_id")
  private ObjectId issueSystemId;
  private String title;
  private String desc;
  @Property("created_at")
  private Date createdAt;
  @Property("updated_at")
  private Date updatedAt;
  @Property("creator_id")
  private ObjectId creatorId;
  @Property("reporter_id")
  private ObjectId reporterId;
  @Property("assignee_id")
  private ObjectId assigneeId;
  @Property("issueType")
  private String issueType;
  private String priority;
  private String status;
  private String environment;
  @Property("parent_issue_id")
  private ObjectId parentIssueId;
  private String resolution;
  @Property("affects_versions")
  private List<String> affectsVersions;
  @Property("components")
  private List<String> components;
  @Property("labels")
  private List<String> labels;
  @Property("fix_versions")
  private List<String> fixVersions;
  @Property("original_time_estimate")
  private Integer originalTimeEstimate;
  @Property("issue_links")
  private List<Map<String, String>> issueLinks;

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public ObjectId getIssueSystemId() {
    return issueSystemId;
  }

  public void setIssueSystemId(ObjectId issueSystemId) {
    this.issueSystemId = issueSystemId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public ObjectId getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(ObjectId creatorId) {
    this.creatorId = creatorId;
  }

  public ObjectId getReporterId() {
    return reporterId;
  }

  public void setReporterId(ObjectId reporterId) {
    this.reporterId = reporterId;
  }

  public ObjectId getAssigneeId() {
    return assigneeId;
  }

  public void setAssigneeId(ObjectId assigneeId) {
    this.assigneeId = assigneeId;
  }

  public String getIssueType() {
    return issueType;
  }

  public void setIssueType(String issueType) {
    this.issueType = issueType;
  }

  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  public ObjectId getParentIssueId() {
    return parentIssueId;
  }

  public void setParentIssueId(ObjectId parentIssueId) {
    this.parentIssueId = parentIssueId;
  }

  public String getResolution() {
    return resolution;
  }

  public void setResolution(String resolution) {
    this.resolution = resolution;
  }

  public List<String> getAffectsVersions() {
    return affectsVersions;
  }

  public void setAffectsVersions(List<String> affectsVersions) {
    this.affectsVersions = affectsVersions;
  }

  public List<String> getComponents() {
    return components;
  }

  public void setComponents(List<String> components) {
    this.components = components;
  }

  public List<String> getLabels() {
    return labels;
  }

  public void setLabels(List<String> labels) {
    this.labels = labels;
  }

  public List<String> getFixVersions() {
    return fixVersions;
  }

  public void setFixVersions(List<String> fixVersions) {
    this.fixVersions = fixVersions;
  }

  public Integer getOriginalTimeEstimate() {
    return originalTimeEstimate;
  }

  public void setOriginalTimeEstimate(Integer originalTimeEstimate) {
    this.originalTimeEstimate = originalTimeEstimate;
  }

  public List<Map<String, String>> getIssueLinks() {
    return issueLinks;
  }

  public void setIssueLinks(List<Map<String, String>> issueLinks) {
    this.issueLinks = issueLinks;
  }
}
