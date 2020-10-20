package de.ugoe.cs.tcs.simparameter.persons;

import com.google.common.collect.Maps;
import de.ugoe.cs.tcs.simparameter.util.MutableInt;

import java.util.List;
import java.util.Map;

public class PersonData {
  private int numOfDevelopers;
  private Map<DeveloperType, MutableInt> numOfDeveloperPerType;
  private Map<DeveloperRole, MutableInt> numOfDeveloperPerRole;
  private Map<DeveloperType, MutableInt> numOfMaintainerPerType;
  private Map<DeveloperRole, MutableInt> numOfMaintainerPerRole;

  public PersonData(List<Identity> identities) {
    numOfDeveloperPerType = Maps.newHashMap();
    numOfDeveloperPerRole = Maps.newHashMap();
    numOfMaintainerPerType = Maps.newHashMap();
    numOfMaintainerPerRole = Maps.newHashMap();
    init(identities);
  }

  private void init(List<Identity> identities) {
    numOfDevelopers = identities.size();
    for (Identity id : identities) {
      increment(id, numOfDeveloperPerType, numOfDeveloperPerRole);
      if (id.isMaintainer()) {
        increment(id, numOfMaintainerPerType, numOfMaintainerPerRole);
      }
    }
  }

  private void increment(Identity id, Map<DeveloperType, MutableInt> types, Map<DeveloperRole, MutableInt> roles) {
    MutableInt type = types.get(id.getType());
    if (type == null) {
      types.put(id.getType(), new MutableInt());
    } else {
      type.increment();
    }
    MutableInt role = roles.get(id.getRole());
    if (role == null) {
      roles.put(id.getRole(), new MutableInt());
    } else {
      role.increment();
    }
  }

  public void print() {
    System.out.println("Person information:");
    System.out.println("Numebr of developers: " + numOfDevelopers);

    System.out.println("Number of developers per type:");
    for (DeveloperType t : numOfDeveloperPerType.keySet()) {
      System.out.println("\t" + t + ": " + numOfDeveloperPerType.get(t).get());
    }

    System.out.println("Number of developers per role:");
    for (DeveloperRole t : numOfDeveloperPerRole.keySet()) {
      System.out.println("\t" + t + ": " + numOfDeveloperPerRole.get(t).get());
    }

    System.out.println("Number of maintainer per type:");
    for (DeveloperType t : numOfMaintainerPerType.keySet()) {
      System.out.println("\t" + t + ": " + numOfMaintainerPerType.get(t).get());
    }

    System.out.println("Number of maintainer per type:");
    for (DeveloperRole t : numOfMaintainerPerRole.keySet()) {
      System.out.println("\t" + t + ": " + numOfMaintainerPerRole.get(t).get());
    }
  }

  public int numOfDevelopers() {
    return numOfDevelopers;
  }

  // types
  public int numOfKeyDevelopers() {
    return numOfDeveloperPerType.get(DeveloperType.key) == null ? 0 : numOfDeveloperPerType.get(DeveloperType.key).get();
  }

  public int numOfKeyDevelopersM() {
    return numOfMaintainerPerType.get(DeveloperType.key) == null ? 0 : numOfMaintainerPerType.get(DeveloperType.key).get();
  }

  public int numOfMinorDevelopers() {
    return numOfDeveloperPerType.get(DeveloperType.minor) == null ? 0 : numOfDeveloperPerType.get(DeveloperType.minor).get();
  }

  public int numOfMinorDevelopersM() {
    return numOfMaintainerPerType.get(DeveloperType.minor) == null ? 0 : numOfMaintainerPerType.get(DeveloperType.minor).get();
  }

  public int numOfMajorDevelopers() {
    return numOfDeveloperPerType.get(DeveloperType.major) == null ? 0 : numOfDeveloperPerType.get(DeveloperType.major).get();
  }

  public int numOfMajorDevelopersM() {
    return numOfMaintainerPerType.get(DeveloperType.major) == null ? 0 : numOfMaintainerPerType.get(DeveloperType.major).get();
  }

  // roles
  public int numOfCoreDevelopers() {
    return numOfDeveloperPerRole.get(DeveloperRole.core) == null ? 0 : numOfDeveloperPerRole.get(DeveloperRole.core).get();
  }

  public int numOfCoreDevelopersM() {
    return numOfMaintainerPerRole.get(DeveloperRole.core) == null ? 0 : numOfMaintainerPerRole.get(DeveloperRole.core).get();
  }

  public int numOfPeripheralDevelopers() {
    return numOfDeveloperPerRole.get(DeveloperRole.peripheral) == null ? 0 : numOfDeveloperPerRole.get(DeveloperRole.peripheral).get();
  }

  public int numOfPeripheralDevelopersM() {
    return numOfMaintainerPerRole.get(DeveloperRole.peripheral) == null ? 0 : numOfMaintainerPerRole.get(DeveloperRole.peripheral).get();
  }

}
