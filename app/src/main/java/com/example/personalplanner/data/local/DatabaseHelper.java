package com.example.personalplanner.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.personalplanner.data.model.PlanCategory;
import com.example.personalplanner.data.model.StudyPlan;
import com.example.personalplanner.data.model.StudyStatistics;
import com.example.personalplanner.data.model.User;
import com.example.personalplanner.utils.PasswordUtils;

import java.util.ArrayList;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "personal_planner.db";
    private static final int DATABASE_VERSION = 4;

    public static final int FILTER_ALL = -1;
    public static final int STATUS_UPCOMING = StudyPlan.STATUS_UPCOMING;
    public static final int STATUS_IN_PROGRESS = StudyPlan.STATUS_IN_PROGRESS;
    public static final int STATUS_COMPLETED = StudyPlan.STATUS_COMPLETED;
    public static final int STATUS_CANCELLED = StudyPlan.STATUS_CANCELLED;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_CATEGORIES = "plan_categories";
    private static final String TABLE_PLANS = "tasks";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE COLLATE NOCASE, " +
                "email TEXT NOT NULL UNIQUE COLLATE NOCASE, " +
                "password TEXT NOT NULL, " +
                "created_at TEXT NOT NULL)");
        createCategoriesTable(db);
        createPlansTable(db);
        createIndexes(db);
    }

    private void createCategoriesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORIES + " (" +
                "category_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category_name TEXT NOT NULL, " +
                "category_code TEXT, " +
                "note TEXT, " +
                "color TEXT NOT NULL DEFAULT '#1F6F68', " +
                "user_id INTEGER NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(user_id) ON DELETE CASCADE)");
    }

    private void createPlansTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PLANS + " (" +
                "task_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "date TEXT NOT NULL, " +
                "time TEXT NOT NULL, " +
                "end_time TEXT, " +
                "status INTEGER NOT NULL DEFAULT 0 CHECK(status BETWEEN 0 AND 3), " +
                "category_id INTEGER DEFAULT 0, " +
                "plan_type TEXT NOT NULL DEFAULT 'PERSONAL', " +
                "priority INTEGER NOT NULL DEFAULT 1 CHECK(priority BETWEEN 0 AND 2), " +
                "duration_minutes INTEGER NOT NULL DEFAULT 60, " +
                "reminder_enabled INTEGER NOT NULL DEFAULT 0, " +
                "reminder_minutes INTEGER NOT NULL DEFAULT 0, " +
                "location TEXT, " +
                "room TEXT, " +
                "subject TEXT, " +
                "repeat_rule TEXT NOT NULL DEFAULT 'NONE', " +
                "repeat_until TEXT, " +
                "wage REAL NOT NULL DEFAULT 0, " +
                "submitted INTEGER NOT NULL DEFAULT 0, " +
                "user_id INTEGER NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(user_id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            createLegacyCoursesTable(db);
            addColumnIfMissing(db, TABLE_PLANS, "course_id", "INTEGER DEFAULT 0");
            addColumnIfMissing(db, TABLE_PLANS, "priority", "INTEGER NOT NULL DEFAULT 1");
            addColumnIfMissing(db, TABLE_PLANS, "duration_minutes", "INTEGER NOT NULL DEFAULT 60");
            addColumnIfMissing(db, TABLE_PLANS, "reminder_enabled", "INTEGER NOT NULL DEFAULT 0");
        }
        if (oldVersion < 4) {
            createCategoriesTable(db);
            migrateCoursesToCategories(db);
            addColumnIfMissing(db, TABLE_PLANS, "category_id", "INTEGER DEFAULT 0");
            if (columnExists(db, TABLE_PLANS, "course_id")) {
                db.execSQL("UPDATE " + TABLE_PLANS +
                        " SET category_id = course_id WHERE category_id = 0 AND course_id > 0");
            }
            addColumnIfMissing(db, TABLE_PLANS, "end_time", "TEXT");
            addColumnIfMissing(db, TABLE_PLANS, "plan_type", "TEXT NOT NULL DEFAULT 'PERSONAL'");
            addColumnIfMissing(db, TABLE_PLANS, "reminder_minutes", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfMissing(db, TABLE_PLANS, "location", "TEXT");
            addColumnIfMissing(db, TABLE_PLANS, "room", "TEXT");
            addColumnIfMissing(db, TABLE_PLANS, "subject", "TEXT");
            addColumnIfMissing(db, TABLE_PLANS, "repeat_rule", "TEXT NOT NULL DEFAULT 'NONE'");
            addColumnIfMissing(db, TABLE_PLANS, "repeat_until", "TEXT");
            addColumnIfMissing(db, TABLE_PLANS, "wage", "REAL NOT NULL DEFAULT 0");
            addColumnIfMissing(db, TABLE_PLANS, "submitted", "INTEGER NOT NULL DEFAULT 0");
        }
        createIndexes(db);
    }

    private void createLegacyCoursesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS courses (" +
                "course_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "course_name TEXT NOT NULL, " +
                "course_code TEXT, " +
                "lecturer TEXT, " +
                "color TEXT NOT NULL DEFAULT '#1F6F68', " +
                "user_id INTEGER NOT NULL)");
    }

    private void migrateCoursesToCategories(SQLiteDatabase db) {
        if (!tableExists(db, "courses")) {
            return;
        }
        db.execSQL("INSERT OR IGNORE INTO " + TABLE_CATEGORIES +
                "(category_id, category_name, category_code, note, color, user_id) " +
                "SELECT course_id, course_name, course_code, lecturer, color, user_id FROM courses");
    }

    private void addColumnIfMissing(SQLiteDatabase db, String table, String column,
                                    String definition) {
        if (!columnExists(db, table, column)) {
            db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
        }
    }

    private boolean tableExists(SQLiteDatabase db, String table) {
        try (Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{table})) {
            return cursor.moveToFirst();
        }
    }

    private boolean columnExists(SQLiteDatabase db, String table, String column) {
        try (Cursor cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null)) {
            int nameIndex = cursor.getColumnIndexOrThrow("name");
            while (cursor.moveToNext()) {
                if (column.equals(cursor.getString(nameIndex))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void createIndexes(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_plans_user_date ON " +
                TABLE_PLANS + "(user_id, date, time)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_plans_user_status ON " +
                TABLE_PLANS + "(user_id, status)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_plans_category ON " +
                TABLE_PLANS + "(category_id)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_plans_type ON " +
                TABLE_PLANS + "(user_id, plan_type)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_categories_user ON " +
                TABLE_CATEGORIES + "(user_id, category_name)");
    }

    public boolean registerUser(String username, String email, String password) {
        ContentValues values = new ContentValues();
        values.put("username", username.trim());
        values.put("email", email.trim().toLowerCase(Locale.ROOT));
        values.put("password", PasswordUtils.hashPassword(password));
        values.put("created_at", String.valueOf(System.currentTimeMillis()));
        try (SQLiteDatabase db = getWritableDatabase()) {
            return db.insert(TABLE_USERS, null, values) != -1;
        }
    }

    public boolean checkUsernameExists(String username) {
        return userValueExists("username", username.trim());
    }

    public boolean checkEmailExists(String email) {
        return userValueExists("email", email.trim());
    }

    private boolean userValueExists(String column, String value) {
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.query(TABLE_USERS, new String[]{"user_id"},
                     column + " = ? COLLATE NOCASE", new String[]{value},
                     null, null, null, "1")) {
            return cursor.moveToFirst();
        }
    }

    public int loginUser(String username, String password) {
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.query(TABLE_USERS, new String[]{"user_id"},
                     "username = ? COLLATE NOCASE AND password = ?",
                     new String[]{username.trim(), PasswordUtils.hashPassword(password)},
                     null, null, null, "1")) {
            return cursor.moveToFirst()
                    ? cursor.getInt(cursor.getColumnIndexOrThrow("user_id")) : -1;
        }
    }

    public User getUser(int userId) {
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.query(TABLE_USERS,
                     new String[]{"user_id", "username", "email"},
                     "user_id = ?", new String[]{String.valueOf(userId)},
                     null, null, null, "1")) {
            if (!cursor.moveToFirst()) {
                return null;
            }
            return new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("username")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email"))
            );
        }
    }

    public long addCategory(String name, String code, String note, String color, int userId) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            return db.insert(TABLE_CATEGORIES, null, categoryValues(name, code, note, color, userId));
        }
    }

    public boolean updateCategory(int categoryId, String name, String code, String note,
                                  String color, int userId) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            return db.update(TABLE_CATEGORIES,
                    categoryValues(name, code, note, color, userId),
                    "category_id = ? AND user_id = ?",
                    new String[]{String.valueOf(categoryId), String.valueOf(userId)}) > 0;
        }
    }

    public boolean deleteCategory(int categoryId, int userId) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                values.put("category_id", 0);
                db.update(TABLE_PLANS, values, "category_id = ? AND user_id = ?",
                        new String[]{String.valueOf(categoryId), String.valueOf(userId)});
                boolean deleted = db.delete(TABLE_CATEGORIES,
                        "category_id = ? AND user_id = ?",
                        new String[]{String.valueOf(categoryId), String.valueOf(userId)}) > 0;
                db.setTransactionSuccessful();
                return deleted;
            } finally {
                db.endTransaction();
            }
        }
    }

    private ContentValues categoryValues(String name, String code, String note,
                                         String color, int userId) {
        ContentValues values = new ContentValues();
        values.put("category_name", name.trim());
        values.put("category_code", code.trim());
        values.put("note", note.trim());
        values.put("color", color);
        values.put("user_id", userId);
        return values;
    }

    public ArrayList<PlanCategory> getCategories(int userId) {
        ArrayList<PlanCategory> categories = new ArrayList<>();
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.query(TABLE_CATEGORIES, null, "user_id = ?",
                     new String[]{String.valueOf(userId)}, null, null,
                     "category_name COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                categories.add(mapCategory(cursor));
            }
        }
        return categories;
    }

    public long addStudyPlan(String title, String description, String date, String time,
                             String endTime, int categoryId, String planType, int priority,
                             int durationMinutes, boolean reminderEnabled, int reminderMinutes,
                             String location, String room, String subject, String repeatRule,
                             String repeatUntil, double wage, boolean submitted, int userId) {
        ContentValues values = planValues(title, description, date, time, endTime, categoryId,
                planType, priority, durationMinutes, reminderEnabled, reminderMinutes,
                location, room, subject, repeatRule, repeatUntil, wage, submitted);
        values.put("status", STATUS_UPCOMING);
        values.put("user_id", userId);
        try (SQLiteDatabase db = getWritableDatabase()) {
            return db.insert(TABLE_PLANS, null, values);
        }
    }

    public boolean updateStudyPlan(int planId, int userId, String title, String description,
                                   String date, String time, String endTime, int status,
                                   int categoryId, String planType, int priority,
                                   int durationMinutes, boolean reminderEnabled,
                                   int reminderMinutes, String location, String room,
                                   String subject, String repeatRule, String repeatUntil,
                                   double wage, boolean submitted) {
        ContentValues values = planValues(title, description, date, time, endTime, categoryId,
                planType, priority, durationMinutes, reminderEnabled, reminderMinutes,
                location, room, subject, repeatRule, repeatUntil, wage, submitted);
        values.put("status", status);
        try (SQLiteDatabase db = getWritableDatabase()) {
            return db.update(TABLE_PLANS, values, "task_id = ? AND user_id = ?",
                    new String[]{String.valueOf(planId), String.valueOf(userId)}) > 0;
        }
    }

    private ContentValues planValues(String title, String description, String date, String time,
                                     String endTime, int categoryId, String planType, int priority,
                                     int durationMinutes, boolean reminderEnabled,
                                     int reminderMinutes, String location, String room,
                                     String subject, String repeatRule, String repeatUntil,
                                     double wage, boolean submitted) {
        ContentValues values = new ContentValues();
        values.put("title", title.trim());
        values.put("description", description.trim());
        values.put("date", date);
        values.put("time", time);
        values.put("end_time", endTime);
        values.put("category_id", categoryId);
        values.put("plan_type", planType);
        values.put("priority", priority);
        values.put("duration_minutes", durationMinutes);
        values.put("reminder_enabled", reminderEnabled ? 1 : 0);
        values.put("reminder_minutes", reminderMinutes);
        values.put("location", location.trim());
        values.put("room", room.trim());
        values.put("subject", subject.trim());
        values.put("repeat_rule", repeatRule);
        values.put("repeat_until", repeatUntil);
        values.put("wage", wage);
        values.put("submitted", submitted ? 1 : 0);
        return values;
    }

    public ArrayList<StudyPlan> getStudyPlans(int userId, String keyword, int statusFilter,
                                               int categoryFilter, String typeFilter) {
        ArrayList<StudyPlan> plans = new ArrayList<>();
        StringBuilder selection = new StringBuilder("p.user_id = ?");
        ArrayList<String> args = new ArrayList<>();
        args.add(String.valueOf(userId));

        if (keyword != null && !keyword.trim().isEmpty()) {
            selection.append(" AND (p.title LIKE ? OR p.description LIKE ? OR pc.category_name LIKE ? OR p.subject LIKE ? OR p.location LIKE ?)");
            String pattern = "%" + keyword.trim() + "%";
            args.add(pattern);
            args.add(pattern);
            args.add(pattern);
            args.add(pattern);
            args.add(pattern);
        }
        if (statusFilter >= STATUS_UPCOMING && statusFilter <= STATUS_CANCELLED) {
            selection.append(" AND p.status = ?");
            args.add(String.valueOf(statusFilter));
        }
        if (categoryFilter > 0) {
            selection.append(" AND p.category_id = ?");
            args.add(String.valueOf(categoryFilter));
        }
        if (typeFilter != null && !typeFilter.trim().isEmpty()) {
            selection.append(" AND p.plan_type = ?");
            args.add(typeFilter);
        }

        String sql = selectPlanSql() + " WHERE " + selection + " ORDER BY p.date ASC, p.time ASC";
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.rawQuery(sql, args.toArray(new String[0]))) {
            while (cursor.moveToNext()) {
                plans.add(mapStudyPlan(cursor));
            }
        }
        return plans;
    }

    public ArrayList<StudyPlan> getStudyPlans(int userId, String keyword, int statusFilter,
                                               int categoryFilter) {
        return getStudyPlans(userId, keyword, statusFilter, categoryFilter, "");
    }

    public ArrayList<StudyPlan> getPendingReminderPlans() {
        ArrayList<StudyPlan> plans = new ArrayList<>();
        String sql = selectPlanSql() +
                " WHERE p.status IN (0, 1) AND p.reminder_enabled = 1 ORDER BY p.date, p.time";
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                plans.add(mapStudyPlan(cursor));
            }
        }
        return plans;
    }

    public ArrayList<StudyPlan> getStudyPlansByDate(int userId, String date) {
        ArrayList<StudyPlan> plans = new ArrayList<>();
        String sql = selectPlanSql() +
                " WHERE p.user_id = ? AND p.date = ? ORDER BY p.time ASC";
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.rawQuery(sql,
                     new String[]{String.valueOf(userId), date})) {
            while (cursor.moveToNext()) {
                plans.add(mapStudyPlan(cursor));
            }
        }
        return plans;
    }

    public ArrayList<StudyPlan> getUpcomingPlans(int userId, String fromDate, int limit) {
        ArrayList<StudyPlan> plans = new ArrayList<>();
        String sql = selectPlanSql() +
                " WHERE p.user_id = ? AND p.status IN (0, 1) AND p.date >= ? " +
                "ORDER BY p.date ASC, p.time ASC LIMIT ?";
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.rawQuery(sql,
                     new String[]{String.valueOf(userId), fromDate, String.valueOf(limit)})) {
            while (cursor.moveToNext()) {
                plans.add(mapStudyPlan(cursor));
            }
        }
        return plans;
    }

    public boolean hasTimeConflict(int userId, String date, String startTime, String endTime,
                                   int excludePlanId) {
        String effectiveEnd = endTime == null || endTime.trim().isEmpty() ? startTime : endTime;
        String sql = "SELECT task_id FROM " + TABLE_PLANS +
                " WHERE user_id = ? AND date = ? AND status IN (0, 1) AND task_id != ? " +
                "AND time < ? AND COALESCE(NULLIF(end_time, ''), time) > ? LIMIT 1";
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.rawQuery(sql, new String[]{
                     String.valueOf(userId), date, String.valueOf(excludePlanId),
                     effectiveEnd, startTime})) {
            return cursor.moveToFirst();
        }
    }

    public boolean deleteStudyPlan(int planId, int userId) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            return db.delete(TABLE_PLANS, "task_id = ? AND user_id = ?",
                    new String[]{String.valueOf(planId), String.valueOf(userId)}) > 0;
        }
    }

    public boolean updateStudyPlanStatus(int planId, int userId, int status) {
        ContentValues values = new ContentValues();
        values.put("status", status);
        try (SQLiteDatabase db = getWritableDatabase()) {
            return db.update(TABLE_PLANS, values, "task_id = ? AND user_id = ?",
                    new String[]{String.valueOf(planId), String.valueOf(userId)}) > 0;
        }
    }

    public StudyStatistics getStudyStatistics(int userId) {
        int total = 0;
        int completed = 0;
        int minutes = 0;
        int categories = 0;
        int overdue = 0;
        int assignments = 0;
        int classes = 0;
        int partTime = 0;
        int personal = 0;
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US)
                .format(new java.util.Date());
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor planCursor = db.rawQuery(
                     "SELECT COUNT(*) total, " +
                             "SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) completed, " +
                             "COALESCE(SUM(duration_minutes), 0) minutes, " +
                             "SUM(CASE WHEN status IN (0, 1) AND date < ? THEN 1 ELSE 0 END) overdue, " +
                             "SUM(CASE WHEN plan_type = 'ASSIGNMENT' THEN 1 ELSE 0 END) assignment_count, " +
                             "SUM(CASE WHEN plan_type = 'CLASS' THEN 1 ELSE 0 END) class_count, " +
                             "SUM(CASE WHEN plan_type = 'PART_TIME' THEN 1 ELSE 0 END) part_time_count, " +
                             "SUM(CASE WHEN plan_type = 'PERSONAL' THEN 1 ELSE 0 END) personal_count " +
                             "FROM " + TABLE_PLANS + " WHERE user_id = ?",
                     new String[]{today, String.valueOf(userId)});
             Cursor categoryCursor = db.rawQuery(
                     "SELECT COUNT(*) FROM " + TABLE_CATEGORIES + " WHERE user_id = ?",
                     new String[]{String.valueOf(userId)})) {
            if (planCursor.moveToFirst()) {
                total = planCursor.getInt(0);
                completed = planCursor.getInt(1);
                minutes = planCursor.getInt(2);
                overdue = planCursor.getInt(3);
                assignments = planCursor.getInt(4);
                classes = planCursor.getInt(5);
                partTime = planCursor.getInt(6);
                personal = planCursor.getInt(7);
            }
            if (categoryCursor.moveToFirst()) {
                categories = categoryCursor.getInt(0);
            }
        }
        return new StudyStatistics(total, completed, total - completed, categories, minutes,
                overdue, assignments, classes, partTime, personal);
    }

    private String selectPlanSql() {
        return "SELECT p.*, COALESCE(pc.category_name, '') AS category_name FROM " +
                TABLE_PLANS + " p LEFT JOIN " + TABLE_CATEGORIES +
                " pc ON p.category_id = pc.category_id AND p.user_id = pc.user_id";
    }

    private PlanCategory mapCategory(Cursor cursor) {
        return new PlanCategory(
                cursor.getInt(cursor.getColumnIndexOrThrow("category_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("category_name")),
                cursor.getString(cursor.getColumnIndexOrThrow("category_code")),
                cursor.getString(cursor.getColumnIndexOrThrow("note")),
                cursor.getString(cursor.getColumnIndexOrThrow("color")),
                cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
        );
    }

    private StudyPlan mapStudyPlan(Cursor cursor) {
        return new StudyPlan(
                cursor.getInt(cursor.getColumnIndexOrThrow("task_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getString(cursor.getColumnIndexOrThrow("date")),
                cursor.getString(cursor.getColumnIndexOrThrow("time")),
                cursor.getString(cursor.getColumnIndexOrThrow("end_time")),
                cursor.getInt(cursor.getColumnIndexOrThrow("status")),
                cursor.getInt(cursor.getColumnIndexOrThrow("category_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("category_name")),
                cursor.getString(cursor.getColumnIndexOrThrow("plan_type")),
                cursor.getInt(cursor.getColumnIndexOrThrow("priority")),
                cursor.getInt(cursor.getColumnIndexOrThrow("duration_minutes")),
                cursor.getInt(cursor.getColumnIndexOrThrow("reminder_enabled")) == 1,
                cursor.getInt(cursor.getColumnIndexOrThrow("reminder_minutes")),
                cursor.getString(cursor.getColumnIndexOrThrow("location")),
                cursor.getString(cursor.getColumnIndexOrThrow("room")),
                cursor.getString(cursor.getColumnIndexOrThrow("subject")),
                cursor.getString(cursor.getColumnIndexOrThrow("repeat_rule")),
                cursor.getString(cursor.getColumnIndexOrThrow("repeat_until")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("wage")),
                cursor.getInt(cursor.getColumnIndexOrThrow("submitted")) == 1,
                cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
        );
    }
}
