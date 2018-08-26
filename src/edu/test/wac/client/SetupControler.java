package edu.test.wac.client;

import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SetupControler {

	static boolean fatalError = false;
	static void showFatalError(String txt) {
		if (fatalError)
			return;
		fatalError = true;

		SmartDialog sd = new SmartDialog("Fatal Error", true);
		FlowPanel fp = new FlowPanel();
		fp.add(new Label(txt));
		fp.add(new Label("Fix errors or reload browser & re-calibrate"));
		sd.add(fp);
		sd.center();

	}
	private GreetingServiceAsync greetingService;
	private HumanMouseEventServiceAsync hmeService;

	private Logger log = Logger.getLogger(this.getClass().getName());

	private SimpleAsyncCallback<State> mainCallback;
	final ObjectDictionary od = new ObjectDictionary();

	private ServerStorageEventServiceAsync ssService;

	private boolean started = false;
	private boolean debugCalibration=false;
	
	private State state;

	private UIControler uiControler = new UIControler();

	public SetupControler(GreetingServiceAsync greetingService, HumanMouseEventServiceAsync hmeService,
			ServerStorageEventServiceAsync ssService, SimpleAsyncCallback<State> mainCallback) {
		this.state = new State();
		this.greetingService = greetingService;
		this.hmeService = hmeService;
		this.ssService = ssService;
		this.mainCallback = mainCallback;

	}

	public static DateTimeFormat df=DateTimeFormat.getFormat("HH:mm");
	
	private CommandGetInput.FormatCheck timeFormatCheck=new CommandGetInput.FormatCheck() {
		
		@Override
		public String isOk(String s) {
			try {
				df.parse(s);
				
			}catch(IllegalArgumentException ex) {
				return "Incorrect format.";
			}
			//
			return null;
		}
	};

	private void _setupCalibrator(final Calibrator calibrator) {
		
		String now=df.format(new Date());
		
		calibrator.add(new CommandGetInput("Response time window start, Format: 00:00 (Current time: "+now+")", ObjectDictionary.STR_TIME__START, timeFormatCheck));
		calibrator.add(new CommandGetInput("Response time window end, Format: 00:00 (Current time: "+now+")", ObjectDictionary.STR_TIME__END, timeFormatCheck));
		//if(1==1) return;	
		
calibrator.add(new CommandGetInput("Whats app user names to be ignored (comma separated). Useful to have same state for calibration.", ObjectDictionary.STR_USERS_TO_IGNORE, null));
		
		calibrator.add(new CommandGetInput("Bot Secret", ObjectDictionary.STR_BOT__SECRET, null).setPwd());
		
		
		//if(1==1) return;
		
		// calibrator.add(new CommandGetElement("Current chat user image (select any
				// contact first)", ObjectDictionary.E_CURRENT_CHAT_USER_IMAGE));

				// calibrator.add(new CommandGetElement("Contact image on the left",
				// ObjectDictionary.E_CONTACT_IMAGE));

				// calibrator.add(new CommandGetUrlParameter("that describes user (choose int
				// over telnr)", ObjectDictionary.PARAM_USER_ID,
				// ObjectDictionary.E_CONTACT_IMAGE, "src"));

				calibrator.add(new CommandGetElement("up arrow of chat area scroll bar",
						ObjectDictionary.E_CHAT_AREA));

				calibrator.add(new CommandGetElement("chat text input field.", ObjectDictionary.E_TEXT_INPUT));

				// calibrator.add(new CommandGetElement("last date/time bubble in chat",
				// ObjectDictionary.E_CHAT_DATE_MSG));

				//
				calibrator.add(new CommandGetElement("a chat message from another person (without speech bubble arrow)",
						ObjectDictionary.E_CHAT_MSG_FROM_OTHER));

				calibrator.add(new CommandGetElement(
						"the time of that chat message from the other person (click+drag mouse pointer over time to prevent the mousover effect)",
						ObjectDictionary.E_CHAT_MSG_FROM_OTHER_TIME));

				calibrator.add(new CommandGetElement("a chat message from you (without speech bubble arrow)",
						ObjectDictionary.E_CHAT_MSG_FROM_YOU));

				calibrator.add(new CommandGetElement("a contact name on the left WITH a new message indicator (send a message from that contact first if necessary)",
						ObjectDictionary.E_CONTACT_NAME_WITH_INDICATOR));
				calibrator.add(new CommandGetElement("a new message indicator on the left", ObjectDictionary.E_INDICATOR));

				calibrator.add(new CommandGetElement("another contact name on the left WITHOUT new message indicator",
						ObjectDictionary.E_CONTACT_NAME_WITHOUT_INDICATOR));

				calibrator.add(new CommandChooseExlusiveClass("describes outgoing messages?", ObjectDictionary.CLASS_MSG_OUT,
						ObjectDictionary.E_CHAT_MSG_FROM_YOU, ObjectDictionary.E_CHAT_MSG_FROM_OTHER));
				calibrator.add(new CommandChooseExlusiveClass("describes incoming messages?", ObjectDictionary.CLASS_MSG_IN,
						ObjectDictionary.E_CHAT_MSG_FROM_OTHER, ObjectDictionary.E_CHAT_MSG_FROM_YOU));

				calibrator.add(new CommandHighlighter() {

					@Override
					public boolean cannotBeAutoconfirmed() {
						// TODO Auto-generated method stub
						return true;
					}

					@Override
					public void onUserConfirmation(final boolean ok) {
						// TODO Auto-generated method stub

						if (!ok)
							calibrator.exit(1);
						else
							unhighlightAll();
					}

					@Override
					public String confirmMessage() {
						// TODO Auto-generated method stub
						return "Check colors: chat area: red, txt input=pink, incoming chat=red, outgoing=green, chat text=black, chat time=orange?";
					}

					@Override
					public String requestMessage() {
						// TODO Auto-generated method stub
						return "Observe...";
					}

					@Override
					public void run(ObjectDictionary od, ReturnHandler rh) {

						
						Element textInput = od.getElement(ObjectDictionary.E_TEXT_INPUT);
						highlight(textInput, "pink");
						state.textInputXpath = new XPath(textInput).toString();

						//
						Element chatFromOtherText = od.getElement(ObjectDictionary.E_CHAT_MSG_FROM_OTHER);
						Element chatFromOtherTime = od.getElement(ObjectDictionary.E_CHAT_MSG_FROM_OTHER_TIME);
						Element chatFromOther = DomUtil.findCommonAncestor(chatFromOtherText, chatFromOtherTime);
						Element chatFromYou = od.getElement(ObjectDictionary.E_CHAT_MSG_FROM_YOU);

						final Element chatWin = od.getElement(ObjectDictionary.E_CHAT_AREA);
						state.chatAreaXpath=	new XPath(chatWin).toString();
						highlight(chatWin, "red");
						
						XPath xp_chatFromYou = new XPath(chatFromYou);

						XPath xp_chatFromOther = new XPath(chatFromOther);

						state.xp_rel_chatFromOtherText = new XPath(chatFromOtherText, chatFromOther);

						state.xp_rel_chatFromOtherTime = new XPath(chatFromOtherTime, chatFromOther);

						state.xps_chat = xp_chatFromOther.toString(xp_chatFromYou);

						state.outClass = od.getString(ObjectDictionary.CLASS_MSG_OUT);
						state.inClass = od.getString(ObjectDictionary.CLASS_MSG_IN);

						Element chatsFromOther[] = XPath.evaluateXPath(state.xps_chat);

						for (Element e : chatsFromOther) {
							XPath xp_c = new XPath(e);
							boolean isIn = xp_c.containsClass(state.inClass);
							boolean isOut = xp_c.containsClass(state.outClass);
							if ((isIn && isOut) || (!isIn && !isOut)) {
								rh.getDisplayPanel()
										.add(new Label("Conflicting chat msg state: in=" + isIn + ", out=" + isOut));
								rh.exit(24);
								return;
							}

							highlight(e, isIn ? "red" : "lightgreen");

							//
							Element text[] = XPath.evaluateXPath(state.xp_rel_chatFromOtherText.toString(), e);
							if (text.length > 1) {
								throw new RuntimeException("More than one texts found in chat bubble from other.");
								
								
							} else if (text.length == 1) {
								highlight(text[0], "black");
							}

							//
							Element time[] = XPath.evaluateXPath(state.xp_rel_chatFromOtherTime.toString(), e);
							if (time.length > 1) {
								throw new RuntimeException("More than one texts found in time in chat bubble from other.");
								
							} else if (time.length == 1) {
								highlight(time[0], "orange");
							}
						}

						/*
						 * Element chatDate=od.getElement(ObjectDictionary.E_CHAT_DATE_MSG); XPath
						 * xp_chatDate=new XPath(chatDate); Element
						 * chatDates[]=XPath.evaluateXPath(xp_chatDate.toString()); for(Element e :
						 * chatDates) { highlight(e, "lightblue"); }
						 */

						// get differentiating classes between in and out (message-in AND message-out)
						// from xpath comparison

						//
						rh.exit(0);
					}

					@Override
					public String successMessage() {
						// TODO Auto-generated method stub
						return null;
					}

				});

				calibrator.add(new CommandHighlighter() {

					@Override
					public boolean cannotBeAutoconfirmed() {
						// TODO Auto-generated method stub
						return true;
					}

					@Override
					public void onUserConfirmation(final boolean ok) {

						if (!ok)
							calibrator.exit(1);
						else
							unhighlightAll();
					}

					@Override
					public String confirmMessage() {
						// TODO Auto-generated method stub
						return "Check colors: all contacts: green; new msg indicator: red; name with new message indicator: blue";
					}

					@Override
					public String requestMessage() {
						// TODO Auto-generated method stub
						return "Observe....";
					}

					@Override
					public void run(ObjectDictionary od, ReturnHandler rh) {
						// Element contactImage=od.getElement(ObjectDictionary.E_CONTACT_IMAGE);

						Element newMessageIndicator = od.getElement(ObjectDictionary.E_INDICATOR);

						Element contactNameWithNewMessageIndicator = od
								.getElement(ObjectDictionary.E_CONTACT_NAME_WITH_INDICATOR);
						Element contactNameWithoutNewMessageIndicator = od
								.getElement(ObjectDictionary.E_CONTACT_NAME_WITHOUT_INDICATOR);
						Element contactWithNewMessageIndicator = DomUtil.findCommonAncestor(contactNameWithNewMessageIndicator,
								newMessageIndicator);

						XPath xp_contactWithNewMessageIndicator = new XPath(contactWithNewMessageIndicator);

						XPath xp_contactNameWithoutNewMessageIndicator = new XPath(contactNameWithoutNewMessageIndicator);

						state.xps_contactWithNewIndicator = xp_contactWithNewMessageIndicator
								.toString(xp_contactNameWithoutNewMessageIndicator);

						state.xp_rel_newMessageIndicator = new XPath(newMessageIndicator, contactWithNewMessageIndicator);
						state.xp_rel_name = new XPath(contactNameWithNewMessageIndicator, contactWithNewMessageIndicator);

						// state.xp_rel_img=new XPath(contactImage, contactWithNewMessageIndicator);

						Element contacts[] = XPath.evaluateXPath(state.xps_contactWithNewIndicator);
						for (Element c : contacts) {
							highlight(c, "lightgreen");
							//
							Element messageIndicator[] = XPath.evaluateXPath(state.xp_rel_newMessageIndicator.toString(), c);
							if (messageIndicator.length > 1) {
								throw new RuntimeException("More than one texts found new message indicator.");
								
							} else if (messageIndicator.length == 1) {
								highlight(messageIndicator[0], "red");
							}

							//
							Element name[] = XPath.evaluateXPath(state.xp_rel_name.toString(), c);
							if (name.length > 1) {
								throw new RuntimeException("More than one texts in name of contact with new message indicator.");	
							
							} else if (name.length == 1) {
								highlight(name[0], "lightblue");
							}

							//
							/*
							 * Element img[]=XPath.evaluateXPath(state.xp_rel_img.toString(), c);
							 * if(img.length>1) { throw new RuntimeException("More than one contact image"); } else if(img.length==1) {
							 * highlight(img[0], "orange"); }
							 */
						}

						//
						rh.exit(0);
					}

					@Override
					public String successMessage() {
						// TODO Auto-generated method stub
						return null;
					}
				});

				calibrator.add(new Command() {

					String r;

					@Override
					public boolean cannotBeAutoconfirmed() {
						// TODO Auto-generated method stub
						return true;
					}

					@Override
					public void onUserConfirmation(boolean ok) {
						// TODO Auto-generated method stub

					}

					@Override
					public String confirmMessage() {
						//
						return "Successful: " + r;
					}

					@Override
					public String requestMessage() {
						// TODO Auto-generated method stub
						return "Successful?";
					}

					@Override
					public void run(final ObjectDictionary od, final ReturnHandler rh) {
						Integer LS = od.getInt(ObjectDictionary.INT_LOCAL_SERVER);
						if (LS != null && LS == 1) {
							rh.exit(0);
							return;
						}

						greetingService.greetServer("WAC test", new AsyncCallback<String>() {

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								showMsg("Calibration failed", caught.getMessage(), true, null);
								rh.exit(1);
							}

							@Override
							public void onSuccess(String result) {
								// TODO Auto-generated method stub
								r = result;
								od.put(ObjectDictionary.INT_LOCAL_SERVER, 1);
								rh.exit(0);
							}
						});
					}

					@Override
					public String successMessage() {
						// TODO Auto-generated method stub
						return null;
					}
				});

				// mozilla only!!!
				if (Navigator.getUserAgent().toUpperCase().contains("FIREFOX"))
					calibrator.add(new Command() {

						private HandlerRegistration hr;

						String s;
						@Override
						public boolean cannotBeAutoconfirmed() {
							// TODO Auto-generated method stub
							return false;
						}

						@Override
						public void onUserConfirmation(boolean ok) {
							// TODO Auto-generated method stub

						}

						@Override
						public String confirmMessage() {
							return s;
						}

						@Override
						public String requestMessage() {
							return "Move pointer to the left and then slow up to reach the top left of this webpage until confirmation.";
						}

						@Override
						public void run(final ObjectDictionary od, final ReturnHandler rh) {

							final Element d = Document.get().cast();
							// d.getStyle().setCursor(Cursor.MOVE);

							Event.sinkEvents(d, Event.ONMOUSEMOVE);

							hr = Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
								public void onPreviewNativeEvent(final NativePreviewEvent event) {
									final int eventType = event.getTypeInt();
									switch (eventType) {
									case Event.ONMOUSEMOVE:
										final int x = event.getNativeEvent().getClientX();
										final int y = event.getNativeEvent().getClientY();

										if (x == 0 && y == 0) {

											//
											// d.getStyle().setCursor(Cursor.AUTO);

											hr.removeHandler();

											//
											hmeService.execHumanMouseEvent(new HumanMouseEvent(),
													new AsyncCallback<HumanMouseEventResponse>() {

														@Override
														public void onFailure(Throwable caught) {
															showFatalError(caught.getMessage());
															rh.exit(1);

														}

														@Override
														public void onSuccess(HumanMouseEventResponse result) {

															//
															if (result.getExitCode() != 0) {

																showFatalError("HumanMouse exec failure: "
																		+ result.getExitCode() + ": " + result.getError());

																rh.exit(result.getExitCode());
															} else {

																s = "Viewport Origin plausible?: (" + result.getX() + ", "
																		+ result.getY() + ")";
																od.put(ObjectDictionary.INT_VP_ORIGIN_X, result.getX());
																od.put(ObjectDictionary.INT_VP_ORIGIN_Y, result.getY());

																state.vpOriginX = result.getX();
																state.vpOriginY = result.getY();

																rh.exit(0);
															}
														}
													});
											//
											//

										}

										break;
									default:
										// not interested in other events
									}
								}
							});
						}

						@Override
						public String successMessage() {
							// TODO Auto-generated method stub
							return null;
						}
					});
	}
	
	private void _calibrate() {
		boolean autoconfirmed = false;

		final SmartDialog _dialogBox = new SmartDialog("Calibration", false);
		final FlowPanel fp = new FlowPanel();
		_dialogBox.add(fp);

		final ServerStorage ss = new ServerStorage(ssService);

		final Calibrator calibrator = new Calibrator(uiControler, od, new ReturnHandler() {

			@Override
			public void exit(int code) {
				
				if (code != 0) {
					showMsg("Fatal error", "Calibration failed.", true, null);
					mainCallback.run(null);
				} else {
					_dialogBox.hide();

					_collectRemainingStateValues(state);
				
					//
					if(debugCalibration) {
						Window.alert("Calibration ended with: "+code);
						throw new RuntimeException("Controled exception: debugCalibration=true");
					}
					
					//
					mainCallback.run(state);

				}
			}

			@Override
			public FlowPanel getDisplayPanel() {
				return fp;
			}

			@Override
			public ServerStorage getServerStorage() {
				return ss;
			}
		}, autoconfirmed);

		_setupCalibrator(calibrator);
		//
		calibrator.run();
		//
		_dialogBox.center();
	}

	private void _collectRemainingStateValues(State state) {
		state.botSecret=od.getString(ObjectDictionary.STR_BOT__SECRET);
		state.start=df.parse(od.getString(ObjectDictionary.STR_TIME__START));
		state.end=df.parse(od.getString(ObjectDictionary.STR_TIME__END));
		//
		String ignoredUsers=od.getString(ObjectDictionary.STR_USERS_TO_IGNORE);
		if(ignoredUsers!=null) {
			ignoredUsers=ignoredUsers.trim();
			if(ignoredUsers.length()>0) {
				String us[]=ignoredUsers.split(",");
				for(String u : us) {
					u=u.trim();
					if(u.length()>0) {
						state.ignoredUsers.add(u);
					}
				}
			}
		}	
		
		/*
		Date now=new Date();
		Window.alert("START ("+now.getHours()+":"+now.getMinutes()+"): "+MainControler.compareTime(state.start, now));
		Window.alert("END: "+MainControler.compareTime(state.end, now));
		*/
	}
	
	private native void highlight(Element e, String col) /*-{

		e.style.border = "solid 1px " + col;
	}-*/;

	private native void hl(String xpath) /*-{
		var xpathResult = $doc.evaluate(xpath, $doc, null,
				XPathResult.UNORDERED_NODE_ITERATOR_TYPE, null);
		var x = [];
		try {
			var thisNode = xpathResult.iterateNext();

			while (thisNode) {
				x[x.length] = thisNode;
				thisNode = xpathResult.iterateNext();
			}
		} catch (e) {
			alert('Error: Document tree modified during iteration ' + e);
		}

		for (var i = 0; i < x.length; i++) {
			x[i].style.border = "solid 1px red";
		}
	}-*/;

	public void showMsg(String title, String msg, boolean cancel, boolean modal, final ReturnHandler rh) {
		// should use other callback then this ReturnHandler

		final SmartDialog _dialogBox = new SmartDialog(title, modal);

		FlowPanel fp = new FlowPanel();
		Label l = new Label(msg);
		fp.add(l);

		Button b = new Button("Ok");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				_dialogBox.hide();
				if (rh != null)
					rh.exit(0);
			}

		});
		fp.add(b);

		if (cancel) {
			Button b2 = new Button("Cancel");
			b2.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					_dialogBox.hide();
					if (rh != null)
						rh.exit(-1);
				}

			});
			fp.add(b2);

		}

		_dialogBox.setWidget(fp);

		//
		_dialogBox.center();
	}

	public void showMsg(String title, String msg, boolean modal, final ReturnHandler rh) {
		showMsg(title, msg, false, modal, rh);
	}

	public void start() {
		if (started)
			return;
		started = true;
		//
		showMsg("Initialize", "Make sure Firefox is using 100% Zoom. Then select a contact with NO NEW messages. Then click ok to continue.", false, new ReturnHandler() {

			@Override
			public void exit(int code) {

				_calibrate();

			}

			@Override
			public FlowPanel getDisplayPanel() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ServerStorage getServerStorage() {
				// TODO Auto-generated method stub
				return null;
			}
		});

	}

}
