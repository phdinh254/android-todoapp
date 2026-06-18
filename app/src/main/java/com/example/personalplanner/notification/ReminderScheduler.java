package com.example.personalplanner.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class ReminderScheduler {
    private static final SimpleDateFormat DATE_TIME_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

    private ReminderScheduler() {
    }

    public static void schedule(Context context, int planId, String title, String categoryName,
                                String date, String time, int reminderMinutes) {
        try {
            Date reminderTime = DATE_TIME_FORMAT.parse(date + " " + time);
            long triggerAt = reminderTime == null ? 0
                    : reminderTime.getTime() - reminderMinutes * 60_000L;
            if (reminderTime == null || triggerAt <= System.currentTimeMillis()) {
                cancel(context, planId);
                return;
            }
            AlarmManager alarmManager =
                    (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAt,
                        createPendingIntent(context, planId, title, categoryName)
                );
            }
        } catch (ParseException ignored) {
            cancel(context, planId);
        }
    }

    public static void cancel(Context context, int planId) {
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(createPendingIntent(context, planId, "", ""));
        }
    }

    public static void schedule(Context context, int planId, String title, String categoryName,
                                String date, String time) {
        schedule(context, planId, title, categoryName, date, time, 0);
    }

    private static PendingIntent createPendingIntent(Context context, int planId, String title,
                                                     String courseName) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(ReminderReceiver.EXTRA_PLAN_ID, planId);
        intent.putExtra(ReminderReceiver.EXTRA_TITLE, title);
        intent.putExtra(ReminderReceiver.EXTRA_COURSE, courseName);
        return PendingIntent.getBroadcast(
                context,
                planId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
}
