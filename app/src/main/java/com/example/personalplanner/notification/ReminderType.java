package com.example.personalplanner.notification;

public enum ReminderType {
    NONE(-9999, false),
    ON_TIME(0, false),
    BEFORE_1_MIN(-1, false),
    BEFORE_5_MIN(-5, false),
    BEFORE_10_MIN(-10, false),
    BEFORE_20_MIN(-20, false),
    BEFORE_30_MIN(-30, false),
    EVERY_24_HOURS(-1440, true);

    private final int storedValue;
    private final boolean allDay;

    ReminderType(int storedValue, boolean allDay) {
        this.storedValue = storedValue;
        this.allDay = allDay;
    }

    public int getStoredValue() {
        return storedValue;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public long getOffsetMillis() {
        return (long) storedValue * 60_000L;
    }

    public static ReminderType fromStoredValue(int storedValue) {
        for (ReminderType type : values()) {
            if (type.storedValue == storedValue) {
                return type;
            }
        }
        if (storedValue == 1) return BEFORE_1_MIN;
        if (storedValue == 5) return BEFORE_5_MIN;
        if (storedValue == 10) return BEFORE_10_MIN;
        if (storedValue == 20) return BEFORE_20_MIN;
        if (storedValue == 30) return BEFORE_30_MIN;
        return ON_TIME;
    }
}
