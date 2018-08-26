package edu.test.wac.client;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SmartDialog extends DialogBox {


	public SmartDialog(String title, boolean modal) {
		super();
		
		final DialogBox _dialogBox = this;
    	
		_dialogBox.getElement().getStyle().setZIndex(Integer.MAX_VALUE);
		_dialogBox.setText(title);
		_dialogBox.setAnimationEnabled(true);
		_dialogBox.setModal(modal);
		
		
		final Timer dt=new Timer() {
			
			@Override
			public void run() {
				_dialogBox.setPopupPosition(_dialogBox.getPopupLeft(), Window.getScrollTop() + _dialogBox.getPopupTop());
			
			}
		}; 
		
		final HandlerRegistration r=Window.addWindowScrollHandler(new Window.ScrollHandler() {
			
			@Override
			public void onWindowScroll(ScrollEvent event) {
				dt.cancel();
				dt.schedule(500);
			}
		});
		
		_dialogBox.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
			
				dt.cancel();
				r.removeHandler();
			}
		});
	}
}
