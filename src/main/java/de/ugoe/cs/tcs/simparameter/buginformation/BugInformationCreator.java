package de.ugoe.cs.tcs.simparameter.buginformation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.ugoe.cs.tcs.simparameter.model.Issue;
import de.ugoe.cs.tcs.simparameter.util.Common;
import de.ugoe.cs.tcs.simparameter.util.DatabaseContext;
import org.apache.commons.math3.util.Precision;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class BugInformationCreator {
  private Map<Integer, BugPriorityMap> yearIssuePriorityMap;
  private BugPriorityMap issuePriorityMap;
  private BugPriorityMap issuePriorityMapFixed;
  private DatabaseContext ctx;
  private final List<String> keyWordsBugFix;

  public BugInformationCreator() {
    this.ctx = DatabaseContext.getInstance();
    this.yearIssuePriorityMap = Maps.newHashMap();
    this.issuePriorityMap = new BugPriorityMap();
    this.issuePriorityMapFixed = new BugPriorityMap();
    this.keyWordsBugFix = Lists.newArrayList("resolved", "closed");
    init();
  }

  private void init() {
    for (Issue i : ctx.getAllIssues()) {
      issuePriorityMap.insert(i);
      if (keyWordsBugFix.contains(i.getStatus().toLowerCase().trim())) {
        issuePriorityMapFixed.insert(i);
      }
      int year = Common.getYear(i.getCreatedAt());
      if (yearIssuePriorityMap.containsKey(year)) {
        yearIssuePriorityMap.get(year).insert(i);
      } else {
        BugPriorityMap bpm = new BugPriorityMap();
        bpm.insert(i);
        yearIssuePriorityMap.put(year, bpm);
      }
    }
  }

  public void printInfo() {
    System.out.println("-------- CREATED BUGS --------");
    issuePriorityMap.printInfo(false);
    System.out.println("\n");
    System.out.println("-------- FIXED BUGS --------");
    issuePriorityMapFixed.printInfo(false);
    System.out.println("\n");
  }

  public void printBugsPerMonth() {
    Date d1 = ctx.getFirstCommitDate();
    Date d2 = ctx.getLastCommitDate();
    long month = Common.computeMonthBetweenDates(d1, d2);

    for (BugPriority p : issuePriorityMap.keySet()) {
      System.out.println("Priority " + p + " occur " + (int) Precision.round(issuePriorityMap.get(p).size() *1.0 / month, 0) + " times per month.");
    }
  }

  public void printInfoPerYear() {
    int startYear = Common.getYear(ctx.getFirstCommitDate());
    int endYear = Common.getYear(ctx.getLastCommitDate());

    for (int i = startYear; i <= endYear; i++) {
      System.out.println("------------------------------------------------");
      System.out.println("Bugs created in " + i);
      System.out.println("------------------------------------------------");
      if (yearIssuePriorityMap.containsKey(i)) {
        yearIssuePriorityMap.get(i).printInfo(false);
      } else {
        System.out.println("No entries found.");
      }
      System.out.println('\n');
    }
  }

  public Map<BugPriority, Integer> createBugCountComplete() {
    Map<BugPriority, Integer> res = Maps.newHashMap();

    for (var k : issuePriorityMap.keySet()) {
      res.put(k, issuePriorityMap.get(k).size());
    }

    return res;
  }

  public Map<BugPriority, Integer> createBugCountCompleteFixed() {
    Map<BugPriority, Integer> res = Maps.newHashMap();

    for (var k : issuePriorityMapFixed.keySet()) {
      res.put(k, issuePriorityMapFixed.get(k).size());
    }

    return res;
  }

  public Map<Integer, Map<BugPriority, Integer>> createBugCountYearly() {
    Map<Integer, Map<BugPriority, Integer>> res = Maps.newHashMap();

    for (var k : yearIssuePriorityMap.keySet()) {
      Map<BugPriority, Integer> inner = Maps.newHashMap();
      for (var ki : yearIssuePriorityMap.get(k).keySet()) {
        inner.put(ki, yearIssuePriorityMap.get(k).get(ki).size());
      }
      res.put(k, inner);
    }

    return res;
  }
}
