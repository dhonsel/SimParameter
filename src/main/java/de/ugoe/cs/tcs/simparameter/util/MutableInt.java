package de.ugoe.cs.tcs.simparameter.util;

public class MutableInt implements Comparable<MutableInt> {
  private int value = 1;

  public MutableInt() { }

  public void increment () { ++value;      }
  public int  get ()       { return value; }

  @Override
  public int compareTo(MutableInt o) {
    return Integer.compare(this.get(), o.get());
  }
}

