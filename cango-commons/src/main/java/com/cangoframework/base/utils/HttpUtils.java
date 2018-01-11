package com.cangoframework.base.utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;


public class HttpUtils {
	
	public static String sendGet(String url) throws IOException {
		return sendGet(url,null,"UTF-8");
	}

	/**
	 * 使用Get方式获取数据
	 * @param url
	 * @param header 请求头
	 * @return
	 * @throws IOException
     */
	public static String sendGet(String url , Map<String, Object> header) throws IOException {
		return sendGet(url,header,"UTF-8");
	}

	/**
	 * 使用Get方式获取数据
	 * @param url  URL包括参数
	 * @param encoding 编码格式
	 * @param header 请求参数头
	 * @return
	 * @throws IOException 
	 */
	public static String sendGet(String url, Map<String, Object> header ,String encoding) throws IOException {
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);

			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			setRequestHeader(header, connection);
			// 建立实际的连接
			connection.connect();
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), encoding));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
	
	public static String sendPost(String url, Map<String, Object> param) throws IOException {
		return sendPost(url, param, "UTF-8");
	}

	public static String sendPost(String url, Map<String, Object> param,Map<String, Object> header) throws IOException {
		return sendPost(url, param, header, "UTF-8");
	}

	/**
	 * POST请求，Map形式数据
	 * @param url 请求地址
	 * @param param 请求数据
	 * @param encoding 编码格式
	 * @throws IOException 
	 */
	public static String sendPost(String url, Map<String, Object> param,Map<String, Object> header, String encoding) throws IOException {
		StringBuffer buffer = new StringBuffer();
		if (param != null && !param.isEmpty()) {
			for (Map.Entry<String, Object> entry : param.entrySet()) {
				try {
					buffer.append(entry.getKey()).append("=")
							.append(URLEncoder.encode(String.valueOf(entry.getValue()),encoding))
							.append("&");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		buffer.deleteCharAt(buffer.length() - 1);
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			setRequestHeader(header, conn);

			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(buffer);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), encoding));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static String sendPost(String url, Map<String, Object> param, String encoding) throws IOException {
		return sendPost(url,param,null,encoding);
	}

	private static void setRequestHeader(Map<String, Object> header, URLConnection conn) {
		conn.setRequestProperty("accept", "*/*");
		conn.setRequestProperty("connection", "Keep-Alive");
		conn.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		if(header!=null&& !header.isEmpty()){
            Set<Map.Entry<String, Object>> entries = header.entrySet();
            for (Map.Entry<String, Object> entry:entries){
                conn.setRequestProperty(entry.getKey(),String.valueOf(entry));
            }
        }
	}


}
