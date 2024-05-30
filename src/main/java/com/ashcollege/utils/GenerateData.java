package com.ashcollege.utils;

import com.ashcollege.entities.League;
import com.ashcollege.entities.Match;
import com.ashcollege.entities.Player;
import com.ashcollege.entities.Team;
import com.ashcollege.utils.Persist;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class GenerateData {

    private Faker faker;

    @Autowired
    private Persist persist;

    public GenerateData() {
        faker = new Faker();

    }

    public void generateAll(){
        List<League> leagues = generateLeagues();
        List<Team> teams = generateTeams();
        List<Player> players = generatePlayers();
        List<Match> matches = generateSchedule();
    }

    public List<League> generateLeagues(){
        List<League> leagues = persist.loadList(League.class);
        if (leagues.size() == 0){
            League league = new League("la liga");
            leagues.add(league);
        }
        persist.saveAll(leagues);
        return leagues;
    }

    public List<Team> generateTeams(){
        List<Team> teams = persist.loadList(Team.class);
        if (teams.size() == 0){
            List<League> leagues = generateLeagues();
            String[] teamsArray = Constants.TEAM_NAMES;
            Random random = new Random();
            for (String name:teamsArray) {
                int rating = random.nextInt(Constants.MIN_RATING,Constants.MAX_RATING);
                int morale = random.nextInt(Constants.MIN_MORALE,Constants.MAX_MORALE);

                Team team = new Team(name,rating,morale, leagues.get(0));
                persist.save(team.getTeamStatistics());
                teams.add(team);
            }
        }
        persist.saveAll(teams);
        return teams;
    }

    public List<Player> generatePlayers(){
        List<Player> players = persist.loadList(Player.class);
        List<Team> teams = generateTeams();
        if (players.size() == 0){
            players = new ArrayList<>();
            for (Team team: teams) {
                team.setPlayers(persist.loadPlayersFromTeam(team.getId()));
                for (int i = 0; i < Constants.PLAYERS_PER_TEAM; i++) {
                    String fullName = faker.name().fullName();
                    Player player = new Player(fullName, team,false);
                    players.add(player);
                    team.addPlayer(player);
                }
            }
        }
        persist.saveAll(players);
        return players;
    }



    public List<Match> generateSchedule() {
        List<Match> matches = persist.loadList(Match.class);
        List<Team> teams = generateTeams();
        Random random = new Random();
        if (matches.size() == 0){
            List<Team> teamsCopy = new ArrayList<>(teams);
            boolean isEvenRound = false;
            for (int i = 0; i < teams.size() - 1; i++) {
                isEvenRound =!isEvenRound;
                for (int j = 0; j < teams.size() / 2; j++) {
                    Team homeTeam = teamsCopy.get(j);
                    Team awayTeam = teamsCopy.get(teams.size() - 1 - j);
                    Match[] teamsMeeting = null;
                    if((j % 2 == 0 && j != 0)||(j == 0 && isEvenRound)){
                        teamsMeeting = createTeamsMeeting(homeTeam,awayTeam,i+1,random);
                    }else {
                        teamsMeeting = createTeamsMeeting(awayTeam,homeTeam,i+1,random);
                    }
                    matches.add(teamsMeeting[Constants.FIRST_TEAMS_MEETING]);
                    matches.add(teamsMeeting[Constants.SECOND_TEAMS_MEETING]);
                }
                Collections.rotate(teamsCopy.subList(1, teamsCopy.size()), 1);
            }
        }
        persist.saveAll(matches);
        return matches;
    }

    private Match[] createTeamsMeeting(Team homeTeam,Team awayTeam,int round, Random random){
        Match match = new Match(homeTeam,awayTeam,round,random.nextInt(Constants.MAX_TEMPERATURE));
        Match match2 = new Match(awayTeam,homeTeam,round + Constants.TEAM_NAMES.length - 1,random.nextInt(Constants.MAX_TEMPERATURE));
        return new Match[]{match,match2};
    }
}
