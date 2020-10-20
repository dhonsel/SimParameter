package de.ugoe.cs.tcs.simparameter.refinfo;

import com.google.common.collect.Lists;
import de.ugoe.cs.tcs.simparameter.model.CodeEntityState;
import de.ugoe.cs.tcs.simparameter.model.Commit;
import de.ugoe.cs.tcs.simparameter.model.Refactoring;
import de.ugoe.cs.tcs.simparameter.util.DatabaseContext;
import de.ugoe.cs.tcs.simparameter.util.Parameter;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

public class RefactoringAnalyzer {
    private Parameter param;
    private DatabaseContext ctx;
    private final List<String> metricNames;
    private List<MoveMethodData> moveMethodData;
    private List<MoveMethodData> moveMethodNewClassData;
    private List<ExtractMethodData> extractMethodData;
    private List<InlineMethodData> inlineMethodData;

    public RefactoringAnalyzer() {
        param = Parameter.getInstance();
        ctx = DatabaseContext.getInstance();
        this.metricNames = Lists.newArrayList("LOC", "LLOC", "TLLOC", "TCLOC", "TNOS", "NOS", "NOI", "RFC", "CBO", "CBOI", "CC", "CI", "WMC", "McCC");
        moveMethodData = Lists.newArrayList();
        moveMethodNewClassData = Lists.newArrayList();
        extractMethodData = Lists.newArrayList();
        inlineMethodData = Lists.newArrayList();
    }

    public void analyze() {
        List<String> refTypes = param.getRefactoringTypes();

        for (Commit c : ctx.getAllCommits()) {
            for (String t : refTypes) {
                if (t.equals("move_method")) {
                    List<Refactoring> refactorings = ctx.getRefactorings(c.getId(), "move_method");
                    analyzeMoveMethod(refactorings);
                } else if (t.equals("extract_method")) {
                    List<Refactoring> refactorings = ctx.getRefactorings(c.getId(), "extract_method");
                    analyzeExtractMethod(refactorings);
                } else if (t.equals("inline_method")) {
                    List<Refactoring> refactorings = ctx.getRefactorings(c.getId(), "inline_method");
                    analyzeInlineMethod(refactorings);
                }
            }
        }

        System.out.println("FOUND REFACTORINGS:");
        System.out.println("Move Method: " + moveMethodData.size());
        System.out.println("Move Method to new Class: " + moveMethodNewClassData.size());
        System.out.println("Extract Method: " + extractMethodData.size());
        System.out.println("Inline Method: " + inlineMethodData.size());
    }

    private void analyzeMoveMethod(List<Refactoring> refactorings) {

        for (var r : refactorings) {

            ObjectId baseClassID = null;
            ObjectId targetClassID = null;
            ObjectId methodId = null;
            ObjectId parentBaseClassId = null;
            ObjectId parentTargetClassId = null;
            ObjectId parentMethodId = null;

            if (r.getState() != null) {
                baseClassID = r.getState().getParentCeBefore();
                targetClassID = r.getState().getParentCeAfter();
                methodId = r.getState().getCeAfter();
            }

            if (r.getParentStates().size() > 0) {
                parentBaseClassId = r.getParentStates().get(0).getParentCeBefore();
                parentTargetClassId = r.getParentStates().get(0).getParentCeAfter(); // can be missing if a new class is created as target
                parentMethodId = r.getParentStates().get(0).getCeBefore();
            }

            if (baseClassID != null && targetClassID != null && methodId != null
                                    && parentBaseClassId != null && parentMethodId != null) {

                CodeEntityState baseClass = ctx.getCes(baseClassID);
                CodeEntityState parentBaseClass = ctx.getCes(parentBaseClassId);
                CodeEntityState targetClass = ctx.getCes(targetClassID);
                CodeEntityState method = ctx.getCes(methodId);
                CodeEntityState parentMethod = ctx.getCes(parentMethodId);
                MoveMethodData data = new MoveMethodData();

                createMetricDiffsChanged(baseClass.getMetrics(), parentBaseClass.getMetrics(), data.getDeltaBaseClass());
                createMetricDiffsAdded(targetClass.getMetrics(), data.getDeltaTargetClass());
                createMetricDiffsChanged(method.getMetrics(), parentMethod.getMetrics(), data.getDeltaMethod());
                createMetricDiffsAdded(parentBaseClass.getMetrics(), data.getStartBaseClass());
                createMetricDiffsAdded(parentMethod.getMetrics(), data.getStartMethod());

                if (parentTargetClassId == null) { // a new created class is the target class for the moved method
                    // init
                    for (String s : metricNames) {
                        data.getStartTargetClass().put(s, 0.0);
                    }
                    moveMethodNewClassData.add(data);
                } else { // the target class already exists
                    CodeEntityState parentTargetClass = ctx.getCes(parentTargetClassId);
                    createMetricDiffsChanged(targetClass.getMetrics(), parentTargetClass.getMetrics(), data.getDeltaTargetClass());
                    createMetricDiffsAdded(parentTargetClass.getMetrics(), data.getStartTargetClass());
                    moveMethodData.add(data);
                }
            }
        }
    }

