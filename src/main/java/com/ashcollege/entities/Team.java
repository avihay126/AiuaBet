package com.ashcollege.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Team<T> {

    private int id;
    private String name;
    @JsonIgnore
    private List<Player> players;
    @JsonIgnore
    private int rating;
    @JsonIgnore
    private int morale;
    private League league;



    public Team() {

    }

    public Team(String name, int rating, int morale, League league) {
        this.name = name;
        this.players = new ArrayList<>();
        this.rating = rating;
        this.morale = morale;
        this.league = league;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }


    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getMorale() {
        return morale;
    }

    public void setMorale(int morale) {
        this.morale = morale;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }
}
