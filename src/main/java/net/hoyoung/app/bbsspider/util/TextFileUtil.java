package net.hoyoung.app.bbsspider.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author hoyoung
 *
 */
public class TextFileUtil {
	public static List<String> readLineToList(String filePath) throws IOException {
		File file = new File(filePath);
		return readLineToList(file);
	}

	public static List<String> readLineToList(File file) throws IOException {
		FileInputStream fs = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fs, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		List<String> list = new ArrayList<String>();
		String temp;
		while ((temp = br.readLine()) != null) {
			list.add(temp);
		}
		br.close();
		isr.close();
		fs.close();
		return list;
	}

	public static void main(String[] args) {
		try {
			List<String> list = TextFileUtil.readLineToList("conf/category.txt");
			for (String s : list) {
				System.out.println(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
