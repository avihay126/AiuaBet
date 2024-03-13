package com.ashcollege.controllers;

import com.ashcollege.Persist;
import com.ashcollege.entities.User;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static com.ashcollege.utils.Errors.ERROR_SIGN_UP_USERNAME_TAKEN;
import static com.ashcollege.utils.Errors.ERROR_SIGN_UP_WRONG_CREDS;

@RestController
public class LoginController {


    @Autowired
    private Persist persist;



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
                User user = new User(username,email,password);
                persist.save(user);

                response = new LoginResponse(true,null,user.getId(),user.getSecret());
            }
        }
        if (response == null){
            response = new BasicResponse(false, ERROR_SIGN_UP_WRONG_CREDS);
        }
        return response;
    }

}
