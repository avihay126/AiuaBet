package com.ashcollege.entities;

public class Bet {

    private int id;
    private User owner;
    private Match match;
    private int userBet;
    private double ratio;


    public Bet() {
    }

    public Bet(int id, User owner, Match match, double ratio) {
        this.id = id;
        this.owner = owner;
        this.match = match;

        this.ratio = ratio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public int getUserBet() {
        return userBet;
    }

    public void setUserBet(int userBet) {
        this.userBet = userBet;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
}
