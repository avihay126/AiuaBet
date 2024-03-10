package com.ashcollege.controllers;

import com.ashcollege.GenerateData;
import com.ashcollege.Persist;
import com.ashcollege.entities.Match;
import com.ashcollege.entities.Player;
import com.ashcollege.entities.User;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import com.ashcollege.utils.Constants;
import com.ashcollege.utils.DbUtils;
import com.ashcollege.utils.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.ashcollege.utils.Errors.*;

@RestController
public class GeneralController {

    @Autowired
    private DbUtils dbUtils;
    @Autowired
    private Persist persist;




    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public Object test () {
        return "Hello From Server";
    }

    @RequestMapping(value = "register", method = {RequestMethod.GET,RequestMethod.POST})
    public BasicResponse register(String username, String email, String password){
        BasicResponse response = null;
        if (User.areInputsCorrect(username,email,password)){
          List<User> users =persist.loadList(User.class);
            for (User user: users) {
                if (user.getUsername().equals(username) || user.getEmail().equals(email)){
                    response = new BasicResponse(false, ERROR_SIGN_UP_USERNAME_TAKEN);
                }
            }
            if (response == null){
                response = new BasicResponse(true,0);
                User user = new User(username,email,password);
                persist.save(user);
            }
        }
        if (response == null){
            response = new BasicResponse(false, ERROR_SIGN_UP_WRONG_CREDS);
        }
        return response;
    }


//        @RequestMapping(value = "/gen-p", method = {RequestMethod.GET, RequestMethod.POST})
//    public Object generatePlayers () {
//        List <Player> players = generator.generatePlayers();
//        persist.saveAll(players);
//        return players;
//    }
//
//    @RequestMapping(value = "/gen-m", method = {RequestMethod.GET, RequestMethod.POST})
//    public Object generateSchedule() {
//        List<Match> matches = generator.generateSchedule();
//        persist.saveAll(matches);
//        return matches;
//    }


//    @RequestMapping (value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
//    public BasicResponse login (String username, String password) {
//        BasicResponse basicResponse = null;
//        boolean success = false;
//        Integer errorCode = null;
//        if (username != null && username.length() > 0) {
//            if (password != null && password.length() > 0) {
//                User user = dbUtils.login(username, password);
//                if (user != null) {
//                    basicResponse = new LoginResponse(true, errorCode, user.getId(), user.getSecret());
//                } else {
//                    errorCode = ERROR_LOGIN_WRONG_CREDS;
//                }
//            } else {
//                errorCode = ERROR_SIGN_UP_NO_PASSWORD;
//            }
//        } else {
//            errorCode = ERROR_SIGN_UP_NO_USERNAME;
//        }
//        if (errorCode != null) {
//            basicResponse = new BasicResponse(success, errorCode);
//        }
//        return basicResponse;
//    }
//
//    @RequestMapping (value = "add-user")
//    public boolean addUser (String username, String password) {
//        User userToAdd = new User(username, password);
//        return dbUtils.addUser(userToAdd);
//    }
//
//    @RequestMapping (value = "get-users")
//    public List<User> getUsers () {
//        return dbUtils.getAllUsers();
//    }
//
//
//    @RequestMapping (value = "add-product")
//    public boolean addProduct (String description, float price, int count) {
//        Product toAdd = new Product(description, price, count);
//        dbUtils.addProduct(toAdd);
//        return true;
//    }

}
