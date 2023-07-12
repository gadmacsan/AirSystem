package airSystem.entities;

import java.util.Objects;

public class Actuators {
	private Integer idActuator;
	private String mode;
	private Integer idDevice;

	public Actuators() {
		super();
	}

	public Actuators(Integer idActuator, String mode, Integer idDevice) {
		super();
		this.idActuator = idActuator;
		this.mode = mode;
		this.idDevice = idDevice;
	}

	public Integer getIdActuator() {
		return idActuator;
	}

	public void setIdActuator(Integer idActuator) {
		this.idActuator = idActuator;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Integer getIdDevice() {
		return idDevice;
	}

	public void setIdDevice(Integer idDevice) {
		this.idDevice = idDevice;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idActuator, idDevice, mode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Actuators other = (Actuators) obj;
		return Objects.equals(idActuator, other.idActuator) && Objects.equals(idDevice, other.idDevice)
				&& Objects.equals(mode, other.mode);
	}

	@Override
	public String toString() {
		return "Actuators [idActuator=" + idActuator + ", mode=" + mode + ", idDevice=" + idDevice + "]";
	}

}
