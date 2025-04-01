package com.group46.controllers;

public class Score {

    private int score;

    public Score(int score) {
        this.score = 0;
    }

    public void scoreIncrease(int points) {
        score = score + points;
    }

    public int getScore() {
        return score;
    }
}
