package de.ugoe.cs.tcs.simparameter.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.util.Objects;

/**
 * This class represents a mapping for the mungodb collection <code>people</code>.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
@Entity("people")
public class People {
  @Id
  @Property("_id")
  private ObjectId id;
  private String email;
  private String name;
  private String username;

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    People people = (People) o;
    return Objects.equals(id, people.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public People clone() {
    People p = new People();
    p.setId(this.getId());
    p.setEmail(this.getEmail());
    p.setName(this.getName());
    p.setUsername(this.getUsername());
    return p;
  }
}
