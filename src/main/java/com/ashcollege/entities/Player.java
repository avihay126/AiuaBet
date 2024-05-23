package com.ashcollege.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Player<T> implements Comparable{

    private int id;
    private String fullName;
    @JsonIgnore
    private Team team;
    @JsonIgnore
    private boolean injured;
    private List<Goal> goals;



    public Player(){

    }

    public Player(String fullName, Team team, boolean injured) {
        this.id = id;
        this.fullName = fullName;
        this.team = team;
        this.injured = injured;
        this.goals= new ArrayList<>();
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public boolean isInjured() {
        return injured;
    }

    public void setInjured(boolean injured) {
        this.injured = injured;
    }

    @Override
    public int compareTo(Object other) {
        int goalComparison = Integer.compare(((Player) other).getGoals().size(), this.getGoals().size());
        if (goalComparison == 0) {
            return this.fullName.compareTo(((Player) other).fullName);
        } else {
            return goalComparison;
        }
    }
}
