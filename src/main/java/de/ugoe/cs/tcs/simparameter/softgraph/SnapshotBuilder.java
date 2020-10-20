package de.ugoe.cs.tcs.simparameter.softgraph;

import ch.qos.logback.classic.Logger;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.common.collect.Lists;
import de.ugoe.cs.tcs.simparameter.model.Commit;
import de.ugoe.cs.tcs.simparameter.util.Common;
import de.ugoe.cs.tcs.simparameter.util.CommonGit;
import de.ugoe.cs.tcs.simparameter.util.DatabaseContext;
import de.ugoe.cs.tcs.simparameter.util.SplitName;
import de.ugoe.cs.tcs.simparameter.util.Parameter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.bson.types.ObjectId;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;

//TODO: compute number of external calls for methods and classes

public class SnapshotBuilder {
  private Logger logger = (Logger) LoggerFactory.getLogger("de.ugoe.cs.tcs.simparameter.softgraph.SnapshotBuilder");
  private Parameter params = Parameter.getInstance();
  private DatabaseContext ctx = DatabaseContext.getInstance();
  private File dir;
  private TypeSolver typeSolver;

  private int ok = 0;
  private int notFoundCallerC = 0;
  private int notFoundCallerM = 0;
  private int notFoundCalleeC = 0;
  private int notFoundCalleeM = 0;
  private int error = 0;

  private Graph<SoftwareEntity, DefaultEdge> graph;

  public SnapshotBuilder() {
  }

  public void createSnapshots() {
    for (String r : params.getSgCommits()) {
      CommonGit.resetHard(r);
      init();
      createSnapshot(r);
      Common.<SoftwareEntity, DefaultEdge>exportDOT(graph, "sg_" + DatabaseContext.getInstance().getProject().getName() + r);
    }
    if (params.getSgCommits().size() > 0) {
      CommonGit.resetHard("HEAD");
    }
  }

