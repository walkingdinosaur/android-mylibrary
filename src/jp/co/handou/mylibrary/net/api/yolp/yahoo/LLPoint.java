package jp.co.handou.mylibrary.net.api.yolp.yahoo;

public class LLPoint {
	private double lat = 0.0;
	private double lon = 0.0;

	public LLPoint() {
		super();
	}

	public LLPoint(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String toCoordinatesString() {
		String coord = this.lon + "," + this.lat;
		return coord;
	}
}
