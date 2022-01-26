package com.example.week9;

import java.util.ArrayList;

public class Theathers {
    private static Theathers t = new Theathers();
    private String name;
    private int id;

    ArrayList<Theater> teatterit = new ArrayList<>();

    public static Theathers getInstance() {
        return t;
    }

    public void addTheater(String n, int i){
        Theater tt = new Theater(n, i);
        teatterit.add(tt);
    }

}

