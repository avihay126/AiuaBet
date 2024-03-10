package com.ashcollege;


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
        try (Session session = this.getQuerySession()) {
            String hql = "FROM " + clazz.getName();
            return session.createQuery(hql, clazz).list();
        }
    }

//    public <T> List<T> loadLeagues() {
//        return this.sessionFactory.getCurrentSession().createQuery("FROM com.ashcollege.entities.League").list();
//
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
//
//    }
//    public <T> List<T> loadMatchs() {
//        return this.sessionFactory.getCurrentSession().createQuery("FROM com.ashcollege.entities.Match").list();
//    }
//    public <T> List<T> loadPlayers() {
//        return this.sessionFactory.getCurrentSession().createQuery("FROM com.ashcollege.entities.Player").list();
//    }

    public <T> List<T> loadPlayersFromTeam(int teamId) {
        return this.sessionFactory.getCurrentSession().createQuery("FROM Player where team.id = :teamId")
                .setParameter("teamId",teamId)
                .list();
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