  private void createSnapshot(String commit) {
    Collection<File> files = FileUtils.listFiles(dir,
        new RegexFileFilter("^.*java$"), //"^.*ttcn3$|^.*ttcn$|^.*3mp$"
        DirectoryFileFilter.DIRECTORY);

    SoftwareClass classVertex;
    SoftwareMethod methodVertex;

    for (File f : files) {
      try {
        CompilationUnit cu = JavaParser.parse(f);
        List<ClassOrInterfaceDeclaration> classDeclarations = Navigator.findAllNodesOfGivenClass(cu, ClassOrInterfaceDeclaration.class);

        for (ClassOrInterfaceDeclaration cid : classDeclarations) {
          // find class and method declarations for node creation
          // TODO: consider usage of private classes (as nested structure)
          if (!cid.isInterface()  /* && !cid.isPrivate() */) { // private classes are included
            logger.info("Create class node: " + createUniqueName(cid));
            classVertex = new SoftwareClass(createUniqueName(cid));
            graph.addVertex(classVertex);
            for (MethodDeclaration md : cid.getMethods()) {
              logger.info("Create method node: " + createMethodName(md));
              methodVertex = new SoftwareMethod(createMethodName(md));
              methodVertex.setDeclarationString(md.getDeclarationAsString(false, false, true));
              graph.addVertex(methodVertex);
              graph.addEdge(classVertex, methodVertex, new MethodMember(classVertex, methodVertex));
            }
            if (cid.getDefaultConstructor().isPresent()) {
              String ctrName = cid.getDefaultConstructor().get().getDeclarationAsString(false, false, true);
              logger.info("Create method node: " + ctrName);
              methodVertex = new SoftwareMethod(ctrName);
              methodVertex.setDeclarationString(ctrName);
              graph.addVertex(methodVertex);
              graph.addEdge(classVertex, methodVertex, new MethodMember(classVertex, methodVertex));
            }
            getMetrics(commit, classVertex);
          }
        }

        //TODO: consider using this code and build nested classes
        // Created objects with method calls (no metrics available)
//        List<ObjectCreationExpr> objectCreations = Navigator.findAllNodesOfGivenClass(cu, ObjectCreationExpr.class);
//        for (ObjectCreationExpr e : objectCreations) {
//          if (e.getChildNodes().stream().anyMatch(x -> x instanceof MethodDeclaration)) {
//            classVertex = new SoftwareClass(createUniqueName(e));
//            logger.info("Create class node: " + createUniqueName(e));
//            graph.addVertex(classVertex);
//            for (Node md : e.getChildNodes().stream().filter(x -> x instanceof MethodDeclaration).collect(Collectors.toList())) {
//              methodVertex = new SoftwareMethod(createMethodName((MethodDeclaration) md));
//              logger.info("Create method node: " + createMethodName((MethodDeclaration) md));
//              graph.addVertex(methodVertex);
//              graph.addEdge(classVertex, methodVertex, new MethodMember(classVertex, methodVertex));
//            }
//          }
//        }

      } catch (FileNotFoundException e) {
        logger.error(e.getMessage());
      }
    }

    // TODO: check unresolved methods!!!
    for (File f : files) {
      try {
        CompilationUnit cu = JavaParser.parse(f);
        List<MethodCallExpr> methodCalls = Navigator.findAllNodesOfGivenClass(cu, MethodCallExpr.class);

        for (MethodCallExpr mc : methodCalls) {
          try {
            String signature = JavaParserFacade.get(typeSolver).solve(mc).getCorrespondingDeclaration().getQualifiedSignature();
            SplitName calleeName = new SplitName(signature);

            SoftwareClass callerClass = findClass(getParentClassName(mc));
            SoftwareMethod callerMethod = findMethod(callerClass, findMethodDeclarationAndGetMethodName(mc));
            SoftwareClass calleeClass = findClass(calleeName.getClassName());
            SoftwareMethod calleeMethod = findMethod(calleeClass, calleeName.getMethodName());

            StringBuilder message = new StringBuilder();
            if (callerClass == null) {
              message.append("Could not find caller class: " + getParentClassName(mc));
              message.append(";");
              notFoundCallerC++;
            }
            if (callerMethod == null) {
              message.append("Could not find caller method: " + findMethodDeclarationAndGetMethodName(mc));
              message.append(";");
              notFoundCallerM++;
            }
            if (calleeClass == null) {
              message.append("Could not find callee class: " + calleeName.getClassName());
              message.append(";");
              notFoundCalleeC++;
            }
            if (calleeMethod == null) {
              message.append("Could not find callee method: " + calleeName.getMethodName());
              message.append(";");
              notFoundCalleeM++;
            }

            if (callerMethod != null && (calleeClass == null || calleeMethod == null)) {
              callerMethod.increaseUnresolvedMethodCalls();
            }

            if (message.toString().isEmpty()) {
              ok++;
              MethodCall call = new MethodCall(callerClass, callerMethod, calleeClass, calleeMethod);
              graph.addEdge(callerMethod, calleeMethod, call);
              logger.info("Created method call: " + callerClass + "." + callerMethod + " -> " + calleeClass + "." + calleeMethod);
            } else {
              String warning = "Could not create method call edge:" + '\n' + '\t' + message.toString();
              logger.info(warning);
            }

          } catch (Exception e) {
            error++;
            logger.info("Could not create method call edge: " + mc.getNameAsString());
            var callerClass = findClass(getParentClassName(mc));
            if (callerClass != null) {
              callerClass.setNumOfExternalCalls(callerClass.getNumOfExternalCalls() + 1);
            }
          }
        }
      } catch (Exception e) {
        logger.error(e.getMessage());
      }

    }

    // TODO: find parent ant child classes for inheritance edge creation
    printInfo();
  }

  private boolean init() {
    graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    ok = 0;
    notFoundCallerC = 0;
    notFoundCallerM = 0;
    notFoundCalleeC = 0;
    notFoundCalleeM = 0;
    error = 0;

    dir = new File(params.getDirToAnalyze());
    if (!dir.isDirectory()) {
      logger.error("The given path is not a directory!");
      return false;
    }
    typeSolver = new CombinedTypeSolver(
        new ReflectionTypeSolver(),
        new JavaParserTypeSolver(dir)
    );

    return true;
  }

  private void printInfo() {
    StringBuilder message = new StringBuilder();
    message.append("Solved method calls: " + ok);
    message.append('\n');
    message.append("Unresolved method calls: ");
    message.append('\n');
    message.append('\t' + "CallerC: " + notFoundCallerC);
    message.append('\n');
    message.append('\t' + "CallerM: " + notFoundCallerM);
    message.append('\n');
    message.append('\t' + "CalleeC: " + notFoundCalleeC);
    message.append('\n');
    message.append('\t' + "CalleeM: " + notFoundCalleeM);
    message.append('\n');
    message.append("Errors: " + error);
    logger.warn(message.toString());
  }

