package com.example.dell.eventmanager.Event;

import android.view.View;
import android.widget.TextView;

//import com.daimajia.swipe.SwipeLayout;
import com.example.dell.eventmanager.R;

import java.io.Serializable;
import java.util.HashMap;

public class Events implements Serializable {

    private String date;
    private String name;
    private String id;
    private String completed;

    public Events() {
    }

    public Events(String name, String date , String id , String completed) {
        this.date = date;
        this.name = name;
        this.id = id;
        this.completed = completed;
    }

    public String getEventName() {
        return name;
    }

    public void setEventName(String name) {
        this.name = name;
    }

    public String getEventDate() {
        return date;
    }

    public void setEventDate(String date) {
        this.date = date;
    }

    public String getEventKey() {
        return id;
    }

    public void setEventKey(String name) {
        this.id = id;
    }

    public String getEventComplete() {
        return completed;
    }

    public void setEventComplete(String completed) {
        this.completed = completed;
    }

    public HashMap<String,String> toEventsFirebaseObject() {
        HashMap<String,String> event =  new HashMap<String,String>();
        event.put("name", name);
        event.put("date", date);
        event.put("completed", completed);
        return event;
    }

}
