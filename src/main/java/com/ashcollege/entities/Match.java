package com.ashcollege.entities;

import com.ashcollege.MatchProbabilities;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Match<T> {

    private int id;
    private Team homeTeam;
    private Team awayTeam;
    private int round;
    private List<Goal> goals;
    @JsonIgnore
    private MatchProbabilities matchProbabilities;
    private int temperature;

    public Match(){

    }

    public Match(Team homeTeam, Team awayTeam, int round, int temperature) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.round = round;
        this.goals = new ArrayList<>();
        this.temperature = temperature;
    }



    public MatchProbabilities getMatchProbabilities() {
        return matchProbabilities;
    }

    public void setMatchProbabilities(MatchProbabilities matchProbabilities) {
        this.matchProbabilities = matchProbabilities;
    }

    public void addGoals(List<Goal> goals){
        this.goals.addAll(goals);
    }

    public void addGoal(Goal goal){
        this.goals.add(goal);
    }

    public List<Goal> getHomeGoals(){
        List<Goal> goals = new ArrayList<>();
        for (Goal goal: this.goals) {
            if (goal.isHome()){
                goals.add(goal);
            }
        }
        return goals;
    }

    public List<Goal> getAwayGoals(){
        List<Goal> goals = new ArrayList<>();
        for (Goal goal: this.goals) {
            if (!goal.isHome()){
                goals.add(goal);
            }
        }
        return goals;
    }


    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }
}
