package fi.helsinki.cs.tmc.client.core.testrunner.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Results of a single test run.
 */
public class TestRunResult {

    private final List<TestCaseResult> testCaseResults;

    public TestRunResult(final List<TestCaseResult> testCaseResults) {

        this.testCaseResults = testCaseResults;
    }

    public List<TestCaseResult> getTestCaseResults() {

        return testCaseResults;
    }

    public List<String> getAwardedPoints() {

        final Map<String, Boolean> pointsAwarded = new HashMap<>();

        for (final TestCaseResult result : testCaseResults) {
            for (String pointName : result.getPointNames()) {
                if (pointsAwarded.containsKey(pointName)) {
                    if (pointsAwarded.get(pointName) && !result.isSuccessful()) {
                        pointsAwarded.put(pointName, false);
                    }
                } else {
                    pointsAwarded.put(pointName, result.isSuccessful());
                }
            }
        }

        final List<String> awardedPoints = new ArrayList<>();

        for (Entry<String, Boolean> entry : pointsAwarded.entrySet()) {
            if (entry.getValue()) {
                awardedPoints.add(entry.getKey());
            }
        }

        return awardedPoints;
    }

    public List<String> getPointNames() {

        final Set<String> pointNames = new HashSet<>();
        for (final TestCaseResult result : testCaseResults) {
            for (String pointName : result.getPointNames()) {
                pointNames.add(pointName);
            }
        }

        return new ArrayList<String>(pointNames);
    }

    public boolean allTestsPassed() {

        for (final TestCaseResult result : testCaseResults) {
            if (!result.isSuccessful()) {
                return false;
            }
        }

        return true;
    }
}
