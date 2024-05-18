package com.ashcollege.controllers;

import com.ashcollege.GenerateResult;
import com.ashcollege.Persist;
import com.ashcollege.entities.*;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.UserResponse;
import com.ashcollege.utils.Constants;
import com.ashcollege.utils.DbUtils;
import com.ashcollege.utils.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.scanner.Constant;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
public class GeneralController {

    @Autowired
    private DbUtils dbUtils;
    @Autowired
    private Persist persist;

    @Autowired
    private GenerateResult generateResult;



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
