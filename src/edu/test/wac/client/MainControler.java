package edu.test.wac.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class MainControler {

	private boolean speedup=false;
	
	private class Contact {
		@SuppressWarnings("unused")
		private String name;
		private int newMsg;
		private Element target;

		public Contact(int newMsg, String name, Element target) {
			super();
			this.newMsg = newMsg;
			this.name = name;
			this.target = target;
		}

	}

	private interface Runner {
		public void run();
	}
	
	private BotClient botClient;
	private Button button;
	private Contact currentContact;
	
	private MsgStack currentMsgStack = null;

	private boolean debug;
	private int errorBotMsgCnt = 0;
	private boolean humanInTheloop = false;
	private HumanUser humanUser;

	private boolean interrupted = true;

	private Logger log = Logger.getLogger(this.getClass().getName());

	private FlowPanel mp;

	private SmartDialog sd;

	private boolean started = false;

	private State state;

	private HashSet<Timer> timers = new HashSet<Timer>();

	private Timer wfr = new Timer() {

		@Override
		public void run() {
			_waitForResume();
		}

	};

	public MainControler(State state, HumanUser humanUser, BotClient botClient) {
		this.state = state;
		this.humanUser = humanUser;
		this.botClient = botClient;
	}

	static public int compareTime(Date ref, Date other) {
		int h=Integer.compare(other.getHours(), ref.getHours());
		if(h!=0) return h;
		
		int m=Integer.compare(other.getMinutes(), ref.getMinutes());
		if(m!=0) return m;
		
		return 0;
	}
	
	private boolean wasOutOfTime=false;
	
	private boolean checkTimeWindow() {
		Date now=new Date();
		boolean cs=compareTime(state.start, now)<0;
		
		boolean ce=(state.end.getHours()!=0 || state.end.getMinutes()!=0) ? compareTime(state.end, now)>0 : false;
		
		if(cs || ce) {
			
			if(!interrupted) setStatusMessage("Out of time window ("+(cs ? "Too early" : "Too late")+"), current time: "+SetupControler.df.format(new Date()));
			wasOutOfTime=true;
			return false;
			
		} else {
			if(wasOutOfTime) {
				setStatusMessage("Running...");
			}
			wasOutOfTime=false;
			return true;
		}
	}
	
	private void _waitForResume() {
		
		//
		wfr.cancel();
		
		// check time window
		
		if(!checkTimeWindow()) {
			consoleLog("waitForResume: out of time window!");
			
			// check again in a minute
						wfr.schedule(10000);
						return;
		}
		
		//
		if (interrupted) {
			consoleLog("waitForResume: still interrupted!");
			wfr.schedule(5000);

		} else {

			consoleLog("waitForResume: resuming...");
			
			if (currentContact!=null && currentMsgStack != null) {

				consoleLog("step 1");
				
				MsgStack currentMsg = new MsgStack(true);
				getAllMessagesInChat(currentMsg, currentMsgStack.getMsg(0));
				
				consoleLog("step 2");
				
				if(currentMsg.size()>0) {
					consoleLog("step 3");
					
					exploreChat(new SimpleAsyncCallback<Void>() {

						
						@Override
						public void run(Void v) {
							consoleLog("step 4");
							
							workOnChat(currentMsgStack, System.currentTimeMillis(), System.currentTimeMillis());
						}
					});
	
				} else {
					goToNextChat();
				}
				
								
			} else {
				goToNextChat();
			}
		}
	}

	private void askHumanForConfirmation(final SimpleAsyncCallback<Boolean> cb) {
		if (sd != null)
			return;
		//

		sd = new SmartDialog("Confirm or update the text first", false);
		FlowPanel fp = new FlowPanel();
		Button b1 = new Button("yes");
		b1.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				sd.hide();
				sd = null;
				cb.run(true);

			}
		});
		Button b2 = new Button("no");
		b2.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				sd.hide();
				sd = null;
				cb.run(false);

			}
		});
		fp.add(b1);
		fp.add(b2);
		sd.add(fp);
		sd.center();
	}

	public void consoleLog(String s) {
		if (debug) {
			log.log(Level.SEVERE, s);
		}
	}

	private void createControlDialog() {
		final SmartDialog sd = new SmartDialog("MainControler", false);
		FlowPanel fp = new FlowPanel();
		mp = new FlowPanel();
		mp.getElement().getStyle().setOverflowY(Overflow.SCROLL);

		mp.setWidth("400px");
		mp.setHeight("200 px");

		FlowPanel bp = new FlowPanel();
		fp.add(mp);
		fp.add(bp);
		//
		button = new Button("button");
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (interrupted) {
					resume();
				} else {
					interrupt();
				}
			}
		});
		bp.add(button);
		bp.add(new InlineLabel(" "));
		
		CheckBox d = new CheckBox("Logging");
		d.setValue(debug);
		d.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				debug=event.getValue();
				
			}
		});
		
		bp.add(d);
		bp.add(new InlineLabel(" "));
		
		//
		CheckBox auto = new CheckBox("Autopilot");
		auto.setValue(!humanInTheloop);
		auto.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				humanInTheloop=!event.getValue();
			}
		});
		bp.add(auto);
		bp.add(new InlineLabel(" "));
		
		bp.add(new Label("NEVER use the following in production: "));
		
		//
		CheckBox speed = new CheckBox("speedup");
		speed.setValue(speedup);
		speed.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				speedup=event.getValue();
			}
		});
		bp.add(speed);
		bp.add(new InlineLabel(" "));
		
		//
		CheckBox simBot = new CheckBox("simulate bot");
		simBot.setValue(botClient.isSimulated());
		simBot.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				boolean s=event.getValue();
				botClient.setSimultationMode(s);
			}
		});
		bp.add(simBot);
		bp.add(new InlineLabel(" "));
		
		//
		sd.add(fp);
		sd.setPopupPosition(Window.getClientWidth() - 400, Window.getScrollTop());
		sd.show();

		//
		@SuppressWarnings("unused")
		HandlerRegistration esc = RootPanel.get().addDomHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent ev) {
				if (ev.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {

					interrupt();
				}

			}
		}, KeyDownEvent.getType());

		//
		updateButton();
	}

	private void createTimer(String id, final Runner t, int scheduleInMs) {
		consoleLog("Create timer: "+id+" in "+scheduleInMs);
		//
		Timer timer = new Timer() {

			@Override
			public void run() {
				timers.remove(this);
				t.run();
			}
		};

		timers.add(timer);
		timer.schedule(scheduleInMs);
	}

	private void dealWithMsg(final MsgStack oldMsg, final MsgStack newMsg, final int i, final long startInMs,
			final long lastNewMsgInMs) {

		if (i >= newMsg.size()) {
			// all messages have been responded
			createTimer("dealWithMsg", new Runner() {
				public void run() {
					workOnChat(oldMsg, startInMs, lastNewMsgInMs);
				}
			}, speedup ? 5000 : 5000);

			return;
		}

		// respond to all new messages!
		consoleLog("dealWithMsg: msg at index: " + i);

		//
		final MsgStack.Msg msg = newMsg.getMsg(i);
		// consider msg old when dealt with

		//
		if (!msg.in) {

			oldMsg.add(msg);
			//
			dealWithMsg(oldMsg, newMsg, i + 1, startInMs, lastNewMsgInMs);
			return;
		}

		//
		setStatusMessage("Sending msg to bot: "+msg.txt);
		botClient.send(msg.txt, new SimpleAsyncCallback<String>() {

			@Override
			public void run(String v) {

				consoleLog("bot replies: >"+v+"<");
				
				// deal with a speechless bot
				if(v==null || v.equals("") || v.equals("[}Empty{]")) {
					
					if(v==null) {
						setStatusMessage("Connection timeout from bot.");	
					} else {
						setStatusMessage("Bot response was speechless.");	
					}
					
					//
					oldMsg.add(msg);
					dealWithMsg(oldMsg, newMsg, i + 1, startInMs, lastNewMsgInMs);
					return;
				}
				
				//
				setStatusMessage("Received reply from bot.");	
				
				if (humanInTheloop) {
					humanUser.humanWriteChatTextAsync(v, false, new SimpleAsyncCallback<Void>() {

						@Override
						public void run(Void v) {

							askHumanForConfirmation(new SimpleAsyncCallback<Boolean>() {

								@Override
								public void run(Boolean ok) {
									if (ok) {
										humanUser.humanWriteChatTextAsync("", true, new SimpleAsyncCallback<Void>() {

											@Override
											public void run(Void v) {
												oldMsg.add(msg);
												//
												dealWithMsg(oldMsg, newMsg, i + 1, startInMs, lastNewMsgInMs);
											}
										});
									} else {
										interrupt();
									}
								}

							});

						}
					});

				} else {
					humanUser.humanWriteChatTextAsync(v, true, new SimpleAsyncCallback<Void>() {

						@Override
						public void run(Void v) {
							oldMsg.add(msg);
							//
							dealWithMsg(oldMsg, newMsg, i + 1, startInMs, lastNewMsgInMs);
						}
					});
				}
			}
		});

	}

	private void getAllMessagesInChat(MsgStack oldMsg, MsgStack.Msg earliestMsg) {
		Element chatsFromOther[] = XPath.evaluateXPath(state.xps_chat);

		consoleLog("getAllMessagesInChat: START");
		if(earliestMsg!=null) consoleLog("getMsgs: "+earliestMsg);
		
		boolean start = earliestMsg == null;
		consoleLog("MSG CNT: "+chatsFromOther.length);
		List<Element> l=new ArrayList<Element>();
		boolean first=true;
		
		for (Element e : chatsFromOther) {
			XPath xp_c = new XPath(e);
			boolean isIn = xp_c.containsClass(state.inClass);
			boolean isOut = xp_c.containsClass(state.outClass);

			if ((isIn && isOut) || (!isIn && !isOut)) {
				String em = "Conflicting chat msg state: in=" + isIn + ", out=" + isOut;
				throw new RuntimeException(em);

			}

			//
			String _text = "";
			Element text[] = XPath.evaluateXPath(state.xp_rel_chatFromOtherText.toString(), e);
			if (text.length > 1) {
				String em = "unexpected text elements: " + text.length;
				throw new RuntimeException(em);
			}
			if (text.length > 0) {
				_text = text[0].getInnerText().replaceAll("\\s+", " ");
				//UiUtil.highlight(text[0], "red");
				//l.add(text[0]);
			}

			//
			Element time[] = XPath.evaluateXPath(state.xp_rel_chatFromOtherTime.toString(), e);
			if (time.length > 1) {
				String em = "unexpected time elements: " + time.length;
				throw new RuntimeException(em);
			}
			String _time = time[0].getInnerText();
			//

			MsgStack.Msg msg = MsgStack.createMsg(isIn, _text, _time);
			
			if(first) {
				consoleLog("getMsgs: First: "+msg+ " (start="+start+")");
				first=false;
			}
			
			
			if (earliestMsg != null && earliestMsg.equals(msg)) {
				start = true;
				continue;
			}

			if (!start)
				continue;
			//
			oldMsg.add(msg);
		}


		//Window.alert("stop?");
		
		for(Element e : l) {
			UiUtil.resetBorder(e, "");
		}
		
		consoleLog("getAllMessagesInChat: END");
	}

	private Contact getNextCOntactWithMessages(Element lastContact) {

		Element contacts[] = XPath.evaluateXPath(state.xps_contactWithNewIndicator);
		for (Element c : contacts) {
			//
			int newMsg = 0;
			Element messageIndicator[] = XPath.evaluateXPath(state.xp_rel_newMessageIndicator.toString(), c);
			if(messageIndicator.length==0) continue;
			if (messageIndicator.length > 1) {
				throw new RuntimeException("Unexpected nr of indicators in contact: " + messageIndicator.length);
			} else if (messageIndicator.length == 1) {
				newMsg = Integer.parseInt(messageIndicator[0].getInnerText());
			}

			//
			Element name[] = XPath.evaluateXPath(state.xp_rel_name.toString(), c);
			if(name.length==0) continue;
			if (name.length > 1) {
				throw new RuntimeException("Unexpected nr of names in contact: " + name.length);

			}

			//
			Element contact = name[0];
			String _name = contact.getInnerText();
			_name = _name.replaceAll("\\s+", " ").trim();

			//
			if(state.ignoredUsers.contains(_name)) {
				consoleLog("Ignoring user: "+_name);
				continue;
			}
			
			if (lastContact != null && contact == lastContact) {
				consoleLog("skipping contact found already last time: "+_name);
				continue;
			}

			
			//
			if (newMsg > 0) {
				consoleLog("contact found: "+_name);
				return new Contact(newMsg, _name, contact);
			}

			// Window.alert(_name+": "+userId+": "+newMsg);
		}

		//
		return null;
	}

	private void goToNextChat() {
		if (interrupted)
			return;
		
		//
		if(!checkTimeWindow()) {
			_waitForResume();
			return;
		}
		
		//
		consoleLog("goToNextChat");

		// loop through contacts
		final Contact contact = getNextCOntactWithMessages(currentContact!=null ? currentContact.target : null);
		currentMsgStack = null;
		currentContact = contact;
		
		if (contact == null) {
			
			createTimer("goToNextChat", new Runner() {
				public void run() {

					goToNextChat();
				}
			}, speedup ? 1000: 10000);

		} else {

			exploreChat(new SimpleAsyncCallback<Void>() {

				@Override
				public void run(Void v) {
					startWorkOnChat(contact);
				}
			});
	
			}
	}

	public void interrupt() {
		interrupted = true;
		updateButton();
		setStatusMessage("Interrupted.");

		//
		botClient.interrupt();
		
		// cancel ongoing timers
		for (Timer t : timers) {
			t.cancel();
		}
		timers.clear();

		//
		if (sd != null) {
			sd.hide();
			sd = null;
		}

		//
		_waitForResume();
	}

	public void resume() {
		interrupted = false;
		updateButton();
	}

	private void sendErrorToBot(String s) {
		if (errorBotMsgCnt >= 10)
			return;
		errorBotMsgCnt++;

		// 
		botClient.shout("[}Error{] " + s);
	}

	public void setStatusMessage(String m) {
		mp.clear();
		mp.add(new Label(m));
		//
		consoleLog("Status: "+m);
	}

	public void start() {
		if (started)
			return;
		started = true;

		// create display
		createControlDialog();

		//
		consoleLog("Start");

		//
		//GWT.setUncaughtExceptionHandler(null);
		
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			public void onUncaughtException(Throwable e) {

				String em = "Uncaught exception: " + e.getMessage();

				log.log(Level.SEVERE, em, e);
				setStatusMessage(em);
				//
				interrupt();

				//
				sendErrorToBot(em);
			}
		});

		//
		if(!interrupted) {
			new Timer() {

				@Override
				public void run() {
					goToNextChat();
				}
				
			}.schedule(1);
		} else {
			_waitForResume();
		}
	}

	private void exploreChat(final SimpleAsyncCallback<Void> cb) {
		
		consoleLog("exploreChat: click target");
		
		//
		humanUser.humanClickElementAsync(currentContact.target, new SimpleAsyncCallback<Void>() {

			@Override
			public void run(Void v) {
				consoleLog("exploreChat: select text input");
				
				humanUser.selectTextInput(new SimpleAsyncCallback<Void>() {
					@Override
					public void run(Void v) {
						
						consoleLog("exploreChat: find text input");
						
						Element ca[]=XPath.evaluateXPath(state.textInputXpath);
						if(ca.length!=1) throw new RuntimeException("Chat area not found!");
						final Element chatArea=ca[0];
						
						//
						 createTimer("exploreChat: scroll", new Runner() {
							 public void run() {
								 chatArea.setScrollTop(chatArea.getScrollHeight());
								 
								 createTimer("explore chat: run", new Runner() {
									 public void run() {
										 cb.run(null);
									 }
									}, 2000);
							 }
							}, 2000);					}
				});

			}
		});

		
	}
	
	private void startWorkOnChat(final Contact contact) {
		if (interrupted)
			return;
		consoleLog("startWorkOnChat: nr=" + contact.newMsg);
				
		//
		final MsgStack msg = new MsgStack(true);
		getAllMessagesInChat(msg, null);

		// remove new messages so to approximate the previous message state
		// NOTE: this could be perfectionized if the previous state was stored on the
		// server side and retrieved from there (if available else this workaround)
		// only consider maximally the past 10 messages
		for (int i = 0; i < contact.newMsg && i < 10; i++) {
			msg.removeLast();
		}

		//
		createTimer("startWorkOnChat", new Runner() {
			public void run() {

				workOnChat(msg, System.currentTimeMillis(), System.currentTimeMillis());
			}
		}, speedup ? 1000 : 10000);
	}

	private void updateButton() {
		if(interrupted) {
			setStatusMessage("Interrupted");
		} else {
			setStatusMessage("Running...");
		}
		
		button.setText(interrupted ? "Start" : "Interrupt");	
	}

	private void workOnChat(final MsgStack oldMsg, final long startInMs, long _lastNewMsgInMs) {
		if (interrupted)
			return;
		//
		currentMsgStack = oldMsg;
		//
		consoleLog("workOnChat ("+currentContact.name+")");
		consoleLog("workOnChat: prev msg count=" + oldMsg.size());

		int timeSinceStartInSec = (int) (System.currentTimeMillis() - startInMs) / 1000;

		MsgStack currentMsg = new MsgStack(true);
		getAllMessagesInChat(currentMsg, oldMsg.getMsg(0));
		consoleLog("workOnChat: current msg count=" + currentMsg.size());

		//
		MsgStack newMsg = oldMsg.getNew(currentMsg, -1, 5);
		consoleLog("workOnChat: nr new msg=" + newMsg.size() + ": " + newMsg);

		if (newMsg.size() > 0) {
			_lastNewMsgInMs = System.currentTimeMillis();
		}

		final long lastNewMsgInMs = _lastNewMsgInMs;
		int timeSinceNewMsgInSec = (int) (System.currentTimeMillis() - lastNewMsgInMs) / 1000;

		//
		if (timeSinceNewMsgInSec >= 120 || timeSinceStartInSec > (speedup ? 10 : 1000)) {
			// there was no new message in the past 3 minutes or this chat takes already
			// longer than 10 minutes

			consoleLog("workOnChat: we should leave this chat, cause too long no activity or too long activity.");

			if (getNextCOntactWithMessages(currentContact.target)!=null) {

				goToNextChat();
				return;
			} else {
				consoleLog("... but there is no other new chat activity.");
			}

		}

		//
		dealWithMsg(oldMsg, newMsg, 0, startInMs, lastNewMsgInMs);
	}
}
