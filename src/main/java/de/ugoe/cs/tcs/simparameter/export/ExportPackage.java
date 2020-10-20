package de.ugoe.cs.tcs.simparameter.export;

public class ExportPackage {
  private String name;
  private int files;
  private double percent;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getFiles() {
    return files;
  }

  public void setFiles(int files) {
    this.files = files;
  }

  public double getPercent() {
    return percent;
  }

  public void setPercent(double percent) {
    this.percent = percent;
  }

  @Override
  public String toString() {
    return "ExportPackage{" +
        "name='" + name + '\'' +
        ", files=" + files +
        ", percent=" + percent +
        '}';
  }
}
