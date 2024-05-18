package com.ashcollege.entities;

import com.ashcollege.Persist;

import java.util.List;

public class BetsForm<T> {

    private int id;
    private User owner;
    private List<Bet> bets;
    private int moneyBet;
    private int round;


    public BetsForm(User owner,int moneyBet, int round) {
        this.owner = owner;
        this.moneyBet=moneyBet;
        this.round=round;
    }

    public BetsForm(){

    }

    public boolean winForm(Persist persist){
        for (Bet bet: this.bets) {
            bet.getMatch().setGoals(persist.loadMatchGoals(bet.getMatch().getId()));
            if (bet.getMatch().winnerTeam() != bet.getUserBet()){
                return false;
            }
        }
        return true;
    }

    public float getTotalRatio(){
        float ratio = 1;
        for (Bet bet: this.bets) {
            ratio*= bet.getRatio();
        }
        return ratio;
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

    public List<Bet> getBets() {
        return bets;
    }

    public void setBets(List<Bet> bets) {
        this.bets = bets;
    }

    public int getMoneyBet() {
        return moneyBet;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public void setMoneyBet(int moneyBet) {
        this.moneyBet = moneyBet;
    }
}
