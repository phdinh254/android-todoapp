package com.example.personalplanner.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.personalplanner.model.Task;
import com.example.personalplanner.utils.PasswordUtils;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "personal_planner.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String TABLE_TASKS = "tasks";
    public static final String TABLE_CATEGORIES = "categories";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "email TEXT, " +
                "password TEXT NOT NULL, " +
                "created_at TEXT)";

        String createCategoriesTable = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                "category_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category_name TEXT NOT NULL, " +
                "user_id INTEGER)";

        String createTasksTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                "task_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "date TEXT, " +
                "time TEXT, " +
                "status INTEGER DEFAULT 0, " +
                "category_id INTEGER DEFAULT 0, " +
                "user_id INTEGER)";

        db.execSQL(createUsersTable);
        db.execSQL(createCategoriesTable);
        db.execSQL(createTasksTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);

        onCreate(db);
    }

    public boolean registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        String hashedPassword = PasswordUtils.hashPassword(password);

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", hashedPassword);
        values.put("created_at", String.valueOf(System.currentTimeMillis()));

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1;
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT user_id FROM " + TABLE_USERS + " WHERE username = ?",
                new String[]{username}
        );

        boolean exists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return exists;
    }

    public int loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String hashedPassword = PasswordUtils.hashPassword(password);

        Cursor cursor = db.rawQuery(
                "SELECT user_id FROM " + TABLE_USERS + " WHERE username = ? AND password = ?",
                new String[]{username, hashedPassword}
        );

        int userId = -1;

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
        }

        cursor.close();
        db.close();

        return userId;
    }

    public boolean addTask(String title, String description, String date, String time,
                           int categoryId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("date", date);
        values.put("time", time);
        values.put("status", 0);
        values.put("category_id", categoryId);
        values.put("user_id", userId);

        long result = db.insert(TABLE_TASKS, null, values);
        db.close();

        return result != -1;
    }

    public ArrayList<Task> getAllTasks(int userId) {
        ArrayList<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_TASKS + " WHERE user_id = ? ORDER BY date ASC, time ASC",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            do {
                Task task = mapCursorToTask(cursor);
                taskList.add(task);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return taskList;
    }

    public boolean updateTask(int taskId, String title, String description,
                              String date, String time, int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("date", date);
        values.put("time", time);
        values.put("status", status);

        int result = db.update(
                TABLE_TASKS,
                values,
                "task_id = ?",
                new String[]{String.valueOf(taskId)}
        );

        db.close();

        return result > 0;
    }

    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_TASKS,
                "task_id = ?",
                new String[]{String.valueOf(taskId)}
        );

        db.close();

        return result > 0;
    }

    public ArrayList<Task> searchTasks(String keyword, int userId) {
        ArrayList<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_TASKS +
                        " WHERE user_id = ? AND title LIKE ? ORDER BY date ASC, time ASC",
                new String[]{String.valueOf(userId), "%" + keyword + "%"}
        );

        if (cursor.moveToFirst()) {
            do {
                Task task = mapCursorToTask(cursor);
                taskList.add(task);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return taskList;
    }

    public boolean updateTaskStatus(int taskId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", status);

        int result = db.update(
                TABLE_TASKS,
                values,
                "task_id = ?",
                new String[]{String.valueOf(taskId)}
        );

        db.close();

        return result > 0;
    }

    private Task mapCursorToTask(Cursor cursor) {
        return new Task(
                cursor.getInt(cursor.getColumnIndexOrThrow("task_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getString(cursor.getColumnIndexOrThrow("date")),
                cursor.getString(cursor.getColumnIndexOrThrow("time")),
                cursor.getInt(cursor.getColumnIndexOrThrow("status")),
                cursor.getInt(cursor.getColumnIndexOrThrow("category_id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
        );
    }
}
