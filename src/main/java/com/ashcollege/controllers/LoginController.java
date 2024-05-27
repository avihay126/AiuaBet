package com.ashcollege.controllers;

import com.ashcollege.Persist;
import com.ashcollege.entities.User;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.UserResponse;
import com.ashcollege.utils.Constants;
import com.ashcollege.utils.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.ashcollege.utils.Errors.ERROR_SIGN_UP_USERNAME_TAKEN;
import static com.ashcollege.utils.Errors.ERROR_SIGN_UP_WRONG_CREDS;

@RestController
public class LoginController {


    @Autowired
    private Persist persist;

    @Autowired
    LiveController liveController;



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
                User user = new User(username,email.toLowerCase(),password);
                persist.save(user);
                response = new UserResponse(true,null,user);

            }
        }
        if (response == null){
            response = new BasicResponse(false, ERROR_SIGN_UP_WRONG_CREDS);
        }
        return response;
    }

    @RequestMapping(value = "login", method = {RequestMethod.POST})
    public BasicResponse login(String email, String password){
        BasicResponse basicResponse = null;
        User user = persist.loadUserByEmail(email.toLowerCase());
        if (user == null){
            basicResponse = new BasicResponse(false, Errors.ERROR_NO_SUCH_USER);
        }else{
            if (password.equals(user.getPassword())){
                basicResponse = new UserResponse(true, null, user);
            }else {
                basicResponse = new BasicResponse(false, Errors.ERROR_SIGN_IN_WRONG_PASSWORD);
            }
        }
        return basicResponse;
    }


    @RequestMapping(value = "update-user-details",method = {RequestMethod.GET,RequestMethod.POST})
    public BasicResponse updateUserDetails(String username, String email, String newPassword, String currentPassword, HttpServletRequest request){
        String secret = getSecretFromCookie(request);
        User user = persist.loadUserBySecret(secret);
        List<User> users = persist.loadList(User.class);
        if (user.getPassword().equals(currentPassword)){
            if (User.areInputsCorrect(username,email,newPassword)){
                for (User u:users ) {
                    if (u.getId()!= user.getId()){
                        if (u.getUsername().equals(username)||u.getEmail().equals(email)){
                            return new BasicResponse(false, ERROR_SIGN_UP_USERNAME_TAKEN);
                        }
                    }
                }
                user.setEmail(email);
                user.setUsername(username);
                user.setPassword(newPassword);
                persist.save(user);
                return new UserResponse(true,null,user);
            }
        }
        return new BasicResponse(false, Errors.ERROR_SIGN_IN_PASSWORDS_DONT_MATCH);
    }



    @RequestMapping(value = "login-by-secret",method = {RequestMethod.GET,RequestMethod.POST})
    public  User login2 (HttpServletRequest request) {
        String secret = getSecretFromCookie(request);
        User user = persist.loadUserBySecret(secret);
        return user;
    }

    public static String getSecretFromCookie(HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("secret")) {
                return cookie.getValue();
            }
        }
        return null;
    }



}
