#include <Arduino.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>
#include "ServoEasing.hpp"
#include "RestClient.h"
#include "ArduinoJson.h"
#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <cstring>
#include <iostream>

#define DISABLE_COMPLEX_FUNCTIONS
#define ENABLE_MICROS_AS_DEGREE_PARAMETER
#define DHTTYPE DHT11
#define STASSID "AirSystem"      // Añadir nombre de la red wifi SSID
#define STAPSK "DADAirSystem21-22" // Añadir la contraseña de la red wifi
#define START_DEGREE_VALUE 0

ServoEasing Servo1;
RestClient restClient = RestClient("apirest.manzamontedev.site"); // Añadir aquí la Ip y puerto de la APIRest
WiFiClient espClient;
PubSubClient mqttClient(espClient);
DHT_Unified dht(D4, DHTTYPE);
uint32_t delayMS;
String response;
boolean describe_tests = true;

unsigned long wait = 1000 * 60 * 1; // tiempo entre lectura y lectura del sensor 1 minuto, cambiar el ultimo número para modificar cada cuantos minutos se hará una lectura.
unsigned long now = 0;
String mqttServer = "4.tcp.eu.ngrok.io";
int mqttPort = 16626;
// long lastMsg = 0;
// char msg[50];
String id_sensor = "3"; // En cada placa hay que poner el ID y no pueden ser iguales en 2 placas distintas.
String id_servo = id_sensor;
String id_device = "2";      // Hay que poner el ID DEVICE será igual en todas las placas de una misma sala.
String device_name = "home"; // Nombre del Device para identificar la sala.
bool is_exist_Sensor = false;

