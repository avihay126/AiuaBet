package com.ashcollege.entities;

public class Goal {

    private int id;
    private boolean home;
    private Player scorer;
    private int minute;
    private Match match;


    public Goal() {
    }

    public Goal(int id, boolean home, Player scorer, int minute, Match match) {
        this.id = id;
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
