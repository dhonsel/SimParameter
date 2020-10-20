package de.ugoe.cs.tcs.simparameter.commitinfo;

import org.apache.commons.math3.stat.descriptive.moment.Mean;


public class CommitTypeInformation {
  private final Mean mChanged;
  private final Mean mAdded;
  private final Mean mRemoved;

  public CommitTypeInformation() {
    this.mChanged = new Mean();
    this.mAdded = new Mean();
    this.mRemoved = new Mean();
  }

  public void addChangedFiles(int a) {
    mChanged.increment(a);
  }

  public void addAddedFiles(int a) {
    mAdded.increment(a);
  }

  public void addRemovedFiles(int a) {
    mRemoved.increment(a);
  }

  public long length() {
    assert mChanged.getN() == mAdded.getN() : mAdded.getN() == mRemoved.getN();

    return mChanged.getN();
  }

  public double pChange() {
    return 1.0 / (mChanged.getResult() + 1);
  }

  public double pAdd() {
    return 1.0 / (mAdded.getResult() + 1);
  }

  public double pRemove() {
    return 1.0 / (mRemoved.getResult() + 1);
  }
}
