package com.ashcollege.controllers;

import com.ashcollege.utils.*;
import com.ashcollege.entities.*;
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
    private DbUtils dbUtils;

    @Autowired
    private Persist persist;

    @Autowired
    private GenerateData generateData;

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
        runGameThread();
    }

    public void runGameThread(){
        startSeason();
        new Thread(()->{
            while (currentRound<= Constants.FINAL_ROUND){
                try {
                    Thread.sleep(loopTime);
                    time += secondToAddOrDip;
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (this.time == (Constants.MAX_MINUTES +1) && this.inGame){
                    endRound();
                }
                if (this.time == 0 && !this.inGame){
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

    public void endSeason(){

    }

    @RequestMapping(value = "restart",method = {RequestMethod.POST,RequestMethod.GET})
    public void restartSeason(){
        persist.deleteAll(Goal.class);
        persist.deleteAll(TeamStatistics.class);
        persist.deleteAll(Bet.class);
        persist.deleteAll(BetsForm.class);
        persist.deleteAll(Match.class);
        persist.deleteAll(Player.class);
        persist.deleteAll(Team.class);
        generateData.generateAll();
        runGameThread();
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
            jsonObject.put("roundNumber", currentRound);

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
            if (winner == Constants.HOME_TEAM_WINS){
                match.getHomeTeam().win();
                match.getAwayTeam().lose();
                persist.save(match.getHomeTeam());
                persist.save(match.getAwayTeam());
            }else if (winner== Constants.AWAY_TEAM_WINS){
                match.getAwayTeam().win();
                match.getHomeTeam().lose();
                persist.save(match.getAwayTeam());
                persist.save(match.getHomeTeam());
            }

        }
    }




    public void startSeason(){
        loopTime = Constants.SPEED_BETWEEN_MATCHES;
        currentRound = Constants.START_ROUND;
        matches= persist.loadRoundMatches(currentRound);
        currentRoundGoals = getRoundGoals(currentRound);
        secondToAddOrDip = Constants.SECOND_TO_DIP;
        time = Constants.ONE_MINUTE;
    }
    public void startRound(){
        this.time = 0;
        this.inGame = !this.inGame;
        loopTime = Constants.SPEED_IN_GAME;
        persist.addMatchForAll();
        secondToAddOrDip = Constants.SECOND_TO_ADD ;
    }

    public void endRound(){
        this.time=Constants.ONE_MINUTE;
        this.inGame = !this.inGame;
        checkWinner();
        loopTime = Constants.SPEED_BETWEEN_MATCHES;
        currentRound++;
        matches = persist.loadRoundMatches(currentRound);
        currentRoundGoals = getRoundGoals(currentRound);
        secondToAddOrDip = Constants.SECOND_TO_DIP;
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
        SseEmitter sseEmitter= new SseEmitter((long)(Constants.USER_MINUTES_TIME_CYCLE * 60 * 1000));
            users.add(new UserEvent(sseEmitter));
        return sseEmitter;
    }





}
