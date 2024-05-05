package com.ashcollege.entities;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class UserEvent {


    private SseEmitter sseEmitter;


    public UserEvent(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;

    }


    public SseEmitter getSseEmitter() {
        return sseEmitter;
    }

    public void setSseEmitter(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

}


