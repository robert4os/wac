package edu.test.wac.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Button;

public class CommandGetUrlParameter implements Command {

	private String result;
	private String u;
	private String resultId;
	private String urlAttr;
	private String paramDescription;
	private String resultExample;
	
	@Override
	public boolean cannotBeAutoconfirmed() {
		// TODO Auto-generated method stub
		return false;
	}

	public CommandGetUrlParameter(String paramDescription, String resultId, String imgId, String urlAttr) {
	this.resultId=resultId;
	this.paramDescription=paramDescription;
	this.u=imgId;
	this.urlAttr=urlAttr;
		// TODO Auto-generated constructor stub
	}
	
	public static String[] _getParams(String attr) {
		attr=URL.decode(attr);
		String query = attr.substring(attr.lastIndexOf("?"));
		
		String ps[]=query.split("&");
		
		return ps;
	}
	
	private String[] getParams(ObjectDictionary od, ReturnHandler rh) {
		Element img=od.getElement(u);
		String attr=img.getAttribute(urlAttr);
		if(attr==null) {
			rh.exit(23);
			return new String[] {};
		}
		
		return _getParams(attr);
		  
	}
	
	public static Map<String, String> getParams(String attr) {
		HashMap<String, String> kv=new HashMap<String, String>();
		String ps[]=_getParams(attr);
		for(String p : ps) {
			String pv[]=p.split("=");
			kv.put(pv[0], pv[1]);
		}
		
		return kv;
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
				
					String ps[]=getParams(od, rh);
					  for(String p : ps) {
						  String kv[]=p.split("=");
						  if(kv[0].equals(result)) resultExample=kv[1];
					  }
					  
					od.put(resultId, result);
					rh.exit(0);
					return;
			}
				

				String ps[]=getParams(od, rh);
				  for(String p : ps) {
					  String kv[]=p.split("=");
					
					final String cn=kv[0];
					final String cne=kv[1];
					
					Button n=new Button(kv[1]);
					n.addClickHandler(new ClickHandler() {
						
						
						@Override
						public void onClick(ClickEvent event) {
							// TODO Auto-generated method stub
							result=cn;
							resultExample=cne;
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
				
		}});
		
	}
	
	@Override
	public String requestMessage() {
		return "Choose a value that "+paramDescription;
	}
	
	@Override
	public String successMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String confirmMessage() {
		// TODO Auto-generated method stub
		return "Correct: "+result+" ("+resultExample+")";
	}

	@Override
	public void onUserConfirmation(boolean ok) {
		// TODO Auto-generated method stub
		
	}


}
