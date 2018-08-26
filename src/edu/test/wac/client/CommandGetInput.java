package edu.test.wac.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

public class CommandGetInput implements Command {

	private String descr;
	private String id;
	private FormatCheck fc;
	private boolean pwd=false;
	
	static public interface FormatCheck {
		/**
		 * Return null if ok
		 * @param s
		 * @return
		 */
		public String isOk(String s);
	}
	
    public CommandGetInput(String descr, String id, FormatCheck fc) {
		super();
		this.descr = descr;
		this.id = id;
		this.fc=fc;
	}

    public CommandGetInput setPwd() {
    	pwd=true;
    	return this;
	}
    
	@Override
	public void run(final ObjectDictionary od, final ReturnHandler rh) {
		rh.getServerStorage().get(id, new SimpleAsyncCallback<String>() {
			
			@Override
			public void run(String oldVal) {
				
				// TODO Auto-generated method stub
				FlowPanel fp=rh.getDisplayPanel();
				final Label error=new Label();
				error.getElement().getStyle().setColor("#ff0000");
				fp.add(error);
				fp.add(new Label(descr+": "));
				//
				final TextBox tb=pwd ? new PasswordTextBox() : new TextBox();
				if(oldVal!=null) {
					tb.setValue(oldVal);
				}
				
				fp.add(tb);
				
				
				//
				Button b=new Button("Ok");
				b.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						final String val=tb.getValue();
						//
						String ok=fc==null ? null : fc.isOk(val);
						if(ok!=null) {
							error.setText(ok);
							return;
						} else {
							error.setText("");
						}
						
						//
						rh.getServerStorage().put(id, val, new SimpleAsyncCallback<Void>() {
							
							@Override
							public void run(Void v) {
								od.put(id, val);
								rh.exit(0);		
							}
						});
						
					}
				});
				
				fp.add(b);
			}
		});
		
		
	}

	@Override
	public String successMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String requestMessage() {
		return "Please enter the following information:";
	}

	@Override
	public String confirmMessage() {
		return null;
	}

	@Override
	public void onUserConfirmation(boolean ok) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean cannotBeAutoconfirmed() {
		return false;
	}

}
