package jp.co.handou.mylibrary.net.api.yahoo;

import jp.co.handou.mylibrary.net.HttpTask;

public class YahooAPI extends HttpTask {
	public static final String PARAM_APPID = "appid";
	public static final String PARAM_OUTPUT = "output";

	protected String appid = null;
	protected String mYahooApiUrl = null;

	public YahooAPI(String appid, String url) {
		this.appid = appid;
		this.mYahooApiUrl = url;
		this.setParam(PARAM_APPID, appid);
	}

	public String getAppID() {
		return this.appid;
	}

	public String getUrl() {
		return this.mYahooApiUrl;
	}
}
