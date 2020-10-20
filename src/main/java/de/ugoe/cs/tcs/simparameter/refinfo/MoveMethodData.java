package de.ugoe.cs.tcs.simparameter.refinfo;

import com.google.common.collect.Maps;

import java.util.Map;

public class MoveMethodData extends RefData {
    Map<String, Double> deltaBaseClass;
    Map<String, Double> deltaTargetClass;
    Map<String, Double> deltaMethod;
    Map<String, Double> startBaseClass;
    Map<String, Double> startTargetClass;
    Map<String, Double> startMethod;

    public MoveMethodData() {
        deltaBaseClass = Maps.newHashMap();
        deltaTargetClass = Maps.newHashMap();
        deltaMethod = Maps.newHashMap();
        startBaseClass = Maps.newHashMap();
        startTargetClass = Maps.newHashMap();
        startMethod = Maps.newHashMap();
    }

    public Map<String, Double> getDeltaBaseClass() {
        return deltaBaseClass;
    }

    public Map<String, Double> getDeltaTargetClass() {
        return deltaTargetClass;
    }

    public Map<String, Double> getDeltaMethod() {
        return deltaMethod;
    }

    public Map<String, Double> getStartBaseClass() {
        return startBaseClass;
    }

    public Map<String, Double> getStartTargetClass() {
        return startTargetClass;
    }

    public Map<String, Double> getStartMethod() {
        return startMethod;
    }
}
