package com.example.dell.eventmanager.Legend;

import java.io.Serializable;
import java.util.HashMap;

public class Legend implements Serializable {

    private String color;
    private String name;
    private String id;

    public Legend() {
    }

    public Legend(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getLegendName() {
        return name;
    }

    public void setLegendName(String name) {
        this.name = name;
    }

    public String getLegendColor() {
        return color;
    }

    public void setLegendColor(String color) {
        this.color = color;
    }

    public String getLegendKey() {
        return id;
    }

    public void setLegendKey(String id) {
        this.id = id;
    }


    public HashMap<String, String> toLegendsFirebaseObject() {
        HashMap<String, String> legend = new HashMap<String, String>();
        legend.put("name", name);
        legend.put("color",color);
        return legend;
    }

}


