package com.ashcollege.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Player<T> {

    private int id;
    private String fullName;
    @JsonIgnore
    private Team team;
    @JsonIgnore
    private boolean injured;



    public Player(){

    }

    public Player(String fullName, Team team, boolean injured) {
        this.id = id;
        this.fullName = fullName;
        this.team = team;
        this.injured = injured;
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
}
