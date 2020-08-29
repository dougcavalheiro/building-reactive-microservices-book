package com.agileeze.reactivemicroservices.hello_consumer_microservice_http;


import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

public class MainVerticle extends AbstractVerticle {

  private WebClient client;

  @Override
  public void start() throws Exception {
    client = WebClient.create(vertx);
    Router router = Router.router(vertx);
    router.get("/").handler(this::invokeMyFirstMicroservice);

    vertx.createHttpServer().requestHandler(router).listen(8081);
  }

  private void invokeMyFirstMicroservice(RoutingContext routingContext) {
    HttpRequest<JsonObject> request1 = createRequestForName("Luke");
    HttpRequest<JsonObject> request2 = createRequestForName("Leia");

    Single<JsonObject> s1 = request1.rxSend().map(HttpResponse::body);
    Single<JsonObject> s2 = request2.rxSend().map(HttpResponse::body);

    Single.zip(s1,s2, (luke, leia) -> {
      return new JsonObject().put("Luke", luke.getString("message"))
        .put("Leia", leia.getString("message"));
    }).subscribe(
      result -> routingContext.response().end(result.encodePrettily()), error -> {
        error.printStackTrace();
        routingContext.response().setStatusCode(500).end(error.getMessage());
      }
    );

    request1.send(ar -> {
      if(ar.failed()) {
        routingContext.fail(500, ar.cause());
      } else {
        routingContext.response().end(ar.result().body().encode());
      }
    });
  }

  private HttpRequest<JsonObject> createRequestForName(String name) {
    return client.get(8888, "localhost", "/"+name).as(BodyCodec.jsonObject());
  }

}

