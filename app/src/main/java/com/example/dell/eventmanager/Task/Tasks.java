package com.example.dell.eventmanager.Task;

import java.io.Serializable;
import java.util.HashMap;

public class Tasks implements Serializable {

    private String date;
    private String name;
    private String id;
    private String label;
    private String labelColor;
    private String completed;

    public Tasks() {
    }

    public Tasks(String name, String date, String id, String label, String labelColor, String completed) {
        this.date = date;
        this.name = name;
        this.id = id;
        this.label = label;
        this.labelColor = labelColor;
        this.completed = completed;
    }

    public String getTaskName() {
        return name;
    }

    public void setTaskName(String name) {
        this.name = name;
    }

    public String getTaskDate() {
        return date;
    }

    public void setTaskDate(String date) {
        this.date = date;
    }

    public String getTaskKey() {
        return id;
    }

    public void setTaskKey(String id) {
        this.id = id;
    }

    public void setTaskLabel(String lable) {
        this.label = lable;
    }

    public String getTaskLabel() {
        return label;
    }

    public String getTaskLabelColor() {
        return labelColor;
    }

    public void setTaskLabelColor(String lableColor) {
        this.labelColor = lableColor;
    }

    public String getTaskComplete() {
        return completed;
    }

    public void setTaskComplete(String completed) {
        this.completed = completed;
    }

    public HashMap<String, String> toTasksFirebaseObject() {
        HashMap<String, String> task = new HashMap<String, String>();
        task.put("name", name);
        task.put("date", date);
        task.put("lable", label);
        task.put("lableColor", labelColor);
        task.put("completed", completed);
        return task;
    }
}
