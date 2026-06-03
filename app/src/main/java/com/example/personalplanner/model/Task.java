package com.example.personalplanner.model;

import java.io.Serializable;

public class Task implements Serializable {
    private int taskId;
    private String title;
    private String description;
    private String date;
    private String time;
    private int status;
    private int categoryId;
    private int userId;

    public Task() {
    }

    public Task(int taskId, String title, String description, String date,
                String time, int status, int categoryId, int userId) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.status = status;
        this.categoryId = categoryId;
        this.userId = userId;
    }

    public int getTaskId() {
        return taskId;
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

    public int getStatus() {
        return status;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
