package se.kry.codetest;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import java.util.Map;

public class BackgroundPoller {

  DataService dataService;
  WebClient webClient;
  public BackgroundPoller(DataService dataService, WebClient client) {
    this.dataService = dataService;
    this.webClient = client;
  }

  public void pollServices(Map<String, String> services) {
    System.out.println("services polling started");

    dataService.getAllServices().setHandler(dbServices ->{
     for(JsonObject service : dbServices.result()){
       String url = service.getString("url");
       pingService(url).setHandler(res -> {
           updateService(url, res.result());
       });
     }
    });

  }

  private Future<Boolean> pingService(String url) {
      Future<Boolean> pingResult = Future.future() ;
     webClient.get(80, url, "/").send(res -> {
      if(res.succeeded()){
          pingResult.complete(true);
      }
      else{
          pingResult.complete(false);
      }
         System.out.println("ping completed for service "+ url + " with result: " +pingResult.result());

     });
     return pingResult;
  }

    private void updateService(String url, Boolean pingResult) {
        dataService.updateServiceStatus(url,pingResult).setHandler(res -> {
            if(res.succeeded())
                System.out.println(url + " service updated successfully");
            else{
                System.out.println("error updating service " + url);
                res.cause().printStackTrace();
            }

        });
    }
}
