package de.ugoe.cs.tcs.simparameter.changecoupling;

import com.google.common.collect.Maps;
import de.ugoe.cs.tcs.simparameter.persons.Identity;
import de.ugoe.cs.tcs.simparameter.persons.PersonInformation;
import de.ugoe.cs.tcs.simparameter.util.Common;
import de.ugoe.cs.tcs.simparameter.util.MutableInt;
import org.bson.types.ObjectId;

import java.util.Map;
import java.util.Objects;

public class CCFile {
  private ObjectId id;
  private String path;
  private Map<Identity, MutableInt> contributingDevelopers;
  private Identity creator;
  private String javaPackage;

  public CCFile(ObjectId id, String path, ObjectId creator) {
    this.contributingDevelopers = Maps.newHashMap();
    this.id = id;
    this.path = path;
    this.creator = PersonInformation.getInstance().getIdentityMap().get(creator);
    this.javaPackage = "UNKNOWN";
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getJavaPackage() {
    return javaPackage;
  }

  public void setJavaPackage(String javaPackage) {
    this.javaPackage = javaPackage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CCFile ccFile = (CCFile) o;
    return Objects.equals(id, ccFile.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  public void addContribution(ObjectId personId) {
    var identity = PersonInformation.getInstance().getIdentityMap().get(personId);
    if (identity == null) {
      return;
    }

    MutableInt i = contributingDevelopers.get(identity);
    if (i == null) {
      contributingDevelopers.put(identity, new MutableInt());
    } else {
      i.increment();
    }
  }

  public Identity getOwner(boolean print) {
    var cdSorted = Common.sortByValue(contributingDevelopers, true);
    if (cdSorted.size() > 0) {
      var key = cdSorted.keySet().stream().findFirst().get();
      if (print) {
        System.out.println("File: " + path + " is owned by " + key.getName() + " with " + contributingDevelopers.get(key).get() + " of " + getNumberOfChanges() + " commit(s). Owner is creator: " + key.equals(getCreator()));
      }
      return key;
    } else {
      return null;
    }
  }

  public int getNumberOfChanges() {
    int changes = 0;
    for (MutableInt i :  contributingDevelopers.values()) {
      changes += i.get();
    }
    return changes;
  }

  public Identity getCreator() {
    return creator;
  }

  public Map<Identity, MutableInt> getContributingDevelopers() {
    return contributingDevelopers;
  }
}
