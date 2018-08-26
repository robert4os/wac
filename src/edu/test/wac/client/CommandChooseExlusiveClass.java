package edu.test.wac.client;

import java.util.HashSet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class CommandChooseExlusiveClass implements Command {

	private String resultId;
	private String idXpath;
	private String idXpathOther;
	private String result;
	private String claszDescription;
	
	public CommandChooseExlusiveClass(String claszDescription, String resultId, String idXpath, String idXpathOther) {
		super();
		this.claszDescription=claszDescription;
		this.resultId = resultId;
		this.idXpath = idXpath;
		this.idXpathOther = idXpathOther;
	}

	@Override
	public String successMessage() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private boolean tryOld=true;
	
	@Override
	public void run(final ObjectDictionary od, final ReturnHandler rh) {
		// TODO Auto-generated method stub
		
		rh.getServerStorage().get(resultId, new SimpleAsyncCallback<String>() {
			
			@Override
			public void run(String oldResult) {
				if(tryOld && oldResult!=null) {
					tryOld=false;
					result=oldResult;
					od.put(resultId, result);
					rh.exit(0);
					return;
				}
				
				
				Element chatFromOtherText=od.getElement(idXpathOther);
				Element chatFromYouText=od.getElement(idXpath);
				
				XPath xp_chatFromYou=new XPath(chatFromYouText);
				XPath xp_chatFromOther=new XPath(chatFromOtherText);
				
				HashSet<String> exclClasses=xp_chatFromYou.getClassesNotInOther(xp_chatFromOther);
				
				for(String c : exclClasses) {
					
					final String cn=c;
					
					Button n=new Button(c);
					n.addClickHandler(new ClickHandler() {
						
						
						@Override
						public void onClick(ClickEvent event) {
							// TODO Auto-generated method stub
							result=cn;
							rh.getServerStorage().put(resultId, result, new SimpleAsyncCallback<Void>() {
								
								@Override
								public void run(Void v) {
									od.put(resultId, result);
									rh.exit(0);
									
								}
							});
							
							
						}
					});
					
					rh.getDisplayPanel().add(n);
				}
				
			}
		});
		
	}
	
	@Override
	public String requestMessage() {
		return "Choose a class that "+claszDescription;
	}
	
	@Override
	public String confirmMessage() {
		
		// TODO Auto-generated method stub
		return "Correct class? "+result;
	}

	@Override
	public void onUserConfirmation(boolean ok) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean cannotBeAutoconfirmed() {
		// TODO Auto-generated method stub
		return false;
	}
}
