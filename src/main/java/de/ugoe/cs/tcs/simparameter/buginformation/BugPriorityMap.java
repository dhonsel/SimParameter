package de.ugoe.cs.tcs.simparameter.buginformation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.ugoe.cs.tcs.simparameter.model.Issue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BugPriorityMap extends HashMap<BugPriority, List<Issue>> {
  private final Map<BugPriority, List<String>> keywords;
  private final List<String> keywordDuplicate;

  public BugPriorityMap() {
    this.keywordDuplicate = Lists.newArrayList("duplicate", "duplicated");
    this.keywords = Maps.newHashMap();
    this.keywords.put(BugPriority.CRITICAL, Lists.newArrayList("blocker", "critical"));
    this.keywords.put(BugPriority.MAJOR, Lists.newArrayList("major", "normal"));
    this.keywords.put(BugPriority.MINOR, Lists.newArrayList("minor", "trivial"));
    this.keywords.put(BugPriority.NONE, Lists.newArrayList("null"));
    this.put(BugPriority.CRITICAL, Lists.newArrayList());
    this.put(BugPriority.MAJOR, Lists.newArrayList());
    this.put(BugPriority.MINOR, Lists.newArrayList());
    this.put(BugPriority.NONE, Lists.newArrayList());
  }

  public void insert(Issue i) {
    String prio = i.getPriority() == null ? "null" : i.getPriority().toLowerCase().trim();
    String dupl = i.getResolution() == null ? "null" : i.getResolution().toLowerCase().trim();
    for (BugPriority p : keywords.keySet()) {
      if (keywords.get(p).contains(prio) && !keywordDuplicate.contains(dupl)) {
        this.get(p).add(i);
      }
    }
  }

  public void printInfo(boolean title) {
    for (BugPriority p : this.keySet()) {
      System.out.println("Priority " + p + " contains " + this.get(p).size() + " issues.");
      if (title) {
        this.get(p).forEach(x -> System.out.println('\t' + x.getTitle()));
      }
    }
  }

}
