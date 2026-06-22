package com.example.personalplanner.data.model;

public class PlanRangeStats {
    private final int totalPlans;
    private final int completedPlans;
    private final int classMinutes;
    private final int workMinutes;
    private final int unsubmittedAssignments;
    private final int overduePlans;

    public PlanRangeStats(int totalPlans, int completedPlans, int classMinutes,
                          int workMinutes, int unsubmittedAssignments, int overduePlans) {
        this.totalPlans = totalPlans;
        this.completedPlans = completedPlans;
        this.classMinutes = classMinutes;
        this.workMinutes = workMinutes;
        this.unsubmittedAssignments = unsubmittedAssignments;
        this.overduePlans = overduePlans;
    }

    public int getTotalPlans() {
        return totalPlans;
    }

    public int getCompletedPlans() {
        return completedPlans;
    }

    public int getClassMinutes() {
        return classMinutes;
    }

    public int getWorkMinutes() {
        return workMinutes;
    }

    public int getUnsubmittedAssignments() {
        return unsubmittedAssignments;
    }

    public int getOverduePlans() {
        return overduePlans;
    }

    public int getCompletionPercent() {
        return totalPlans == 0 ? 0 : Math.round(completedPlans * 100f / totalPlans);
    }
}