// setup_wifi -> Configura el wifi cambiar STASSID(NombreTuWifi), STAPPSK(PasswordTuWifi).
void setup_wifi()
{
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(STASSID);
  WiFi.mode(WIFI_STA);
  WiFi.begin(STASSID, STAPSK);

  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

// test_sensor_servo prueba de sensores y activacion de prueba del servo en funcion de la temperatura.
void test_sensor_servo()
{
  delay(delayMS);
  sensors_event_t event;
  dht.temperature().getEvent(&event);
  if (isnan(event.temperature))
  {
    Serial.println(F("Error reading temperature!"));
  }
  else
  {
    Serial.print(F("Temperature: "));
    Serial.print(event.temperature);
    Serial.println(F("°C"));
  }
  // Get humidity event and print its value.
  dht.humidity().getEvent(&event);
  if (isnan(event.relative_humidity))
  {
    Serial.println(F("Error reading humidity!"));
  }
  else
  {
    Serial.print(F("Humidity: "));
    Serial.print(event.relative_humidity);
    Serial.println(F("%"));
  }
  if (event.temperature >= 20.00)
  {
    Servo1.easeTo(90);
  }
  else
  {
    Servo1.easeTo(START_DEGREE_VALUE);
  }
}

// reconnect necesario para el funcionamiento de mqtt, permite estar siempre conectados con el broker mqtt ademas de subscribirnos a los canales que necesitemos.
void reconnect()
{
  while (!mqttClient.connected())
  {
    Serial.print("Attempting MQTT connection...");
    if (mqttClient.connect("ESP8266Client" + rand() % 100))
    {
      Serial.println("connected");
      mqttClient.subscribe("VerticleServo");
    }
    else
    {
      Serial.print("failed, rc=");
      Serial.print(mqttClient.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}

// mqttConnect permite reconectarnos al broker mqtt si se desconecta y mostrar el mensaje recibido por consola.
void mqttConnect()
{
  if (!mqttClient.connected())
  {
    reconnect();
  }

  mqttClient.loop();
  /*
  long now = millis();
  if (now - lastMsg > 2000)
  {
    lastMsg = now;
    Serial.print("Publish message: ");
    Serial.println(msg);
  }
  */
}

// serializeBodySensorValue se encarga de serializar el cuerpo sensorValue para realizar el POST.
String serializeBodySensorValue(double temp, double hum)
{
  StaticJsonDocument<200> doc;
  doc["temp"] = temp;
  doc["hum"] = hum;
  doc["idSensor"] = (int)id_sensor.toInt();

  String output;
  serializeJson(doc, output);
  Serial.println(output);
  return output;
}

// serializeBodyDevice se encarga de serializar el cuerpo device para realizar el POST.
String serializeBodyDevice()
{
  StaticJsonDocument<200> doc;
  doc["idDevice"] = (int)id_device.toInt();
  doc["name"] = device_name;

  String output;
  serializeJson(doc, output);
  Serial.println(output);
  return output;
}

// serializeBodySensor se encarga de serializar el cuerpo sensor para realizar el POST.
String serializeBodySensor()
{
  StaticJsonDocument<200> doc;
  doc["idSensor"] = (int)id_sensor.toInt();
  doc["idDevice"] = (int)id_device.toInt();

  String output;
  serializeJson(doc, output);
  Serial.println(output);
  return output;
}

// serializeBodyActuator se encarga de serializar el cuerpo Actuator para realizar el POST.
String serializeBodyActuator(String mode)
{
  StaticJsonDocument<200> doc;
  doc["idActuator"] = (int)id_servo.toInt();
  doc["mode"] = mode;
  doc["idDevice"] = (int)id_device.toInt();

  String output;
  serializeJson(doc, output);
  Serial.println(output);
  return output;
}

// serializeBodyActuatorValue se encarga de serializar el cuerpo ActuatorValue para realizar el POST.
String serializeBodyActuatorValue(double value)
{
  StaticJsonDocument<200> doc;
  doc["value"] = value;
  doc["idActuator"] = (int)id_servo.toInt();

  String output;
  serializeJson(doc, output);
  Serial.println(output);
  return output;
}

// test_response muestra la respuesta.
void test_response()
{
  Serial.println("TEST RESULT: (response body = " + response + ")");
}

// decribe muestra la descripcion.
void describe(char *description)
{
  if (describe_tests)
    Serial.println(description);
}

// test_status comprueba el status code devuelto por la APIrest.
void test_status(int statusCode)
{
  delay(delayMS);
  if (statusCode == 200 || statusCode == 201)
  {
    Serial.print("TEST RESULT: ok (");
    Serial.print(statusCode);
    Serial.println(")");
  }
  else
  {
    Serial.print("TEST RESULT: fail (");
    Serial.print(statusCode);
    Serial.println(")");
  }
}

// POST_Tests realiza y testea las peticiones post a la APIRest.
void POST_test(String uri, String body)
{
  response.clear();
  char msg_describe[42] = "Test POST with path and body and response";
  describe(msg_describe);
  restClient.setHeader("Access-Control-Allow-Origin: *");

  test_status(restClient.post(uri.c_str(), body.c_str(), &response));
  test_response();
}

// GET realiza y testea las peticiones GET a la APIRest.
void GET_test(String uri)
{
  response.clear();
  char msg_describe[19] = "Test GET with path";
  describe(msg_describe);
  restClient.setHeader("Access-Control-Allow-Origin: *");
  
  test_status(restClient.get(uri.c_str(), &response));
  test_response();
}

// POST condicional es usado despues de un GETbyID, para insertar solo si no existe el elemento.
void POST_test_condicional(String uri, String body)
{
  if (strcmp(response.c_str(), "[ ]") == 0)
  {
    POST_test(uri, body);
  }
}

// callback es un metodo necesario para el funcionamiento de mqtt en el podemos configurar lo que queremos hacer al recibir un mensaje mqtt, ademas podemos filtrar por canal.
void callback(char *topic, byte *payload, unsigned int length)
{
  Serial.print("Message MQTT arrived [");
  Serial.print(topic);
  Serial.print("] -> ");
  Serial.print((char *)payload);
  Serial.println(".");

  char *token = strtok((char *)payload, ": ");
  char *id = token;
  token = strtok(NULL, ": ");
  double body = atof(token);

  if (!is_exist_Sensor && (strcmp(topic, "VerticleServo") == 0))
  {
    if (id_servo.compareTo(id) == 0)
    {
      Servo1.easeTo(90 * body);
      String post_body = serializeBodyActuatorValue(body);
      POST_test("/api/actuatorValues", post_body);
    }
  }
}

// setup_mqtt establece el servidor (mqttServer) y puerto (mqttPort) del broker mqtt, y la función callback.
void setup_mqtt()
{
  mqttClient.setServer(mqttServer.c_str(), mqttPort);
  mqttClient.setCallback(callback);
}

// setup_servo configura la velocidad del servo y pone el servo en 0 grados como posicion inicial.
void setup_servo()
{
  Servo1.setSpeed(50);
  if(is_exist_Sensor==false){
    String uri = "/api/actuators/" + id_servo;
    GET_test(uri);
    String body = serializeBodyActuator("auto");
    POST_test_condicional("/api/actuators", body);
  }
  if (Servo1.attach(D3, START_DEGREE_VALUE) == INVALID_SERVO)
  {
    Serial.print("Fallo en el servo");
  }
}

// setup_sensor configura los sensores, muestra los datos de los sensores y ajusta el delay.
void setup_sensor()
{
  sensors_event_t event;
  dht.begin();
  Serial.println(F("DHTxx Unified Sensor Example"));

  // Print temperature sensor details.
  sensor_t sensor;
  dht.temperature().getSensor(&sensor);
  Serial.println(F("------------------------------------"));
  Serial.println(F("Temperature Sensor"));
  Serial.print(F("Sensor Type: "));
  Serial.println(sensor.name);
  Serial.print(F("Driver Ver:  "));
  Serial.println(sensor.version);
  Serial.print(F("Unique ID:   "));
  Serial.println(sensor.sensor_id);
  Serial.print(F("Max Value:   "));
  Serial.print(sensor.max_value);
  Serial.println(F("°C"));
  Serial.print(F("Min Value:   "));
  Serial.print(sensor.min_value);
  Serial.println(F("°C"));
  Serial.print(F("Resolution:  "));
  Serial.print(sensor.resolution);
  Serial.println(F("°C"));
  Serial.println(F("------------------------------------"));

  // Print humidity sensor details.
  dht.humidity().getSensor(&sensor);
  Serial.println(F("Humidity Sensor"));
  Serial.print(F("Sensor Type: "));
  Serial.println(sensor.name);
  Serial.print(F("Driver Ver:  "));
  Serial.println(sensor.version);
  Serial.print(F("Unique ID:   "));
  Serial.println(sensor.sensor_id);
  Serial.print(F("Max Value:   "));
  Serial.print(sensor.max_value);
  Serial.println(F("%"));
  Serial.print(F("Min Value:   "));
  Serial.print(sensor.min_value);
  Serial.println(F("%"));
  Serial.print(F("Resolution:  "));
  Serial.print(sensor.resolution);
  Serial.println(F("%"));
  Serial.println(F("------------------------------------"));

  // Set delay between sensor readings based on sensor details.
  delayMS = sensor.min_delay / 1000;

  // Lectura de prueba para saber si este ESP tiene sensor conectado
  dht.temperature().getEvent(&event);
  if (!(isnan(event.temperature)))
  {
    is_exist_Sensor = true;

    String uri = "/api/sensors/" + id_sensor;
    GET_test(uri);
    String body = serializeBodySensor();
    POST_test_condicional("/api/sensors", body);
  }
  else
  {
    pinMode(D4, OUTPUT);
  }
}

// setup_device configura el dispositivo.
void setup_device()
{
  String uri = "/api/devices/" + id_device;
  GET_test(uri);
  String body = serializeBodyDevice();
  delay(1000);
  POST_test_condicional("/api/devices", body);
  setup_sensor();
  setup_servo();
}

// sensorRead realiza la lectura de los sensores y realiza un post a la ApiRest
void sensorRead()
{
  delay(delayMS);
  sensors_event_t eventTemp;
  sensors_event_t eventHum;

  // Lectura de Temperatura
  dht.temperature().getEvent(&eventTemp);
  dht.humidity().getEvent(&eventHum);
  if (isnan(eventTemp.temperature) && isnan(eventHum.relative_humidity))
  {
    Serial.println(F("No Sensors!"));
  }
  else
  {

    // Lectura Temperatura
    Serial.print(F("Temperature: "));
    Serial.print(eventTemp.temperature);
    Serial.println(F("°C"));

    // Lectura Humedad
    Serial.print(F("Humidity: "));
    Serial.print(eventHum.relative_humidity);
    Serial.println(F("%"));

    // Petición Post para guardar la lectura
    String post_body = serializeBodySensorValue(eventTemp.temperature, eventHum.relative_humidity);
    POST_test("/api/sensorValues", post_body);
  }
}

void setup()
{
  Serial.begin(9600);
  setup_wifi();
  setup_mqtt();
  setup_device();
}

void loop()
{
  // test_sensor_servo();
  mqttConnect();
  if (is_exist_Sensor && (millis() > now + wait))
  {
    now = millis();
    sensorRead();
    Serial.println();
  }
}