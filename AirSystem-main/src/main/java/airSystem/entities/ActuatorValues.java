package airSystem.entities;

import java.util.Objects;

public class ActuatorValues {
	private Integer idActuatorValue;
	private Double value;
	private Integer idActuator;

	public ActuatorValues() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ActuatorValues(Integer idActuatorValue, Double value, Integer idActuator) {
		super();
		this.idActuatorValue = idActuatorValue;
		this.value = value;
		this.idActuator = idActuator;
	}

	public Integer getIdActuatorValue() {
		return idActuatorValue;
	}

	public void setIdActuatorValue(Integer idActuatorValue) {
		this.idActuatorValue = idActuatorValue;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Integer getIdActuator() {
		return idActuator;
	}

	public void setIdActuator(Integer idActuator) {
		this.idActuator = idActuator;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idActuator, idActuatorValue, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActuatorValues other = (ActuatorValues) obj;
		return Objects.equals(idActuator, other.idActuator) && Objects.equals(idActuatorValue, other.idActuatorValue)
				&& Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "ActuatorValues [idActuatorValue=" + idActuatorValue + ", value=" + value + ", idActuator=" + idActuator
				+ "]";
	}
}