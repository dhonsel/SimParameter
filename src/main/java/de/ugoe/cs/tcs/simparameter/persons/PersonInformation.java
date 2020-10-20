package de.ugoe.cs.tcs.simparameter.persons;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.ugoe.cs.tcs.simparameter.model.Commit;
import de.ugoe.cs.tcs.simparameter.model.People;
import de.ugoe.cs.tcs.simparameter.util.Common;
import de.ugoe.cs.tcs.simparameter.util.DatabaseContext;
import de.ugoe.cs.tcs.simparameter.util.MutableCommitStatistics;
import org.bson.types.ObjectId;

import java.util.*;


public class PersonInformation {
  private DatabaseContext ctx;
  private Map<ObjectId, Identity> identityMap;
  private List<Identity> identities;
  private int sumOfFixes;
  private int sumOfCommits;
  private static PersonInformation instance;

  private PersonInformation() {
    identityMap = Maps.newHashMap();
    identities = Lists.newArrayList();
    init();
  }

  /**
   * Returns the instance of the person information. If there is no instance one will be instantiated.
   *
   * @return The instance of the person information
   */
  public static synchronized PersonInformation getInstance() {
    if (instance == null) {
      instance = new PersonInformation();
    }
    return instance;
  }

  private void init() {
    ctx = DatabaseContext.getInstance();
    Map<People, MutableCommitStatistics> authors = Maps.newHashMap();
    List<Commit> commits = DatabaseContext.getInstance().getAllCommits();

    sumOfCommits = commits.size();
    for (Commit c : commits) {
      var p = ctx.getPeopleById(c.getAuthorId());
      MutableCommitStatistics i = authors.get(p);
      if (i == null) {
        authors.put(p, new MutableCommitStatistics(commits.size()));
      } else {
        i.increment();
      }
    }

    // TODO: use logger for test output.
    merge(authors);
    considerCommitLabel();

    DeveloperClassifier dc = new DeveloperClassifier(sumOfFixes, sumOfCommits);
    dc.classify(identities);
  }

  private void merge(Map<People, MutableCommitStatistics> authors) {
    Set<People> people = Sets.newHashSet();
    for (People p : authors.keySet()) {
      people.add(p.clone());
    }

    while (people.size() > 0) {
      var first = people.iterator().next();
      var l1 = createLabel(first);
      people.remove(first);

      List<People> match = Lists.newArrayList();
      match.add(first);
      for (Iterator<People> i = people.iterator(); i.hasNext(); ) {
        var p = i.next();
        var l2 = createLabel(p);
        if (compareLabel(l1, l2)) {
          match.add(p);
          i.remove();
        }
      }

      // create identity object
      Identity id = new Identity();
      int numberOfCommits = 0;
      double percent = .0;
      String name = "";
      for (People p : match) {
        numberOfCommits += authors.get(p).get();
        percent += authors.get(p).percent();
        if (name.length() < p.getName().length()) {
          name = p.getName();
        }
        id.getPeople().add(p.getId());
        identityMap.put(p.getId(), id);
      }
      id.setName(name);
      id.setNumberOfCommits(numberOfCommits);
      id.setPercent(percent);
      identities.add(id);
    }
  }

  private void considerCommitLabel() {
    List<Commit> commits = DatabaseContext.getInstance().getAllCommits();

    for (Commit c : commits) {
      var id = identityMap.get(c.getAuthorId());
      if (c.getLabels() != null) {
        if (c.getLabels().get("adjustedszz_bugfix") || c.getLabels().get("issueonly_bugfix")) {
          sumOfFixes++;
          id.incrementNumberOfFixes();
        }
        // TODO: consider more labels
      }
    }
  }

  private List<String> createLabel(People p) {
    List<String> label = Lists.newArrayList();
    List<String> ignoreEmailPrefixes = Lists.newArrayList(
        "mail", "dev_null", "dev-null", "noreply", "github"
    );
    List<String> ignoreNames = Lists.newArrayList(
        "unknown"
    );

    // consider username
    if (p.getUsername() != null) {
      label.add(p.getUsername().toLowerCase().trim());
    }

    // consider email prefix
    var emailPrefix = p.getEmail().split("@")[0].trim().toLowerCase();
    if (!ignoreEmailPrefixes.contains(emailPrefix)) {
      label.add(emailPrefix);
    }

    // consider name
    if (p.getName() != null && !ignoreNames.contains(p.getName())) { // TODO: consider checking ignores for split values
      if (p.getName().contains(".") && !p.getName().contains(" ")) {
        var splitDot = p.getName().split("\\.");
        if (splitDot.length == 2) { // one default case
          label.add(splitDot[0].trim().toLowerCase() + splitDot[1].trim().toLowerCase());
          label.add(splitDot[1].trim().toLowerCase() + splitDot[0].trim().toLowerCase());
          label.add(splitDot[0].trim().toLowerCase().substring(0, 0) + splitDot[1].trim().toLowerCase());
          label.add(splitDot[1].trim().toLowerCase() + splitDot[0].trim().toLowerCase().substring(0, 0));
        }
      } else {
        var splitSpace = p.getName().split(" ");
        if (splitSpace.length == 0) {
          label.add(splitSpace[0].trim().toLowerCase());
        } else if (splitSpace.length == 1) {
          label.add(splitSpace[0].trim().toLowerCase());
        } else if (splitSpace.length == 2) {
          label.add(splitSpace[0].trim().toLowerCase() + splitSpace[1].trim().toLowerCase());
          label.add(splitSpace[0].trim().toLowerCase() + "." + splitSpace[1].trim().toLowerCase());
          label.add(splitSpace[1].trim().toLowerCase() + splitSpace[0].trim().toLowerCase());
          label.add(splitSpace[1].trim().toLowerCase() + "." + splitSpace[0].trim().toLowerCase());
          label.add(splitSpace[0].trim().toLowerCase().substring(0, 1) + splitSpace[1].trim().toLowerCase());
        } else if (splitSpace.length == 3) {
          label.add(splitSpace[0].trim().toLowerCase() + "." + splitSpace[2].trim().toLowerCase());
          label.add(splitSpace[1].trim().toLowerCase() + "." + splitSpace[2].trim().toLowerCase());
          label.add(splitSpace[0].trim().toLowerCase() + "." + splitSpace[1].trim().toLowerCase() + splitSpace[2].trim().toLowerCase());
          label.add(splitSpace[0].trim().toLowerCase() + splitSpace[1].trim().toLowerCase() + "." + splitSpace[2].trim().toLowerCase());
          label.add(splitSpace[0].trim().toLowerCase() + "." + splitSpace[1].trim().toLowerCase() + "." + splitSpace[2].trim().toLowerCase());
          label.add(splitSpace[0].trim().toLowerCase().substring(0, 1) + splitSpace[2].trim().toLowerCase());
          label.add(splitSpace[1].trim().toLowerCase().substring(0, 1) + splitSpace[2].trim().toLowerCase());
          label.add(splitSpace[0].trim().toLowerCase().substring(0, 1) + splitSpace[1].trim().toLowerCase().substring(0, 1) + splitSpace[2].trim().toLowerCase());
        } else {
          // TODO: do or log something...
        }
      }

    }

    return label;
  }

