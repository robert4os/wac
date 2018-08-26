package edu.test.wac.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ss")
public interface ServerStorageEventService extends RemoteService {

	ServerStorageEventResponse execServerStorageEvent(ServerStorageEvent input) throws IllegalArgumentException;
}
