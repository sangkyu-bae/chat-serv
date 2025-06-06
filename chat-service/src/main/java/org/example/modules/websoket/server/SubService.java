package org.example.modules.websoket.server;

public interface SubService<T> {

    void sendMessage(T sendObj);
}
