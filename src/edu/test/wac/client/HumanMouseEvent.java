package edu.test.wac.client;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class HumanMouseEvent implements Serializable, IsSerializable {

	private static final long serialVersionUID = -566730354932921528L;

	private boolean click;

	private boolean enter;
	private String txt;
	private int type;
	private int x;
	private int y;
	public HumanMouseEvent() {
		this.type = 2;
	}

	public HumanMouseEvent(int x, int y, boolean click) {
		super();
		this.type = 0;
		this.x = x;
		this.y = y;
		this.click = click;
	}

	public HumanMouseEvent(String txt, boolean enter) {
		super();
		this.type = 1;
		this.txt = txt;
		this.enter = enter;
	}

	public boolean getClick() {
		return click;
	}

	public String getTxt() {
		return txt;
	}

	public int getType() {
		return type;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isEnter() {
		return enter;
	}

	@Override
	public String toString() {
		return "HumanMouseEvent [x=" + x + ", y=" + y + ", click=" + click + ", type=" + type + ", txt=" + txt
				+ ", enter=" + enter + "]";
	}
}
