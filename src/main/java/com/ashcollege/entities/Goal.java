package com.ashcollege.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Goal<T> {

    private int id;
    private boolean home;
    private Player scorer;
    private int minute;
    @JsonIgnore
    private Match match;


    public Goal() {

    }

    public Goal(boolean home, Player scorer, int minute, Match match) {
        this.home = home;
        this.scorer = scorer;
        this.minute = minute;
        this.match = match;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isHome() {
        return home;
    }

    public void setHome(boolean home) {
        this.home = home;
    }

    public Player getScorer() {
        return scorer;
    }

    public void setScorer(Player scorer) {
        this.scorer = scorer;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
}
