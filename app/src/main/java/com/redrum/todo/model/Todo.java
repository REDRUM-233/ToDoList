package com.redrum.todo.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

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

    public String getTypeDesc() {
        String[] str = type.split("-");
        switch (Integer.parseInt(str[0])) {
            case 0:
                return "单次事件";
            case 1: {
                if (Integer.parseInt(str[1]) == 0)
                    return "每周事件-本次事件截止时间:" + str[2] + "月" + str[3] + "日";
                else
                    return "每周" + str[1] + "事件-本次事件截止时间:" + str[2] + "月" + str[3] + "日";
            }
            case 2:
                return "每月事件-本次事件截止时间:" + str[1] + "月";
            case 3:
                return "每年事件-本次事件截止时间:" + str[1] + "年";
            case 4:
                return "自定义次数任务-剩余完成次数:" + str[1];
        }
        return "错误类型";
    }

    public boolean isShowable(Date date) {
        String[] str = type.split("-");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (Integer.parseInt(str[0])) {
            case 0:
                return checked == 0;
            case 1:
                return str[2].contains(1 + cal.get(Calendar.MONTH) + "") &&
                        Integer.parseInt(str[3]) <= cal.get(Calendar.DAY_OF_MONTH) &&
                        str[1].contains("" + cal.get(Calendar.DAY_OF_WEEK));
            case 2:
                return str[1].contains(1 + cal.get(Calendar.MONTH) + "");
            case 3:
                return str[1].contains(cal.get(Calendar.YEAR) + "");
            case 4:
                return Integer.parseInt(str[1]) > 0;
        }
        return false;
    }

    public boolean isRenewable() {
        return "1234".contains(type.split("-")[0]);
    }

    public Todo renew() {
        String[] str = type.split("-");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        switch (Integer.parseInt(str[0])) {
            case 1:
                cal.add(Calendar.DATE, 7 - cal.get(Calendar.DAY_OF_WEEK));
                type = str[0] + "-" + str[1] + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
                return this;
            case 2:
                type = str[0] + "-" + str[1] + "-" + (cal.get(Calendar.MONTH) + 1);
                return this;
            case 3:
                type = str[0] + "-" + str[1] + "-" + (cal.get(Calendar.YEAR) + 1);
                return this;
            case 4: {
                type = str[0] + "-" + (Math.max(Integer.parseInt(str[1]) - 1, 0));
            }
        }
        return this;
    }

    public Todo changeType(int t, String addition) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        switch (t) {
            case 0:
                type = "0";
                break;
            case 1:
                cal.add(Calendar.DATE, 7 - cal.get(Calendar.DAY_OF_WEEK));
                type = "1-" + addition + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
                break;
            case 2:
                type = "2-" + cal.get(Calendar.MONTH);
                break;
            case 3:
                type = "3-" + cal.get(Calendar.YEAR);
                break;
            case 4:
                type = "4-" + addition;
        }
        return this;
    }
}


