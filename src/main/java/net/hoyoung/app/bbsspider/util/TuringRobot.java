package net.hoyoung.app.bbsspider.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import net.hoyoung.app.bbsspider.vo.TuringResponse;

import org.codehaus.jackson.map.ObjectMapper;

public class TuringRobot {
	public static TuringResponse getComment(String info) throws Exception{
		String APIKEY = "629bb4941680723cc730a999cdc7b051"; 
	     String INFO = URLEncoder.encode(info, "utf-8"); 
	    String getURL = "http://www.tuling123.com/openapi/api?key=" + APIKEY + "&info=" + INFO; 
	    URL getUrl = new URL(getURL); 
	    HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection(); 
	    connection.connect(); 

	    // 取得输入流，并使用Reader读取 
	    BufferedReader reader = new BufferedReader(new InputStreamReader( connection.getInputStream(), "utf-8")); 
	    StringBuffer sb = new StringBuffer(); 
	    String line = ""; 
	    while ((line = reader.readLine()) != null) { 
	        sb.append(line); 
	    } 
	    reader.close(); 
	    // 断开连接 
	    connection.disconnect(); 
	    ObjectMapper om = new ObjectMapper();
	    TuringResponse turingResponse = om.readValue(sb.toString(), TuringResponse.class);
		return turingResponse;
	}
	public static void main(String[] args) throws Exception {
		TuringResponse turingResponse = TuringRobot.getComment("额，真的吗");
		System.err.println(turingResponse);
	}
}
