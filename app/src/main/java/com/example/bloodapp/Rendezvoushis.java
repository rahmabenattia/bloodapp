package com.example.bloodapp;

public class Rendezvoushis {
    private String id;
    private String date;
    private String time;

    public Rendezvoushis() {
    }

    public Rendezvoushis(String id, String date, String time) {
        this.id = id;
        this.date = date;
        this.time = time;
    }

    public Rendezvoushis(String date, String time) {
    }

    public String getId() {
        return id;
    }


    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

  }
