package de.ugoe.cs.tcs.simparameter.commitinfo;

import com.google.common.collect.Maps;
import de.ugoe.cs.tcs.simparameter.model.Commit;
import de.ugoe.cs.tcs.simparameter.model.File;
import de.ugoe.cs.tcs.simparameter.model.FileAction;
import de.ugoe.cs.tcs.simparameter.util.Common;
import de.ugoe.cs.tcs.simparameter.util.DatabaseContext;
import de.ugoe.cs.tcs.simparameter.util.Parameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CommitAnalyzer {
  private static final int MAX_COMMIT_SIZE = 250;
  private static final int COMPARE_SIZE_WITH_X_LAST_COMMITS = 1;
  private final Map<String, Number> projectSize;
  private final Map<String, Number> commitGrowth;
  //public static final int SIZE_GROWTH_PHASE = 322;
  private int maxInitSize;

  private CommitTypeInformation bugfix;
  //private CommitTypeInformation pureBugfix;
  //private CommitTypeInformation pureFeatureAdd;
  //private CommitTypeInformation featureAdd;
  //private CommitTypeInformation fixAndFeature;
  private CommitTypeInformation averageCommit;

  private CommitTypeInformation initialCommit;
  private CommitTypeInformation developmentCommit;

  // TODO: consider new labels

  public CommitAnalyzer(Map<String, Number> projectSize) {
    this.projectSize = projectSize;
    this.commitGrowth = Maps.newLinkedHashMap();
    init();
  }

  private void init() {
    bugfix = new CommitTypeInformation();
    averageCommit = new CommitTypeInformation();
    initialCommit = new CommitTypeInformation();
    developmentCommit = new CommitTypeInformation();
    analyze();
  }

  private void analyze() {
    //TODO: get sorted commit list from database
    List<Commit> commits = DatabaseContext.getInstance().getAllCommits();
    commits.sort(Comparator.comparing(Commit::getAuthorDate));
    int analyzed = 0;

    for (Commit c : commits) {
      if (c.getLabels() != null) {
        if (c.getLabels().get("validated_bugfix") != null && c.getLabels().get("validated_bugfix")) {
          updateCommitInfo(c, bugfix);
        }
      }
      updateCommitInfo(c, averageCommit);
      analyzed++;

      // call only one time to find size of the development phase
      if (!Parameter.getInstance().isDeterminedGrowthPhase()) {
        computeCommitGrowth(c);
      }

      if (analyzed > Parameter.getInstance().getSizeGrowthPhase()) {
        updateCommitInfo(c, developmentCommit);
      } else {
        updateCommitInfo(c, initialCommit);
      }
    }
  }


  private void updateCommitInfo(Commit c, CommitTypeInformation cti) {
    List<FileAction> actions = DatabaseContext.getInstance().getFileActions(c);
    int addedFiles = 0;
    int removedFiles = 0;
    int changedFiles = 0;
    if (actions.size() <= MAX_COMMIT_SIZE) {
      for (FileAction a : actions) {
        File file = DatabaseContext.getInstance().getFile(a.getFileId());
        if (file.getPath().endsWith(".java")) {
          if (a.getMode().equals("M")) {
            changedFiles++;
          } else if (a.getMode().equals("A")) {
            addedFiles++;
          } else if (a.getMode().equals("D")) {
            removedFiles++;
          }
        }
      }
      cti.addAddedFiles(addedFiles);
      cti.addRemovedFiles(removedFiles);
      cti.addChangedFiles(changedFiles);
    }
  }

  public void print() {
    StringBuilder output = new StringBuilder();
    output.append("Commit type information: " + '\n');
    output.append("--------------------------------" + '\n');
    output.append("Found " + averageCommit.length() + " average commits with average behavior: " + '\n');
    output.append(averageOutput(averageCommit));
    output.append("Found " + bugfix.length() + " fixes with average behavior: " + '\n');
    output.append(averageOutput(bugfix));
    System.out.println(output.toString());
  }

  private String averageOutput(CommitTypeInformation cti) {
    StringBuilder average = new StringBuilder();
    average.append('\t');
    average.append("Modified: ");
    average.append(cti.pChange());
    average.append(" Added: ");
    average.append(cti.pAdd());
    average.append(" Deleted: ");
    average.append(cti.pRemove());
    average.append('\n');
    return average.toString();
  }

  public CommitTypeInformation getBugfix() {
    return bugfix;
  }

  public CommitTypeInformation getAverageCommit() {
    return averageCommit;
  }

  public CommitTypeInformation getInitialCommit() {
    return initialCommit;
  }

  public CommitTypeInformation getDevelopmentCommit() {
    return developmentCommit;
  }

  private void computeCommitGrowth(Commit c) {
    Map.Entry<String, Number> entry = projectSize.entrySet().stream().filter(x -> x.getKey().equals(c.getRevisionHash())).findFirst().get();
    List<Map.Entry<String, Number>> list = new ArrayList<>(projectSize.entrySet());
    int idx = list.indexOf(entry);
    int size = (int) list.get(idx).getValue();
    int sum = 0;
    double averageLastSize;

    for (int i = 1; i <= COMPARE_SIZE_WITH_X_LAST_COMMITS; i++) {
      try {
        sum += (int) list.get(idx - i).getValue();
      } catch (Exception e) {
        sum = 0;
        break;
      }
    }
    if (sum > 0) {
      averageLastSize = sum / COMPARE_SIZE_WITH_X_LAST_COMMITS * 1.0;
      double percent = size * 100.0 / (averageLastSize * 1.0);
      //System.out.println(percent);
      commitGrowth.put(c.getRevisionHash(), percent);
    }
  }

  public void exportCommitGrowth() {
    if (Parameter.getInstance().isDeterminedGrowthPhase()) {
      return;
    }

    try {
      Common.writeProjectSizeToFile(commitGrowth, "output/" + DatabaseContext.getInstance().getProject().getName() + "_commit_growth.txt");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
