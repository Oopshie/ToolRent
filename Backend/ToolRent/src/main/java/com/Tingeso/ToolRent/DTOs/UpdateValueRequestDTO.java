package com.Tingeso.ToolRent.DTOs;

public class UpdateValueRequestDTO {

    private String name;
    private String category;
    private int newReplacementValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getNewReplacementValue() {
        return newReplacementValue;
    }

    public void setNewReplacementValue(int newReplacementValue) {
        this.newReplacementValue = newReplacementValue;
    }
}
