package com.ashcollege;


import com.ashcollege.entities.Goal;
import com.ashcollege.entities.Match;
import com.ashcollege.entities.Team;
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
//    public <T> List<T> loadTeams() {
//        List<T> teams = this.sessionFactory.getCurrentSession().createQuery("FROM com.ashcollege.entities.Team").list();
//        for (T team: teams) {
//            ((Team)team).setPlayers(new ArrayList<>());
//            ((Team) team).setPlayers(loadPlayersFromTeam(((Team) team).getId()));
//        }
//        return teams;
//    }
//    public <T> List<T> loadMatchs() {
//        return this.sessionFactory.getCurrentSession().createQuery("FROM com.ashcollege.entities.Match").list();
//    }
//    public <T> List<T> loadPlayers() {
//        return this.sessionFactory.getCurrentSession().createQuery("FROM com.ashcollege.entities.Player").list();
//    }


    public <T> List<T> loadRoundMatches(int roundId) {
        return this.sessionFactory.getCurrentSession().createQuery("FROM Match where round = :roundId")
                .setParameter("roundId",roundId)
                .list();
    }

//    public <T> List<T> loadRoundGoals(int roundId) {
//        List<Match> matches =  this.sessionFactory.getCurrentSession().createQuery("FROM Match where round = :roundId")
//                .setParameter("roundId",roundId)
//                .list();
//        List<Goal> goals = new ArrayList<>();
//        for (Match match: matches) {
//            match.setGoals(loadMatchGoals(match.getId()));
//            goals.addAll(loadMatchGoals(match.getId()));
//        }
//        return (List<T>) goals;
//    }

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
//    public void save(Object object) {
//        this.sessionFactory.getCurrentSession().saveOrUpdate(object);
//    }
//
//    public <T> T loadObject(Class<T> clazz, int oid) {
//        return this.getQuerySession().get(clazz, oid);
//    }
//



}