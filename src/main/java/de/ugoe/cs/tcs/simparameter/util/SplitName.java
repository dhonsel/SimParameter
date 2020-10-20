package de.ugoe.cs.tcs.simparameter.util;

public class SplitName {
  private String original;
  private String className;
  private String methodName;

  public SplitName(String original) {
    this.original = original;
    setSplitName(this.original);
  }

  public String getOriginal() {
    return original;
  }

  public String getClassName() {
    return className;
  }

  public String getMethodName() {
    return methodName;
  }

  private void setSplitName(String original) {
    String[] nameWithoutBraceOpen = original.split("\\(");
    String[] classNameParts = nameWithoutBraceOpen[0].split("\\.");
    StringBuilder className = new StringBuilder();
    StringBuilder methodName = new StringBuilder();

    for (int i = 0; i < classNameParts.length - 1; i++) {
      className.append(classNameParts[i]);
      if (i < classNameParts.length - 2) {
        className.append(".");
      }
    }

    methodName.append(classNameParts[classNameParts.length - 1]);
    methodName.append("(");
    methodName.append(nameWithoutBraceOpen[1]);

    this.methodName = methodName.toString().replace(" ", "");
    this.className = className.toString().replace(" ", "");
  }
}
