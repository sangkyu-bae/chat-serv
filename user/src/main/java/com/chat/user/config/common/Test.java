package com.chat.user.config.common;
import java.util.concurrent.TimeUnit;
public class Test {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("3초 대기 중...");
        TimeUnit.SECONDS.sleep(3);
        System.out.println("끝!");
    }
}
