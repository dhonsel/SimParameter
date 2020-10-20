package de.ugoe.cs.tcs.simparameter.changecoupling;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.ugoe.cs.tcs.simparameter.export.ExportPackage;
import de.ugoe.cs.tcs.simparameter.model.File;
import de.ugoe.cs.tcs.simparameter.model.FileAction;
import de.ugoe.cs.tcs.simparameter.model.SmallCommit;
import de.ugoe.cs.tcs.simparameter.util.Common;
import de.ugoe.cs.tcs.simparameter.util.DatabaseContext;
import de.ugoe.cs.tcs.simparameter.util.MutableInt;
import de.ugoe.cs.tcs.simparameter.util.Parameter;
import org.bson.types.ObjectId;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class CCGraphCreator {
  private Logger logger = (Logger) LoggerFactory.getLogger("de.ugoe.cs.tcs.simparameter.changecoupling.CCGraphCreator");
  private Graph<CCFile, CCEdge> graph;
  private static final int MAX_COMMIT_SIZE = 25;
  private Map<ObjectId, ObjectId> renamedFiles;
  private Map<String, MutableInt> packages;
  private List<ExportPackage> exportPackages;
  private DatabaseContext ctx;
  private Map<String, Number> projectSize;


  public CCGraphCreator() {
    graph = new SimpleWeightedGraph<>(CCEdge.class);
    renamedFiles = Maps.newHashMap();
    packages = Maps.newHashMap();
    exportPackages = Lists.newArrayList();
    projectSize = Maps.newLinkedHashMap();
    ctx = DatabaseContext.getInstance();
  }

  public void create(List<String> commitsToExport) {
    renamedFiles.clear();

    //TODO: consider problems (memory) with large projects
    //TODO: get sorted commit list from database
    List<SmallCommit> commits = ctx.getAllSmallCommits();
    commits.sort(Comparator.comparing(SmallCommit::getAuthorDate));
    if (commits.size() == 0) {
      logger.warn("No commits found!");
      return;
    }

    // create list with renamed files
    logger.info("Creating map of renamed files...");
    for (SmallCommit c : commits) {
      List<FileAction> actions = ctx.getFileActions(c);
      if (actions.size() <= MAX_COMMIT_SIZE) {
        for (FileAction a : actions) {
          File file = ctx.getFile(a.getFileId());
          if (file.getPath().endsWith(".java")) {
            if (a.getMode().equals("R")) {
              renamedFiles.put(a.getFileId(), a.getOldFileId());
            }
          }
        }
      }
    }
    logger.info("Found " + renamedFiles.size() + " renamed files.");

    // create cc graph
    logger.info("Creating change coupling graph...");
    //TODO: change used calendar lib
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(commits.get(0).getAuthorDate());
    int exportYear = calendar.get(Calendar.YEAR);

    int exportNo = 1;
    for (SmallCommit c : commits) {
      List<FileAction> actions = ctx.getFileActions(c);
      List<File> javaFiles = Lists.newArrayList();
      calendar.setTime(c.getAuthorDate());
      int currentYear = calendar.get(Calendar.YEAR);

      // export graph file for every year
      if (currentYear > exportYear) {
        StringBuilder filename = new StringBuilder();
        filename.append("cc_");
        filename.append(ctx.getProject().getName());
        filename.append("_");
        filename.append(String.format("%02d", exportNo++));
        Common.<CCFile, CCEdge>exportDOT(graph, filename.toString());
        Common.<CCFile, CCEdge>exportCSV(graph, filename.toString());
        exportYear = currentYear;
        logger.info("Exported: " + filename.toString());
      }

      // export graph file for given commits
      if (commitsToExport != null && commitsToExport.contains(c.getRevisionHash())) {
        StringBuilder filename = new StringBuilder();
        filename.append("cc_");
        filename.append(ctx.getProject().getName());
        filename.append("_");
        filename.append(c.getRevisionHash());
        Common.<CCFile, CCEdge>exportDOT(graph, filename.toString());
        Common.<CCFile, CCEdge>exportCSV(graph, filename.toString());
        logger.info("Exported: " + filename.toString());
      }

      if (actions.size() <= MAX_COMMIT_SIZE) {
        for (FileAction a : actions) {
          File file = ctx.getFile(a.getFileId());
          if (file.getPath().endsWith(".java") && a.getMode().equals("D")) {
            deleteFile(file);
          } else if (file.getPath().endsWith(".java")) {
            javaFiles.add(file);
          }
        }

        for (File f : javaFiles) {
          updateDistributingDevelopers(f, c.getAuthorId());
        }

        for (int i = 0; i < javaFiles.size(); i++) {
          for (int j = i + 1; j < javaFiles.size(); j++) {
            createOrUpdateEdge(javaFiles.get(i), javaFiles.get(j));
          }
        }
      }

      // size of the graph
      projectSize.put(c.getRevisionHash(), graph.vertexSet().size());

    }
    computeExportPackages();
  }

  public Graph<CCFile, CCEdge> getGraph() {
    return graph;
  }

  public CCFile findFileInGraph(ObjectId file) {
    return graph.vertexSet().stream().filter(x -> x.getId().equals(file)).collect(Common.singletonCollector());
  }

  private void deleteFile(File file) {
    ObjectId id = findRepresentativeFile(file.getId());
    CCFile cc = findFileInGraph(id);

    if (cc != null) {
      graph.removeVertex(cc);
    }
  }

  private void createOrUpdateEdge(File f1, File f2) {
    ObjectId id1 = findRepresentativeFile(f1.getId());
    ObjectId id2 = findRepresentativeFile(f2.getId());
    CCFile cc1 = findFileInGraph(id1);
    CCFile cc2 = findFileInGraph(id2);

    if (cc1 != null && cc2 != null) {
      CCEdge edge = graph.getEdge(cc1, cc2);
      if (edge != null) {
        edge.increaseWeight();
        graph.setEdgeWeight(edge, edge.getWeight());
      } else {
        if (!cc1.equals(cc2)) {
          edge = new CCEdge(cc1, cc2);
          graph.addEdge(cc1, cc2, edge);
          graph.setEdgeWeight(edge, edge.getWeight());
        }
      }
    } else {
      logger.info("Could not find file in cc graph!");
    }
  }

  private void updateDistributingDevelopers(File file, ObjectId personId) {
    ObjectId rep = findRepresentativeFile(file.getId());
    CCFile ccFile = findFileInGraph(rep);
    if (ccFile != null) {
      ccFile.addContribution(personId);
    } else {
      ccFile = createCCFile(rep, personId);
      graph.addVertex(ccFile);
      ccFile.addContribution(personId);
    }
  }

  private CCFile createCCFile(ObjectId fileID, ObjectId creator) {
    File file = ctx.getFile(fileID);
    CCFile ccFile = new CCFile(file.getId(), file.getPath(), creator);

    // set package name and manage package count
    var classOfFile = ctx.getCes(ctx.findAnyClassEntityState(fileID));
    if (classOfFile != null) {
      var splitName = classOfFile.getLongName().split("\\.");
      StringBuilder pName = new StringBuilder();
      int packageLength = splitName.length - 1 <= Parameter.getInstance().getMaxPackageSplitName()
          ? splitName.length - 1 : Parameter.getInstance().getMaxPackageSplitName();
      for (int i = 0; i < packageLength; i++) {
        pName.append(splitName[i]);
        if (i < packageLength - 1) {
          pName.append(".");
        }
      }
      ccFile.setJavaPackage(pName.toString());
      MutableInt p = packages.get(pName.toString());
      if (p == null) {
        packages.put(pName.toString(), new MutableInt());
      } else {
        p.increment();
      }
    } else {
      logger.info("Could not find class, enum, or interface to file: " + file.getId() + " - " + file.getPath());
    }

    return ccFile;
  }

  private ObjectId findRepresentativeFile(ObjectId id) {
    if (renamedFiles.containsKey(id)) {
      return findParent(id);
    } else {
      return id;
    }
  }

  private ObjectId findParent(ObjectId id) {
    ObjectId currentId = id;
    ObjectId parent = renamedFiles.get(currentId);
    Set<ObjectId> parents = Sets.newHashSet(parent);
    if (parent == null) {
      logger.warn("Renamed ID: " + id.toString() + " has no old id!");
      return currentId;
    }

    // second condition prevents endless loop when cyclic dependencies occurs
    // TODO: treat cyclic dependencies somehow else
    while (parent != null && !parents.contains(currentId)) {
      currentId = parent;
      parent = renamedFiles.get(currentId);
      parents.add(parent);
    }

    return currentId;
  }

  public void printFileInfo() {
    graph.vertexSet().stream()
        .sorted((f1, f2) -> Integer.compare(f1.getNumberOfChanges(), f2.getNumberOfChanges()))
        .collect(Collectors.toCollection(LinkedList::new))
        .descendingIterator().forEachRemaining(x -> x.getOwner(true));
  }

  public void printPackageInfo() {
    System.out.println("Package information:");
    var sortedMap = Common.sortByValue(packages, true);
    int sum = 0;
    for (String p : sortedMap.keySet()) {
      System.out.println("\t" + p + ": " + sortedMap.get(p).get());
      sum += sortedMap.get(p).get();
    }
    System.out.println(packages.size() + " packages contain " + sum + " java files.");
    sum = 0;
    for (ExportPackage p : exportPackages) {
      sum += p.getFiles();
      System.out.println(p);
    }
    System.out.println("Consider " + sum + " files in " + exportPackages.size() + " packages.");
  }

  private void computeExportPackages() {
    var sortedMap = Common.sortByValue(packages, true);
    int sum = 0;
    for (String p : sortedMap.keySet()) {
      sum += sortedMap.get(p).get();
    }
    int filesToAnalyze = (int)(sum * Parameter.getInstance().getMaxFilesToAnalyze());
    int partFiles = 0;
    for (String p : sortedMap.keySet()) {
      partFiles += sortedMap.get(p).get();
      if (partFiles > filesToAnalyze || sortedMap.get(p).get() < Parameter.getInstance().getMinPackageSize())
      {
        partFiles -= sortedMap.get(p).get();
        break;
      }
      var tmpPackage = new ExportPackage();
      tmpPackage.setName(p);
      tmpPackage.setFiles(sortedMap.get(p).get());
      exportPackages.add(tmpPackage);
    }
    for (ExportPackage p : exportPackages) {
      p.setPercent(p.getFiles() * 100.0 / partFiles * 1.0);
    }
  }

  public List<ExportPackage> getExportPackages() {
    return exportPackages;
  }

  public Map<String, Number> getProjectSize() {
    return projectSize;
  }
}
