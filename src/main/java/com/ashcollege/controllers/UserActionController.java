package com.ashcollege.controllers;


import com.ashcollege.entities.Bet;
import com.ashcollege.entities.BetsForm;
import com.ashcollege.entities.User;
import com.ashcollege.utils.Constants;
import com.ashcollege.utils.DbUtils;
import com.ashcollege.utils.GenerateResult;
import com.ashcollege.utils.Persist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserActionController {


    @Autowired
    private Persist persist;


    @RequestMapping(value = "deposit-user-balance", method = {RequestMethod.GET, RequestMethod.POST})
    public User depositUserBalance(int balance, HttpServletRequest request){
        String secret = LoginController.getSecretFromCookie(request);
        User user = persist.loadUserBySecret(secret);
        if (balance < Constants.MAX_DEPOSIT && balance > Constants.MIN_DEPOSIT){
            user.setBalance(user.getBalance()+balance);
            persist.save(user);
        }
        return user;
    }

    @RequestMapping(value = "withdraw-user-balance", method = {RequestMethod.GET, RequestMethod.POST})
    public User withdrawUserBalance(int balance, HttpServletRequest request){
        String secret = LoginController.getSecretFromCookie(request);
        User user = persist.loadUserBySecret(secret);
        if (balance <= user.getBalance()){
            user.setBalance(user.getBalance()-balance);
            persist.save(user);
        }
        return user;
    }

    @RequestMapping(value = "/get-user-bet-history-forms", method = {RequestMethod.GET, RequestMethod.POST})
    public List<BetsForm> getUserBetsForms(HttpServletRequest request){
        String secret = LoginController.getSecretFromCookie(request);
        User user = persist.loadUserBySecret(secret);
        List<BetsForm> betsForms = persist.loadFormsByUser(user.getId());
        List<BetsForm> filterForms = new ArrayList<>();
        for (BetsForm form: betsForms) {
            if (form.getRound()<LiveController.currentRound){
                filterForms.add(form);
            }
        }
        initForm(filterForms);

        return filterForms;
    }

    @RequestMapping(value = "/get-user-bet-current-forms", method = {RequestMethod.GET, RequestMethod.POST})
    public List<BetsForm> getUserBetsCurrentForms(HttpServletRequest request){
        String secret = LoginController.getSecretFromCookie(request);
        User user = persist.loadUserBySecret(secret);
        List<BetsForm> betsForms = persist.loadFormsByUser(user.getId());
        List<BetsForm> filterForms = new ArrayList<>();
        for (BetsForm form: betsForms) {
            if (form.getRound()==LiveController.currentRound){
                filterForms.add(form);
            }
        }
        initForm(filterForms);

        return filterForms;
    }

    public void initForm(List<BetsForm>filterForms){
        for (BetsForm form: filterForms) {
            form.setBets(persist.loadBetsByForm(form.getId()));
            for (int i = 0; i < form.getBets().size(); i++) {
                Bet bet = (Bet) form.getBets().get(i);
                bet.getMatch().setGoals(new ArrayList<>());
                if (LiveController.currentRound > form.getRound()){
                    bet.getMatch().setGoals(persist.loadMatchGoals(bet.getMatch().getId()));
                }
            }
        }
    }

}
