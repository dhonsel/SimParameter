package de.ugoe.cs.tcs.simparameter.refinfo;

import com.google.common.collect.Maps;

import java.util.Map;

public class ExtractMethodData extends RefData {
    Map<String, Double> deltaClass;
    Map<String, Double> deltaBaseMethod;
    Map<String, Double> deltaNewMethod;
    Map<String, Double> startClass;
    Map<String, Double> startBaseMethod;

    public ExtractMethodData() {
        this.deltaClass = Maps.newHashMap();
        this.deltaBaseMethod = Maps.newHashMap();
        this.deltaNewMethod = Maps.newHashMap();
        this.startClass = Maps.newHashMap();
        this.startBaseMethod = Maps.newHashMap();
    }

    public Map<String, Double> getDeltaClass() {
        return deltaClass;
    }

    public Map<String, Double> getDeltaBaseMethod() {
        return deltaBaseMethod;
    }

    public Map<String, Double> getDeltaNewMethod() {
        return deltaNewMethod;
    }

    public Map<String, Double> getStartClass() {
        return startClass;
    }

    public Map<String, Double> getStartBaseMethod() {
        return startBaseMethod;
    }

}
