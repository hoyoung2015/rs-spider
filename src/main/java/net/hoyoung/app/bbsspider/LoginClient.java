package net.hoyoung.app.bbsspider;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class LoginClient {
//	private static Logger logger = LoggerFactory.getLogger(LoginClient.class);
	private static WebClient webClient;

	public static WebClient login(String loginUrl, String username,
			String password) throws IOException {
		if (webClient == null) {
			webClient = new WebClient(BrowserVersion.CHROME);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			HtmlPage page = null;
			page = webClient.getPage(loginUrl);
			HtmlForm loginForm = page.getForms().get(0);
			HtmlInput usernameInput = loginForm.getInputByName("username");
			HtmlInput passwordInput = loginForm.getInputByName("password");
			DomNodeList<HtmlElement> buttons = loginForm
					.getElementsByTagName("button");
			HtmlElement loginButton = buttons.get(0);
			usernameInput.setValueAttribute(username);
			passwordInput.setValueAttribute(password);
			loginButton.click();
		}
		return webClient;
	}
}
