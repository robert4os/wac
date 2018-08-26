package edu.test.wac.client;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class CommandGetElement implements Command {
	private Element e;
	private HandlerRegistration hr;
	private String kind;
	private String id;
	
	private boolean tryOld=true;
	private boolean tryingOld=false;
	
	
	public CommandGetElement(String kind, String id) {
		this.kind=kind;
		this.id=id;
	}
	
	Logger logger = java.util.logging.Logger.getLogger("CommandGetElement");
	
	public static int count(final String string, final String substring)
	  {
	     int count = 0;
	     int idx = 0;

	     while ((idx = string.indexOf(substring, idx)) != -1)
	     {
	        idx++;
	        count++;
	     }

	     return count;
	  }
	
	private ReturnHandler _rh;
	private ObjectDictionary _od;
	
	@Override
	public void run(final ObjectDictionary od, final ReturnHandler rh) {
	 	this._rh=rh;
		this._od=od;
		
rh.getServerStorage().get(id, new SimpleAsyncCallback<String>() {
			
	
			@Override
			public void run(String xpath) {
			
rh.getServerStorage().get(id, new SimpleAsyncCallback<String>() {
					
					@Override
					public void run(String xpath) {
				
						tryingOld=false;
						if(tryOld) {
						 tryOld=false;
						 
						 
						 	if(xpath!=null) {
						 		 		
								//
						 	  logger.severe("TRY OLD: "+id+": "+xpath);
						 	  List<String> classes=XPath.getClasses(xpath);
						 	  
							  Element es[]=XPath.evaluateXPath(xpath);
							  logger.severe("FOUND: NR: "+es.length);
							  int bm=-1;
							  
							  if(es.length>0) {
								
								  for(int i=0; i<es.length; i++) {
									  Element t=es[i];
									  XPath tp=new XPath(t);
									  int mm=tp.getNrMissmatches(classes);
								
									  if(bm<0 || mm<bm) {
										  bm=mm;
										  e=t;
									  }
								  }
								  
								  
								  od.put(id,  e); 
								  
								  tryingOld=true;	
								  
								  //
								  rh.exit(0);
								  return;
							  }
								
						  }
						}
						
						final Timer t=new Timer() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								  hr.removeHandler();
						      
								  
									rh.exit(0);	
									
						    	  
							}
							
						};
						
						Element d=Document.get().cast();
						Event.sinkEvents(d, Event.ONMOUSEMOVE);
						Event.sinkEvents(d, Event.ONMOUSEOVER);
						
							hr=Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
							  public void onPreviewNativeEvent(final NativePreviewEvent event) {
							    final int eventType = event.getTypeInt();
							    switch (eventType) {
							      case Event.ONMOUSEMOVE:
									  
							    	  t.cancel();
							    	  e=(Element)event.getNativeEvent().getEventTarget().cast();
							    	  
							    	  if(e!=null)
							    	t.schedule(5000);
							    	
							    	  break;
							    		  
							      default:
								         
							    }

								 }
							}

									);
					}
					}
				);
						

						
					}
				});
						
	}
	
	@Override
	public String requestMessage() {
		return "Move over: "+kind+". AND then wait 5 seconds.";
	}
	
	private String bs;
	@Override
	public String confirmMessage() {
		bs=UiUtil.highlight(e, "red");
		new Timer() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				e.scrollIntoView();
			}
			
		}.schedule(1000);
		new Timer() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				e.scrollIntoView();
			}
			
		}.schedule(10);

		//
		String s;
		
		if(e.getNodeType()==Element.TEXT_NODE)
  		{
    		  s=e.getNodeValue();
  		}
		else
		if(e.getNodeType()==Element.ELEMENT_NODE)
		{
			s="Check if the correct element is highlighted? "+e.getTagName();
		} else {
			s="Is this strange element type correct (tag="+e.getTagName()+")?";
		}
		 
		//
		String t=s;
		t+=": "+e.getInnerText();
		t+=" -> "+new XPath(e).toString();
		logger.log(Level.SEVERE, t);
		
		//
		return s;
	}

	@Override
	public String successMessage() {
		if(tryingOld) {
			
			if(e==null) return "Not found!";
			return null;
		}
		
		// TODO Auto-generated method stub
		return null;
		
	}

	@Override
	public void onUserConfirmation(boolean ok) {
		if(ok) {
				
			_od.put(id, e);
			
			_rh.getServerStorage().put(id, new XPath(e).toString(), new SimpleAsyncCallback<Void>() {
				
				@Override
				public void run(Void v) {
				}
			});
		}
		
		// TODO Auto-generated method stub
		UiUtil.resetBorder(e, bs);
	}

	@Override
	public boolean cannotBeAutoconfirmed() {
		// TODO Auto-generated method stub
		return false;
	}
}
	
