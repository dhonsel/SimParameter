package de.ugoe.cs.tcs.simparameter.changecoupling;

import org.jgrapht.io.Attribute;
import org.jgrapht.io.AttributeType;

public class CommonAttribute implements Attribute {
  private String value;
  private AttributeType aType;

  public CommonAttribute(String value, AttributeType aType) {
    this.value = value;
    this.aType = aType;
  }

  @Override
  public String getValue() {
    return this.value;
  }

  @Override
  public AttributeType getType() {
    return this.aType;
  }
}
