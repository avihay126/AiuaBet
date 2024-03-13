package com.ashcollege;

import com.ashcollege.entities.Goal;
import com.ashcollege.entities.Match;
import com.ashcollege.entities.Player;
import com.ashcollege.entities.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class GenerateResult {

    @Autowired
    private Persist persist;


    public List<Match> roundResult(List<Match> round){
        for (Match match: round) {
            matchResultDecide(match);
            match.setGoals(persist.loadMatchGoals(match.getId()));
        }
        return round;
    }



    public Match matchResultDecide(Match match){
        match.getHomeTeam().reInitTeam(persist);
        match.getAwayTeam().reInitTeam(persist);
        Random random = new Random();
        MatchProbabilities probabilities = matchProbabilityGenerator(match);


        int result = random.nextInt(101);
        if (result < probabilities.getHomeTeamWin()){
            System.out.println("home win");
            generateResult(match, '1');
        }else if (result >= probabilities.getHomeTeamWin() && result < probabilities.getHomeTeamWin()+ probabilities.getAwayTeamWin()){
            System.out.println("away win");
            generateResult(match, '2');
        }else {
            System.out.println("draw");
            generateResult(match, 'x');
        }
        return match;
    }

    public void generateResult(Match match, char result){
        Random random = new Random();
        double homeProbability = match.getMatchProbabilities().getHomeTeamWin();
        double awayProbability = match.getMatchProbabilities().getAwayTeamWin();
        int homeGoals = 0;
        int awayGoals = 0;
        if (result == '1'){
            homeGoals = random.nextInt(1,(int)homeProbability/10 + 2);
            awayGoals = random.nextInt(homeGoals);
        }else if (result == '2'){
            awayGoals = random.nextInt(1,(int) awayProbability/10 + 2);
            homeGoals = random.nextInt(awayGoals);
        }else {
            homeGoals = random.nextInt((int)homeProbability/10 + 2);
            awayGoals = homeGoals;
        }
        List<Goal> homeTeamGoals = generateGoals(homeGoals, match, true);
        List<Goal> awayTeamGoals = generateGoals(awayGoals, match, false);
        persist.save(match);


    }

    public List<Goal> generateGoals(int numOfGoals, Match match, boolean home){
        List<Goal> goals = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numOfGoals; i++) {
            Player scorer = chooseScorer(home? match.getHomeTeam():match.getAwayTeam());
            int minute = random.nextInt(1,91);
            Goal goal = new Goal(home,scorer,minute,match);
            persist.save(goal);
            goals.add(goal);
        }

        return goals;
    }

    public Player chooseScorer(Team team){
        Player player = null;
        Random random = new Random();
        List<Player> players = team.getPlayers();
        do {
            player = players.get(random.nextInt(players.size()));
        }while (player.isInjured());


        return player;
    }




    public MatchProbabilities matchProbabilityGenerator(Match match){
        int homeTeamScore = match.getHomeTeam().calcTeamScore() + 10;
        int awayTeamScore = match.getAwayTeam().calcTeamScore();
        double homeWin = (double) homeTeamScore / (homeTeamScore+awayTeamScore);
        double awayWin = 1 - homeWin;
        double draw = getDrawScoreByWeather(match.getTemperature());
        homeWin = homeWin*100;
        awayWin = awayWin*100;
        double diff = Math.abs(homeWin - awayWin);
        draw += getDrawScoreByTeamsScore(diff);
        homeWin = (1-(draw/100))* homeWin;
        awayWin = (1-(draw/100))* awayWin;
        MatchProbabilities matchProbabilities = new MatchProbabilities(homeWin, awayWin, draw);
        match.setMatchProbabilities(matchProbabilities);
        return matchProbabilities;
    }

    public double getDrawScoreByTeamsScore(double diff){
        double draw = 0;
        if (diff < 20){
            draw = 30;
        }else if (diff >= 20 && diff < 40){
            draw = 20;
        }else if (diff >= 40 && diff < 60){
            draw = 10;
        }else if (diff >=60 && diff<80){
            draw = 5;
        }else {
            draw = 2.5;
        }
        return draw;
    }


    public int getDrawScoreByWeather(double temp){
        int score = 0;
        if (temp < 10 || temp >= 35){
            score += 2;
        }else if ((temp >=10 && temp<15) || (temp >=30 && temp < 35)){
            score += 1;
        }else if (temp >=20 && temp < 30){
            score -= 1;
        }
        return score;
    }




}
