package com.agileeze.reactivemicroservices.hello_microservice_http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    Router router = Router.router(vertx);
    router.get("/").handler(this::helloHandler);
    router.get("/:name").handler(this::helloHandler);

    vertx.createHttpServer().requestHandler(router).listen(8888);
  }

  private void helloHandler(RoutingContext rc) {
    String message = "hello";

    String nameParam = rc.pathParam("name");
    if(Objects.nonNull(nameParam) && ! nameParam.isBlank())
      message = String.format("%s %s",message, nameParam);

    JsonObject json = new JsonObject().put("message", message);
    rc.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      .end(json.encode());
  }

}
