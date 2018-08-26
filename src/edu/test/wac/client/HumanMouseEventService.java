package edu.test.wac.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("hme")
public interface HumanMouseEventService extends RemoteService {
	HumanMouseEventResponse execHumanMouseEvent(HumanMouseEvent input) throws IllegalArgumentException;
}
