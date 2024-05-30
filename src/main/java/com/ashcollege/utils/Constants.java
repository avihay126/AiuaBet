package com.ashcollege.utils;


import org.hibernate.collection.internal.PersistentIdentifierBag;

public class Constants {
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "1234";






    //MATCH

    public static final int  HOME_TEAM_INDEX = 0;
    public static final int AWAY_TEAM_INDEX = 1;
    public static final int HOME_TEAM_WINS = 1;
    public static final int AWAY_TEAM_WINS = 2;
    public static final int DRAW = 0;






    //TEAM
    public static final int INJURY_PENALTY = 3;
    public static final int WIN_BONUS = 2;
    public static final int MIN_SCORE = 10;
    public static final int MAX_SCORE = 120;
    public static final int MORALE_WIN_INCREASE = 3;
    public static final int MORALE_LOSE_DECREASE = 2;
    public static final int MAX_MORALE = 20;
    public static final int MIN_MORALE = 1;



    //TEAM_STATISTICS
    public static final int POINTS_BY_WIN = 3;



    //USER
    public static final int SECRET_LEN = 10;
    public static final int USERNAME_MIN_LEN = 5;
    public static final int PASSWORD_MIN_LEN = 8;
    public static final int JOINING_GIFT= 200;





    //GENERATE_DATA
    public static final String[] TEAM_NAMES = {
            "Real Madrid", "Girona", "Barcelona", "Atletico Madrid", "Athletic Bilbao", "Real Betis", "Real Sociedad", "Las Palmas",
            "Valencia", "Getafe", "Osasuna", "Alaves", "Villarreal", "Rayo Vallecano", "Sevilla", "Mallorca", "Celta Vigo", "Cadiz", "Granada", "Almeria"
    };
    public static final int MIN_RATING = 15;
    public static final int MAX_RATING = 100;
    public static final int MAX_TEMPERATURE = 45;
    public static final int PLAYERS_PER_TEAM = 11;
    public static final int FIRST_TEAMS_MEETING = 0;
    public static final int SECOND_TEAMS_MEETING = 1;





    //GENERATE_RESULT
    public static final int MAX_PERCENTS = 100;
    public static final int INJURY_PROB_VERY_HIGH = 30;
    public static final int INJURY_PROB_HIGH = 18;
    public static final int INJURY_PROB_MEDIUM = 9;
    public static final int INJURY_PROB_LOW = 3;
    public static final int MANY_INJURIES = 4;
    public static final int MEDIUM_INJURIES = 3;
    public static final int FEW_INJURIES = 2;
    public static final int ONE_INJURY= 1;
    public static final int HOME_TEAM_INJURY_CONDITION = 0;
    public static final int MIN_GOALS_BOUND_PER_MATCH = 1;
    public static final int MAX_MINUTES = 90;
    public static final int HOME_BONUS = 10;
    public static final double SCORE_DRAW_HIGH_DIFF = 80;
    public static final double SCORE_DRAW_MEDIUM_DIFF = 60;
    public static final double SCORE_DRAW_LOW_DIFF = 40;
    public static final double SCORE_DRAW_VERY_LOW_DIFF = 20;
    public static final int VERY_HIGH_DRAW_PROB = 30;
    public static final double HIGH_DRAW_PROB = 20;
    public static final double MEDIUM_DRAW_PROB = 10;
    public static final double LOW_DRAW_PROB = 5;
    public static final double VERY_LOW_DRAW_PROB = 2.5;
    public static final int TEMPERATURE_LOW_BOUND = 10;
    public static final int TEMPERATURE_LOW = 15;
    public static final int TEMPERATURE_NORMAL = 20;
    public static final int TEMPERATURE_HIGH = 30;
    public static final int TEMPERATURE_HIGH_BOUND = 35;
    public static final int DRAW_SCORE_INCREASE_BAD_WEATHER = 2;
    public static final int DRAW_SCORE_INCREASE_WARM = 1;
    public static final int DRAW_SCORE_DECREASE_OPTIMAL = -1;
    public static final int SCORE_INCREASE_OPTIMAL = 2;
    public static final int SCORE_INCREASE_WARM = 1;
    public static final int SCORE_DECREASE_BAD_WEATHER = -1;
    public static final char HOME_WIN = '1';
    public static final char AWAY_WIN = '2';
    public static final char DRAW_RESULT = 'x';
    public static final boolean PLAYER_INJURY = true;
    public static final boolean IS_HOME = true;
    public static final boolean IS_AWAY = false;




    //USER_ACTION_CONTROLLER

    public static final int MAX_DEPOSIT = 10000;
    public static final int MIN_DEPOSIT = 0;





    //LIVE_CONTROLLER

    public static final int FINAL_ROUND = 38;
    public static final int START_ROUND = 1;
    public static final int SPEED_BETWEEN_MATCHES = 1000;
    public static final int SPEED_IN_GAME = 333;
    public static final int SECOND_TO_DIP = -1;
    public static final int SECOND_TO_ADD = 1;
    public static final int ONE_MINUTE = 60;

    public static final int USER_MINUTES_TIME_CYCLE = 40;




    //GENERAL_CONTROLLER
    public static final int MAX_TOP_SCORER_PLAYERS = 5;



}
