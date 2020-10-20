package de.ugoe.cs.tcs.simparameter.persons;

import java.util.Collections;
import java.util.List;

public class DeveloperClassifier {
  private int sumOfFixes;
  private int sumOfCommits;

  public DeveloperClassifier(int sumOfFixes, int sumOfCommits) {
    this.sumOfFixes = sumOfFixes;
    this.sumOfCommits = sumOfCommits;
  }

  public void classify(List<Identity> identities) {
    for (Identity id : identities) {
      classifyType(id);
    }

    classifyRole(identities);
  }

  private void classifyType(Identity id) {
    if (id.getNumberOfCommits() * 1.0 / sumOfCommits >= .25) {
      id.setType(DeveloperType.key);
    } else if (id.getNumberOfCommits() * 1.0 / sumOfCommits >= .02) {
      id.setType(DeveloperType.major);
    } else {
      id.setType(DeveloperType.minor);
    }

    if (id.getNumberOfFixes() * 1.0 / sumOfFixes >= .15) {
      id.setMaintainer(true);
    }
  }

  private void classifyRole(List<Identity> identities) {
    Collections.sort(identities);

    //int size = identities.size();
    //Double rank = Math.ceil(80.0 / 100.0 * size);
    //int percentileValue = identities.get(rank.intValue() -1).getNumberOfCommits();

    Collections.reverse(identities);
    int value = 0;
    double percent = .0;
    for (Identity id : identities) {
      percent += id.getPercent();
      value = id.getNumberOfCommits();
      if (percent >= 80.0) {
        break;
      }
    }

    for (Identity id : identities) {
      if (id.getNumberOfCommits() >= value) {
        id.setRole(DeveloperRole.core);
      } else {
        id.setRole(DeveloperRole.peripheral);
      }
    }
  }
}
