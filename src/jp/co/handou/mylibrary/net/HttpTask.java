package jp.co.handou.mylibrary.net;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.net.Uri;
import android.os.AsyncTask;

public class HttpTask extends AsyncTask<String, Integer, Integer> {
	public static final int DEFAULT_TIMEOUT = 5000;

	private String orgUrl;
	private String protocol;
	private String authority;
	private String host;
	private String port;
	private String path;
	private String query;
	private HashMap<String, String> params;
	private HttpTaskListener listener;
	private HttpURLConnection con;
	private int timeout;
	private byte[] byteResponse;
	private String lastAccessUrl;
	private Exception lastException;

	public HttpTask() {
		this.params = new HashMap<String, String>();
		this.timeout = DEFAULT_TIMEOUT;
		this.byteResponse = null;
	}

	public HttpTask(String url) throws MalformedURLException, UnsupportedEncodingException {
		this();
		this.orgUrl = url;
		this.parseUrl(url);
	}

	private void parseUrl(String url) throws MalformedURLException, UnsupportedEncodingException {
		URL aUrl = new URL(url);
		this.protocol = aUrl.getProtocol(); // http
		this.host = aUrl.getHost();
		this.authority = aUrl.getAuthority();
		this.port = aUrl.getPath();
		this.path = aUrl.getPath();
		this.query = aUrl.getQuery();

		if (null != query && !"".equals(query)) {
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				int idx = pair.indexOf("=");
				String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
				String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");

				this.params.put(key, value);
			}
		}
	}

	public void setTimeOut(int timeOut) {
		this.timeout = timeOut;
	}

	public int getTimeOut() {
		return this.timeout;
	}

	public byte[] getResponseByteArray() {
		return this.byteResponse;
	}

	public void setUrl(String url) throws MalformedURLException, UnsupportedEncodingException {
		this.orgUrl = url;
		this.parseUrl(url);
	}

	public void setParam(String key, String value) {
		this.params.put(key, value);
	}

	public String getParam(String key) {
		return this.params.get(key);
	}

	public void cancel() {
		
	}

	public void destroy() {
		
	}

	public void execAsync() {
		this.execute();
	}

	public void execAsync(HttpTaskListener listener) {
		this.setHttpTaskListener(listener);
		this.execAsync();
	}

	public void setHttpTaskListener(HttpTaskListener listener) {
		this.listener = listener;
	}

	public HttpTaskListener getHttpTaskListener() {
		return this.listener;
	}

	protected String buildQueryString() {
		// BasicNameValuePair
		if (null == this.params) {
			return "";
		}
		if (this.params.isEmpty()) {
			return "";
		}

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Iterator<Map.Entry<String, String>> ite = this.params.entrySet().iterator(); ite.hasNext(); ) {
			Map.Entry<String, String> entry = ite.next();
			String key = entry.getKey();
			String value = entry.getValue();

			params.add(new BasicNameValuePair(key, value));
		}

		String queryString = URLEncodedUtils.format(params, "UTF-8");
		return queryString;
	}

	protected String builUrlString() {

		Uri.Builder builder = new Uri.Builder();
		builder.scheme(this.protocol);
		builder.authority(this.authority);
		builder.path(this.path);

		String queryString = this.buildQueryString();
		builder.fragment(queryString);

		String url = builder.build().toString();

		return url;
	}

	public String getLastAccessUrl() {
		return this.lastAccessUrl;
	}

	public Exception getLastException() {
		return this.lastException;
	}

	protected void execUrl() {
		boolean isSuccess = false;
		String httpUrl = this.builUrlString();

		if (null != con) {
			con.disconnect();
			con = null;
		}

		InputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			URL url = new URL(httpUrl);
			this.lastAccessUrl = httpUrl;

			this.con = (HttpURLConnection) url.openConnection();
			this.con.setRequestMethod("GET");
			this.con.setConnectTimeout(this.getTimeOut());
			this.con.setReadTimeout(this.getTimeOut());
			this.con.connect();

			in = this.con.getInputStream();
			out = new ByteArrayOutputStream();
			byte[] w = new byte[1024];
			int size = 0;
			while (true) {
				size = in.read(w);
				if (size <= 0) {
					break;
				}
				out.write(w, 0, size);
			}

			// write byte array
			this.byteResponse = out.toByteArray();
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
			this.lastException = e;
		} finally {
			if (con != null) {
				con.disconnect();
			}
			con = null;
			this.terminateStream(in, out);
			in = null;
			out = null;

			if (null != this.listener) {
				if (isSuccess) {
					this.listener.onSuccessHttpTask(this);
				} else {
					this.listener.onFailHttpTask(this);
				}
			}
		}
	}

	private void terminateStream(InputStream in, ByteArrayOutputStream out) {
		if (null != in) {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (null != out) {
			try {
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected Integer doInBackground(String... params) {
		this.execUrl();
		return null;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	public interface HttpTaskListener {
		public void onSuccessHttpTask(HttpTask httpTask);
		public void onFailHttpTask(HttpTask httpTask);
	}
}