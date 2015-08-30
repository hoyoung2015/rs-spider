package net.hoyoung.app.bbsspider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.hoyoung.app.bbsspider.util.PropertiesReader;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;

public class OfQianDaoTest2 {

	public static void main(String[] args) {
		System.out.println("正在启动...");
		PropertiesReader pr = null;
		try {
			pr = new PropertiesReader("conf/conf.properties");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		String username = pr.getProperty("username");
		String password = pr.getProperty("password");
		String loginUrl = pr.getProperty("loginUrl");
		WebClient webClient = null;
		try {
			webClient = LoginClient.login(loginUrl, username, password);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		HtmlPage page = null;
		try {
			page = webClient
					.getPage("http://rs.xidian.edu.cn/plugin.php?id=dsu_paulsign:sign");
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(0);
		}
		HtmlForm form = (HtmlForm) page.getElementById("qiandao");

		String formhash = form.getInputByName("formhash").getValueAttribute();

		Set<Cookie> cookies = webClient.getCookieManager().getCookies();
		CookieStore cookieStore = new BasicCookieStore();
		for (Cookie cookie : cookies) {
			cookieStore.addCookie(new BasicClientCookie(cookie.getName(), cookie.getValue()));
			System.err.println(cookie.toString());
		}

		String url = "http://rs.xidian.edu.cn/plugin.php?id=dsu_paulsign:sign&operation=qiandao&infloat=1&inajax=1";
		CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
		HttpPost httppost = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("todaysay", "新的一天新的开始！"));
		params.add(new BasicNameValuePair("qdmode", "1"));
		params.add(new BasicNameValuePair("qdxq", "kx"));
		params.add(new BasicNameValuePair("formhash", formhash));
		System.out.println(params.toString());
//		System.exit(0);
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		CloseableHttpResponse response;
		try {
			response = httpclient.execute(httppost);
			System.out.println(response.toString());
			HttpEntity entity = response.getEntity();
			String jsonStr = EntityUtils.toString(entity, "utf-8");
			System.out.println(jsonStr);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		httppost.releaseConnection();

	}

}
