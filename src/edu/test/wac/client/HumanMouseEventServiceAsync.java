package edu.test.wac.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface HumanMouseEventServiceAsync {
	 void execHumanMouseEvent(HumanMouseEvent input, AsyncCallback<HumanMouseEventResponse> callback) throws IllegalArgumentException;
}