    private void analyzeExtractMethod(List<Refactoring> refactorings) {
        // TODO: consider extractions to already existing method (parentNewMethodID != null)
        for (var r : refactorings) {
            ObjectId classID = null;
            ObjectId baseMethodID = null;
            ObjectId newMethodID = null;
            ObjectId parentClassID = null;
            ObjectId parentBaseMethodID = null;

            if (r.getState() != null) {
                classID = r.getState().getParentCeBefore();
                baseMethodID = r.getState().getCeBefore();
                newMethodID = r.getState().getCeAfter();
            }

            if (r.getParentStates() != null) {
                parentClassID = r.getParentStates().get(0).getParentCeBefore();
                parentBaseMethodID = r.getParentStates().get(0).getCeBefore();
            }

            if (classID != null && baseMethodID != null && newMethodID != null
                            && parentClassID != null && parentBaseMethodID != null)  {
                ExtractMethodData data = new ExtractMethodData();
                CodeEntityState clazz = ctx.getCes(classID);
                CodeEntityState parentClass = ctx.getCes(parentClassID);
                CodeEntityState baseMethod = ctx.getCes(baseMethodID);
                CodeEntityState parentBaseMethod = ctx.getCes(parentBaseMethodID);
                CodeEntityState newMethod = ctx.getCes(newMethodID);

                createMetricDiffsChanged(clazz.getMetrics(), parentClass.getMetrics(), data.getDeltaClass());
                createMetricDiffsChanged(baseMethod.getMetrics(), parentBaseMethod.getMetrics(), data.getDeltaBaseMethod());
                createMetricDiffsAdded(newMethod.getMetrics(), data.getDeltaNewMethod());
                createMetricDiffsAdded(parentClass.getMetrics(), data.getStartClass());
                createMetricDiffsAdded(parentBaseMethod.getMetrics(), data.getStartBaseMethod());
                extractMethodData.add(data);
            }
        }
    }

    private void analyzeInlineMethod(List<Refactoring> refactorings) {
        // TODO: consider refactorings with different classes as inlined and destination?
        for (var r : refactorings) {
            ObjectId classID = null;
            ObjectId callerMethodID = null;
            ObjectId parentClassID = null;
            ObjectId parentCallerMethodID = null;
            ObjectId parentInlinedMethodID = null;

            if (r.getState() != null) {
                classID = r.getState().getParentCeAfter();
                callerMethodID = r.getState().getCeAfter();
            }

            if (r.getParentStates() != null) {
                parentClassID = r.getParentStates().get(0).getParentCeAfter();
                parentCallerMethodID = r.getParentStates().get(0).getCeAfter();
                parentInlinedMethodID = r.getParentStates().get(0).getCeBefore();
            }

            if (classID != null && callerMethodID != null && parentClassID != null
                                && parentCallerMethodID != null && parentInlinedMethodID != null) {
                InlineMethodData data = new InlineMethodData();
                CodeEntityState clazz = ctx.getCes(classID);
                CodeEntityState callerMethod = ctx.getCes(callerMethodID);
                CodeEntityState parentClass = ctx.getCes(parentClassID);
                CodeEntityState parentCallerMethod = ctx.getCes(parentCallerMethodID);
                CodeEntityState parentInlinedMethod = ctx.getCes(parentInlinedMethodID);

                createMetricDiffsChanged(clazz.getMetrics(), parentClass.getMetrics(), data.getDeltaClass());
                createMetricDiffsChanged(callerMethod.getMetrics(), parentCallerMethod.getMetrics(), data.getDeltaCallerMethod());
                createMetricDiffsDeleted(parentInlinedMethod.getMetrics(), data.getDeltaInlinedMethod());
                createMetricDiffsAdded(parentClass.getMetrics(), data.getStartClass());
                createMetricDiffsAdded(parentCallerMethod.getMetrics(), data.getStartCallerMethod());
                createMetricDiffsAdded(parentInlinedMethod.getMetrics(), data.getStartInlinedMethod());
                inlineMethodData.add(data);
            }
        }
    }

