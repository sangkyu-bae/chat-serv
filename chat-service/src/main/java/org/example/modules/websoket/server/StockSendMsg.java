package org.example.modules.websoket.server;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class StockSendMsg {

    private String msg;

    public static StockSendMsg createGenerate(String msg){
        return StockSendMsg.builder()
                .msg(msg)
                .build();
    }
}
