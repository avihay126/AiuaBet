package com.ashcollege;


import com.ashcollege.entities.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Transactional
@Component
@SuppressWarnings("unchecked")
public class Persist {

    private static final Logger LOGGER = LoggerFactory.getLogger(Persist.class);

    private final SessionFactory sessionFactory;




    @Autowired
    public Persist(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public Session getQuerySession() {
        return sessionFactory.getCurrentSession();
    }

    public <T> void saveAll(List<T> objects) {
        for (T object : objects) {
            sessionFactory.getCurrentSession().saveOrUpdate(object);
        }
    }
    public void save(Object object) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(object);
    }

    public <T> List<T> loadList(Class<T> clazz) {
        Session session = this.getQuerySession();
        String hql = "FROM " + clazz.getName();
        return session.createQuery(hql, clazz).list();

    }

//    public <T> List<T> loadLeagues() {
//        return this.sessionFactory.getCurrentSession().createQuery("FROM com.ashcollege.entities.League").list();
//    }
//    public <T> List<T> loadUsers() {
//        return this.sessionFactory.getCurrentSession().createQuery("FROM com.ashcollege.entities.User").list();
//
//    }
//
    public List<Team> loadTeamsWithStatistics() {
        List<Team> teams = this.sessionFactory.getCurrentSession().createQuery("FROM com.ashcollege.entities.Team").list();
        for (Team team: teams) {
            team.setTeamStatistics(loadTeamStatistics(team.getId()));
        }
        return teams;
    }
//    public <T> List<T> loadMatchs() {
//        return this.sessionFactory.getCurrentSession().createQuery("FROM com.ashcollege.entities.Match").list();
//    }
//    public <T> List<T> loadPlayers() {
//        return this.sessionFactory.getCurrentSession().createQuery("FROM com.ashcollege.entities.Player").list();
//    }


    public void addMatchForAll(){
        List<Team> teams = loadList(Team.class);
        for (Team team:teams) {
            TeamStatistics teamStatistics = loadTeamStatistics(team.getId());
            teamStatistics.addMatch();
            teamStatistics.addDraw();
            updateTeamPoints(team);
            save(teamStatistics);
        }
        saveAll(teams);
    }

    public void addGoalForTeam(Team team){
        TeamStatistics teamStatistics = loadTeamStatistics(team.getId());
        teamStatistics.addGoal();
        save(teamStatistics);
    }

    public void addConcededGoalForTeam(Team team){
        TeamStatistics teamStatistics = loadTeamStatistics(team.getId());
        teamStatistics.addConcededGoal();
        save(teamStatistics);
    }
    public void addWinForTeam(Team team){
        TeamStatistics teamStatistics = loadTeamStatistics(team.getId());
        teamStatistics.addWin();
        save(teamStatistics);
    }
    public void removeWinForTeam(Team team){
        TeamStatistics teamStatistics = loadTeamStatistics(team.getId());
        teamStatistics.removeWin();
        save(teamStatistics);
    }

    public void addLoseForTeam(Team team){
        TeamStatistics teamStatistics = loadTeamStatistics(team.getId());
        teamStatistics.addLose();
        save(teamStatistics);
    }
    public void removeLoseForTeam(Team team){
        TeamStatistics teamStatistics = loadTeamStatistics(team.getId());
        teamStatistics.removeLose();
        save(teamStatistics);
    }
    public void addDrawForTeam(Team team){
        TeamStatistics teamStatistics = loadTeamStatistics(team.getId());
        teamStatistics.addDraw();
        save(teamStatistics);
    }
    public void removeDrawForTeam(Team team){
        TeamStatistics teamStatistics = loadTeamStatistics(team.getId());
        teamStatistics.removeDraw();
        save(teamStatistics);
    }

    public void updateTeamPoints(Team team){
        TeamStatistics teamStatistics = loadTeamStatistics(team.getId());
        teamStatistics.calcPoints();
        save(teamStatistics);
    }


    public <T> List<T> loadRoundMatches(int roundId) {
        List<T> rounds =  this.sessionFactory.getCurrentSession().createQuery("FROM Match where round = :roundId")
                .setParameter("roundId",roundId)
                .list();
        for (T match:rounds) {
            ((Match)match).setGoals(new ArrayList<>());
        }
        return rounds;
    }

    public <T> List<T> loadRoundGoals(int roundId) {
        List<Match> matches =  this.sessionFactory.getCurrentSession().createQuery("FROM Match where round = :roundId")
                .setParameter("roundId",roundId)
                .list();
        List<Goal> goals = new ArrayList<>();
        for (Match match: matches) {

            goals.addAll(loadMatchGoals(match.getId()));
        }
        return (List<T>) goals;
    }

    public <T> List<T> loadMatchGoals(int matchId) {
        return this.sessionFactory.getCurrentSession().createQuery("FROM Goal where match.id = :matchId")
                .setParameter("matchId",matchId)
                .list();
    }


    public <T> List<T> loadPlayersFromTeam(int teamId) {
        return this.sessionFactory.getCurrentSession().createQuery("FROM Player where team.id = :teamId")
                .setParameter("teamId",teamId)
                .list();
    }

    public <T> T loadTeamStatistics(int teamId) {
        return (T) this.sessionFactory.getCurrentSession().createQuery("FROM TeamStatistics where teamId = :teamId")
                .setParameter("teamId",teamId)
                .uniqueResult();
    }
    public <T> T loadUserBySecret(String secret) {
        return (T) this.sessionFactory.getCurrentSession().createQuery("FROM User where secret = :secret")
                .setParameter("secret",secret)
                .uniqueResult();
    }
    public <T> T loadUserByEmail(String email) {
        return (T) this.sessionFactory.getCurrentSession().createQuery("FROM User where email = :email")
                .setParameter("email",email)
                .uniqueResult();
    }
//    public void save(Object object) {
//        this.sessionFactory.getCurrentSession().saveOrUpdate(object);
//    }
//
//    public <T> T loadObject(Class<T> clazz, int oid) {
//        return this.getQuerySession().get(clazz, oid);
//    }
//

    public Team loadTeamWithPlayers (int teamId) {
        Team team = (Team) getQuerySession().createQuery("FROM Team WHERE id = :id ")
                .setParameter("id", teamId)
                .uniqueResult();
        team.setPlayers(loadPlayersFromTeam(teamId));
        return team;
    }



}