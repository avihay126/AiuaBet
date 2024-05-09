package com.ashcollege.controllers;

import com.ashcollege.GenerateData;
import com.ashcollege.GenerateResult;
import com.ashcollege.Persist;
import com.ashcollege.entities.*;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import com.ashcollege.utils.Constants;
import com.ashcollege.utils.DbUtils;
import com.ashcollege.utils.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

import static com.ashcollege.utils.Errors.*;
import static com.ashcollege.utils.Errors.ERROR_SIGN_UP_WRONG_CREDS;

@RestController
public class GeneralController {

    @Autowired
    private DbUtils dbUtils;
    @Autowired
    private Persist persist;

    @Autowired
    private GenerateResult generateResult;



    @RequestMapping(value = "/get-schedule", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Match> getSchedule(){
        List<Match> matches = persist.loadList(Match.class);
        for (Match match : matches) {
            List<Goal> goals = persist.loadMatchGoals(match.getId());
            match.setGoals(goals);
        }
        return matches;
    }

}
