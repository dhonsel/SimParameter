package de.ugoe.cs.tcs.simparameter.util;

public class MutableCommitStatistics implements Comparable<MutableCommitStatistics> {
  private final int numberOfCommits;
  private int value = 1;

  public MutableCommitStatistics(int numberOfCommits) {
    this.numberOfCommits = numberOfCommits;
  }

  public void increment () { ++value;      }
  public int  get ()       { return value; }
  public double percent() {
    return value * 100.0 / numberOfCommits ;
  }

  @Override
  public int compareTo(MutableCommitStatistics o) {
    return Integer.compare(this.get(), o.get());
  }
}

