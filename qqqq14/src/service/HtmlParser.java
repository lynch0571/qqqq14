package service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HtmlParser {

	public static String getURLSource(String urlStr) {
		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			System.out.println("URL转换错误");
			e.printStackTrace();
		}
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			System.out.println("打开URL连接错误");
			e.printStackTrace();
		}
		try {
			conn.setRequestMethod("GET");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		conn.setConnectTimeout(5000);
		InputStream inStream = null;
		try {
			inStream = conn.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		} // 通过输入流获取html二进制数据
		byte[] data = null;
		try {
			data = readInputStream(inStream);
		} catch (Exception e) {
			e.printStackTrace();
		} // 把二进制数据转化为byte字节数据
		String htmlSource = new String(data);
		return htmlSource;
	}

	/**
	 * 把二进制流转化为byte字节数组
	 * 
	 * @param instream
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream instream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1204];
		int len = 0;
		while ((len = instream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		instream.close();
		return outStream.toByteArray();
	}
}
