package edu.test.wac.client;

import com.google.gwt.user.client.ui.FlowPanel;

public interface ReturnHandler {

	public void exit(int code);
	
	public FlowPanel getDisplayPanel();
	
	public ServerStorage getServerStorage();
}
