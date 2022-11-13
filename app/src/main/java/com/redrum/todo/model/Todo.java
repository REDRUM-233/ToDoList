package com.redrum.todo.model;

import java.io.Serializable;

public class Todo implements Serializable {
    private int id;
    private String type;
    private String title;
    private String desc;

    public Todo(int id, String type, String title, String desc) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}


