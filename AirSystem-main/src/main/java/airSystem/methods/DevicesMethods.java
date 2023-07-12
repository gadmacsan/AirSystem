package airSystem.methods;

import com.google.gson.Gson;

import airSystem.Verticles.VerticleServer;
import airSystem.entities.Devices;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class DevicesMethods {

	// TODO: Hecho Metodo get para mostrar todos los sensores
	public static void getAllDevices(RoutingContext routingContext) {
		VerticleServer.mySqlClient.query("SELECT * FROM devices", res -> {
			if (res.succeeded()) {
				RowSet<Row> resultSet = res.result();
				JsonArray result = new JsonArray();
				for (Row elem : resultSet) {
					result.add(JsonObject.mapFrom(new Devices(elem.getInteger("idDevice"), elem.getString("name"))));
				}
				System.out.println(result.toString());
				routingContext.response().setStatusCode(200)
						.putHeader("content-type", "application/json; charset=utf-8").end(result.encodePrettily());
			} else {
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}

	// TODO: Hecho metodo Get para buscar un sensor por ID con conexion
	public static void getDeviceById(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("idDevice"));
		VerticleServer.mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM devices WHERE idDevice = ?", Tuple.of(id), res -> {
					if (res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						JsonArray result = new JsonArray();
						for (Row elem : resultSet) {
							result.add(JsonObject
									.mapFrom(new Devices(elem.getInteger("idDevice"), elem.getString("name"))));
						}
						System.out.println(result.toString());
						routingContext.response().setStatusCode(200)
								.putHeader("content-type", "application/json; charset=utf-8")
								.end(result.encodePrettily());
					} else {
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
					connection.result().close();
				});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}

	// TODO: Hecho Metodo Post para insertar sensores
	public static void addDeviceOne(RoutingContext routingContext) {
		Devices device = new Gson().fromJson(routingContext.getBodyAsString(), Devices.class);
		System.out.println(device.toString());
		VerticleServer.mySqlClient.preparedQuery("INSERT INTO devices (idDevice, name) VALUES (?, ?);",
				Tuple.of(device.getIdDevice(), device.getName()), res -> {
					if (res.succeeded()) {
						routingContext.response().setStatusCode(201)
								.putHeader("content-type", "application/json; charset=utf-8")
								.end(new Gson().toJson(device));
					} else {
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
				});
	}
}
