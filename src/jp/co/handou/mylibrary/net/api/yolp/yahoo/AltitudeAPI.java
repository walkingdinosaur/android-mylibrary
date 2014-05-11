package jp.co.handou.mylibrary.net.api.yolp.yahoo;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import jp.co.handou.mylibrary.net.api.yahoo.YahooAPI;


public class AltitudeAPI extends YahooAPI {
	public static final String DEFAULT_URL = "http://alt.search.olp.yahooapis.jp/OpenLocalPlatform/V1/getAltitude";
	public static final String PARAM_COORDINATES = "coordinates";

	private CoordinatesList coordList = null;

	public AltitudeAPI(String appid, String url) {
		super(appid, url);
	}

	public AltitudeAPI(String appid) {
		this(appid, DEFAULT_URL);
	}

	private void initialize() {
		this.coordList = new CoordinatesList();
	}

	public void setPoint(double lat, double lon) {
		this.coordList.addPoint(lat, lon);
	}

	@Override
	public void execAsync(HttpTaskListener listener) {

		synchronized (this) {
			try {
				this.setUrl(this.mYahooApiUrl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			String coordinates = this.coordList.toString();
			this.setParam(PARAM_COORDINATES, coordinates);
			this.setParam(PARAM_OUTPUT, "json");
			
			this.execAsync(listener);
		}
	}

	private static class CoordinatesList extends ArrayList<LLPoint> {

		public void addPoint(double lat, double lon) {
			this.add(new LLPoint(lat, lon));
		}

		@Override
		public String toString() {
			String coordinates = null;
			int size = this.size();

			for (int i = 0, len = size -2; i < len; ++i) {
				LLPoint llp = this.get(i);
				String coord = llp.toCoordinatesString();
				coordinates += coord + ",";
			}

			LLPoint lastPoint = this.get(size -1);
			String coord = lastPoint.toCoordinatesString();
			coordinates += coord;

			return coordinates;
		}
	}
}
