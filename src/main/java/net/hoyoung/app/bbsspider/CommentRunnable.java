package net.hoyoung.app.bbsspider;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.hoyoung.app.bbsspider.util.CommentsResource;
import net.hoyoung.app.bbsspider.util.PropertiesReader;
import net.hoyoung.app.bbsspider.util.TextFileUtil;
import net.hoyoung.app.bbsspider.util.TuringRobot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;

public class CommentRunnable implements Runnable {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private WebClient webClient;

	private int globalThreadCount;

	private String viewThreadUrl;

	private int sleepTime;

	private int waitTime;

	private boolean isRunning = false;

	private PropertiesReader pr;

	private CommentsResource commentsResource;
	
	private List<String> replaceList;//存储需要被替换的正则
	private List<String> ignoreList;//存储需要掠过的
	private List<String> ignorePlateList;//需要忽略的板块


	public CommentRunnable() throws IOException {
		pr = new PropertiesReader("conf/conf.properties");
		String username = pr.getProperty("username");
		String password = pr.getProperty("password");
		String loginUrl = pr.getProperty("loginUrl");
		String currentUrl = pr.getProperty("currentUrl");

		sleepTime = Integer.parseInt(pr.getProperty("sleepTime"));

		waitTime = Integer.parseInt(pr.getProperty("waitTime"));

		commentsResource = new CommentsResource("conf/comments.txt");

		replaceList = TextFileUtil.readLineToList(new File("conf/replace.txt"));
		ignoreList = TextFileUtil.readLineToList(new File("conf/ignore.txt"));
		ignorePlateList = TextFileUtil.readLineToList(new File("conf/ignoreplate.txt"));
		
		logger.info("开始登录，用户名：" + username);
		webClient = LoginClient.login(loginUrl, username, password);
		logger.info("登录完成");
		String temp = currentUrl.substring(currentUrl.lastIndexOf("=") + 1,
				currentUrl.length());
		globalThreadCount = Integer.parseInt(temp);
		viewThreadUrl = new String(currentUrl.replace(temp, ""));
	}

	public synchronized void stop() {
		isRunning = false;
	}
	private boolean checkIgnore(String content){
		for(String ignore : ignoreList){
			if(content.contains(ignore)){
				return true;
			}
		}
		return false;
	}
	private boolean checkIgnorePlate(HtmlPage threadPage){
		DomElement div = threadPage.getElementById("pt");
		DomNodeList<HtmlElement> anchors = div
				.getElementsByTagName("a");
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i < anchors.size() - 1; i++) {
			if (i > 1) {
				sb.append(">");
			}
			sb.append(anchors.get(i).asText());
		}
		String title = sb.toString();
		for(String ignore : ignorePlateList){
			if(title.contains(ignore)){
				logger.info(ignore+"板块，掠过！");
				return true;
			}
		}
		return false;
	}
	private Random random = new Random();
	//替换敏感次
	private String replaceSensitivity(String content){
		String[] replacements = {
				"{:16_998:}",
				"{:8_163:}"
		};
		String str = new String(content);
		for(String regx : replaceList){
			int index = random.nextInt(replacements.length);//随机选取替换对象
			str = str.replaceAll(regx, replacements[index]);
		}
		return str;
	}
	//判断页面是否沙发
	private boolean checkIsSofa(HtmlPage threadPage){
		List<?> shafa = threadPage.getByXPath("//div[@class='pi']/strong/a/text()");
		for (Object object : shafa) {
			if ("沙发".equals(((DomText) object).asText())) {
				return true;
			}
		}
		return false;
	}
	//检查该帖子是否存在，可能有的被删除了
	private boolean checkPageIsExists(HtmlPage threadPage) throws IOException, InterruptedException{
		// 判断是否存在
		List<?> a = threadPage.getByXPath("//div[@class='f_c altw']");
		if (a.size() > 0) {// 不存在页面
			// 探测该帖子是否删除，处理帖子id不连续的情况
			if (this.hasNextPage()) {
				globalThreadCount++;
			} else {
				logger.info("没有帖子了，等待" + waitTime + "秒");
				Thread.sleep(waitTime * 1000);
			}
			return false;//不存在
		}
		return true;
	}
	public void run() {
		isRunning = true;
		try {
			while (isRunning) {
				HtmlPage threadPage = webClient.getPage(viewThreadUrl
						+ globalThreadCount);
				
				//检查该页是否存在，因为实在帖子id递增的情况下抓取的，如果中间有删除断号，需要跳过
				if(!checkPageIsExists(threadPage)){
					continue;
				}
				// 进入下一个帖子
				globalThreadCount++;
				
				//排除不评论的板块，比如资源区不能大量水
				if(checkIgnorePlate(threadPage)){
					continue;
				}
				// 获取评论文本域
				HtmlForm replyForm = threadPage.getForms().get(2);
				
				HtmlTextArea messageTextArea = null;
				try{
					messageTextArea = replyForm
							.getTextAreaByName("message");
				}catch(ElementNotFoundException e){
					e.printStackTrace();
					logger.info("此帖出错，掠过！");
					continue;
				}
				//获取贴子标题
				String info = threadPage.getElementById("thread_subject").asText();
				//从机器人获取评论内容
				String content = TuringRobot.getComment(info).getText();
				
				//含有需要掠过的关键词，此帖不评论
				if(this.checkIgnore(content)){
					continue;
				}
				
				//替换敏感词
				content = replaceSensitivity(content);
				
				// 判断是否沙发
				if(checkIsSofa(threadPage)){
					content = "沙发，" + content;
				}
				
				logger.info(" [主题]" + info + " [评论]" + content);
				messageTextArea.setText(content);
				HtmlButton replyButton = replyForm.getButtonByName("replysubmit");
				replyButton.click();// 触发评论按钮
				// this.stop();
				if (isRunning) {
					logger.info(sleepTime + "秒后下一帖 " + viewThreadUrl
							+ globalThreadCount);
					Thread.sleep(sleepTime * 1000);// 休眠16秒
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // 为了保险起见再次请求中断一次
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.stop();
			// 记录当前地址
			pr.getProperties().put("currentUrl",
					viewThreadUrl + globalThreadCount);
			pr.save();
		}
	}

	/**
	 * 当没有新帖子的时候可能是那个帖子被删除了，取首页最新主题，探测是否为最新
	 * 
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws FailingHttpStatusCodeException
	 */
	private boolean hasNextPage() throws IOException {
		HtmlPage page = webClient.getPage("http://rs.xidian.edu.cn/forum.php");
		DomElement element = page.getElementById("portal_block_314_content");
		DomElement hrefElement = element.getElementsByTagName("a").get(0);
		String href = hrefElement.getAttribute("href");
		String s = href.substring(href.lastIndexOf("=") + 1);

		int latestTid = Integer.parseInt(s);

		return latestTid > globalThreadCount;
	}
}
