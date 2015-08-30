package net.hoyoung.app.bbsspider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

import net.hoyoung.app.bbsspider.util.PropertiesReader;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomCharacterData;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.javascript.host.Event;
import com.gargoylesoftware.htmlunit.util.Cookie;

public class QianDaoTest {

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
		
		try {
			
			HtmlPage page = webClient.getPage("http://rs.xidian.edu.cn/plugin.php?id=dsu_paulsign:sign");
//			System.err.println(page.asXml());
			HtmlForm form = (HtmlForm)page.getElementById("qiandao");
//			System.err.println(form.asXml());
			//表情
			HtmlRadioButtonInput tt = form.getRadioButtonsByName("qdxq").get(0);//.setAttribute("ckecked", "true");
			
			tt.setAttribute("checked", "checked");
			
//			System.err.println(tt.asXml());
			//我今天最想说
			form.getInputByName("todaysay").setValueAttribute("新的一天新的开始！");
			//点击签到
			 List<HtmlElement> list = form.getElementsByAttribute("a", "onclick", "showWindow('qwindow', 'qiandao', 'post', '0');return false");
			 page = list.get(0).click();
//			form.fireEvent(Event.TYPE_SUBMIT);
//			 System.err.println(page.asXml());
			 
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			webClient.closeAllWindows();
		}
	}

}
