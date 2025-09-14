package com.example.medlo;

public class Medicine {
    private String name, time;

    public Medicine(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public String getName() { return name; }
    public String getTime() { return time; }
}
