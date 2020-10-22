package de.ugoe.cs.tcs.simparameter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ugoe.cs.tcs.simparameter.buginformation.BugInformationCreator;
import de.ugoe.cs.tcs.simparameter.changecoupling.CCGraphCreator;
import de.ugoe.cs.tcs.simparameter.commitinfo.CommitAnalyzer;
import de.ugoe.cs.tcs.simparameter.commitinfo.CommitsPerDevType;
import de.ugoe.cs.tcs.simparameter.export.CoreData;
import de.ugoe.cs.tcs.simparameter.persons.*;
import de.ugoe.cs.tcs.simparameter.refinfo.RefactoringAnalyzer;
import de.ugoe.cs.tcs.simparameter.softgraph.SnapshotBuilder;
import de.ugoe.cs.tcs.simparameter.util.Common;
import de.ugoe.cs.tcs.simparameter.util.DatabaseContext;
import de.ugoe.cs.tcs.simparameter.util.Parameter;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;


public class SimParameter {

  public static void main(String[] args) {
    Parameter param = Parameter.getInstance();
    param.init(args);

    setLogLevel();

    DatabaseContext ctx = DatabaseContext.getInstance();

    //create output folder
    File f = new File(param.getOutputFolder());
    if (!f.exists()) {
      //noinspection ResultOfMethodCallIgnored
      f.mkdir();
    }

    //TODO: make output options configurable

    CommitsPerDevType cpt = new CommitsPerDevType();
    try {
      cpt.exportCSV();
    } catch (IOException e) {
      e.printStackTrace();
    }

    RefactoringAnalyzer ra = new RefactoringAnalyzer();
    ra.analyze();
    String refData = ra.getAverageResults();
    System.out.println(refData);
    Common.writeTextToFile(refData, "output/" + ctx.getProject().getName() + "_refData.txt");
    
    BugInformationCreator bic = new BugInformationCreator();
    bic.printInfo();
    bic.printBugsPerMonth();
    bic.printInfoPerYear();

    PersonInformation pi = PersonInformation.getInstance();
    pi.printIdentities();
    pi.printPersonData();

    CCGraphCreator cc = new CCGraphCreator();
    cc.create(param.getCcExportCommits());
    cc.printFileInfo();
    cc.printPackageInfo();

    SnapshotBuilder sb = new SnapshotBuilder();
    sb.createSnapshots();

    CommitAnalyzer ca = new CommitAnalyzer(cc.getProjectSize());
    ca.print();
    ca.exportCommitGrowth();

    // Export
    CoreData cd = new CoreData();
    cd.setMaxFiles(cc.getGraph().vertexSet().size()); // size at the end
    cd.setExportPackages(cc.getExportPackages());

    PersonData pData = pi.getPersonData();
    cd.setKeyDeveloper(pData.numOfKeyDevelopers());
    cd.setMajorDeveloper(pData.numOfMajorDevelopers());
    cd.setMinorDeveloper(pData.numOfMinorDevelopers());
    cd.setCoreDeveloper(pData.numOfCoreDevelopers());
    cd.setPeripheralDeveloper(pData.numOfPeripheralDevelopers());
    cd.setKeyDeveloperMaintainer(pData.numOfKeyDevelopersM());
    cd.setMajorDeveloperMaintainer(pData.numOfMajorDevelopersM());
    cd.setMinorDeveloperMaintainer(pData.numOfMinorDevelopersM());
    cd.setCoreDeveloperMaintainer(pData.numOfCoreDevelopersM());
    cd.setPeripheralDeveloperMaintainer(pData.numOfPeripheralDevelopersM());

    cd.setIdentities(pi.getIdentities());

    int numberOfCommitsKeyType = 0;
    int numberOfCommitsMajorType = 0;
    int numberOfCommitsMinorType = 0;
    int numberOfCommitsCoreRole = 0;
    int numberOfCommitsPeripheralRole = 0;
    int numberOfFixesKeyType = 0;
    int numberOfFixesMajorType = 0;
    int numberOfFixesMinorType = 0;
    int numberOfFixesCoreRole = 0;
    int numberOfFixesPeripheralRole = 0;

    for (Identity i : pi.getIdentities()) {
      if (i.getType().equals(DeveloperType.key)) {
        numberOfCommitsKeyType += i.getNumberOfCommits();
        numberOfFixesKeyType += i.getNumberOfFixes();
      }
      if (i.getType().equals(DeveloperType.major)) {
        numberOfCommitsMajorType += i.getNumberOfCommits();
        numberOfFixesMajorType += i.getNumberOfFixes();
      }
      if (i.getType().equals(DeveloperType.minor)) {
        numberOfCommitsMinorType += i.getNumberOfCommits();
        numberOfFixesMinorType += i.getNumberOfFixes();
      }
      if (i.getRole().equals(DeveloperRole.core)) {
        numberOfCommitsCoreRole += i.getNumberOfCommits();
        numberOfFixesCoreRole += i.getNumberOfFixes();
      }
      if (i.getRole().equals(DeveloperRole.peripheral)) {
        numberOfCommitsPeripheralRole += i.getNumberOfCommits();
        numberOfFixesPeripheralRole += i.getNumberOfFixes();
      }
    }
    cd.setKeyDeveloperCommits(numberOfCommitsKeyType);
    cd.setMajorDeveloperCommits(numberOfCommitsMajorType);
    cd.setMinorDeveloperCommits(numberOfCommitsMinorType);
    cd.setCoreDeveloperCommits(numberOfCommitsCoreRole);
    cd.setPeripheralDeveloperCommits(numberOfCommitsPeripheralRole);

    cd.setKeyDeveloperFixes(numberOfFixesKeyType);
    cd.setMajorDeveloperFixes(numberOfFixesMajorType);
    cd.setMinorDeveloperFixes(numberOfFixesMinorType);
    cd.setCoreDeveloperFixes(numberOfFixesCoreRole);
    cd.setPeripheralDeveloperFixes(numberOfFixesPeripheralRole);

    cd.setNumberOfAverageCommits(ca.getAverageCommit().length());
    cd.setpAverageCommitAdd(ca.getAverageCommit().pAdd());
    cd.setpAverageCommitDelete(ca.getAverageCommit().pRemove());
    cd.setpAverageCommitUpdate(ca.getAverageCommit().pChange());

    cd.setNumberOfInitialCommits(ca.getInitialCommit().length());
    cd.setpInitialCommitAdd(ca.getInitialCommit().pAdd());
    cd.setpInitialCommitDelete(ca.getInitialCommit().pRemove());
    cd.setpInitialCommitUpdate(ca.getInitialCommit().pChange());

    cd.setNumberOfDevelopmentCommits((ca.getDevelopmentCommit().length()));
    cd.setpDevelopmentCommitAdd(ca.getDevelopmentCommit().pAdd());
    cd.setpDevelopmentCommitDelete(ca.getDevelopmentCommit().pRemove());
    cd.setpDevelopmentCommitUpdate(ca.getDevelopmentCommit().pChange());

    Date first = ctx.getFirstCommitDate();
    Date last = ctx.getLastCommitDate();
    cd.setFirstCommitDate(first);
    cd.setLastCommitDate(last);
    cd.setMonthToSimulate(Common.computeMonthBetweenDates(first, last));
    cd.setRoundsToSimulate(Common.computeDaysBetweenDates(first, last));
    cd.setInitialCommits(Parameter.getInstance().getSizeGrowthPhase());

    cd.setIssueInformationComplete(bic.createBugCountComplete());
    cd.setIssueInformationCompleteFixed(bic.createBugCountCompleteFixed());
    cd.setIssueInformationYearly(bic.createBugCountYearly());

    try {
      Common.writeProjectSizeToFile(cc.getProjectSize(), "output/" + ctx.getProject().getName() + "_size_distribution.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Write export
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      objectMapper.writeValue(new File("output/" + ctx.getProject().getName() + "_data.json"), cd);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void setLogLevel() {
    Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    String level = Parameter.getInstance().getDebugLevel();

    switch (level) {
      case "INFO":
        root.setLevel(Level.INFO);
        break;
      case "WARNING":
        root.setLevel(Level.WARN);
        break;
      case "ERROR":
        root.setLevel(Level.ERROR);
        break;
      default:
        root.setLevel(Level.DEBUG);
        break;
    }
  }

}
