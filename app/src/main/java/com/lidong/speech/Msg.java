package com.lidong.speech;

public class Msg {

    public static final int TYPE_RECEIVED=0;
	public static final int TYPE_SENT=1;
	public static final int TYPE_RECEIVED_WEB=2;
	public static final int TYPE_RECEIVED_OPENAPP=3;
	public static final int TYPE_RECEIVED_CALL=4;
	public static final int TYPE_RECEIVED_MESSAGE=5;
	public static final int TYPE_RECEIVED_CALENDAR=6;
	
	private String content;
	private int type;
	
	public Msg(String content,int type) {
		// TODO Auto-generated constructor stub
		this.content=content;
		this.type=type;
	}
	
	public String getContent() {
		return content;
	}
	public int getType() {
		return type;
	}
}