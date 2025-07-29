package com.example.application.models;

public class Categories {
    private String id;
    private String name;
    private String description;
    private int is_active;

    public Categories() {
    }

    public Categories(String id, String name, String description, int is_active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.is_active = is_active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    @Override
    public String toString() {
        return name;
    }
}
