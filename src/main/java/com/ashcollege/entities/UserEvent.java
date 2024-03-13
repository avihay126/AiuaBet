package com.ashcollege.entities;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class UserEvent {


    private SseEmitter sseEmitter;
    private String secret;


    public UserEvent(SseEmitter sseEmitter, String secret) {
        this.sseEmitter = sseEmitter;
        this.secret = secret;
    }


    public SseEmitter getSseEmitter() {
        return sseEmitter;
    }

    public void setSseEmitter(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}


