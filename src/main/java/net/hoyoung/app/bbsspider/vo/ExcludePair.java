package net.hoyoung.app.bbsspider.vo;

public class ExcludePair {
	private String keywordRegx;
	
	private String replacement;

	public String getKeywordRegx() {
		return keywordRegx;
	}

	public void setKeywordRegx(String keywordRegx) {
		this.keywordRegx = keywordRegx;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}
	
}