    private void createMetricDiffsAdded(Map<String, Double> commitMetrics, Map<String, Double> delta) {
        for (String s : metricNames) {
            if (commitMetrics.containsKey(s)) {
                delta.put(s, commitMetrics.get(s));
            } else {
                delta.put(s, 0.0);
            }
        }
    }

    private void createMetricDiffsDeleted(Map<String, Double> parentMetrics, Map<String, Double> delta) {
        for (String s : metricNames) {
            if (parentMetrics.containsKey(s)) {
                delta.put(s, parentMetrics.get(s)*-1);
            } else {
                delta.put(s, 0.0);
            }
        }
    }

    private void createMetricDiffsChanged(Map<String, Double> commitMetrics, Map<String, Double> parentMetrics, Map<String, Double> delta) {
        for (String s : metricNames) {
            if (commitMetrics.containsKey(s) && parentMetrics.containsKey(s)) {
                delta.put(s, commitMetrics.get(s) - parentMetrics.get(s));
            } else {
                delta.put(s, 0.0);
            }
        }
    }

    public String getAverageResults() {
        StringBuilder result = new StringBuilder();

        result.append("Extract Method Data");
        result.append('\n');
        result.append("--------------------------------------------------" + '\n');
        int emdSize = extractMethodData.size();
        result.append("Analyzed items: " + emdSize);
        result.append('\n');
        ExtractMethodData d = sumExtractMethodData();
        result.append("Delta Class:");
        result.append('\n');
        result.append(averageMetrics(d.getDeltaClass(), emdSize));
        result.append('\n');
        result.append("Delta Base Method:");
        result.append('\n');
        result.append(averageMetrics(d.getDeltaBaseMethod(), emdSize));
        result.append('\n');
        result.append("Delta New Method:");
        result.append('\n');
        result.append(averageMetrics(d.getDeltaNewMethod(), emdSize));
        result.append('\n');
        result.append("Start Class:");
        result.append('\n');
        result.append(averageMetrics(d.getStartClass(), emdSize));
        result.append('\n');
        result.append("Start Base Method:");
        result.append('\n');
        result.append(averageMetrics(d.getStartBaseMethod(), emdSize));
        result.append('\n');

        result.append("Inline Method Data");
        result.append('\n');
        result.append("--------------------------------------------------" + '\n');
        int imdSize = inlineMethodData.size();
        result.append("Analyzed items: " + imdSize);
        result.append('\n');
        InlineMethodData i = sumInlineMethodData();
        result.append("Delta Class:");
        result.append('\n');
        result.append(averageMetrics(i.getDeltaClass(), imdSize));
        result.append('\n');
        result.append("Delta Caller Method:");
        result.append('\n');
        result.append(averageMetrics(i.getDeltaCallerMethod(), imdSize));
        result.append('\n');
        result.append("Delta Inlined Method:");
        result.append('\n');
        result.append(averageMetrics(i.getDeltaInlinedMethod(), imdSize));
        result.append('\n');
        result.append("Start Class:");
        result.append('\n');
        result.append(averageMetrics(i.getStartClass(), imdSize));
        result.append('\n');
        result.append("Start Caller Method:");
        result.append('\n');
        result.append(averageMetrics(i.getStartCallerMethod(), imdSize));
        result.append('\n');
        result.append("Start Inlined Method:");
        result.append('\n');
        result.append(averageMetrics(i.getStartInlinedMethod(), imdSize));
        result.append('\n');

        result.append("Move Method Data");
        result.append('\n');
        result.append("--------------------------------------------------" + '\n');
        int mmdSize = moveMethodData.size();
        result.append("Analyzed items: " + mmdSize);
        result.append('\n');
        MoveMethodData m = sumMoveMethod(moveMethodData);
        result.append('\n');
        result.append("Delta Base Class:");
        result.append('\n');
        result.append(averageMetrics(m.getDeltaBaseClass(), mmdSize));
        result.append('\n');
        result.append("Delta Target Class:");
        result.append('\n');
        result.append(averageMetrics(m.getDeltaTargetClass(), mmdSize));
        result.append('\n');
        result.append("Delta Method:");
        result.append('\n');
        result.append(averageMetrics(m.getDeltaMethod(), mmdSize));
        result.append('\n');
        result.append("Start Base Class:");
        result.append('\n');
        result.append(averageMetrics(m.getStartBaseClass(), mmdSize));
        result.append('\n');
        result.append("Start Target Class:");
        result.append('\n');
        result.append(averageMetrics(m.getStartTargetClass(), mmdSize));
        result.append('\n');
        result.append("Start Method:");
        result.append('\n');
        result.append(averageMetrics(m.getStartMethod(), mmdSize));
        result.append('\n');

        result.append("Move Method to new Class Data");
        result.append('\n');
        result.append("--------------------------------------------------" + '\n');
        int mmndSize = moveMethodNewClassData.size();
        result.append("Analyzed items: " + mmndSize);
        result.append('\n');
        MoveMethodData mn = sumMoveMethod(moveMethodNewClassData);
        result.append("Delta Base Class:");
        result.append('\n');
        result.append(averageMetrics(mn.getDeltaBaseClass(), mmndSize));
        result.append('\n');
        result.append("Delta Target Class:");
        result.append('\n');
        result.append(averageMetrics(mn.getDeltaTargetClass(), mmndSize));
        result.append('\n');
        result.append("Delta Method:");
        result.append('\n');
        result.append(averageMetrics(mn.getDeltaMethod(), mmndSize));
        result.append('\n');
        result.append("Start Base Class:");
        result.append('\n');
        result.append(averageMetrics(mn.getStartBaseClass(), mmndSize));
        result.append('\n');
        result.append("Start Target Class:");
        result.append('\n');
        result.append(averageMetrics(mn.getStartTargetClass(), mmndSize));
        result.append('\n');
        result.append("Start Method:");
        result.append('\n');
        result.append(averageMetrics(mn.getStartMethod(), mmndSize));

        return result.toString();
    }

