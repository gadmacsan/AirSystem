package airSystem.Verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class VerticleLauncher extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startFuture) {

		String server = VerticleServer.class.getName();
		getVertx().deployVerticle(server, deployResult -> {
			if (deployResult.succeeded()) {
				System.out.println(server + " (" + deployResult.result() + ") ha sido desplegado correctamente");
			} else {
				deployResult.cause().printStackTrace();
			}
		});

		String servo = VerticleServos.class.getName();
		getVertx().deployVerticle(servo, deployResult -> {
			if (deployResult.succeeded()) {
				System.out.println(servo + " (" + deployResult.result() + ") ha sido desplegado correctamente");
			} else {
				deployResult.cause().printStackTrace();
			}
		});

		// Comentados metodos de prueba conexiones p2p y broadcast

//		String sensorVerticleSenderBroadcast = SensorVerticleSenderBroadcast.class.getName();
//		getVertx().deployVerticle(sensorVerticleSenderBroadcast, deployResult -> {
//			if (deployResult.succeeded()) {
//				System.out.println(sensorVerticleSenderBroadcast + " (" + deployResult.result()
//						+ ") ha sido desplegado correctamente");
//			} else {
//				deployResult.cause().printStackTrace();
//			}
//		});
//
//		String sensorVerticleConsumerBroadcast = SensorVerticleSenderBroadcast.class.getName();
//		getVertx().deployVerticle(sensorVerticleConsumerBroadcast, deployResult -> {
//			if (deployResult.succeeded()) {
//				System.out.println(sensorVerticleConsumerBroadcast + " (" + deployResult.result()
//						+ ") ha sido desplegado correctamente");
//			} else {
//				deployResult.cause().printStackTrace();
//			}
//		});
//
//		String sensorVerticleSender = SensorVerticleSender.class.getName();
//		getVertx().deployVerticle(sensorVerticleSender, deployResult -> {
//			if (deployResult.succeeded()) {
//				System.out.println(
//						sensorVerticleSender + " (" + deployResult.result() + ") ha sido desplegado correctamente");
//			} else {
//				deployResult.cause().printStackTrace();
//			}
//		});
//
//		String sensorVerticleConsumer = SensorVerticleConsumer.class.getName();
//		getVertx().deployVerticle(sensorVerticleConsumer, deployResult -> {
//			if (deployResult.succeeded()) {
//				System.out.println(
//						sensorVerticleConsumer + " (" + deployResult.result() + ") ha sido desplegado correctamente");
//			} else {
//				deployResult.cause().printStackTrace();
//			}
//		});
	}

	public void stop(Promise<Void> stopFuture) throws Exception {
		getVertx().undeploy(VerticleServer.class.getName());
		getVertx().undeploy(VerticleServos.class.getName());
//		getVertx().undeploy(SensorVerticleSenderBroadcast.class.getName());
//		getVertx().undeploy(SensorVerticleConsumerBroadcast.class.getName());
//		getVertx().undeploy(SensorVerticleSender.class.getName());
//		getVertx().undeploy(SensorVerticleConsumer.class.getName());
		super.stop(stopFuture);
	}
}
