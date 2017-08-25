package cz.learn.demo.vertx;

import java.util.HashMap;
import java.util.Map;

import cz.learn.demo.vertx.util.JsonUtil;
import io.vertx.core.AbstractVerticle;

public class MyFirstVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        final Map<String, Object> map = new HashMap<>();
        map.put("name", "GouSheng");
        map.put("age", 27);
        vertx.createHttpServer().requestHandler(req -> {
            req.response().putHeader("content-type", "application/json").end(JsonUtil.toString(map));
        }).listen(8080);
    }
}