  private String createUniqueName(Node node) {
    StringBuilder name = new StringBuilder();
    int level = 0;
    List<String> names = Lists.newArrayList();

    while (node != null) {
      if (node instanceof ClassOrInterfaceDeclaration) {
        names.add(((ClassOrInterfaceDeclaration) node).getNameAsString());
        level++;
      } else if (node instanceof CompilationUnit) {
        ((CompilationUnit) node).getPackageDeclaration().ifPresent(n -> names.add(n.getName().asString()));
      } else if (node instanceof ObjectCreationExpr) {
        if (node.getChildNodes().stream().anyMatch(x -> x instanceof MethodDeclaration)) {
          names.add(((ObjectCreationExpr) node).getType().getName().getIdentifier());
          level++;
        }
      }

      if (node.getParentNode().isPresent()) {
        node = node.getParentNode().get();
      } else {
        node = null;
      }
    }

    for (int i = names.size() - 1; i >= 0; i--) {
      name.append(names.get(i));
      if (i > 0 && i < level) {
        name.append("$");
      } else if (i > 0) {
        name.append(".");
      }
    }

    return name.toString();
  }

  private String findMethodDeclarationAndGetMethodName(Node node) {
    StringBuilder name = new StringBuilder();

    while (node != null) {
      if (node instanceof MethodDeclaration) {
        name.append(createMethodName((MethodDeclaration) node));
        return name.toString();
      }
      if (node instanceof ConstructorDeclaration) {
        name.append(((ConstructorDeclaration) node).getDeclarationAsString(false, false, true));
        return name.toString();
      }
      if (node.getParentNode().isPresent()) {
        node = node.getParentNode().get();
      } else {
        node = null;
      }
    }

    return name.toString();
  }

  private String createMethodName(MethodDeclaration md) {
    StringBuilder name = new StringBuilder();
    var tmpName = md.getDeclarationAsString(false, false, true).split("\\(")[0].split(" ");
    name.append(tmpName[tmpName.length - 1]);
    String signature = "";
    String s1 = md.getDeclarationAsString(false, false, true).split("\\(")[1];
    if (s1.length() > 1) {
      signature = s1.split("\\)")[0];
    }
    signature = signature.replace("final", "");
    String[] splitSignature = signature.split(",");

    name.append("(");
    for (int i = 0; i < splitSignature.length; i++) {
      var parameter = splitSignature[i].replace("...", "[]").trim().split(" ")[0];

      parameter = parameter.replaceAll("<.*>", "");
      if (parameter.contains(".")) {
        var splitParam = parameter.split("\\.");
        parameter = splitParam[splitParam.length - 1];
      }
      name.append(parameter);
      if (i < splitSignature.length - 1) {
        name.append(",");
      }
    }
    name.append(")");

    return name.toString();
  }

  private String getParentClassName(Node n) {
    return createUniqueName(n);
  }

  //TODO: log not found classes
  private SoftwareClass findClass(String name) {
    return (SoftwareClass) graph.vertexSet().stream().filter(x -> x.getName().equals(name)).collect(Common.singletonCollector());
  }

  private SoftwareMethod findMethod(SoftwareClass c, String name) {
    if (c == null || name == null) {
      return null;
    }
    for (DefaultEdge e : graph.outgoingEdgesOf(c)) {
      if (e instanceof MethodMember) {
        if (((MethodMember) e).getSoftwareMethod().getName().equals(name)) {
          return ((MethodMember) e).getSoftwareMethod();
        }
      }
    }
    return null;
  }

  private void getMetrics(String hash, SoftwareClass classVertex) {
    var commit = ctx.findCommit(hash);
    var classEntityId = ctx.findClassEntityState(classVertex.getName(), commit, true);
    if (classEntityId != null) {
      var classEntity = ctx.getCes(classEntityId);
      classVertex.setLOC(classEntity.getMetrics().get("LOC"));
      getMethodMetrics(classVertex, commit, classEntityId);
    } else {
      logger.warn("Could not find metrics for class: " + classVertex.getName() + " in commit: " + hash);
    }
  }

  private void getMethodMetrics(SoftwareClass classVertex, Commit c, ObjectId parentClassId) {
    var edges = graph.edgesOf(classVertex);
    for (DefaultEdge e : edges) {
      if (e instanceof MethodMember) {
        var methodEntityState = ctx.findMethodEntityStateNew(classVertex.getName() + "." + ((MethodMember) e).getSoftwareMethod().getName(), parentClassId, c);
        if (methodEntityState != null) {
          var methodEntity = ctx.getCes(methodEntityState);
          ((MethodMember) e).getSoftwareMethod().setLOC(methodEntity.getMetrics().get("LOC"));
        } else {
          logger.warn("Could not find metrics for method: " + classVertex.getName() + " - " + ((MethodMember) e).getSoftwareMethod().getName() + " -> declaration: " + ((MethodMember) e).getSoftwareMethod().getDeclarationString() + " in commit: " + c.getRevisionHash());
        }
      }
    }
  }

}
