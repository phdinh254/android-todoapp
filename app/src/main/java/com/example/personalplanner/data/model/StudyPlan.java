package com.example.personalplanner.data.model;

public class StudyPlan {
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_MEDIUM = 1;
    public static final int PRIORITY_HIGH = 2;

    public static final int STATUS_UPCOMING = 0;
    public static final int STATUS_IN_PROGRESS = 1;
    public static final int STATUS_COMPLETED = 2;
    public static final int STATUS_CANCELLED = 3;

    public static final String TYPE_ASSIGNMENT = "ASSIGNMENT";
    public static final String TYPE_CLASS = "CLASS";
    public static final String TYPE_PART_TIME = "PART_TIME";
    public static final String TYPE_PERSONAL = "PERSONAL";
    public static final String TYPE_EXAM = "EXAM";
    public static final String TYPE_PROJECT = "PROJECT";

    private final int planId;
    private final String title;
    private final String description;
    private final String date;
    private final String time;
    private final String endTime;
    private int status;
    private final int categoryId;
    private final String categoryName;
    private final String planType;
    private final int priority;
    private final int durationMinutes;
    private final boolean reminderEnabled;
    private final int reminderMinutes;
    private final String location;
    private final String room;
    private final String subject;
    private final String repeatRule;
    private final String repeatUntil;
    private final double wage;
    private final boolean submitted;
    private final int userId;

    public StudyPlan(int planId, String title, String description, String date, String time,
                     String endTime, int status, int categoryId, String categoryName,
                     String planType, int priority, int durationMinutes,
                     boolean reminderEnabled, int reminderMinutes, String location,
                     String room, String subject, String repeatRule, String repeatUntil,
                     double wage, boolean submitted, int userId) {
        this.planId = planId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.endTime = endTime;
        this.status = status;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.planType = planType;
        this.priority = priority;
        this.durationMinutes = durationMinutes;
        this.reminderEnabled = reminderEnabled;
        this.reminderMinutes = reminderMinutes;
        this.location = location;
        this.room = room;
        this.subject = subject;
        this.repeatRule = repeatRule;
        this.repeatUntil = repeatUntil;
        this.wage = wage;
        this.submitted = submitted;
        this.userId = userId;
    }

    public int getPlanId() {
        return planId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getPlanType() {
        return planType;
    }

    public int getPriority() {
        return priority;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public boolean isReminderEnabled() {
        return reminderEnabled;
    }

    public int getReminderMinutes() {
        return reminderMinutes;
    }

    public String getLocation() {
        return location;
    }

    public String getRoom() {
        return room;
    }

    public String getSubject() {
        return subject;
    }

    public String getRepeatRule() {
        return repeatRule;
    }

    public String getRepeatUntil() {
        return repeatUntil;
    }

    public double getWage() {
        return wage;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public int getUserId() {
        return userId;
    }
}
