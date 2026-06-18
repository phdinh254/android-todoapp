package com.example.personalplanner.data.model;

public class PlanCategory {
    private final int categoryId;
    private final String categoryName;
    private final String categoryCode;
    private final String note;
    private final String color;
    private final int userId;

    public PlanCategory(int categoryId, String categoryName, String categoryCode,
                        String note, String color, int userId) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryCode = categoryCode;
        this.note = note;
        this.color = color;
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getNote() {
        return note;
    }

    public String getColor() {
        return color;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        if (categoryCode == null || categoryCode.trim().isEmpty()) {
            return categoryName;
        }
        return categoryCode + " - " + categoryName;
    }
}
