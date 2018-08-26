package edu.test.wac.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class HumanUser {
	
	Logger log=Logger.getLogger("HumanUser");
	final static private boolean extremeLog=false;
	
	private HumanMouseEventServiceAsync hmeService;
	private State state;
	
	public HumanUser(State state, HumanMouseEventServiceAsync hmeService) {
		this.state=state;
		this.hmeService=hmeService;
	}
	
	private void consoleLog(String msg) {
		if(!extremeLog) return;
		log.severe(msg);
	}
	
	public void humanClickElementAsync(final Element elem, final SimpleAsyncCallback<Void> cb) {
     //
		
		consoleLog("humanClickElementAsync: 1");
		
	elem.scrollIntoView();
	 
	consoleLog("humanClickElementAsync: 2");
	
	 new Timer() {

		@Override
		public void run() {
			consoleLog("humanClickElementAsync: 3");
			
			// TODO Auto-generated method stub
			int ox=state.vpOriginX;
		    int oy=state.vpOriginY;
		    
		     //
			 int l=elem.getAbsoluteLeft();
			 int t=elem.getAbsoluteTop();
		     
			 if(l==0 && t==0) {
				 consoleLog("humanClickElementAsync: 4");
					
				 log.log(Level.SEVERE, "humanClickElementAsync: click element not found!");
				 cb.run(null);
				 return;
			 }
			 
			 int w=elem.getOffsetWidth();
		     int h=elem.getOffsetHeight();
		    
		     int vl=l-Window.getScrollLeft();
		     int vt=t-Window.getScrollTop();
		     
		     int min=20;
		     int wo=w>=min*3 ? wo=Random.nextInt(w-min*2)+min : w/2;
		     int ho=h>=min*3 ? ho=Random.nextInt(h-min*2)+min : h/2;
		     
		     
		     consoleLog("humanClickElementAsync: 5");
				
		     //
		     SwissKnifeDev.drawPixel(l+wo, t+ho); //??? clear later
		     
		     consoleLog("humanClickElementAsync: 6");
				
		     humanAsync(new HumanMouseEvent(ox+vl+wo, oy+vt+ho, true), cb);
		     
		     //
		     
		}
		 
	 }.schedule(500+Random.nextInt(2000));
    }

		
	private void humanAsync(final HumanMouseEvent hme, final SimpleAsyncCallback<Void> cb) {
		consoleLog("humanAsync: 1");
		 
		hmeService.execHumanMouseEvent(hme, new AsyncCallback<HumanMouseEventResponse>() {
				
				@Override
				public void onSuccess(HumanMouseEventResponse result) {
					consoleLog("humanAsync: 2");
					
					// TODO Auto-generated method stub
					if(result.getExitCode()!=0) throw new RuntimeException("Human input ("+hme+"): "+result.getExitCode()+": "+result.getError());
					else cb.run(null);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					consoleLog("humanAsync: 2: error");
					
					throw new RuntimeException("Human input ("+hme+"): "+caught.getMessage());	
				}
			});
	}
	
	private String cleanStringForSendKeys(String txt) {
		   String special[]= {"\\+", "\\^", "%", "~", "\\(", "\\)", "\\[", "\\]"};
				  
						
		    	//??? should go into server or human dll
				txt=txt.replaceAll("{", "*CBO*");
				txt=txt.replaceAll("}", "*CBC*");
				
				for(String c : special) {
					txt=txt.replaceAll(c, " {"+c+"}");
				}
				
				txt=txt.replaceAll("\\*CBO\\*", "{{}");
				txt=txt.replaceAll("\\*CBC\\*", "{}}");
			
				return txt;
			}
			
	private Element getTextInput() {
		consoleLog("getTextInput 1");
		Element es[]=XPath.evaluateXPath(state.textInputXpath);
		consoleLog("getTextInput 2");
		if(es.length==0) {
			throw new RuntimeException("Text Input Element not found!");
		}
		
		return es[0];
	}
	
	public void selectTextInput(final SimpleAsyncCallback<Void> cb) {
		consoleLog("selectTextInput 1");
		final Element textInput=getTextInput();
		consoleLog("selectTextInput 2");
		humanClickElementAsync(textInput, new SimpleAsyncCallback<Void>() {
			
			@Override
			public void run(Void v) {
		
				consoleLog("selectTextInput 3");
				cb.run(null);

			}
		});
	}
	
	public void humanWriteChatTextAsync(String txt, final boolean confirm, final SimpleAsyncCallback<Void> cb) {
				
		final String ftxt=cleanStringForSendKeys(txt);
		
		final Element textInput=getTextInput();
		
		//
		final String o=UiUtil.highlight(textInput, "#ff0000");
		
		humanClickElementAsync(textInput, new SimpleAsyncCallback<Void>() {
			
			@Override
			public void run(Void v) {
		
				new Timer() {

					@Override
					public void run() {
						humanAsync(new HumanMouseEvent(ftxt, confirm), new SimpleAsyncCallback<Void>() {
							public void run(Void v) {
								
								UiUtil.resetBorder(textInput, o);
								cb.run(v);
							}
						});	
						
					}
				 	
				}.schedule(2000+Random.nextInt(1000));

			}
		});
		
				
	}
}
