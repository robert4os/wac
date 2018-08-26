package edu.test.wac.client;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ServerStorageEvent implements Serializable, IsSerializable {

	public static int CMD_DICT_GET = 1;
	public static int CMD_DICT_SET = 2;
	private static final long serialVersionUID = 8139549295071223567L;

	private int cmd = -1;
	private String k;
	private String v;
	private String ws;

	public ServerStorageEvent() {
	}

	public ServerStorageEvent(String ws, int c) {
		cmd = c;
		this.ws = ws;
	}

	public int getCmd() {
		return cmd;
	}

	public String getK() {
		return k;
	}

	public String getV() {
		return v;
	}

	public String getWs() {
		return ws;
	}

	public void setK(String k) {
		this.k = k;
	}

	public void setV(String v) {
		this.v = v;
	}

}
