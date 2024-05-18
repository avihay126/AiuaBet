package com.ashcollege.controllers;

import com.ashcollege.GenerateResult;
import com.ashcollege.Persist;
import com.ashcollege.entities.*;
import com.github.javafaker.Faker;
import com.github.javafaker.Lorem;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
public class LiveController {


    private List<UserEvent> users = new ArrayList<>();

    @Autowired
    private Persist persist;

    @Autowired
    private GenerateResult generateResult;

    private int time;
    private boolean inGame;
    private int loopTime;
    public static int currentRound;
    private int secondToAddOrDip;
    List<Match> matches = new ArrayList<>();
    List<Goal> currentRoundGoals = new ArrayList<>();


    @PostConstruct
    public void init(){
        startSeason();
        new Thread(()->{
            while (currentRound<= 38){
                try {
                    Thread.sleep(loopTime);
                    time += secondToAddOrDip;
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (this.time ==91 && this.inGame){
                    endRound();
                }
                if (this.time == 0 && !this.inGame){//לא לשכוח להחזיר ל31
                    startRound();
                }
                for (Goal goal:currentRoundGoals) {
                    if (goal.getMinute() == this.time && this.inGame){
                        for (Match match:matches) {
                            if (goal.getMatch().getId()==match.getId()){
                                int homeGoals = match.getHomeGoals().size();
                                int awayGoals = match.getAwayGoals().size();
                                if (goal.isHome()){
                                    if (homeGoals == awayGoals){
                                        takesTheLead(match.getHomeTeam(),match.getAwayTeam());
                                    }else if (homeGoals + 1 == awayGoals){
                                        getADraw(match.getHomeTeam(),match.getAwayTeam());
                                    }
                                    updateGoal(match.getHomeTeam(),match.getAwayTeam());
                                }else {
                                    if (homeGoals == awayGoals){
                                        takesTheLead(match.getAwayTeam(),match.getHomeTeam());
                                    }else if (awayGoals + 1 == homeGoals){
                                        getADraw(match.getAwayTeam(),match.getHomeTeam());
                                    }
                                    updateGoal(match.getAwayTeam(),match.getHomeTeam());
                                }
                                persist.updateTeamPoints(match.getHomeTeam());
                                persist.updateTeamPoints(match.getAwayTeam());
                                match.addGoal(goal);
                                break;
                            }
                        }
                    }
                }
                updateUsersLive();
            }
        }).start();
    }

    public void updateUsersLive(){
        for (Match match:matches) {
            match.getHomeTeam().reInitTeam(persist);
            match.getAwayTeam().reInitTeam(persist);
            match.setMatchProbabilities(generateResult.matchProbabilityGenerator(match));
        }
        for (int i =0; i< users.size();i++) {
            UserEvent user = users.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("time",this.time);
            jsonObject.put("inGame",this.inGame);
            jsonObject.put("round",matches);
            List<Team> teams = persist.loadTeamsWithStatistics();
            jsonObject.put("teams", teams);

            try {
                user.getSseEmitter().send(jsonObject.toString());
            }catch (Exception e){
                user.getSseEmitter().complete();
                users.remove(user);
            }
        }
    }

    public void updateGoal(Team scoring, Team conceded){
        persist.addGoalForTeam(scoring);
        persist.addConcededGoalForTeam(conceded);
    }

    public void takesTheLead(Team scoring, Team conceded){
        persist.removeDrawForTeam(scoring);
        persist.removeDrawForTeam(conceded);
        persist.addWinForTeam(scoring);
        persist.addLoseForTeam(conceded);
    }

    public void getADraw(Team scoring, Team conceded){
        persist.removeLoseForTeam(scoring);
        persist.removeWinForTeam(conceded);
        persist.addDrawForTeam(scoring);
        persist.addDrawForTeam(conceded);
    }


    public void checkWinner(){
        for (Match match: matches) {
            int winner = match.winnerTeam();
            if (winner == 1){
                match.getHomeTeam().win();
                match.getAwayTeam().lose();
                persist.save(match.getHomeTeam());
                persist.save(match.getAwayTeam());
            }else if (winner==2){
                match.getAwayTeam().win();
                match.getHomeTeam().lose();
                persist.save(match.getAwayTeam());
                persist.save(match.getHomeTeam());
            }

        }
    }




    public void startSeason(){
        loopTime = 1000;
        currentRound = 1;
        matches= persist.loadRoundMatches(currentRound);
        currentRoundGoals = getRoundGoals(currentRound);
        secondToAddOrDip = -1;
        time = 60;
    }
    public void startRound(){
        this.time = 0;
        this.inGame = !this.inGame;
        loopTime = 100; //לא לשכוח להחזיר ל333
        persist.addMatchForAll();
        secondToAddOrDip = 1;
    }

    public void endRound(){
        this.time=60;
        this.inGame = !this.inGame;
        checkWinner();
        loopTime = 1000;
        currentRound++;
        matches = persist.loadRoundMatches(currentRound);
        currentRoundGoals = getRoundGoals(currentRound);
        secondToAddOrDip = -1;
    }

    public List<Match> generateCurrentRoundResults (int roundId) {
        List<Match> matches = persist.loadRoundMatches(roundId);
        matches = generateResult.roundResult(matches);
        return matches;
    }

    public List<Goal> getRoundGoals(int roundId){
        List<Match> matches = generateCurrentRoundResults(roundId);
        List<Goal> goals = persist.loadRoundGoals(roundId);
        return goals;
    }

    @RequestMapping(value = "get-bets-result",method = {RequestMethod.POST,RequestMethod.GET})
    public User checkBetsResult(HttpServletRequest request){
        String cookie = LoginController.getSecretFromCookie(request);
        User user = null;
        if (cookie !=null){
            user = persist.loadUserBySecret(cookie);
            ArrayList<BetsForm> betsForms = persist.loadFormsByUserAndRound(user.getId(),currentRound);
            for (BetsForm form: betsForms) {
                form.setBets(persist.loadBetsByForm(form.getId()));
                if (form.winForm(persist)){
                    user.wonABet(form.getMoneyBet(), form.getTotalRatio());
                    persist.save(user);
                }
            }
        }

        return user;
    }





    @GetMapping(value = "start-streaming",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createStreamingSession(){
        SseEmitter sseEmitter= new SseEmitter((long)(40 * 60 * 1000));
            users.add(new UserEvent(sseEmitter));
        return sseEmitter;
    }





}
