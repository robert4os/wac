package edu.test.wac.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class UIControler {

	public void showMsg(String title, String msg, boolean modal, final ReturnHandler rh) {
	    showMsg(title,  msg,  false, modal,  rh);
	    }
	    
	    public void showMsg(String title, String msg, boolean cancel, boolean modal, final ReturnHandler rh) {
	    	// should use other callback then this ReturnHandler
	    	
	    	final SmartDialog _dialogBox = new SmartDialog(title, modal);
	    	
			FlowPanel fp=new FlowPanel();
			Label l=new Label(msg);
			fp.add(l);
			
			
			Button b=new Button("Ok");
			b.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					_dialogBox.hide();
					if(rh!=null) rh.exit(0);
				}
				
				
				
			});
			fp.add(b);
			
			if(cancel) {
				Button b2=new Button("Cancel");
				b2.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						_dialogBox.hide();
						if(rh!=null) rh.exit(-1);
					}
					
						
					
				});
				fp.add(b2);
				
			}
					
			_dialogBox.setWidget(fp);
			
			//
			_dialogBox.center();
	    }
	    
}
