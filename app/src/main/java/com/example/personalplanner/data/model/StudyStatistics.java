package com.example.personalplanner.data.model;

public class StudyStatistics {
    private final int totalPlans;
    private final int completedPlans;
    private final int pendingPlans;
    private final int courseCount;
    private final int plannedMinutes;
    private final int overduePlans;
    private final int assignmentCount;
    private final int classCount;
    private final int partTimeCount;
    private final int personalCount;

    public StudyStatistics(int totalPlans, int completedPlans, int pendingPlans,
                           int courseCount, int plannedMinutes) {
        this(totalPlans, completedPlans, pendingPlans, courseCount, plannedMinutes,
                0, 0, 0, 0, 0);
    }

    public StudyStatistics(int totalPlans, int completedPlans, int pendingPlans,
                           int courseCount, int plannedMinutes, int overduePlans,
                           int assignmentCount, int classCount, int partTimeCount,
                           int personalCount) {
        this.totalPlans = totalPlans;
        this.completedPlans = completedPlans;
        this.pendingPlans = pendingPlans;
        this.courseCount = courseCount;
        this.plannedMinutes = plannedMinutes;
        this.overduePlans = overduePlans;
        this.assignmentCount = assignmentCount;
        this.classCount = classCount;
        this.partTimeCount = partTimeCount;
        this.personalCount = personalCount;
    }

    public int getTotalPlans() {
        return totalPlans;
    }

    public int getCompletedPlans() {
        return completedPlans;
    }

    public int getPendingPlans() {
        return pendingPlans;
    }

    public int getCourseCount() {
        return courseCount;
    }

    public int getPlannedMinutes() {
        return plannedMinutes;
    }

    public int getOverduePlans() {
        return overduePlans;
    }

    public int getAssignmentCount() {
        return assignmentCount;
    }

    public int getClassCount() {
        return classCount;
    }

    public int getPartTimeCount() {
        return partTimeCount;
    }

    public int getPersonalCount() {
        return personalCount;
    }

    public int getCompletionPercent() {
        return totalPlans == 0 ? 0 : Math.round(completedPlans * 100f / totalPlans);
    }
}
