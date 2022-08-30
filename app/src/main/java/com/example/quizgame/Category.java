package com.example.quizgame;

import android.content.Context;

public class Category {

    public static final int SPORTS = 1;                                                              // for foreign ID on the table
    public static final int MUSIC = 2;
    public static final int MATH = 3;



    private int id;
    private String name;


    public Category() {
    }
    public Category(String name) {
        this.name = name;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName(Context context) {
        switch (id){
            case 1:
                return context.getString(R.string.Sport);

            case 2:
                return context.getString(R.string.Music);
            case 3:
                return context.getString(R.string.Math);
        }

        return "";
    }
    public void setName(String name) {
        this.name = name;
    }
}
