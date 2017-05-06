package i3k.util;

import java.io.*;
import java.net.*;
import java.util.*;

public class HttpUtils {

	public static String readContentFromGet(String url) throws IOException {
		URL getUrl = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) getUrl
			.openConnection();
		connection.connect();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "UTF-8"));
		String content = "";
		String lines;
		while ((lines = reader.readLine()) != null) {
			content += lines + System.getProperty("line.separator");
		}
		reader.close();
		connection.disconnect();
		return content.trim();
	}

	public static String readContentFromPost(String url, Map<String, String> params, int readTimeout, int connectTimeout) throws IOException {
		URL postUrl = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) postUrl
			.openConnection();
		connection.setConnectTimeout(connectTimeout);
		connection.setReadTimeout(readTimeout);
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.connect();
		DataOutputStream out = new DataOutputStream(connection
				.getOutputStream());
		String content = "";
		for (Map.Entry<String, String> entry: params.entrySet()) {
			if (content.length() > 0)
				content += "&";
			content += entry.getKey();
			if (entry.getValue().length() > 0)
				content += "=" + URLEncoder.encode(entry.getValue(), "utf-8");
		}
		out.writeBytes(content); 
		out.flush();
		out.close();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
		String line;
		content = "";
		while ((line = reader.readLine()) != null) {
			content += line;
		}
		reader.close();
		connection.disconnect();
		return content;
	}

	public static String readContentFromPost(String url, String param, int readTimeout, int connectTimeout) throws IOException {
		URL postUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) postUrl
			.openConnection();
		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("User-Agent", "25PP");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Content-Length", String.valueOf(param.length()));
		conn.getOutputStream().write(param.getBytes());
		conn.getOutputStream().flush();
		conn.getOutputStream().close();

		BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
		String line;
		String content = "";
		while ((line = reader.readLine()) != null) {
			content += line;
		}
		reader.close();
		conn.disconnect();
		return content;
	}
}

