package cz.learn.demo.jgroups;

import cz.learn.demo.jgroups.util.JsonUtil;
import cz.learn.demo.jgroups.util.SimpleChat;
import io.vertx.core.AbstractVerticle;

public class MyFirstVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.createHttpServer().requestHandler(req -> {
            try {
                SimpleChat.getInstance().sendMsg("bbb");
            } catch (Exception e) {
                e.printStackTrace();
            }
            req.response().putHeader("content-type", "application/json").end(JsonUtil.toString(SimpleChat.msgMap));
        }).listen(Main.port);
    }
}