    private ExtractMethodData sumExtractMethodData() {
        ExtractMethodData sum = new ExtractMethodData();

        //init
        for (String s : metricNames) {
            sum.getDeltaClass().put(s, 0.0);
            sum.getDeltaBaseMethod().put(s, 0.0);
            sum.getDeltaNewMethod().put(s, 0.0);
            sum.getStartClass().put(s, 0.0);
            sum.getStartBaseMethod().put(s, 0.0);
        }

        for (var emd : extractMethodData) {
            for (String s : metricNames) {
                double tmpDeltaClass = sum.getDeltaClass().get(s);
                sum.getDeltaClass().put(s, tmpDeltaClass + emd.getDeltaClass().get(s));
                double tmpDeltaBaseMethod = sum.getDeltaBaseMethod().get(s);
                sum.getDeltaBaseMethod().put(s, tmpDeltaBaseMethod + emd.getDeltaBaseMethod().get(s));
                double tmpDeltaNewMethod = sum.getDeltaNewMethod().get(s);
                sum.getDeltaNewMethod().put(s, tmpDeltaNewMethod + emd.getDeltaNewMethod().get(s));
                double tmpStartClass = sum.getStartClass().get(s);
                sum.getStartClass().put(s, tmpStartClass + emd.getStartClass().get(s));
                double tmpStartBaseMethod = sum.getStartBaseMethod().get(s);
                sum.getStartBaseMethod().put(s, tmpStartBaseMethod + emd.getStartBaseMethod().get(s));
            }
        }
        return sum;
    }

