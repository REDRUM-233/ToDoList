package com.redrum.todo.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Todo implements Serializable {
    private int id;
    private int checked;
    private String type;
    private String title;
    private String desc;

//    下面全是自动生成的

    public Todo(int id, int checked, String type, String title, String desc) {
        this.id = id;
        this.checked = checked;
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

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
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

    @NonNull
    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", checked=" + checked +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}


