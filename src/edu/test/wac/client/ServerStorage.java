package edu.test.wac.client;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ServerStorage implements Serializable, IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7223374375005482707L;

	private Logger log = Logger.getLogger(this.getClass().getName());

	//
	private ServerStorageEventServiceAsync ssService;

	public ServerStorage(ServerStorageEventServiceAsync ssService) {
		this.ssService = ssService;
	}

	public void clear(String id) {
		// todo implement on server id2str.remove(id);
	}

	public void get(String id, final SimpleAsyncCallback<String> callback) {
		ServerStorageEvent c = new ServerStorageEvent(getWs(), ServerStorageEvent.CMD_DICT_GET);
		c.setK(id);

		ssService.execServerStorageEvent(c, new AsyncCallback<ServerStorageEventResponse>() {

			@Override
			public void onFailure(Throwable caught) {
				log.log(Level.SEVERE, caught.getMessage(), caught);
				throw new RuntimeException(caught);
			}

			@Override
			public void onSuccess(ServerStorageEventResponse result) {
				callback.run(result.getV());
			}
		});

	}

	private String getWs() {
		String ws = Window.Location.getParameter("ws");
		return ws;
	}

	public void put(String id, String v, final SimpleAsyncCallback<Void> callback) {
		ServerStorageEvent c = new ServerStorageEvent(getWs(), ServerStorageEvent.CMD_DICT_SET);
		c.setK(id);
		c.setV(v);

		ssService.execServerStorageEvent(c, new AsyncCallback<ServerStorageEventResponse>() {

			@Override
			public void onFailure(Throwable caught) {
				log.log(Level.SEVERE, caught.getMessage(), caught);
				throw new RuntimeException(caught);
			}

			@Override
			public void onSuccess(ServerStorageEventResponse result) {
				callback.run(null);
			}
		});
	}
}
