# AirSystem

  ## Proyecto DAD

   ### Descripción:

Este proyecto denominado AirSystem trata sobre la monitorización de salas donde se encuentran diferentes dispositivos con sensores y actuadores encargados de controlar la humedad y la temperatura de las salas. En el caso de que uno o ambas salas sobrepasen una temperatura y/o humedad haciendo uso de los actuadores abriremos las ventanas para ayudar a la ventilación de la sala y así intentar mejorar la calidad de la estancia en la sala.

[Ver Documentación](Documentacion.md)

  ---

TODO: Completado

- [x] Hacer Método get para mostrar todos los sensores.
- [x] Hacer Método get para mostar un sensor por ID,
- [x] Hacer Método posr para crear un sensor.
- [x] Comentar Métodos de comunicacion p2p y broadcast.
- [X] Consultar con Luismi donde meter la lógica de activación de servos, en el VerticleServer o en el VerticleSevo(Comunicación MQTT).
- [X] Consultar opcion de poner modo manual y automático.
- [X] Consultar con Luismi base de datos (Actualizada Base de Datos).
- [X] Hacer VerticleServos.
- [X] Crear clase actuator.
- [X] Crear clase actuatorValues.
- [X] Crear clase device.
- [X] Crear clase SensorValues.
- [X] Hacer métodos para las clases device.
- [X] Hacer rutas para las clases device.
- [X] Reestructurar el proyecto.
- [X] Hacer métodos para las clases ActuatorValues.
- [X] Hacer métodos para las clases Actuator.
- [X] Hacer métodos para las clases Sensor
- [X] Hacer métodos para las clases SensorValues.
- [X] Hacer rutas para las clases actuator.
- [X] Hacer rutas para las clases actuatorValues.
- [X] Hacer rutas para las clases SensorValues.
- [X] Actualizar clase Sensor.
- [X] Terminar métodos clase sensor (Si se necesita alguno más).
- [X] Pensar lógica de activación de servos.
- [X] Añadida lógica de detección en el postSensorValue.
- [X] Añadida comunicación de verticles mediante eventBus.
- [X] Añadida lógica básica de activación.
- [X] Añadida suscripción a canal MQTT y envío de mensaje.
- [X] Añadir documentación.
- [X] Cambiar Lorem ipsum por descripción del proyecto.
- [X] ESP Hacer un post con valor detectado por el sensor cada x tiempo.
- [X] ESP Subscribir a canal mqtt VerticleServo.
- [X] ESP Añadir condición para que al recibir un msg por mqtt se active el servo.
- [X] ESP Añadir configuración (Hardware configurar wifi, mqtt, servo y sensores).
- [X] ESP Hacer Gets para las comprobaciones antes de realizar los post de device, sensor y actuator.
- [X] ESP hacer post inicial de device, sensor y actuator.
- [X] Añadida logica para cerrar servos si estaba abierto y se detecta que no se pasa el umbral.
- [X] Seguir Documentación.

---

TODO: Pendiente
