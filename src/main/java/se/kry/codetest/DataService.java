package se.kry.codetest;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

class DataService {

	private DBConnector connector;

	 DataService(DBConnector connector) {
		this.connector = connector;
	}

	 Future<Void> addService(String name, String url) {
	 	Future<Void> result = Future.future();
		 connector.query("INSERT INTO service(name, url) VALUES(?,?)", new JsonArray().add(name).add(url)).setHandler(done ->{
			if(done.succeeded())
				result.complete();
			else
				result.fail(done.cause());
		});
		 return result;
	}

	Future<List<JsonObject>> getAllServices() {
	 	Future<List<JsonObject>> result = Future.future();
		connector.query("SELECT * FROM service").setHandler(done ->{
			if(done.succeeded()){
				result.complete(done.result().getRows());
			}
			else
				result.fail(done.cause());
		});

		return result;
	}

	Future<Void> deleteService(String serviceURL) {
		Future<Void> result = Future.future();
		connector.query("DELETE FROM service WHERE url = ?", new JsonArray().add(serviceURL)).setHandler(done ->{
			if(done.succeeded())
				result.complete();
			else
				result.fail(done.cause());
		});
		return result;
	}
}
