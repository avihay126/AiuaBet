package com.ashcollege.controllers;

import com.ashcollege.utils.*;
import com.ashcollege.entities.*;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
public class GeneralController {


    @Autowired
    private Persist persist;



    @RequestMapping(value = "/get-schedule", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Match> getSchedule(){
        List<Match> matches = persist.loadList(Match.class);
        matches = getMatchGoals(matches);
        return matches;
    }

    public List<Match> getMatchGoals(List<Match> matches){
        for (Match match : matches) {
            if (match.getRound() <LiveController.currentRound){
                List<Goal> goals = persist.loadMatchGoals(match.getId());
                match.setGoals(goals);
            }else {
                match.setGoals(new ArrayList<>());
            }
        }
        return matches;
    }

    @RequestMapping(value = "/get-team-matches", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Match> getTeamMatches(int teamId){
        List<Match> matches = persist.loadAllTeamMatches(teamId);
        matches = getMatchGoals(matches);
        return matches;
    }

    @RequestMapping(value = "/get-team-players", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Player> getTeamPlayers(int teamId){
        List<Player> players = persist.loadPlayersFromTeam(teamId);
        return players;
    }

    @RequestMapping(value = "/get-top-scorer", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Player> getTopScorer(){
        List<Player> players = persist.loadPlayersWithGoals();
        players.sort((p1, p2) -> {
            int goalComparison = Integer.compare(p2.getGoals().size(), p1.getGoals().size());
            if (goalComparison == 0) {
                return p1.getFullName().compareTo(p2.getFullName());
            } else {
                return goalComparison;
            }
        });
        List<Player> topScorer = new ArrayList<>();
        for (int i = 0; i < Constants.MAX_TOP_SCORER_PLAYERS; i++) {
            if(i < players.size()){
                topScorer.add(players.get(i));
            }
        }
        return topScorer;
    }


    @RequestMapping(value = "/get-team-home-away", method = {RequestMethod.GET, RequestMethod.POST})
    public Object getTeamMatchesByType(boolean home){
        List<Team> teams = persist.loadList(Team.class);
        for (Team team: teams) {
            getStatByMatchesType(team,home);
        }
        return teams;
    }

    public static int getTeamGoals(Match match, boolean home){
        int homeGoals = 0;
        int awayGoals = 0;
        for (int i = 0; i < match.getGoals().size(); i++) {
            Goal goal =(Goal) match.getGoals().get(i);
            if (goal.isHome()){
                homeGoals+=1;
            }else {
                awayGoals+=1;
            }
        }
        if (home){
            return homeGoals;
        }
        return awayGoals;
    }


    public void getStatByMatchesType(Team team, boolean home){
        int gamesPlayed = 0;
        int goalsScored = 0;
        int goalsConceded = 0;
        int wins = 0;
        int draws = 0;
        int losses = 0;
        List<Match> matches = null;
        if (home){
            matches = persist.loadHomeTeamMatches(team.getId());
        }else {
            matches = persist.loadAwayTeamMatches(team.getId());
        }
        for (Match match: matches) {
            if (match.getRound()<LiveController.currentRound){
                match.setGoals(persist.loadMatchGoals(match.getId()));
                gamesPlayed+=1;
                goalsScored += getTeamGoals(match,home);
                goalsConceded += getTeamGoals(match,!home);
                int result = match.winnerTeam();
                switch (result){
                    case Constants.HOME_TEAM_WINS:
                        if (home){
                            wins+=1;
                        }else {
                            losses+=1;
                        }
                        break;
                    case Constants.AWAY_TEAM_WINS:
                        if (home){
                            losses+=1;
                        }else {
                            wins+=1;
                        }
                        break;
                    case Constants.DRAW:
                        draws+=1;
                        break;
                    default:
                        break;
                }
            }
        }
        int points = (wins * Constants.POINTS_BY_WIN) + draws;
        TeamStatistics teamStatistics = new TeamStatistics(team.getId(),gamesPlayed,goalsScored,goalsConceded,wins,draws,losses,points);
        team.setTeamStatistics(teamStatistics);
    }


    @RequestMapping(value = "/get-user-bet", method = {RequestMethod.GET, RequestMethod.POST})
    public Object getUserBets(@RequestBody LinkedHashMap betForm){
        User user = persist.loadUserBySecret((String) betForm.get("ownerSecret"));
        int moneyBet =Integer.parseInt((String) betForm.get("moneyBet")) ;
        BasicResponse basicResponse =null;
        if (user.getBalance()>= moneyBet){
            user.takeABet(moneyBet);
            persist.save(user);
            int round = (int) betForm.get("round");
            BetsForm betsForm = new BetsForm(user,moneyBet,round);
            persist.save(betsForm);
            betsForm.setBets(new ArrayList<>());
            ArrayList<LinkedHashMap> bets = (ArrayList<LinkedHashMap>) betForm.get("bets");
            for (LinkedHashMap bet: bets) {
                LinkedHashMap linkedMatch = (LinkedHashMap) bet.get("match");
                Match match = persist.loadObject(Match.class,(int)linkedMatch.get("id"));
                int userBet =(int) bet.get("userBet");
                double ratio = Double.parseDouble((String) bet.get("ratio"));
                Bet newBet = new Bet(betsForm,match,userBet,ratio);
                persist.save(newBet);
            }
            basicResponse = new UserResponse(true,null,user);
            return basicResponse;

        }
        basicResponse = new BasicResponse(false, Errors.NOT_ENOUGH_MONEY);

        return basicResponse;
    }

}
