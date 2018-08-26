package edu.test.wac.client;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.core.client.ScriptInjector.FromUrl;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;

public class BotClient {

	private boolean simulateBot=false;
	
	final private static String id="wac";
	private String secret;
	
	String s[]= {"hehe", "ur lieb!", "Bleib cool", "Na und?", "uff!", "biiiittte", "das hab ich dir aber gesagt!", "Brüderlein Brüderlein!"};
	
	private boolean ready=false;
	
	private native JavaScriptObject getWindow() /*-{
		return $wnd;
	}-*/;
	
	public void setSimultationMode(boolean b) {
		simulateBot=b;
	}
	
	public BotClient(String _secret) {
		secret=_secret;

		//
		final BotClient that=this;
		
		FromUrl si=ScriptInjector.fromUrl("https://unpkg.com/botframework-webchat/botchat.js");
		si.setWindow(getWindow());
		si.setCallback(new Callback<Void, Exception>() {
			
			@Override
			public void onSuccess(Void result) {
				setup(that);
				ready=true;
			}
			
			@Override
			public void onFailure(Exception reason) {
				throw new RuntimeException("Failed to load bot javascript library");
				
			}
		});
		
		//
		si.inject();
	}
	 
	native private void setup(BotClient botclient) /*-{
	
	$wnd.wacBotConnection = new $wnd.BotChat.DirectLine({ secret: botclient.@edu.test.wac.client.BotClient::secret });
       
      $wnd.wacBotConnection.activity$.filter(function(activity) {
      	return activity.type === 'message' && activity.from.id != @edu.test.wac.client.BotClient::id;
      }).subscribe(function(message) {
      
      	botclient.@edu.test.wac.client.BotClient::receive(Ljava/lang/String;)(message.text);
      });
       
    }-*/;
	
	public void interrupt() {
		currentCb=null;
		sending=false;
	}
	
	public void receive(String msg) {
		
		if(currentCb!=null) {
			timeout.cancel();
			sending=false;
			//
			SimpleAsyncCallback<String> c=currentCb;
			currentCb=null;
			c.run(msg);
			
		}
	}
	
	native private void post(String s) /*-{

	 $wnd.wacBotConnection.postActivity({ 
	 	type: "message", 
	 	from: { id: @edu.test.wac.client.BotClient::id }, 
	 	text: s }).subscribe(function(id) {
	  console.log("success: msg engage: "+id);
	 });
	
	
	}-*/;

	private SimpleAsyncCallback<String> currentCb;
	private boolean sending=false;
	
	public void shout(String txt) {
		post(txt);
	}
	
	public void send(final String txt, final SimpleAsyncCallback<String> cb) {
		if(sending) throw new RuntimeException("Concurrency issue. Already sending message to bot.");
		
		if(!ready) {
			new Timer() {

				@Override
				public void run() {
					send(txt, cb);
				}
				
			}.schedule(2000);
			
			//
			return;
		}
		
		//
		sending=true;
		
		if(!simulateBot) {
			currentCb=cb;
			
			timeout.schedule(120000);
			post(txt);
			
		} else {
			int i=Random.nextInt(s.length);
			cb.run(s[i]);	
			sending=false;
		}
		
	}
	
	private Timer timeout=new Timer() {

		@Override
		public void run() {
			
			receive(null);
		}
		
	};

	public boolean isSimulated() {
		return simulateBot;
	}
}
