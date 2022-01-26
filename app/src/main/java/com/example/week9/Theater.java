package com.example.week9;

public class Theater {
    private String name;
    private int id;

    public Theater(String n, int i){
        this.name = n;
        this.id = i;
    }
    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return name;
    }
}
