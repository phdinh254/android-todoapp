package com.example.personalplanner.model;

public class Category {
    private int categoryId;
    private String categoryName;
    private int userId;

    public Category() {
    }

    public Category(int categoryId, String categoryName, int userId) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getUserId() {
        return userId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
