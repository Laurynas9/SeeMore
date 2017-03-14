package com.example.seemore;

import android.app.Application;


public class Global {


    private static Global instance = new Global();

    // Getter-Setters
    public static Global getInstance() {
        return instance;
    }

    public static void setInstance(Global instance) {
        Global.instance = instance;
    }

    private String[] notification_index=new String[2];


    private Global() {

    }


    public String getValue(int number) {
        return notification_index[number];
    }


    public void setValue(String notification_index, int number) {
        this.notification_index[number] = notification_index;
    }



}