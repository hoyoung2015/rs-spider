package net.hoyoung.app.bbsspider;


import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.hoyoung.app.bbsspider.util.TextFileUtil;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	String str = "<主人>你好";
    	
    	try {
			List<String> list = TextFileUtil.readLineToList(new File("conf/replace.txt"));
			for (String s : list) {
				System.out.println(s);
				System.out.println("---"+str.replaceAll(s, "hello"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        assertTrue( true );
    }
}
