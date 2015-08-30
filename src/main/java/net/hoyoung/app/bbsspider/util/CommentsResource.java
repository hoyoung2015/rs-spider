package net.hoyoung.app.bbsspider.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class CommentsResource {
	private Map<String,List<String>> map;
	
	public CommentsResource(String filePath) throws IOException {
		List<String> list = TextFileUtil.readLineToList(filePath);
		List<String> currentList = null;
		map = new HashMap<String, List<String>>();
		for (String s : list) {
			boolean isCategory = Pattern.matches("\\[\\[.*\\]\\]", s);
			if(isCategory){//栏目
				String key = s.replaceAll("\\[\\[|\\]\\]| ", "");
				List<String> tmpList = map.get(key);
				if(tmpList==null){
					tmpList = new ArrayList<String>();
					map.put(key, tmpList);
				}
				currentList = tmpList;
				continue;
			}
			//评论内容
			if(Pattern.matches("\\S+", s)){
				currentList.add(s);
			}
		}
//		System.out.println(map.toString());
	}
	/**
	 * 根据栏目获取随机评论
	 * @param category
	 * @return
	 */
	public String getRandomComment(String category){
		return getRandomComment(this.map,category);
	}
	private Random random = new Random();
	/**
	 * @param map
	 * @param category 要求开头必须有"论坛"
	 * @return
	 */
	private String getRandomComment(Map<String,List<String>> map,String category){
		List<String> list = map.get(category);
		if(list==null){//递归查询，递归终结于"论坛"，要求map中必须含有"论坛"的根key
			String newCategory = category.substring(0, category.lastIndexOf(">"));
			return getRandomComment(map,newCategory);
		}
		return list.get(random.nextInt(list.size()));
	}
	
	public static void main(String[] args) throws IOException {
		CommentsResource commentsReader = new CommentsResource("conf/comments.txt");
		System.out.println(commentsReader.getRandomComment("论坛>西电睿思BT资源>电影"));
		System.out.println(commentsReader.getRandomComment("论坛>西电睿思BT资源>剧集"));
		System.out.println(commentsReader.getRandomComment("论坛>西电睿思BT资源"));
		System.out.println(commentsReader.getRandomComment("论坛"));
		
	}

}
