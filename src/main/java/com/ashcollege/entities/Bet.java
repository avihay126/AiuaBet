package com.ashcollege.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Bet<T> {

    private int id;
    @JsonIgnore
    private BetsForm betsForm;
    private Match match;
    private int userBet;
    private double ratio;


    public Bet() {
    }

    public Bet(int id, BetsForm betsForm, Match match, double ratio) {
        this.id = id;
        this.betsForm = betsForm;
        this.match = match;
        this.ratio = ratio;
    }

    public BetsForm getBetsForm() {
        return betsForm;
    }

    public void setBetsForm(BetsForm betsForm) {
        this.betsForm = betsForm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
