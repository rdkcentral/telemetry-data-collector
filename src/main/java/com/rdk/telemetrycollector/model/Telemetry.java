package com.rdk.telemetrycollector.model;

public class Telemetry {

	
	private String Version;
	
	private String Time;
	
	private String mac;

	@Override
	public String toString() {
		return "Telemetry [Version=" + Version + ", Time=" + Time + ", mac=" + mac + "]";
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getVersion() {
		return Version;
	}

	public void setVersion(String Version) {
		this.Version = Version;
	}
}
