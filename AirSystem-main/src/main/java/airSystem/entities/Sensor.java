package airSystem.entities;

import java.util.Objects;

public class Sensor {
	private Integer idSensor;
	private Integer idDevice;

	public Sensor() {
		super();
	}

	public Sensor(Integer idSensor, Integer idDevice) {
		super();
		this.idSensor = idSensor;
		this.idDevice = idDevice;
	}

	public Integer getIdSensor() {
		return idSensor;
	}

	public void setIdSensor(Integer idSensor) {
		this.idSensor = idSensor;
	}

	public Integer getIdDevice() {
		return idDevice;
	}

	public void setIdDevice(Integer idDevice) {
		this.idDevice = idDevice;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idDevice, idSensor);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sensor other = (Sensor) obj;
		return Objects.equals(idDevice, other.idDevice) && Objects.equals(idSensor, other.idSensor);
	}

	@Override
	public String toString() {
		return "Sensor [idSensor=" + idSensor + ", idDevice=" + idDevice + "]";
	}

}