    private InlineMethodData sumInlineMethodData() {
        InlineMethodData sum = new InlineMethodData();

        //init
        for (String s : metricNames) {
            sum.getDeltaClass().put(s, 0.0);
            sum.getDeltaCallerMethod().put(s, 0.0);
            sum.getDeltaInlinedMethod().put(s, 0.0);
            sum.getStartClass().put(s, 0.0);
            sum.getStartCallerMethod().put(s, 0.0);
            sum.getStartInlinedMethod().put(s, 0.0);
        }

        for (var imd : inlineMethodData) {
            for (String s : metricNames) {
                double tmpDeltaClass =  sum.getDeltaClass().get(s);
                sum.getDeltaClass().put(s, tmpDeltaClass + imd.getDeltaClass().get(s));
                double tmpDeltaCallerMethod = sum.getDeltaCallerMethod().get(s);
                sum.getDeltaCallerMethod().put(s, tmpDeltaCallerMethod + imd.getDeltaCallerMethod().get(s));
                double tmpDeltaInlinedMethod = sum.getDeltaInlinedMethod().get(s);
                sum.getDeltaInlinedMethod().put(s, tmpDeltaInlinedMethod + imd.getDeltaInlinedMethod().get(s));
                double tmpStartClass =  sum.getStartClass().get(s);
                sum.getStartClass().put(s, tmpStartClass + imd.getStartClass().get(s));
                double tmpStartCallerMethod = sum.getStartCallerMethod().get(s);
                sum.getStartCallerMethod().put(s, tmpStartCallerMethod + imd.getStartCallerMethod().get(s));
                double tmpStartInlinedMethod = sum.getStartInlinedMethod().get(s);
                sum.getStartInlinedMethod().put(s, tmpStartInlinedMethod + imd.getStartInlinedMethod().get(s));
            }
        }
        return sum;
    }

    private MoveMethodData sumMoveMethod(List<MoveMethodData> dataList) {
        MoveMethodData sum = new MoveMethodData();

        //init
        for (String s : metricNames) {
            sum.getDeltaBaseClass().put(s, 0.0);
            sum.getDeltaTargetClass().put(s, 0.0);
            sum.getDeltaMethod().put(s, 0.0);
            sum.getStartBaseClass().put(s, 0.0);
            sum.getStartTargetClass().put(s, 0.0);
            sum.getStartMethod().put(s, 0.0);
        }

        for (var mmd : dataList) {
            for (String s : metricNames) {
                double tmpDeltaBaseClass = sum.getDeltaBaseClass().get(s);
                sum.getDeltaBaseClass().put(s, tmpDeltaBaseClass + mmd.getDeltaBaseClass().get(s));
                double tmpDeltaTargetClass = sum.getDeltaTargetClass().get(s);
                sum.getDeltaTargetClass().put(s, tmpDeltaTargetClass + mmd.getDeltaTargetClass().get(s));
                double tmpDeltaMethod = sum.getDeltaMethod().get(s);
                sum.getDeltaMethod().put(s, tmpDeltaMethod + mmd.getDeltaMethod().get(s));
                double tmpStartBaseClass = sum.getStartBaseClass().get(s);
                sum.getStartBaseClass().put(s, tmpStartBaseClass + mmd.getStartBaseClass().get(s));
                double tmpStartTargetClass = sum.getStartTargetClass().get(s);
                sum.getStartTargetClass().put(s, tmpStartTargetClass + mmd.getStartTargetClass().get(s));
                double tmpStartMethod = sum.getStartMethod().get(s);
                sum.getStartMethod().put(s, tmpStartMethod + mmd.getStartMethod().get(s));
            }
        }
        return sum;
    }

    private String averageMetrics(Map<String, Double> metrics, double size) {
        StringBuilder res = new StringBuilder();
        for (String s : metricNames) {
            res.append('\t' + s + " -> " + Double.toString(metrics.get(s) / size) + '\n');
        }
        return res.toString();
    }
}
