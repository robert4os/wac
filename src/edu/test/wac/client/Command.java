package edu.test.wac.client;

public interface Command {

	public void run(final ObjectDictionary od, final ReturnHandler rh);
	
	public String successMessage();
	
	public String requestMessage();
	
	public String confirmMessage();
	
	public void onUserConfirmation(boolean ok);
	
	public boolean cannotBeAutoconfirmed();
}
