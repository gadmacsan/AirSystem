package airSystem.Verticles;

import airSystem.methods.ActuatorsMethods;
import airSystem.methods.DevicesMethods;
import airSystem.methods.SensorMethods;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

public class VerticleServer extends AbstractVerticle {

	public static MySQLPool mySqlClient;
	public static EventBus eventBus;
	private String nameDB = "u883070894_airSystem";

	@Override
	public void start(Promise<Void> startFuture) {

		// Conexion para DB Local

		// MySQLConnectOptions connectOptions = new
		// MySQLConnectOptions().setPort(3306).setHost("localhost")
		// .setDatabase("airSystem").setUser("root").setPassword("root");

		// Conexion para DB Local Conexion para DB remota
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("212.107.17.52")
				.setDatabase(nameDB).setUser("u883070894_root").setPassword("airSystem21-22");

		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

		mySqlClient = MySQLPool.pool(vertx, connectOptions, poolOptions);
		Router router = Router.router(vertx);
		eventBus = getVertx().eventBus();

		vertx.createHttpServer().requestHandler(router::handle).listen(8080, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});
		router.route().handler(CorsHandler.create("http://localhost:8080")
			    .allowedMethod(io.vertx.core.http.HttpMethod.GET)
			    .allowedMethod(io.vertx.core.http.HttpMethod.POST)
			    .allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
			    .allowCredentials(true)
			    .allowedHeader("Access-Control-Allow-Headers")
			    .allowedHeader("Authorization")
			    .allowedHeader("Access-Control-Allow-Method")
			    .allowedHeader("Access-Control-Allow-Origin")
			    .allowedHeader("Access-Control-Allow-Credentials")
			    .allowedHeader("Content-Type"));
		
		// sensorValue
		router.route("/api/sensorValues*").handler(BodyHandler.create());
		router.get("/api/sensorValues").handler(SensorMethods::getAllSensorValue);
		router.get("/api/sensorValues/:idSensor").handler(SensorMethods::getSensorValueById);
		router.post("/api/sensorValues").handler(SensorMethods::addOneSensorValue);



		// sensor
		router.route("/api/sensors*").handler(BodyHandler.create());
		router.get("/api/sensors").handler(SensorMethods::getAllSensor);
		router.get("/api/sensors/:idSensor").handler(SensorMethods::getSensorById);
		router.post("/api/sensors").handler(SensorMethods::addOneSensor);

		// actuatorValues
		router.route("/api/actuatorValues*").handler(BodyHandler.create());
		router.get("/api/actuatorValues").handler(ActuatorsMethods::getAllActuatorsValue);
		router.get("/api/actuatorValues/:idActuator").handler(ActuatorsMethods::getActuatorValueById);
		router.post("/api/actuatorValues").handler(ActuatorsMethods::addOneActuatorValue);

		// actuator
		router.route("/api/actuators*").handler(BodyHandler.create());
		router.get("/api/actuators").handler(ActuatorsMethods::getAllActuators);
		router.get("/api/actuators/:idActuator").handler(ActuatorsMethods::getActuatorById);
		router.post("/api/actuators").handler(ActuatorsMethods::addOneActuator);

		// Devices
		router.route("/api/devices*").handler(BodyHandler.create());
		router.get("/api/devices").handler(DevicesMethods::getAllDevices);
		router.get("/api/devices/:idDevice").handler(DevicesMethods::getDeviceById);
		router.post("/api/devices").handler(DevicesMethods::addDeviceOne);

	}
}