  private boolean compareLabel(List<String> label1, List<String> label2) {
    for (String l1 : label1) {
      for (String l2 : label2) {
        if (l1.length() > 3 && l1.equals(l2)) {
          return true;
        }
      }
    }
    return false;
  }

  private void printAuthors(Map<People, MutableCommitStatistics> authors) {
    Map<People, MutableCommitStatistics> sortedAuthors = Common.sortByValue(authors, true);
    StringBuilder authorInfo = new StringBuilder();

    for (Map.Entry<People, MutableCommitStatistics> entry : sortedAuthors.entrySet()) {
      authorInfo.append(entry.getKey().getName());
      authorInfo.append('\t');
      authorInfo.append(entry.getKey().getUsername());
      authorInfo.append('\t');
      authorInfo.append(entry.getKey().getEmail());
      authorInfo.append('\t');
      authorInfo.append(String.valueOf(entry.getValue().get()));
      authorInfo.append(" -> ");
      authorInfo.append(String.valueOf(String.valueOf(entry.getValue().percent())));
      authorInfo.append('\n');
    }
    authorInfo.append('\n');

    System.out.println(authorInfo.toString());
  }

  public void printPersonData() {
    PersonData data = new PersonData(identities);
    data.print();
  }

  public PersonData getPersonData() {
    return new PersonData(identities);
  }

  public void printIdentities() {
    StringBuilder authorInfo = new StringBuilder();

    Collections.sort(identities);
    Collections.reverse(identities);

    System.out.println("Sum of commits: " + getSumOfCommits());
    System.out.println("Sum of fixes: " + getSumOfFixes());

    for (Identity id : identities) {
      authorInfo.append(id.getName());
      authorInfo.append('\t');
      authorInfo.append(" NoC: ");
      authorInfo.append(id.getNumberOfCommits());
      authorInfo.append(" -> ");
      authorInfo.append(id.getPercent());
      authorInfo.append('\t');
      authorInfo.append(" Fixes: ");
      authorInfo.append(id.getNumberOfFixes());

      authorInfo.append('\t');
      authorInfo.append(" Tests: ");
      authorInfo.append(id.getNumberOfTests());
      authorInfo.append('\t');
      authorInfo.append(" Features: ");
      authorInfo.append(id.getNumberOfFeatures());
      authorInfo.append('\t');
      authorInfo.append(" Maintenance: ");
      authorInfo.append(id.getNumberOfMaintenance());
      authorInfo.append('\t');
      authorInfo.append(" Refactoring: ");
      authorInfo.append(id.getNumberOfRefactorings());
      authorInfo.append('\t');
      authorInfo.append(" Documentation: ");
      authorInfo.append(id.getNumberOfDocumentation());

      authorInfo.append('\t');
      authorInfo.append(" Type: ");
      authorInfo.append(id.getType());
      authorInfo.append('\t');
      authorInfo.append(" Maintainer: ");
      authorInfo.append(id.isMaintainer());
      authorInfo.append('\t');
      authorInfo.append(" Role: ");
      authorInfo.append(id.getRole());
      authorInfo.append('\n');
      for (ObjectId p : id.getPeople()) {
        var person = ctx.getPeopleById(p);
        authorInfo.append('\t');
        authorInfo.append(person.getName());
        authorInfo.append(" - ");
        authorInfo.append(person.getUsername());
        authorInfo.append(" - ");
        authorInfo.append(person.getEmail());
        authorInfo.append('\n');
      }
    }

    System.out.println(authorInfo.toString());
  }

  public Map<ObjectId, Identity> getIdentityMap() {
    return identityMap;
  }

  public int getSumOfFixes() {
    return sumOfFixes;
  }

  public int getSumOfCommits() {
    return sumOfCommits;
  }

  public List<Identity> getIdentities() {
    return identities;
  }
}
