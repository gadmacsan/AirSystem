package airSystem.entities;

import java.util.Objects;

public class Devices {
	private Integer idDevice;
	private String name;

	public Devices() {
		super();
	}

	public Devices(Integer idDevice, String name) {
		super();
		this.idDevice = idDevice;
		this.name = name;
	}

	public Integer getIdDevice() {
		return idDevice;
	}

	public void setIdDevice(Integer idDevice) {
		this.idDevice = idDevice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idDevice, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Devices other = (Devices) obj;
		return Objects.equals(idDevice, other.idDevice) && Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "Devices [idDevice=" + idDevice + ", name=" + name + "]";
	}


}
