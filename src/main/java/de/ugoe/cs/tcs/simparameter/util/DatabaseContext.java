package de.ugoe.cs.tcs.simparameter.util;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import de.ugoe.cs.tcs.simparameter.model.*;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * A representation of the database context. It provides connection to a mungodb as well
 * as all required methods for retrieving, deleting and storing desired entities. To avoid
 * multiple instances the singleton pattern is used.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class DatabaseContext {
  private Logger logger = (Logger) LoggerFactory.getLogger("de.ugoe.cs.tcs.simparameter.util.DatabaseContext");
  private static DatabaseContext instance;
  private final Datastore datastore;

  private Project project;
  private VCSystem vcSystem;

  /**
   * The constructor. Creates the instance initialized with a database connection
   * according to the specified program arguments.
   */
  private DatabaseContext() {
    datastore = createDatastore();
    init();
  }

  /**
   * Returns the instance of the database context. If there is no instance one will be instantiated.
   *
   * @return The instance of the database context
   */
  public static synchronized DatabaseContext getInstance() {
    if (instance == null) {
      instance = new DatabaseContext();
    }
    return instance;
  }

  /*
   * Initializes the instance with commit specific information.
   */
  private void init() {
    final List<VCSystem> vcs = datastore.createQuery(VCSystem.class)
        .field("url").equal(Parameter.getInstance().getUrl())
        .asList();
    if (vcs.size() != 1) {
      logger.error("Found: " + vcs.size() + " instances of VCSystem. Expected 1 instance.");
      System.exit(-1);
    }
    vcSystem = vcs.get(0);

    project = datastore.getByKey(Project.class, new Key<Project>(Project.class, "project", vcSystem.getProjectId()));

  }


  public Project getProject() {
    return project;
  }

  public VCSystem getVcSystem() {
    return vcSystem;
  }


  /**
   * Searches for a commit represented by the given revision hash.
   *
   * @param hash The revision hash of the commit.
   * @return The commit represented by the given revision hash.
   */
  public Commit findCommit(String hash) {
    Query<Commit> commitQuery = datastore.find(Commit.class);
    commitQuery.and(
        commitQuery.criteria("revision_hash").equal(hash),
        commitQuery.criteria("vcs_system_id").equal(getVcSystem().getId())
    );
    final List<Commit> commits = commitQuery.asList();

    if (commits.size() == 1) {
      return commits.get(0);
    } else {
      logger.debug("Could not find commit: "  + hash);
      return null;
    }
  }

  /**
   * Searches for the code entity state of the given class name in the
   * given commit.
   *
   * @param name                    The name of the class.
   * @param c                       The commit to analyze.
   * @return The code entity state of the given class name in the given commit.
   */
  public ObjectId findClassEntityState(String name, Commit c, boolean useAssignedCommits) {
    Query<CodeEntityState> cesq = datastore.find(CodeEntityState.class);

    if (useAssignedCommits && c.getCodeEntityStates() != null && c.getCodeEntityStates().size() > 0) {
      cesq.and(
          cesq.criteria("_id").in(c.getCodeEntityStates()),
          cesq.criteria("ce_type").equal("class"),
          cesq.criteria("long_name").equal(name)
      );
    } else {
      cesq.and(
          cesq.criteria("commit_id").equal(c.getId()),
          cesq.criteria("ce_type").equal("class"),
          cesq.criteria("long_name").equal(name)
      );
    }
    final List<CodeEntityState> ces = cesq.asList();

    if (ces.size() == 1) {
      return ces.get(0).getId();
    } else {
      logger.debug("Could not find ces for class: " + name + " in commit " + c.getMessage());
      return null;
    }
  }

  public ObjectId findAnyClassEntityState(ObjectId fileId) {
    Query<CodeEntityState> cesq = datastore.find(CodeEntityState.class);

    cesq.field("file_id").equal(fileId);
    cesq.or(
        cesq.criteria("ce_type").equal("interface"),
        cesq.criteria("ce_type").equal("class"),
        cesq.criteria("ce_type").equal("enum")
    );

    final List<CodeEntityState> ces = cesq.asList();

    if (ces.size() > 1) {
      return ces.get(0).getId();
    } else {
      logger.debug("Could not find ces for file ID: " + fileId + ".");
      return null;
    }
  }

  /**
   * Searches for the code entity state of the given method name in the
   * given commit.
   *
   * @param name             The name of the method.
   * @param classEntityState The class where the method is implemented.
   * @param c                The commit to analyze.
   * @return The code entity state of the method according to the given parameter.
   */
  public ObjectId findMethodEntityState(String name, ObjectId classEntityState, Commit c) {
    if (classEntityState == null) {
      return null;
    }
    Query<CodeEntityState> cesq = datastore.find(CodeEntityState.class);

    if (c.getCodeEntityStates() != null && c.getCodeEntityStates().size() > 0) {
      cesq.and(
          cesq.criteria("_id").in(c.getCodeEntityStates()),
          cesq.criteria("ce_type").equal("method"),
          cesq.criteria("ce_parent_id").equal(classEntityState)
      );
    } else {
      cesq.and(
          cesq.criteria("commit_id").equal(c.getId()),
          cesq.criteria("ce_type").equal("method"),
          cesq.criteria("ce_parent_id").equal(classEntityState)
      );
    }

    final List<CodeEntityState> methods = cesq.asList();

    CodeEntityState method = null;
    for (CodeEntityState s : methods) {
      if (Common.compareMethods(s.getLongName(), name)) {
        method = s;
      }
    }

    return method == null ? null : method.getId();
  }

  public ObjectId findMethodEntityStateNew(String name, ObjectId classEntityState, Commit c) {
    if (classEntityState == null) {
      return null;
    }
    Query<CodeEntityState> cesq = datastore.find(CodeEntityState.class);

//    cesq.and(
//        cesq.criteria("ce_parent_id").equal(classEntityState),
//        cesq.criteria("ce_type").equal("method")
//    );


    if (c.getCodeEntityStates() != null && c.getCodeEntityStates().size() > 0) {
      cesq.and(
          cesq.criteria("_id").in(c.getCodeEntityStates()),
          cesq.criteria("ce_type").equal("method"),
          cesq.criteria("ce_parent_id").equal(classEntityState)
      );
    } else {
      cesq.and(
          cesq.criteria("commit_id").equal(c.getId()),
          cesq.criteria("ce_type").equal("method"),
          cesq.criteria("ce_parent_id").equal(classEntityState)
      );
    }

    final List<CodeEntityState> methods = cesq.asList();

    CodeEntityState method = null;
    for (CodeEntityState s : methods) {
      if (Common.compareMethods(s.getLongName(), name)) {
        method = s;
        break;
      }
    }

    return method == null ? null : method.getId();
  }

  /**
   * Searches for the code entity state of the given attribute name in the
   * given commit.
   *
   * @param name             The name of the attribute.
   * @param classEntityState The class where the attribute is implemented.
   * @param c                The commit to analyze.
   * @return The code entity state of the attribute according to the given parameter.
   */
  public ObjectId findAttributeEntityState(String name, ObjectId classEntityState, Commit c) {
    if (classEntityState == null) {
      return null;
    }
    Query<CodeEntityState> attributeStates = datastore.find(CodeEntityState.class);

    if (c.getCodeEntityStates() != null && c.getCodeEntityStates().size() > 0) {
      attributeStates.and(
          attributeStates.criteria("_id").in(c.getCodeEntityStates()),
          attributeStates.criteria("ce_type").equal("attribute"),
          attributeStates.criteria("ce_parent_id").equal(classEntityState),
          attributeStates.criteria("long_name").startsWith(name.replace("#", "."))

      );
    } else {
      attributeStates.and(
          attributeStates.criteria("commit_id").equal(c.getId()),
          attributeStates.criteria("ce_type").equal("attribute"),
          attributeStates.criteria("ce_parent_id").equal(classEntityState),
          attributeStates.criteria("long_name").startsWith(name.replace("#", "."))

      );
    }
    final List<CodeEntityState> attributes = attributeStates.asList();

    if (attributes.size() == 1) {
      return attributes.get(0).getId();
    } else {
      logger.debug("Could not find ces for attribute: " + name + " in commit " + c.getRevisionHash());
      return null;
    }
  }

  /**
   * Returns all file actions assigned to the given commit.
   * @param commit The commit to analyze.
   * @return All file actions assigned to the given commit.
   */
  public List<FileAction> getFileActions(Commit commit) {
    return datastore.createQuery(FileAction.class)
        .field("commit_id")
        .equal(commit.getId()).asList();
  }

  /**
   * Returns all file actions assigned to the given commit.
   * @param commit The commit to analyze.
   * @return All file actions assigned to the given commit.
   */
  public List<FileAction> getFileActions(SmallCommit commit) {
    return datastore.createQuery(FileAction.class)
        .field("commit_id")
        .equal(commit.getId()).asList();
  }

  public List<Refactoring> getRefactorings(ObjectId commitId, String type) {
    Query<Refactoring> query = datastore.createQuery(Refactoring.class);
    query.and(
        query.criteria("commit_id").equal(commitId),
        query.criteria("type").equal(type)
    );
    return query.asList();
  }

  /**
   * Returns all issues contained in any issue tracking system assigned to this project.
   * @return All Issues assigned to the project.
   */
  public List<Issue> getAllIssues() {
    List<IssueSystem> issueSystem = datastore.createQuery(IssueSystem.class).field("project_id").equal(project.getId()).asList();
    List<ObjectId> systemIDs = Lists.newArrayList();
    issueSystem.forEach(x -> systemIDs.add(x.getId()));

    Query<Issue> queryIssues = datastore.createQuery(Issue.class);
    queryIssues.and(
        queryIssues.criteria("issue_system_id").in(systemIDs),
        queryIssues.criteria("title").notEqual(""),
        queryIssues.criteria("title").exists()
    );
    return queryIssues.asList();
  }

/*  // code for testing purposes
  public void findAllCesWithGivenClassName(String name) {
    List<CodeEntityState> states = Lists.newArrayList();

    for (Commit c: getAllCommits()) {
      ObjectId id = findClassEntityState(name, c, false);
      if (id != null) {
        states.add(getCes(id));
      }
    }
    for (CodeEntityState s : states) {
      Commit c = getCommit(s.getCommitId());
      System.out.println("Class assigned to commit: " + c.getRevisionHash());
      System.out.println('\t' + "CesObjectId: " + s.getId() + " - CommitObjectId: " + c.getId());
      System.out.println('\t' + "Ces in commit CesList: " + c.getCodeEntityStates().contains(s.getId()));
    }
    for (Commit c: getAllCommits()) {
      ObjectId id = findClassEntityState(name, c, true);
      if (id != null) {
        CodeEntityState e = getCes(id);
        System.out.println("Found class in commit " + c.getRevisionHash() + " with type " +  e.getCeType());
      } else {
        System.out.println("Found class in commit " + c.getRevisionHash() + " : ---" );
      }
      System.out.println("Found class in ces list: " + c.getCodeEntityStates().stream().anyMatch(s -> getCes(s).getLongName().equals(name)));
    }


  }*/

  /**
   * Returns the File instance to the given ID.
   * @param fileId The ID of the file.
   * @return The File instance to the given ID.
   */
  public File getFile(ObjectId fileId) {
    return datastore.getByKey(File.class, new Key<>(File.class, "file", fileId));
  }

  public Commit getCommit(ObjectId commitId) {
    return datastore.getByKey(Commit.class, new Key<>(Commit.class, "commit", commitId));
  }

  public CodeEntityState getCes(ObjectId ces) {
    if (ces == null) {
      return null;
    }
    return datastore.getByKey(CodeEntityState.class, new Key<>(CodeEntityState.class, "code_entity_state", ces));
  }

  /**
   * Returns the people instance to the given ID.
   * @param id The peoples ID.
   * @return The people instance to the given ID.
   */
  public People getPeopleById(ObjectId id) {
    if (id == null) {
      return null;
    }
    return datastore.getByKey(People.class, new Key<>(People.class, "people", id));
  }


  public List<SmallCommit> getAllSmallCommits(Date from, Date to) {
    Query<SmallCommit> query = datastore.createQuery(SmallCommit.class);
    query.and(
        query.criteria("vcs_system_id").equal(vcSystem.getId()),
        query.criteria("author_date").greaterThanOrEq(from),
        query.criteria("author_date").lessThanOrEq(to)
    );
    //query.order("author_date");
    return query.asList();
  }

  public List<SmallCommit> getAllSmallCommits() {
    return datastore.createQuery(SmallCommit.class)
        .field("vcs_system_id").equal(vcSystem.getId())
        .asList();
  }

  /**
   * Returns all commits assigned to the current VCS.
   * @return All commits assigned to the current VCS.
   */
  public List<Commit> getAllCommits() {
    return datastore.createQuery(Commit.class)
        .field("vcs_system_id").equal(vcSystem.getId())
        .asList();
  }

  private Datastore createDatastore() {
    Morphia morphia = new Morphia();
    morphia.mapPackage("de.ugoe.cs.tcs.simparameter.model");
    Datastore datastore = null;

    try {
      if (Parameter.getInstance().getUrl().isEmpty() || Parameter.getInstance().getDbPassword().isEmpty()) {
        datastore = morphia.createDatastore(new MongoClient(Parameter.getInstance().getDbHostname(), Parameter.getInstance().getDbPort()), Parameter.getInstance().getDbName());
      } else {
        ServerAddress addr = new ServerAddress(Parameter.getInstance().getDbHostname(), Parameter.getInstance().getDbPort());
        List<MongoCredential> credentialsList = Lists.newArrayList();
        MongoCredential credential = MongoCredential.createCredential(
            Parameter.getInstance().getDbUser(), Parameter.getInstance().getDbAuthentication(), Parameter.getInstance().getDbPassword().toCharArray());
        credentialsList.add(credential);
        MongoClient client = new MongoClient(addr, credentialsList);
        datastore = morphia.createDatastore(client, Parameter.getInstance().getDbName());
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace(System.err);
      System.exit(1);
    }

    return datastore;
  }

  public Date getFirstCommitDate() {
    Commit c = datastore.createQuery(Commit.class)
        .filter("vcs_system_id", vcSystem.getId())
        .order("author_date").get();
    return c.getAuthorDate();
  }

  public Date getLastCommitDate() {
    Commit c = datastore.createQuery(Commit.class)
        .filter("vcs_system_id", vcSystem.getId())
        .order("-author_date").get();
    return c.getAuthorDate();
  }

}

