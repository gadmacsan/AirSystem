package airSystem.entities;

import java.util.Objects;

public class SensorValues {
	private Integer idSensorValue;
	private Double temp;
	private Double hum;
	private Long time;
	private Integer idSensor;


	public SensorValues() {
		super();
	}

	public SensorValues(Integer idSensorValue, Double temp, Double hum, Long time, Integer idSensor) {
		super();
		this.idSensorValue = idSensorValue;
		this.temp = temp;
		this.hum = hum;
		this.time = time;
		this.idSensor = idSensor;

	}

	public Integer getIdSensorValue() {
		return idSensorValue;
	}

	public void setIdSensorValue(Integer idSensorValue) {
		this.idSensorValue = idSensorValue;
	}

	public Integer getIdSensor() {
		return idSensor;
	}

	public void setIdSensor(Integer idSensor) {
		this.idSensor = idSensor;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Double getTemp() {
		return temp;
	}

	public void setTemp(Double temp) {
		this.temp = temp;
	}

	public Double getHum() {
		return hum;
	}

	public void setHum(Double hum) {
		this.hum = hum;
	}

	@Override
	public int hashCode() {
		return Objects.hash(hum, idSensor, idSensorValue, temp, time);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SensorValues other = (SensorValues) obj;
		return Objects.equals(hum, other.hum) && Objects.equals(idSensor, other.idSensor)
				&& Objects.equals(idSensorValue, other.idSensorValue) && Objects.equals(temp, other.temp)
				&& Objects.equals(time, other.time);
	}

	@Override
	public String toString() {
		return "SensorValues [idSensorValue=" + idSensorValue + ", idSensor=" + idSensor + ", time=" + time + ", temp="
				+ temp + ", hum=" + hum + "]";
	}

}
