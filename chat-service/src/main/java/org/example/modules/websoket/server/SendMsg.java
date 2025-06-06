package org.example.modules.websoket.server;//package com.sangsnaginplus.stock.loan.modules.websoket.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class SendMsg {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    protected void send(String sendRoom,Object sendMsgObj){
        messagingTemplate.convertAndSend(sendRoom,sendMsgObj);
    }
}
