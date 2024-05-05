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
        int homeProbability = (int)match.getMatchProbabilities().getHomeTeamWin() / 10;
        int awayProbability = (int)match.getMatchProbabilities().getAwayTeamWin() / 10;
        int byWeather = getGoalsProbabilityByWeather(match.getTemperature());
        int homeGoals = 0;
        int awayGoals = 0;
        if (result == '1'){
            homeGoals = random.nextInt(addGoalByWeather(homeProbability,byWeather))+1;
            awayGoals = random.nextInt(Math.min(homeGoals, addGoalByWeather(awayProbability,byWeather)));
        }else if (result == '2'){
            awayGoals = random.nextInt(addGoalByWeather(awayProbability,byWeather))+1;
            homeGoals = random.nextInt(Math.min(awayGoals,addGoalByWeather(homeProbability,byWeather)));
        }else {
            homeGoals = random.nextInt(Math.min(addGoalByWeather(homeProbability,byWeather),addGoalByWeather(awayProbability,byWeather)));
            awayGoals = homeGoals;
        }
        playersRecovery(match);
        injuredPlayers(match);

        List<Goal> homeTeamGoals = generateGoals(homeGoals, match, true);
        List<Goal> awayTeamGoals = generateGoals(awayGoals, match, false);
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
        int result = random.nextInt(101);
        if (result < 3){
            choosePlayersForInjury(match, 4,random);
        }else if (result < 9){
            choosePlayersForInjury(match, 3,random);
        }else if (result < 18){
            choosePlayersForInjury(match, 2,random);
        }else if (result < 30){
            choosePlayersForInjury(match, 1,random);
        }
    }

    public void choosePlayersForInjury(Match match, int count, Random random){
        Team homeTeam = match.getHomeTeam();
        Team awayTeam = match.getAwayTeam();
        Player player = null;
        for (int i = 0; i < count; i++) {
            int result = random.nextInt(2);
            if (result == 0){
                player = (Player) homeTeam.getPlayers().get(random.nextInt(homeTeam.getPlayers().size()));
            }else {
                player = (Player) awayTeam.getPlayers().get(random.nextInt(awayTeam.getPlayers().size()));
            }
            player.setInjured(true);
        }
        persist.saveAll(homeTeam.getPlayers());
        persist.saveAll(awayTeam.getPlayers());
    }

    public int addGoalByWeather(int teamProbability,int byWeather){
        int result = teamProbability + byWeather;
        if (result < 1){
            result = 1;
        }
        return result;
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


    public int getGoalsProbabilityByWeather(double temp){
        int score = 0;
        if (temp < 10 || temp >= 35){
            score -=1;
        }else if ((temp >=10 && temp<15) || (temp >=30 && temp < 35)){
            score += 1;
        }else if (temp >=20 && temp < 30){
            score += 2;
        }
        return score;
    }




}
