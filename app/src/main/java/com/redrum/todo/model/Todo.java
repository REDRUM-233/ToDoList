package com.redrum.todo.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Todo implements Serializable {
    private int id;
    private int checked;
    /* 格式设置
     * 普通单次任务 0
     * 每周任务 1-从小到大记录生成时间如135,17(0表示每周单次任务)-(该事件月-日)
     * 每月任务 2-(该事件月)
     * 每年任务 3-(该事件年)
     * 自定义次数任务 4-剩余完成次数*/
    private String type;
    private String title;
    private String desc;

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


