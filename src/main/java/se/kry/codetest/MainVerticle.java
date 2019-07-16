package se.kry.codetest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {

  private HashMap<String, String> services = new HashMap<>();
  //TODO use this
  private DBConnector connector;
  private BackgroundPoller poller = new BackgroundPoller();
  private DataService dataService;

  @Override
  public void start(Future<Void> startFuture) {
    connector = new DBConnector(vertx);
    dataService = new DataService(connector);

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    services.put("https://www.kry.se", "UNKNOWN");
    vertx.setPeriodic(1000 * 60, timerId -> poller.pollServices(services));
    setRoutes(router);
    vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(8080, result -> {
          if (result.succeeded()) {
            System.out.println("KRY code test service started");
            startFuture.complete();
          } else {
            startFuture.fail(result.cause());
          }
        });
  }

  private void setRoutes(Router router){
    router.route("/*").handler(StaticHandler.create());

    router.get("/service").handler(req -> {
      List<JsonObject> jsonServices = services
          .entrySet()
          .stream()
          .map(service ->
              new JsonObject()
                  .put("name", service.getKey())
                  .put("status", service.getValue()))
          .collect(Collectors.toList());

      dataService.getAllServices().setHandler(queryResult -> {
        if(queryResult.succeeded()){
          req.response()
                  .putHeader("content-type", "application/json")
                  .end(new JsonArray(queryResult.result()).encode());
        }else
          req.response()
                  .setStatusCode(500)
                  .putHeader("content-type", "application/json")
                  .end(new JsonObject().put("error", queryResult.cause().getMessage()).encode());

      });
    });

    router.post("/service").handler(req -> {
      JsonObject jsonBody = req.getBodyAsJson();
      dataService.addService(jsonBody.getString("url")).setHandler(queryResult -> {
        if(queryResult.succeeded())
          req.response()
                  .setStatusCode(200)
                  .end();
        else
          req.response()
                  .setStatusCode(500)
                  .end(new JsonObject()
                        .put("error", queryResult.cause().getMessage()).encode());
      });
    });
  }
}