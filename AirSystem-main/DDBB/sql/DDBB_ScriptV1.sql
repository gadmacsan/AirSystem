
CREATE TABLE IF NOT EXISTS `u883070894_airSystem`.`devices` (
  `idDevice` INT NOT NULL ,
  `name` VARCHAR(32) NOT NULL,
  PRIMARY KEY (`idDevice`),
  UNIQUE INDEX `idDevice_UNIQUE` (`idDevice` ASC) VISIBLE);

CREATE TABLE IF NOT EXISTS `u883070894_airSystem`.`sensors` (
  `idSensor` INT NOT NULL,
  `idDevice` INT NOT NULL,
  PRIMARY KEY (`idSensor`),
  UNIQUE INDEX `idBoard_UNIQUE` (`idSensor` ASC) VISIBLE,
  INDEX `fk_sensors_devices1_idx` (`idDevice` ASC) VISIBLE,
  CONSTRAINT `fk_sensors_devices1`
    FOREIGN KEY (`idDevice`)
    REFERENCES `u883070894_airSystem`.`devices` (`idDevice`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

CREATE TABLE IF NOT EXISTS `u883070894_airSystem`.`actuators` (
  `idActuator` INT NOT NULL,
  `mode` VARCHAR(6) NOT NULL,
  `idDevice` INT NOT NULL,
  PRIMARY KEY (`idActuator`),
  UNIQUE INDEX `idActuator_UNIQUE` (`idActuator` ASC) VISIBLE,
  INDEX `fk_actuators_devices1_idx` (`idDevice` ASC) VISIBLE,
  CONSTRAINT `fk_actuators_devices1`
    FOREIGN KEY (`idDevice`)
    REFERENCES `u883070894_airSystem`.`devices` (`idDevice`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

CREATE TABLE IF NOT EXISTS `u883070894_airSystem`.`sensorValues` (
  `idSensorValue` INT NOT NULL AUTO_INCREMENT,
  `temp` DOUBLE NOT NULL,
  `hum` DOUBLE NOT NULL,
  `time` BIGINT NOT NULL,
  `idSensor` INT NOT NULL,
  PRIMARY KEY (`idSensorValue`),
  UNIQUE INDEX `idSensorValues_UNIQUE` (`idSensorValue` ASC) VISIBLE,
  INDEX `fk_sensorValues_sensors1_idx` (`idSensor` ASC) VISIBLE,
  CONSTRAINT `fk_sensorValues_sensors1`
    FOREIGN KEY (`idSensor`)
    REFERENCES `u883070894_airSystem`.`sensors` (`idSensor`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

CREATE TABLE IF NOT EXISTS `u883070894_airSystem`.`actuatorValues` (
  `idActuatorValue` INT NOT NULL AUTO_INCREMENT,
  `value` DOUBLE(4,3) NOT NULL,
  `idActuator` INT NOT NULL,
  PRIMARY KEY (`idActuatorValue`),
  UNIQUE INDEX `idActuatorValues_UNIQUE` (`idActuatorValue` ASC) VISIBLE,
  CONSTRAINT `fk_actuatorValues_actuators1`
    FOREIGN KEY (`idActuator`)
    REFERENCES `u883070894_airSystem`.`actuators` (`idActuator`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
