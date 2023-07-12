package airSystem.methods;

import java.util.Calendar;

import com.google.gson.Gson;

import airSystem.Verticles.VerticleServer;
import airSystem.entities.Sensor;
import airSystem.entities.SensorValues;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class SensorMethods {

	static String verticleID = "";

	// Método para la tabla sensor
	// Método Get para mostrar todos los sensores
	public static void getAllSensor(RoutingContext routingContext) {
		VerticleServer.mySqlClient.query("SELECT * FROM sensors", res -> {
			if (res.succeeded()) {
				RowSet<Row> resultSet = res.result();
				JsonArray result = new JsonArray();
				for (Row elem : resultSet) {
					result.add(
							JsonObject.mapFrom(new Sensor(elem.getInteger("idSensor"), elem.getInteger("idDevice"))));
				}
				System.out.println(result.toString());
				routingContext.response().setStatusCode(200)
						.putHeader("content-type", "application/json; charset=utf-8").end(result.encodePrettily());
			} else {
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}

	// Método para mostrar sensor por su ID:
	public static void getSensorById(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("idSensor"));
		VerticleServer.mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM sensors WHERE idSensor = ?", Tuple.of(id), res -> {
					if (res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						JsonArray result = new JsonArray();
						for (Row elem : resultSet) {
							result.add(JsonObject
									.mapFrom(new Sensor(elem.getInteger("idSensor"), elem.getInteger("idDevice"))));
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

	// Método para añadir un Sensor
	public static void addOneSensor(RoutingContext routingContext) {
		Sensor sensor = new Gson().fromJson(routingContext.getBodyAsString(), Sensor.class);
		System.out.println(sensor.toString());
		VerticleServer.mySqlClient.preparedQuery("INSERT INTO sensors (idSensor, idDevice) VALUES (?, ?);",
				Tuple.of(sensor.getIdSensor(), sensor.getIdDevice()), res -> {
					if (res.succeeded()) {
						routingContext.response().setStatusCode(201)
								.putHeader("content-type", "application/json; charset=utf-8")
								.end(new Gson().toJson(sensor));
					} else {
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
				});
	}

	// Métodos para la tabla sensorValue

	public static void getAllSensorValue(RoutingContext routingContext) {
		VerticleServer.mySqlClient.query("SELECT * FROM sensorValues", res -> {
			if (res.succeeded()) {
				RowSet<Row> resultSet = res.result();
				JsonArray result = new JsonArray();
				for (Row elem : resultSet) {
					result.add(JsonObject
							.mapFrom(new SensorValues(elem.getInteger("idSensorValue"), elem.getDouble("temp"),
									elem.getDouble("hum"), elem.getLong("time"), elem.getInteger("idSensor"))));
				}
				System.out.println(result.toString());
				routingContext.response().setStatusCode(200)
						.putHeader("content-type", "application/json; charset=utf-8").end(result.encodePrettily());
			} else {
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}

	public static void getSensorValueById(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("idSensor"));
		VerticleServer.mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM sensorValues WHERE idSensor = ?", Tuple.of(id),
						res -> {
							if (res.succeeded()) {
								RowSet<Row> resultSet = res.result();
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new SensorValues(elem.getInteger("idSensorValue"),
											elem.getDouble("temp"), elem.getDouble("hum"), elem.getLong("time"),
											elem.getInteger("idSensor"))));
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

	public static void addOneSensorValue(RoutingContext routingContext) {
		SensorValues sensorValue = new Gson().fromJson(routingContext.getBodyAsString(), SensorValues.class);
		Double temp = sensorValue.getTemp();
		Double hum = sensorValue.getHum();
		Long time = Calendar.getInstance().getTimeInMillis();
		String idSV = sensorValue.getIdSensor().toString();
		Double rate = 0.0;
		sensorValue.setTime(time);
		if (temp > 25.0 || hum > 60.0) {
			if (temp > 25.0) {
				rate = (temp - 25) / 16;
			}
			if (hum > 60.0) {
				rate += (hum - 60) / 40;
			}
			System.out.println(sensorValue.toString());
			String msgEvent = idSV + ":" + rate.toString();
			VerticleServer.eventBus.request("VerticleServo", msgEvent, reply -> {
				Message<Object> res = reply.result();
				verticleID = res.address();
				if (reply.succeeded()) {
					String replyMessage = (String) res.body();
					System.out.println("Respuesta recibida (" + res.address() + ")" + replyMessage + "\n\n\n");
				} else {
					System.out.println("No ha habido respuesta");
				}
			});
		} else {
			VerticleServer.eventBus.request("StatusServo", idSV, reply -> {
				Message<Object> res = reply.result();
				verticleID = res.address();
				if (reply.succeeded()) {
					String replyMessage = (String) res.body();
					System.out.println("Respuesta recibida (" + res.address() + ")" + replyMessage + "\n\n\n");
				} else {
					System.out.println("No ha habido respuesta");
				}
			});
		}

		VerticleServer.mySqlClient.preparedQuery(
				"INSERT INTO sensorValues (time, temp, hum, idSensor) VALUES (?, ?, ?, ?);",
				Tuple.of(time, sensorValue.getTemp(), sensorValue.getHum(), sensorValue.getIdSensor()), res -> {
					if (res.succeeded()) {
						routingContext.response().setStatusCode(201)
								.putHeader("content-type", "application/json; charset=utf-8")
								.end(new Gson().toJson(sensorValue));
					} else {
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
				});
	}

	
}
