package edu.test.wac.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Wac implements EntryPoint {

	private Logger log = Logger.getLogger(this.getClass().getName());

	private boolean started = false;

	// private State state=new State();
	private UIControler uiControler = new UIControler();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			public void onUncaughtException(Throwable e) {
				log.log(Level.SEVERE, "Uncaught: " + e.getMessage(), e);
				Window.alert("Uncaught: " + e.getMessage());
			}
		});

		/*
		 * { uniqueTest(); return; }
		 */

		final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
		final ServerStorageEventServiceAsync ssService = GWT.create(ServerStorageEventService.class);
		final HumanMouseEventServiceAsync hmeService = GWT.create(HumanMouseEventService.class);
		//
		((ServiceDefTarget) greetingService).setServiceEntryPoint("https://127.0.0.1/wac/greet");
		((ServiceDefTarget) hmeService).setServiceEntryPoint("https://127.0.0.1/wac/hme");
		((ServiceDefTarget) ssService).setServiceEntryPoint("https://127.0.0.1/wac/ss");

		//

		if (SwissKnifeDev.isDevMode()) {

			//
			FlowPanel devp = SwissKnifeDev.createSuperSvrCompileButton();
			devp.getElement().getStyle().setZIndex(2147483647);
			devp.getElement().getStyle().setPosition(Position.ABSOLUTE);
			devp.getElement().getStyle().setLeft(0, Unit.PX);
			devp.getElement().getStyle().setTop(0, Unit.PX);
			RootPanel.get().add(devp);
		}

		SetupControler setupControler = new SetupControler(greetingService, hmeService, ssService,
				new SimpleAsyncCallback<State>() {

					@Override
					public void run(final State state) {
						if (state != null) {
							startMainControler(state, hmeService);
						} else {
							Window.alert("Setup failed! Reload page.");
						}

					}
				});
		setupControler.start();

		//

		//
	}

	private void startMainControler(final State state, final HumanMouseEventServiceAsync hmeService) {
		if (started)
			return;
		started = true;

		//
		uiControler.showMsg("Calibration successful", "Click ok to start.", true,
				new ReturnHandler() {

					@Override
					public void exit(int code) {

						new Timer() {

							@Override
							public void run() {
								//
								MainControler mc = new MainControler(state, new HumanUser(state, hmeService), new BotClient(state.botSecret));
								mc.start();
							}

						}.schedule(2000);
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


	@SuppressWarnings("unused")
	private void uniqueTest() {
		String botSecret="";
		if(botSecret==null || botSecret.length()==0) throw new RuntimeException("Missing bot secret!");
		BotClient bc = new BotClient(botSecret);
		bc.send("hallo", new SimpleAsyncCallback<String>() {

			@Override
			public void run(String v) {
				Window.alert("TRUE: " + v);

			}
		});

	}

}
