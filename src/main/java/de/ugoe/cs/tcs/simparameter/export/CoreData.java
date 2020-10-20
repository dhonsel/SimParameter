package de.ugoe.cs.tcs.simparameter.export;

import de.ugoe.cs.tcs.simparameter.buginformation.BugPriority;
import de.ugoe.cs.tcs.simparameter.persons.Identity;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CoreData {
  // files and commits
  private int maxFiles;
  private long numberOfAverageCommits;
  private double pAverageCommitUpdate;
  private double pAverageCommitDelete;
  private double pAverageCommitAdd;

  private long numberOfInitialCommits;
  private double pInitialCommitUpdate;
  private double pInitialCommitDelete;
  private double pInitialCommitAdd;

  private long numberOfDevelopmentCommits;
  private double pDevelopmentCommitUpdate;
  private double pDevelopmentCommitDelete;
  private double pDevelopmentCommitAdd;

  private Date firstCommitDate;
  private Date lastCommitDate;
  private long monthToSimulate;
  private long roundsToSimulate;
  private long initialCommits;


  // developer
  private int keyDeveloper;
  private int keyDeveloperCommits;
  private int keyDeveloperFixes;
  private int keyDeveloperMaintainer;
  private int majorDeveloper;
  private int majorDeveloperCommits;
  private int majorDeveloperFixes;
  private int majorDeveloperMaintainer;
  private int minorDeveloper;
  private int minorDeveloperCommits;
  private int minorDeveloperFixes;
  private int minorDeveloperMaintainer;

  private int peripheralDeveloper;
  private int peripheralDeveloperCommits;
  private int peripheralDeveloperFixes;
  private int peripheralDeveloperMaintainer;
  private int coreDeveloper;
  private int coreDeveloperCommits;
  private int coreDeveloperFixes;
  private int coreDeveloperMaintainer;

  private Map<BugPriority, Integer> issueInformationComplete;
  private Map<BugPriority, Integer> issueInformationCompleteFixed;
  private Map<Integer, Map<BugPriority, Integer>> issueInformationYearly;

  private List<ExportPackage> exportPackages;

  private List<Identity> identities;

  public int getMaxFiles() {
    return maxFiles;
  }

  public void setMaxFiles(int maxFiles) {
    this.maxFiles = maxFiles;
  }

  public int getKeyDeveloper() {
    return keyDeveloper;
  }

  public void setKeyDeveloper(int keyDeveloper) {
    this.keyDeveloper = keyDeveloper;
  }

  public int getMajorDeveloper() {
    return majorDeveloper;
  }

  public void setMajorDeveloper(int majorDeveloper) {
    this.majorDeveloper = majorDeveloper;
  }

  public int getMinorDeveloper() {
    return minorDeveloper;
  }

  public void setMinorDeveloper(int minorDeveloper) {
    this.minorDeveloper = minorDeveloper;
  }

  public int getPeripheralDeveloper() {
    return peripheralDeveloper;
  }

  public void setPeripheralDeveloper(int peripheralDeveloper) {
    this.peripheralDeveloper = peripheralDeveloper;
  }

  public int getCoreDeveloper() {
    return coreDeveloper;
  }

  public void setCoreDeveloper(int coreDeveloper) {
    this.coreDeveloper = coreDeveloper;
  }

  public int getKeyDeveloperMaintainer() {
    return keyDeveloperMaintainer;
  }

  public void setKeyDeveloperMaintainer(int keyDeveloperMaintainer) {
    this.keyDeveloperMaintainer = keyDeveloperMaintainer;
  }

  public int getMajorDeveloperMaintainer() {
    return majorDeveloperMaintainer;
  }

  public void setMajorDeveloperMaintainer(int majorDeveloperMaintainer) {
    this.majorDeveloperMaintainer = majorDeveloperMaintainer;
  }

  public int getMinorDeveloperMaintainer() {
    return minorDeveloperMaintainer;
  }

  public void setMinorDeveloperMaintainer(int minorDeveloperMaintainer) {
    this.minorDeveloperMaintainer = minorDeveloperMaintainer;
  }

  public int getPeripheralDeveloperMaintainer() {
    return peripheralDeveloperMaintainer;
  }

  public void setPeripheralDeveloperMaintainer(int peripheralDeveloperMaintainer) {
    this.peripheralDeveloperMaintainer = peripheralDeveloperMaintainer;
  }

  public int getCoreDeveloperMaintainer() {
    return coreDeveloperMaintainer;
  }

  public void setCoreDeveloperMaintainer(int coreDeveloperMaintainer) {
    this.coreDeveloperMaintainer = coreDeveloperMaintainer;
  }

  public long getNumberOfAverageCommits() {
    return numberOfAverageCommits;
  }

  public void setNumberOfAverageCommits(long numberOfAverageCommits) {
    this.numberOfAverageCommits = numberOfAverageCommits;
  }

  public double getpAverageCommitUpdate() {
    return pAverageCommitUpdate;
  }

  public void setpAverageCommitUpdate(double pAverageCommitUpdate) {
    this.pAverageCommitUpdate = pAverageCommitUpdate;
  }

  public double getpAverageCommitDelete() {
    return pAverageCommitDelete;
  }

  public void setpAverageCommitDelete(double pAverageCommitDelete) {
    this.pAverageCommitDelete = pAverageCommitDelete;
  }

  public double getpAverageCommitAdd() {
    return pAverageCommitAdd;
  }

  public void setpAverageCommitAdd(double pAverageCommitAdd) {
    this.pAverageCommitAdd = pAverageCommitAdd;
  }

  public Date getFirstCommitDate() {
    return firstCommitDate;
  }

  public void setFirstCommitDate(Date firstCommitDate) {
    this.firstCommitDate = firstCommitDate;
  }

  public Date getLastCommitDate() {
    return lastCommitDate;
  }

  public void setLastCommitDate(Date lastCommitDate) {
    this.lastCommitDate = lastCommitDate;
  }

  public long getMonthToSimulate() {
    return monthToSimulate;
  }

  public void setMonthToSimulate(long monthToSimulate) {
    this.monthToSimulate = monthToSimulate;
  }

  public long getRoundsToSimulate() {
    return roundsToSimulate;
  }

  public void setRoundsToSimulate(long roundsToSimulate) {
    this.roundsToSimulate = roundsToSimulate;
  }

  public int getKeyDeveloperCommits() {
    return keyDeveloperCommits;
  }

  public void setKeyDeveloperCommits(int keyDeveloperCommits) {
    this.keyDeveloperCommits = keyDeveloperCommits;
  }

  public int getMajorDeveloperCommits() {
    return majorDeveloperCommits;
  }

  public void setMajorDeveloperCommits(int majorDeveloperCommits) {
    this.majorDeveloperCommits = majorDeveloperCommits;
  }

  public int getMinorDeveloperCommits() {
    return minorDeveloperCommits;
  }

  public void setMinorDeveloperCommits(int minorDeveloperCommits) {
    this.minorDeveloperCommits = minorDeveloperCommits;
  }

  public int getPeripheralDeveloperCommits() {
    return peripheralDeveloperCommits;
  }

  public void setPeripheralDeveloperCommits(int peripheralDeveloperCommits) {
    this.peripheralDeveloperCommits = peripheralDeveloperCommits;
  }

  public int getCoreDeveloperCommits() {
    return coreDeveloperCommits;
  }

  public void setCoreDeveloperCommits(int coreDeveloperCommits) {
    this.coreDeveloperCommits = coreDeveloperCommits;
  }


  public int getKeyDeveloperFixes() {
    return keyDeveloperFixes;
  }

  public void setKeyDeveloperFixes(int keyDeveloperFixes) {
    this.keyDeveloperFixes = keyDeveloperFixes;
  }

  public int getMajorDeveloperFixes() {
    return majorDeveloperFixes;
  }

  public void setMajorDeveloperFixes(int majorDeveloperFixes) {
    this.majorDeveloperFixes = majorDeveloperFixes;
  }

  public int getMinorDeveloperFixes() {
    return minorDeveloperFixes;
  }

  public void setMinorDeveloperFixes(int minorDeveloperFixes) {
    this.minorDeveloperFixes = minorDeveloperFixes;
  }

  public int getPeripheralDeveloperFixes() {
    return peripheralDeveloperFixes;
  }

  public void setPeripheralDeveloperFixes(int peripheralDeveloperFixes) {
    this.peripheralDeveloperFixes = peripheralDeveloperFixes;
  }

  public int getCoreDeveloperFixes() {
    return coreDeveloperFixes;
  }

  public void setCoreDeveloperFixes(int coreDeveloperFixes) {
    this.coreDeveloperFixes = coreDeveloperFixes;
  }

  public List<ExportPackage> getExportPackages() {
    return exportPackages;
  }

  public void setExportPackages(List<ExportPackage> exportPackages) {
    this.exportPackages = exportPackages;
  }

  public long getNumberOfInitialCommits() {
    return numberOfInitialCommits;
  }

  public void setNumberOfInitialCommits(long numberOfInitialCommits) {
    this.numberOfInitialCommits = numberOfInitialCommits;
  }

  public double getpInitialCommitUpdate() {
    return pInitialCommitUpdate;
  }

  public void setpInitialCommitUpdate(double pInitialCommitUpdate) {
    this.pInitialCommitUpdate = pInitialCommitUpdate;
  }

  public double getpInitialCommitDelete() {
    return pInitialCommitDelete;
  }

  public void setpInitialCommitDelete(double pInitialCommitDelete) {
    this.pInitialCommitDelete = pInitialCommitDelete;
  }

  public double getpInitialCommitAdd() {
    return pInitialCommitAdd;
  }

  public void setpInitialCommitAdd(double pInitialCommitAdd) {
    this.pInitialCommitAdd = pInitialCommitAdd;
  }

  public long getNumberOfDevelopmentCommits() {
    return numberOfDevelopmentCommits;
  }

  public void setNumberOfDevelopmentCommits(long numberOfDevelopmentCommits) {
    this.numberOfDevelopmentCommits = numberOfDevelopmentCommits;
  }

  public double getpDevelopmentCommitUpdate() {
    return pDevelopmentCommitUpdate;
  }

  public void setpDevelopmentCommitUpdate(double pDevelopmentCommitUpdate) {
    this.pDevelopmentCommitUpdate = pDevelopmentCommitUpdate;
  }

  public double getpDevelopmentCommitDelete() {
    return pDevelopmentCommitDelete;
  }

  public void setpDevelopmentCommitDelete(double pDevelopmentCommitDelete) {
    this.pDevelopmentCommitDelete = pDevelopmentCommitDelete;
  }

  public double getpDevelopmentCommitAdd() {
    return pDevelopmentCommitAdd;
  }

  public void setpDevelopmentCommitAdd(double pDevelopmentCommitAdd) {
    this.pDevelopmentCommitAdd = pDevelopmentCommitAdd;
  }

  public long getInitialCommits() {
    return initialCommits;
  }

  public void setInitialCommits(long initialCommits) {
    this.initialCommits = initialCommits;
  }

  public List<Identity> getIdentities() {
    return identities;
  }

  public void setIdentities(List<Identity> identities) {
    this.identities = identities;
  }

  public Map<BugPriority, Integer> getIssueInformationComplete() {
    return issueInformationComplete;
  }

  public void setIssueInformationComplete(Map<BugPriority, Integer> issueInformationComplete) {
    this.issueInformationComplete = issueInformationComplete;
  }

  public Map<Integer, Map<BugPriority, Integer>> getIssueInformationYearly() {
    return issueInformationYearly;
  }

  public void setIssueInformationYearly(Map<Integer, Map<BugPriority, Integer>> issueInformationYearly) {
    this.issueInformationYearly = issueInformationYearly;
  }

  public Map<BugPriority, Integer> getIssueInformationCompleteFixed() {
    return issueInformationCompleteFixed;
  }

  public void setIssueInformationCompleteFixed(Map<BugPriority, Integer> issueInformationCompleteFixed) {
    this.issueInformationCompleteFixed = issueInformationCompleteFixed;
  }
}
