package airSystem.methods;

import com.google.gson.Gson;

import airSystem.Verticles.VerticleServer;
import airSystem.entities.ActuatorValues;
import airSystem.entities.Actuators;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class ActuatorsMethods {

	// Métodos tabla actuators:
	// Método para mostrar todos los actuadores
	public static void getAllActuators(RoutingContext routingContext) {
		VerticleServer.mySqlClient.query("SELECT * FROM actuators", res -> {
			if (res.succeeded()) {
				RowSet<Row> resultSet = res.result();
				JsonArray result = new JsonArray();
				for (Row elem : resultSet) {
					result.add(JsonObject.mapFrom(new Actuators(elem.getInteger("idActuator"), elem.getString("mode"),
							elem.getInteger("idDevice"))));
				}
				System.out.println(result.toString());
				routingContext.response().setStatusCode(200)
						.putHeader("content-type", "application/json; charset=utf-8").end(result.encodePrettily());
			} else {
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}

	// Método para mostrar actuador por su ID:

	public static void getActuatorById(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("idActuator"));
		VerticleServer.mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM actuators WHERE idActuator = ?", Tuple.of(id), res -> {
					if (res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						JsonArray result = new JsonArray();
						for (Row elem : resultSet) {
							result.add(JsonObject.mapFrom(new Actuators(elem.getInteger("idActuator"),
									elem.getString("mode"), elem.getInteger("idDevice"))));
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

	// Método POST para insertar actuadores
	public static void addOneActuator(RoutingContext routingContext) {
		Actuators actuator = new Gson().fromJson(routingContext.getBodyAsString(), Actuators.class);
		System.out.println(actuator.toString());
		VerticleServer.mySqlClient.preparedQuery("INSERT INTO actuators (idActuator, mode, idDevice) VALUES (?, ?, ?);",
				Tuple.of(actuator.getIdActuator(), actuator.getMode(), actuator.getIdDevice()), res -> {
					if (res.succeeded()) {
						routingContext.response().setStatusCode(201).putHeader("Access-Control-Allow-Origin", "*")
								.putHeader("content-type", "application/json; charset=utf-8")
								.end(new Gson().toJson(actuator));
					} else {
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
				});
	}

	// Métodos tabla ActuatorsValue:
	public static void getAllActuatorsValue(RoutingContext routingContext) {
		VerticleServer.mySqlClient.query("SELECT * FROM actuatorValues", res -> {
			if (res.succeeded()) {
				RowSet<Row> resultSet = res.result();
				JsonArray result = new JsonArray();
				for (Row elem : resultSet) {
					result.add(JsonObject.mapFrom(new ActuatorValues(elem.getInteger("idActuatorValue"),
							elem.getDouble("value"), elem.getInteger("idActuator"))));
				}
				System.out.println(result.toString());
				routingContext.response().setStatusCode(200)
						.putHeader("content-type", "application/json; charset=utf-8").end(result.encodePrettily());
			} else {
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}

	// TODO: Mostrar actuador y valor por Id
	public static void getActuatorValueById(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("idActuator"));
		VerticleServer.mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery(
						"SELECT * FROM actuatorValues WHERE idActuator = ? ORDER BY idActuatorValue DESC LIMIT 1;",
						Tuple.of(id), res -> {
							if (res.succeeded()) {
								RowSet<Row> resultSet = res.result();
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new ActuatorValues(elem.getInteger("idActuatorValue"),
											elem.getDouble("value"), elem.getInteger("idActuator"))));
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

	public static void addOneActuatorValue(RoutingContext routingContext) {
		ActuatorValues actuatorValue = new Gson().fromJson(routingContext.getBodyAsString(), ActuatorValues.class);
		System.out.println(actuatorValue.toString());
		VerticleServer.mySqlClient.preparedQuery("INSERT INTO actuatorValues (value, idActuator) VALUES (?, ?);",
				Tuple.of(actuatorValue.getValue(), actuatorValue.getIdActuator()), res -> {
					if (res.succeeded()) {
						routingContext.response().setStatusCode(201)
								.putHeader("content-type", "application/json; charset=utf-8")
								.end(new Gson().toJson(actuatorValue));
					} else {
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
				});

	}
}
