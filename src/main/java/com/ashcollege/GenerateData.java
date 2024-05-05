package com.ashcollege;

import com.ashcollege.entities.League;
import com.ashcollege.entities.Match;
import com.ashcollege.entities.Player;
import com.ashcollege.entities.Team;
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
            String[] teamsArray = {
                    "Real Madrid", "Girona", "Barcelona", "Atletico Madrid", "Athletic Bilbao", "Real Betis", "Real Sociedad", "Las Palmas",
                    "Valencia", "Getafe", "Osasuna", "Alaves", "Villarreal", "Rayo Vallecano", "Sevilla", "Mallorca", "Celta Vigo", "Cadiz", "Granada", "Almeria"
            };
            Random random = new Random();
            for (String name:teamsArray) {
                int rating = random.nextInt(15,100);
                int morale = random.nextInt(1,20);

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
                for (int i = 0; i < 11; i++) {
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
            for (int i = 0; i < teams.size() - 1; i++) {
                for (int j = 0; j < teams.size() / 2; j++) {
                    Team homeTeam = teamsCopy.get(j);
                    Team awayTeam = teamsCopy.get(teams.size() - 1 - j);
                    Match match = new Match(homeTeam, awayTeam, i + 1, random.nextInt(45));
                    Match match2 = new Match(awayTeam, homeTeam, i + teams.size(),random.nextInt(45));
                    matches.add(match);
                    matches.add(match2);
                }
                Collections.rotate(teamsCopy.subList(1, teamsCopy.size()), 1);
            }
        }
        persist.saveAll(matches);
        return matches;
    }
}
