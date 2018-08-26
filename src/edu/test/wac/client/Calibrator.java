package edu.test.wac.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class Calibrator implements ReturnHandler {

	private boolean autconfirm;
	private Command c;
	private List<Command> cmds = new ArrayList<Command>();
	private FlowPanel dp;
	private ReturnHandler masterRH;
	private ObjectDictionary od;

	private UIControler uiControler;

	public Calibrator(UIControler uiControler, ObjectDictionary od, ReturnHandler rh, boolean autconfirm) {
		this.uiControler = uiControler;
		this.od = od;
		this.masterRH = rh;
		this.dp = rh.getDisplayPanel();
		dp.clear();
		this.autconfirm = autconfirm;
	}

	public void add(Command c) {
		cmds.add(c);
	}

	@Override
	public void exit(int code) {

		if (code == 0) {

			String smsg = c.successMessage();
			if (smsg != null) {
				uiControler.showMsg("Success", smsg, true, null);
			}

			//

			// ??? only autoconmfirm if something was found!
			String cmsg = c.confirmMessage();
			if ((!autconfirm || c.cannotBeAutoconfirmed()) && cmsg != null) {

				Button y = new Button("Yes");
				y.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						c.onUserConfirmation(true);
						cmds.remove(0);
						run();
					}
				});

				Button n = new Button("No, repeat");
				n.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						c.onUserConfirmation(false);
						run();
					}
				});

				dp.add(new Label(cmsg));
				dp.add(y);
				dp.add(n);
			} else {
				c.onUserConfirmation(true);
				cmds.remove(0);

				run();

			}

		} else {
			dp.add(new Label("Calibration aborted: " + code));
			masterRH.exit(code);
		}
	}

	@Override
	public FlowPanel getDisplayPanel() {
		return masterRH.getDisplayPanel();
	}

	@Override
	public ServerStorage getServerStorage() {

		return masterRH.getServerStorage();
	}

	public void run() {

		dp.clear();

		if (cmds.size() == 0) {
			dp.add(new Label("Calibration successful!"));
			masterRH.exit(0);

		} else {

			//
			c = cmds.get(0);

			dp.clear();
			dp.add(new Label(c.requestMessage()));
			c.run(od, this);

		}
	}

}
