package cz.learn.demo.jgroups;

import cz.learn.demo.jgroups.util.SimpleChat;
import io.vertx.core.Vertx;

public class Main {
    public static Integer port = 8080;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        new Thread(() -> {
            try {
                SimpleChat.startup();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        vertx.deployVerticle(MyFirstVerticle.class.getName());
    }
}
