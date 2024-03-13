package com.ashcollege.controllers;

import com.ashcollege.Persist;
import com.ashcollege.entities.Match;
import com.ashcollege.entities.UserEvent;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
public class LiveController {


    private List<UserEvent> users = new ArrayList<>();

    @Autowired
    private Persist persist;

    private int time;
    private boolean inGame;
    private int loopTime;


    @PostConstruct
    public void init(){
        loopTime = 333;

        new Thread(()->{
            while (true){
                try {
                    Thread.sleep(loopTime);
                    time++;

                }catch (Exception e){
                    e.printStackTrace();
                }
                if (this.time ==91 && this.inGame){
                    this.time=0;
                    this.inGame = !this.inGame;
                    loopTime = 1000;
                }
                if (this.time == 31 && !this.inGame){
                    this.time = 0;
                    this.inGame =!this.inGame;
                    loopTime = 333;
                }
                for (UserEvent user: users) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("time",this.time);
                        jsonObject.put("data", "avihay");
                        user.getSseEmitter().send(jsonObject.toString());

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    @GetMapping(value = "start-streaming",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createStreamingSession(String secret){
        SseEmitter sseEmitter= new SseEmitter((long)(10 * 60 * 1000));
        try {
            users.add(new UserEvent(sseEmitter,secret));
        }catch (Exception e){
            e.printStackTrace();
        }
        return sseEmitter;
    }

    @GetMapping(value = "start-timer",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createStreamingSession(){
        SseEmitter sseEmitter= new SseEmitter((long)(10 * 60 * 1000));
        try {
            users.add(new UserEvent(sseEmitter,"guest"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return sseEmitter;
    }


}
