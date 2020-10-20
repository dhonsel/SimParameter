package de.ugoe.cs.tcs.simparameter.refinfo;

import com.google.common.collect.Maps;

import java.util.Map;

public class InlineMethodData extends RefData {
    Map<String, Double> deltaClass;
    Map<String, Double> deltaCallerMethod;
    Map<String, Double> deltaInlinedMethod;
    Map<String, Double> startClass;
    Map<String, Double> startCallerMethod;
    Map<String, Double> startInlinedMethod;

    public InlineMethodData() {
        this.deltaClass = Maps.newHashMap();
        this.deltaCallerMethod = Maps.newHashMap();
        this.deltaInlinedMethod = Maps.newHashMap();
        this.startClass = Maps.newHashMap();
        this.startCallerMethod = Maps.newHashMap();
        this.startInlinedMethod = Maps.newHashMap();
    }

    public Map<String, Double> getDeltaClass() {
        return deltaClass;
    }

    public Map<String, Double> getDeltaCallerMethod() {
        return deltaCallerMethod;
    }

    public Map<String, Double> getDeltaInlinedMethod() {
        return deltaInlinedMethod;
    }

    public Map<String, Double> getStartClass() {
        return startClass;
    }

    public Map<String, Double> getStartCallerMethod() {
        return startCallerMethod;
    }

    public Map<String, Double> getStartInlinedMethod() {
        return startInlinedMethod;
    }
}
