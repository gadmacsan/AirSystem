package airSystem.Verticles;

import java.util.Arrays;

import com.google.gson.Gson;

import airSystem.entities.ActuatorValues;
import airSystem.entities.Actuators;
import airSystem.entities.Sensor;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import utils.RestClientUtil;

public class VerticleServos extends AbstractVerticle {

	Gson gson;
	MqttClient mqttClient;
	public RestClientUtil restClientUtil;

	public void start(Promise<Void> startFuture) {
		gson = new Gson();
		WebClientOptions options = new WebClientOptions().setUserAgent("RestClientApp/2.0.2.1");
		options.setKeepAlive(false);
		restClientUtil = new RestClientUtil(WebClient.create(vertx, options));
		mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient.connect(16626, "4.tcp.eu.ngrok.io", s -> {
			mqttClient.subscribe("VerticleServo", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
				if (handler.succeeded()) {
					System.out.println("Suscripción " + mqttClient.clientId());
				} else {
					System.out.println("No se a podido realizar la subcripcion" + mqttClient.clientId());
				}
			});
		});

		getVertx().eventBus().consumer("VerticleServo", msg -> {
			String customMessage = (String) msg.body();
			System.out.println("Mensaje recibido (" + msg.address() + "): " + customMessage);
			String replyMessage = "Sí, yo te he escuchado al mensaje \"" + msg.body().toString() + "\"";
			msg.reply(replyMessage);
			String id = msg.body().toString().split(":")[0].toString();
			String rate = msg.body().toString().split(":")[1].toString();
			activationLogic(id, rate);
		});

		getVertx().eventBus().consumer("StatusServo", msg -> {
			String customMessage = (String) msg.body();
			System.out.println("Mensaje recibido (" + msg.address() + "): " + customMessage);
			String replyMessage = "Sí, yo te he escuchado al mensaje \"" + msg.body().toString() + "\"";
			msg.reply(replyMessage);
			System.out.println("Mensaje event bus: " + msg.body().toString());
			closeActuator(msg.body().toString());
		});

		mqttClient.publishHandler(handler -> {
			System.out.println("Mensaje recibido:");
			System.out.println("    Topic: " + handler.topicName().toString());
			System.out.println("    Id del mensaje: " + handler.messageId());
			System.out.println("    Contenido: " + handler.payload().toString());
		});
	}

	private void closeActuator(String id) {
		Promise<ActuatorValues[]> actuatorValuesRes = Promise.promise();
		actuatorValuesRes.future().onComplete(complete -> {
			if (complete.succeeded()) {
				if (complete.result() != null && complete.result().length > 0) {
					ActuatorValues servo = complete.result()[0];
					if (servo != null && servo.getValue() > 0.1) {
						mqttClient.publish("VerticleServo",
								Buffer.buffer(String.format("%s: %s", servo.getIdActuator(), 0.0)),
								MqttQoS.AT_LEAST_ONCE, false, false);
					}
				} else {
					System.out.println("Empty Body");
				}
			} else {
				System.out.println(complete.cause());
			}
		});
		restClientUtil.getRequest(8080, "http://localhost", "api/actuatorValues/" + id, ActuatorValues[].class,
				actuatorValuesRes);

	}

	private void activationLogic(String idSensor, String rate) {
		Promise<Sensor[]> sensorRes = Promise.promise();
		Promise<Actuators[]> actuatorRes = Promise.promise();

		sensorRes.future().onComplete(complete -> {
			if (complete.succeeded()) {
				if (complete.result() != null) {
					System.out.println(Arrays.asList(complete.result()));
					actuatorRes.future().onComplete(complete1 -> {
						if (complete1.succeeded()) {
							if (complete1.result() != null) {
								Actuators actuator = Arrays.stream(actuatorRes.future().result())
										.filter(a -> a.getIdActuator() == complete.result()[0].getIdSensor()
												&& a.getIdDevice() == complete.result()[0].getIdDevice())
										.findFirst().orElse(null);
								if (actuator != null && actuator.getMode().equals("auto")) {
									mqttClient.publish("VerticleServo",
											Buffer.buffer(String.format("%s: %s", actuator.getIdActuator(), rate)),
											MqttQoS.AT_LEAST_ONCE, false, false);
								}
								System.out.println(Arrays.asList(complete.result()));
							}
						} else {
							System.out.println(complete.cause().toString());
						}
					});
				} else {
					System.out.println("Empty body");
				}
			} else {
				System.out.println(complete.cause().toString());
			}
		});
		restClientUtil.getRequest(8080, "http://localhost", "api/sensors/" + idSensor, Sensor[].class, sensorRes);
		restClientUtil.getRequest(8080, "http://localhost", "api/actuators", Actuators[].class, actuatorRes);
	}
}