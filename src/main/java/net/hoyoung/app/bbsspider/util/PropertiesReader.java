package net.hoyoung.app.bbsspider.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class PropertiesReader {
	private Map<String,String> map = new HashMap<String, String>();
	private File file;
	public PropertiesReader(String filePath) throws IOException {
		Properties pps = new Properties();
		file = new File(filePath);
		FileInputStream fis = new FileInputStream(file);
		pps.load(new InputStreamReader(fis, "UTF-8"));
		Enumeration<?> enum1 = pps.propertyNames();// 得到配置文件的名字
		while(enum1.hasMoreElements()){
			String strKey = (String) enum1.nextElement();
			String strValue = pps.getProperty(strKey);
			map.put(strKey, strValue);
		}
		fis.close();
	}
	public String getProperty(String key){
		return map.get(key);
	}
	public Map<String,String> getProperties(){
		return map;
	}
	public static void main(String[] args) {
		try {
			PropertiesReader pr = new PropertiesReader("conf/conf.properties");
			System.out.println(pr.getProperty("username"));
			System.out.println(pr.getProperty("password"));
			
			pr.getProperties().put("password", "105225ruisi");
			pr.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void save(){
		try {
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			BufferedWriter bfw = new BufferedWriter(osw);
			for (Entry<String,String> entry : map.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				bfw.write(key+"="+value+"\n");
			}
			bfw.close();
			osw.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
