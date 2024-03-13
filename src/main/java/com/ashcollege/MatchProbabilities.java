package com.ashcollege;

public class MatchProbabilities {

    private double homeTeamWin;
    private double awayTeamWin;
    private double draw;

    public MatchProbabilities(double homeTeamWin, double awayTeamWin, double draw) {
        this.homeTeamWin = homeTeamWin;
        this.awayTeamWin = awayTeamWin;
        this.draw = draw;
    }

    public double getHomeTeamWin() {
        return homeTeamWin;
    }

    public void setHomeTeamWin(double homeTeamWin) {
        this.homeTeamWin = homeTeamWin;
    }

    public double getAwayTeamWin() {
        return awayTeamWin;
    }

    public void setAwayTeamWin(double awayTeamWin) {
        this.awayTeamWin = awayTeamWin;
    }

    public double getDraw() {
        return draw;
    }

    public void setDraw(double draw) {
        this.draw = draw;
    }
}
