package edu.test.wac.client;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class HumanMouseEventResponse implements Serializable, IsSerializable {

	private static final long serialVersionUID = -7663761080669225090L;

	private String error;

	private int exitCode;
	private int x;
	private int y;
	public HumanMouseEventResponse() {

	}

	public HumanMouseEventResponse(int exitCode, int x, int y) {
		super();
		this.x = x;
		this.y = y;
		this.exitCode = exitCode;
	}

	public HumanMouseEventResponse(int exitCode, String error) {
		super();
		this.exitCode = exitCode;
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public int getExitCode() {
		return exitCode;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
