package com.ashcollege.entities;

public class TeamStatistics<T> {

    private int teamId;
    private int gamesPlayed;
    private int goalsScored;
    private int goalsConceded;
    private int wins;
    private int draws;
    private int losses;
    private int points;

    public TeamStatistics() {
    }


    public int getDifference(){
        return this.goalsScored - this.goalsConceded;
    }
    public void calcPoints(){
        setPoints((this.wins*3) + this.draws);
    }

    public double getAvgGoalsPerMatch(){
        if (this.gamesPlayed==0){
            return 0;
        }
        return (double) this.goalsScored / this.gamesPlayed;
    }

    public double getAvgConcededPerMatch(){
        if (this.gamesPlayed==0){
            return 0;
        }
        return (double) this.goalsConceded / this.gamesPlayed;
    }

    public void addMatch(){
        this.gamesPlayed += 1;
    }

    public void addGoal(){
        this.goalsScored += 1;
    }

    public void addConcededGoal(){
        this.goalsConceded += 1;
    }

    public void addWin(){
        this.wins += 1;
    }

    public void removeWin(){
        this.wins -= 1;
    }

    public void addLose(){
        this.losses += 1;
    }

    public void removeLose(){
        this.losses -= 1;
    }

    public void addDraw(){
        this.draws += 1;
    }

    public void removeDraw(){
        this.draws -= 1;
    }


    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGoalsScored() {
        return goalsScored;
    }

    public void setGoalsScored(int goalsScored) {
        this.goalsScored = goalsScored;
    }

    public int getGoalsConceded() {
        return goalsConceded;
    }

    public void setGoalsConceded(int goalsConceded) {
        this.goalsConceded = goalsConceded;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}





