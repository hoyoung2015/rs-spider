package net.hoyoung.app.bbsspider.vo;

public class TuringResponse {
	private int code;
	private String text;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public String toString() {
		return "TuringResponse [code=" + code + ", text=" + text + "]";
	}
	
}
