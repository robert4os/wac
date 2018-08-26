package edu.test.wac.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ServerStorageEventServiceAsync {
	 void execServerStorageEvent(ServerStorageEvent input, AsyncCallback<ServerStorageEventResponse> callback) throws IllegalArgumentException;
}
