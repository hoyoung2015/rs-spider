package net.hoyoung.app.bbsspider;

import java.io.IOException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App 
{
	private static Logger logger = LoggerFactory.getLogger(App.class);
    public static void main( String[] args )
    {
    	logger.info("正在启动...");
    	Scanner scanner = new Scanner(System.in);
        CommentRunnable commentRunnable = null;
        try {
			commentRunnable = new CommentRunnable();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
        Thread thread = new Thread(commentRunnable);
        thread.start();
    	do{
    		System.err.println("输入q回车后可停止运行\n");
    	}while(!"q".equals(scanner.next()));
        scanner.close();
        commentRunnable.stop();
        thread.interrupt();
        logger.info("程序关闭了");
    }
}
