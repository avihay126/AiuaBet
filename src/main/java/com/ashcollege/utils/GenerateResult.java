package com.ashcollege.utils;

import com.ashcollege.MatchProbabilities;
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


        int result = random.nextInt(Constants.MAX_PERCENTS);
        if (result < probabilities.getHomeTeamWin()){
            generateResult(match, Constants.HOME_WIN);
        }else if (result >= probabilities.getHomeTeamWin() && result < probabilities.getHomeTeamWin()+ probabilities.getAwayTeamWin()){
            generateResult(match, Constants.AWAY_WIN);
        }else {
            generateResult(match, Constants.DRAW_RESULT);
        }
        return match;
    }

    public void generateResult(Match match, char result){
        Random random = new Random();
        int homeProbability = (int)match.getMatchProbabilities().getHomeTeamWin() / 10;
        int awayProbability = (int)match.getMatchProbabilities().getAwayTeamWin() / 10;
        int byWeather = getGoalsProbabilityByWeather(match.getTemperature());
        int homeGoals = 0;
        int awayGoals = 0;
        if (result == Constants.HOME_WIN){
            homeGoals = random.nextInt(addGoalByWeather(homeProbability,byWeather))+1;
            awayGoals = random.nextInt(Math.min(homeGoals, addGoalByWeather(awayProbability,byWeather)));
        }else if (result == Constants.AWAY_WIN){
            awayGoals = random.nextInt(addGoalByWeather(awayProbability,byWeather))+1;
            homeGoals = random.nextInt(Math.min(awayGoals,addGoalByWeather(homeProbability,byWeather)));
        }else {
            homeGoals = random.nextInt(Math.min(addGoalByWeather(homeProbability,byWeather),addGoalByWeather(awayProbability,byWeather)));
            awayGoals = homeGoals;
        }
        playersRecovery(match);
        injuredPlayers(match);

        List<Goal> homeTeamGoals = generateGoals(homeGoals, match, Constants.IS_HOME);
        List<Goal> awayTeamGoals = generateGoals(awayGoals, match, Constants.IS_AWAY);
        persist.save(match);
    }

    public void playersRecovery(Match match){
        match.getHomeTeam().playersRecovery();
        persist.saveAll(match.getHomeTeam().getPlayers());
        match.getAwayTeam().playersRecovery();
        persist.saveAll(match.getAwayTeam().getPlayers());
    }

    public void injuredPlayers(Match match){
        Random random = new Random();
        int result = random.nextInt(Constants.MAX_PERCENTS);
        if (result < Constants.INJURY_PROB_LOW){
            choosePlayersForInjury(match, Constants.MANY_INJURIES,random);
        }else if (result < Constants.INJURY_PROB_MEDIUM){
            choosePlayersForInjury(match, Constants.MEDIUM_INJURIES,random);
        }else if (result < Constants.INJURY_PROB_HIGH){
            choosePlayersForInjury(match, Constants.FEW_INJURIES,random);
        }else if (result < Constants.INJURY_PROB_VERY_HIGH){
            choosePlayersForInjury(match, Constants.ONE_INJURY,random);
        }
    }

    public void choosePlayersForInjury(Match match, int count, Random random){
        Team homeTeam = match.getHomeTeam();
        Team awayTeam = match.getAwayTeam();
        Player player = null;
        for (int i = 0; i < count; i++) {
            int result = random.nextInt(2);
            if (result == Constants.HOME_TEAM_INJURY_CONDITION){
                player = (Player) homeTeam.getPlayers().get(random.nextInt(homeTeam.getPlayers().size()));
            }else {
                player = (Player) awayTeam.getPlayers().get(random.nextInt(awayTeam.getPlayers().size()));
            }
            player.setInjured(Constants.PLAYER_INJURY);
        }
        persist.saveAll(homeTeam.getPlayers());
        persist.saveAll(awayTeam.getPlayers());
    }

    public int addGoalByWeather(int teamProbability,int byWeather){
        int result = teamProbability + byWeather;
        if (result < Constants.MIN_GOALS_BOUND_PER_MATCH){
            result = Constants.MIN_GOALS_BOUND_PER_MATCH ;
        }
        return result;
    }


    public List<Goal> generateGoals(int numOfGoals, Match match, boolean home){
        List<Goal> goals = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numOfGoals; i++) {
            Player scorer = chooseScorer(home? match.getHomeTeam():match.getAwayTeam());
            int minute = random.nextInt(Constants.MAX_MINUTES) + 1;
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
        int homeTeamScore = match.getHomeTeam().calcTeamScore() + Constants.HOME_BONUS;
        int awayTeamScore = match.getAwayTeam().calcTeamScore();
        double homeWin = (double) homeTeamScore / (homeTeamScore+awayTeamScore);
        double awayWin = 1 - homeWin;
        double draw = getDrawScoreByWeather(match.getTemperature());
        homeWin = homeWin*Constants.MAX_PERCENTS;
        awayWin = awayWin*Constants.MAX_PERCENTS;
        double diff = Math.abs(homeWin - awayWin);
        draw += getDrawScoreByTeamsScore(diff);
        homeWin = (1-(draw/Constants.MAX_PERCENTS))* homeWin;
        awayWin = (1-(draw/Constants.MAX_PERCENTS))* awayWin;
        MatchProbabilities matchProbabilities = new MatchProbabilities(homeWin, awayWin, draw);
        match.setMatchProbabilities(matchProbabilities);
        return matchProbabilities;
    }

    public double getDrawScoreByTeamsScore(double diff){
        double draw = 0;
        if (diff < Constants.SCORE_DRAW_VERY_LOW_DIFF){
            draw = Constants.VERY_HIGH_DRAW_PROB;
        }else if (diff >= Constants.SCORE_DRAW_VERY_LOW_DIFF && diff < Constants.SCORE_DRAW_LOW_DIFF){
            draw = Constants.HIGH_DRAW_PROB;
        }else if (diff >= Constants.SCORE_DRAW_LOW_DIFF && diff < Constants.SCORE_DRAW_MEDIUM_DIFF){
            draw = Constants.MEDIUM_DRAW_PROB;
        }else if (diff >=Constants.SCORE_DRAW_MEDIUM_DIFF && diff<Constants.SCORE_DRAW_HIGH_DIFF){
            draw = Constants.LOW_DRAW_PROB;
        }else {
            draw = Constants.VERY_LOW_DRAW_PROB;
        }
        return draw;
    }


    public int getDrawScoreByWeather(double temp){
        int score = 0;
        if (temp < Constants.TEMPERATURE_LOW_BOUND || temp >= Constants.TEMPERATURE_HIGH_BOUND){
            score += Constants.DRAW_SCORE_INCREASE_BAD_WEATHER;
        }else if ((temp >=Constants.TEMPERATURE_LOW_BOUND && temp<Constants.TEMPERATURE_LOW) || (temp >=Constants.TEMPERATURE_HIGH && temp < Constants.TEMPERATURE_HIGH_BOUND)){
            score += Constants.DRAW_SCORE_INCREASE_WARM;
        }else if (temp >=Constants.TEMPERATURE_NORMAL && temp < Constants.TEMPERATURE_HIGH){
            score += Constants.DRAW_SCORE_DECREASE_OPTIMAL;
        }
        return score;
    }


    public int getGoalsProbabilityByWeather(double temp){
        int score = 0;
        if (temp < Constants.TEMPERATURE_LOW_BOUND || temp >= Constants.TEMPERATURE_HIGH_BOUND){
            score += Constants.SCORE_DECREASE_BAD_WEATHER;
        }else if ((temp >=Constants.TEMPERATURE_LOW_BOUND && temp<Constants.TEMPERATURE_LOW) || (temp >=Constants.TEMPERATURE_HIGH && temp < Constants.TEMPERATURE_HIGH_BOUND)){
            score += Constants.SCORE_INCREASE_WARM;
        }else if (temp >=Constants.TEMPERATURE_NORMAL && temp < Constants.TEMPERATURE_HIGH){
            score += Constants.SCORE_INCREASE_OPTIMAL;
        }
        return score;
    }




}
