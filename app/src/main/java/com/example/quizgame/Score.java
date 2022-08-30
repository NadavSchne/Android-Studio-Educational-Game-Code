package com.example.quizgame;

public class Score {                                                                                // class Score. contains data to set in the list view
    private int score;
    private String username;
    private int category;
    private int difficulty;
    private int id;

    public Score(){

    }
    public Score(int score, String username, int category, int difficulty) {                        // receiving score, name, category and diffculty level
        this.score = score;
        this.username = username;
        this.category = category;
        this.difficulty = difficulty;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
