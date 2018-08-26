package edu.test.wac.client;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ServerStorageEventResponse implements Serializable, IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7834362415291409724L;
	int cmd;
	
	public ServerStorageEventResponse() {
		
	}
	
	public ServerStorageEventResponse(int c) {
		cmd=c;
	}
	
	private String v;

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}
	
	

}
