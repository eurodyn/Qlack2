package com.eurodyn.qlack2.fuse.aaa.impl;

import com.eurodyn.qlack2.fuse.aaa.impl.model.*;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by kumar.prashant on 27/06/2016.
 */
public class TestDBUtil {

    private static EntityManager em = AllAAATests.getEm();

    public static void persistToTemplate(OpTemplate opTemplate){

        em.persist(opTemplate);
    }

    public static User getUserById(String userId) {

        return em.createQuery("select usr from User usr where usr.id=:userId", User.class)
                .setParameter("userId", userId)
                .getSingleResult();

    }

    public static User persistToUser(User user) {

        em.persist(user);
        return user;
    }

    public static Group getGroupById(String id) {

        return em.createQuery("select gp from Group gp where gp.id=:id", Group.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public static void persistToGroup(Group group) {

        em.persist(group);
    }

    public static void persistToUserAttribute(UserAttribute userAttribute) {

        em.persist(userAttribute);
    }

    public static void persistToSession(Session session) {

        em.persist(session);
    }

    public static List<Session> fetchSessions() {

        return em.createQuery("select sess from Session sess", Session.class)
                .getResultList();
    }

    public static Session fetchSessionById(String sessionId) {

        return em.createQuery("select sess from Session sess where sess.id=:sessionId", Session.class)
                .setParameter("sessionId", sessionId)
                .getSingleResult();
    }

    public static void persistToSessionAttribute(SessionAttribute sessionAttribute) {

        em.persist(sessionAttribute);
    }

    public static void cleanOpTemplate() {

        em.createQuery("delete from OpTemplate")
                .executeUpdate();
    }

    public static void cleanOpTemplateHasOperation() {

        em.createQuery("delete from OpTemplateHasOperation ")
                .executeUpdate();
    }

    public static void cleanGroupHasOperation() {

        em.createQuery("delete from GroupHasOperation")
                .executeUpdate();
    }

    public static void cleanOperation() {

        em.createQuery("delete from Operation")
                .executeUpdate();
    }

    public static Operation fetchOperationFromDB() {

        return em.createQuery("select op from Operation  op", Operation.class)
                .getSingleResult();
    }

    public static <T> void  persistToDB(T type ) {

        em.persist(type);
    }

    public static <T> T fetchSingleResultFromDB(String tableName) {

        return (T) em.createQuery("select tbl from "+ tableName +" tbl")
                .getSingleResult();
    }

    public static void cleanTable(String tableName) {

        em.createQuery("delete from " + tableName)
                .executeUpdate();
    }

    public static <T> List<T> fetchResultSet(String tableName) {

        return em.createQuery("select row from "+ tableName + " row")
                .getResultList();
    }

    public static long fetchRecordCount(String tableName) {
        return (long) em.createQuery("select count(tbl) from "+ tableName +" tbl")
                .getSingleResult();
    }

}
