package de.ugoe.cs.tcs.simparameter.util;


import com.google.common.collect.Maps;
import de.ugoe.cs.tcs.simparameter.changecoupling.CCEdge;
import de.ugoe.cs.tcs.simparameter.changecoupling.CCFile;
import de.ugoe.cs.tcs.simparameter.changecoupling.CommonAttribute;
import de.ugoe.cs.tcs.simparameter.persons.Identity;
import de.ugoe.cs.tcs.simparameter.softgraph.*;
import org.jgrapht.Graph;
import org.jgrapht.io.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * A collection of common used helper methods.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class Common {

  /**
   * This method compares the JVM method signature provided by SourceMeter withe the
   * simple method name provided by JavaParser. If both names represents the same method,
   * this method returns true.
   * @param mSourceMeter The JVM method signature provided by SourceMeter.
   * @param mJavaParser The simple method signature provided by JavaParser.
   * @return If both names represents the same method, his method returns true.
   * Otherwise false.
   */
  public static boolean compareMethods(String mSourceMeter, String mJavaParser) {
    String name = mSourceMeter.split("\\(")[0];
    if (name.contains("<init>")) {
      var splitName = name.split("\\.");
      var className = splitName[splitName.length -2 ];
      if (className.contains("$")) {
        className = className.split("\\$")[1];
      }
      name = name.replace("<init>", className);
    }
    String signature = mSourceMeter.split("\\(")[1].split("\\)")[0];
    String simpleSignature = simplifyJavaVMMethodSignature(signature);

    String sm = (name + simpleSignature).replace(" ", "");

    String jp = mJavaParser.replace(" ", "");

    return sm.equals(jp);
  }

  /*
   * Simplifies a JVM signature. It cuts path information of passed types and
   * reformats array brackets.
   */
  public static String simplifyJavaVMMethodSignature(String signature) {
    StringBuilder simpleName = new StringBuilder();
    for (int i = 0; i < signature.length(); ++i) {
      StringBuilder type = new StringBuilder();
      if (signature.charAt(i) == '[') {
        if (signature.charAt(i + 1) == 'L') {
          int j = getTypeName(type, signature, i + 2);
          i = j;
          type.append("[]");
        } else {
          type.append(getJVMPrimitiveType(signature.charAt(i + 1)));
          type.append("[]");
          i += 1;
        }
      } else if (signature.charAt(i) == 'L') {
        int j = getTypeName(type, signature, i + 1);
        i = j;
      } else {
        type.append(getJVMPrimitiveType(signature.charAt(i)));
      }
      if (simpleName.length() == 0) {
        simpleName.append("(");
        simpleName.append(type);
      } else {
        simpleName.append(", ");
        simpleName.append(type);
      }
    }
    if (simpleName.length() == 0) {
      simpleName.append("(");
    }
    simpleName.append(")");
    return simpleName.toString();
  }

  private static int getTypeName(StringBuilder type, String name, int start) {
    String typeName = "";
    for (int j = start; j < name.length(); j++) {
      if (name.charAt(j) == ';') {
        type.append(simpleTypeName(typeName));
        return j;
      } else {
        typeName += name.charAt(j);
      }
    }
    return start + 1;
  }

  private static String getJVMPrimitiveType(char type) {
    switch (type) {
      case 'Z':
        return "boolean";
      case 'B':
        return "byte";
      case 'C':
        return "char";
      case 'S':
        return "short";
      case 'I':
        return "int";
      case 'J':
        return "long";
      case 'F':
        return "float";
      case 'D':
        return "double";
      case 'V':
        return "void";
      default:
        return "";
    }
  }

  private static String simpleTypeName(String name) {
    String[] parts = name.split("/");
    var lastPart = parts[parts.length - 1];
    if (lastPart.contains("$")) {
      var cleanPart = lastPart.split("\\$");
      return cleanPart[cleanPart.length -1];
    } else {
      return lastPart;
    }
  }

  /**
   * Reformats the type name with a '$' as separator for nested types.
   * @param name The type name to be reformatted.
   * @param level The nesting level.
   * @return The reformatted type name with a '$' as separator for nested types.
   */
  public static String formatNestedTypeName(String name, int level) {
    String parts[] = name.split("\\.");
    StringBuilder formattedName = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      formattedName.append(parts[i]);
      if (i < parts.length - (1 + level)) {
        formattedName.append(".");
      } else if (i < parts.length - 1) {
        formattedName.append("$");
      }
    }
    return formattedName.toString();
  }

  public static <T> Collector<T, ?, T> singletonCollector() {
    return Collectors.collectingAndThen(
        Collectors.toList(),
        list -> {
          if (list.size() != 1) {
            return null;
          }
          return list.get(0);
        }
    );
  }

  public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean reverseOrder) {
    if (reverseOrder) {
      return map.entrySet()
          .stream()
          .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
          .collect(Collectors.toMap(
              Map.Entry::getKey,
              Map.Entry::getValue,
              (e1, e2) -> e1,
              LinkedHashMap::new
          ));
    } else {
      return map.entrySet()
          .stream()
          .sorted(Map.Entry.comparingByValue())
          .collect(Collectors.toMap(
              Map.Entry::getKey,
              Map.Entry::getValue,
              (e1, e2) -> e1,
              LinkedHashMap::new
          ));
      }
  }


  public static <N, E> void exportDOT(Graph g, String filename) {
    IntegerComponentNameProvider<N> idProvider = new IntegerComponentNameProvider<>() {  };

    StringComponentNameProvider<N> nodeLabelProvider = new StringComponentNameProvider<>() {
      @Override
      public String getName(N component) {
        if (component instanceof CCFile) {
          return ((CCFile)component).getPath();
        } else if (component instanceof SoftwareClass) {
          return "class";
        } else if (component instanceof SoftwareMethod) {
          return "method";
        } else {
          return "unknown";
        }
      }
    };

    StringComponentNameProvider<E> edgeLabelProvider = new StringComponentNameProvider<>() {
      @Override
      public String getName(E component) {
        if (component instanceof MethodCall) {
          return "mc";
        } else if (component instanceof MethodMember) {
          return "mm";
        } else if (component instanceof CCEdge) {
          return null;
        } else {
          return "unknown";
        }
      }
    };

    ComponentAttributeProvider<N> nodeAttributeProvider = new ComponentAttributeProvider<>() {
      @Override
      public Map<String, Attribute> getComponentAttributes(N node) {
        Map<String, Attribute> attributes = Maps.newHashMap();
        if (node instanceof SoftwareEntity) {
          attributes.put("name", new CommonAttribute(((SoftwareEntity) node).getName(), AttributeType.STRING));
          attributes.put("LOC", new CommonAttribute(String.valueOf(((SoftwareEntity) node).getLOC()), AttributeType.DOUBLE));
        }
        if (node instanceof SoftwareClass) {
          attributes.put("NoEC", new CommonAttribute(String.valueOf(((SoftwareClass) node).getNumOfExternalCalls()), AttributeType.INT));
        }
        if (node instanceof CCFile) {
          attributes.put("package", new CommonAttribute(((CCFile) node).getJavaPackage(), AttributeType.STRING));
          attributes.put("creator", new CommonAttribute(((CCFile) node).getCreator().getObjectID().toString(), AttributeType.STRING));
          attributes.put("owner", new CommonAttribute(((CCFile) node).getOwner(false).getObjectID().toString(), AttributeType.STRING));
          int i = 1;
          var identities = ((CCFile) node).getContributingDevelopers();
          for (Identity dev : identities.keySet()) {
            attributes.put("dev" + i++, new CommonAttribute(dev.getObjectID().toString() + ";" + identities.get(dev).get(), AttributeType.STRING));
          }
        }
        return attributes;
      }
    };

    ComponentAttributeProvider<E> edgeAttributeProvider = new ComponentAttributeProvider<>() {
      @Override
      public Map<String, Attribute> getComponentAttributes(E edge) {
        Map<String, Attribute> attributes = Maps.newHashMap();
        if (edge instanceof CCEdge) {
          attributes.put("weight", new CommonAttribute(String.valueOf(((CCEdge)edge).getWeight()), AttributeType.DOUBLE));
        }
        return attributes;
      }
    };

    DOTExporter<N, E> exporter = new DOTExporter<>(
        idProvider,
        nodeLabelProvider,
        edgeLabelProvider,
        nodeAttributeProvider,
        edgeAttributeProvider
    );

    try {
      exporter.exportGraph(g, new FileWriter(Parameter.getInstance().getOutputFolder() + java.io.File.separator + filename + ".dot"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static long computeMonthBetweenDates(Date d1, Date d2) {
    LocalDate ld1 = getLocalDate(d1);
    LocalDate ld2 = getLocalDate(d2);
    return ChronoUnit.MONTHS.between(ld1, ld2) + 1;
  }

  public static long computeDaysBetweenDates(Date d1, Date d2) {
    LocalDate ld1 = getLocalDate(d1);
    LocalDate ld2 = getLocalDate(d2);
    return ChronoUnit.DAYS.between(ld1, ld2) + 1;
  }

  public static LocalDate getLocalDate(Date date) {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  public static int getYear(Date d) {
    LocalDate ld = getLocalDate(d);
    return ld.getYear();
  }

  public static Date getDate(LocalDate localDate) {
    return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  public static void writeProjectSizeToFile(Map<String, Number> size, String fileName) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
    StringBuilder csv = new StringBuilder();

    // header
    csv.append("Commit");
    csv.append(";");
    csv.append("Size");
    csv.append('\n');

    for (var e: size.entrySet()) {
      csv.append(e.getKey());
      csv.append(";");
      csv.append(e.getValue());
      csv.append('\n');
    }

    writer.write(csv.toString());
    writer.close();
  }

  public static void writeTextToFile(String text, String fileName)  {
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(fileName));
      writer.write(text);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static <N, E> void exportCSV(Graph g, String filename) {
    CSVExporter <N, E> exporter = new CSVExporter();
    exporter.setDelimiter(',');
    exporter.setFormat(CSVFormat.MATRIX);
    exporter.setParameter(CSVFormat.Parameter.EDGE_WEIGHTS, true);
    exporter.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE, true);

    try {
      exporter.exportGraph(g, new FileWriter(Parameter.getInstance().getOutputFolder() + java.io.File.separator + filename + ".csv"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
