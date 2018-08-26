package edu.test.wac.client;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SwissKnifeDev {

	private static String devHost;

	static private Logger log = Logger.getLogger(SwissKnifeDev.class.getName());

	private static List<FlowPanel> pixels = new ArrayList<FlowPanel>();

	private static String superDevHost;

	private static FlowPanel superSvrCompilePanel;

	public static FlowPanel createSuperSvrCompileButton() {
		if (superSvrCompilePanel != null)
			return superSvrCompilePanel;

		// !!! get url from url parameter or auto detect!
		String superServerUrl = SwissKnifeDev.getSuperDevHost();

		//
		//
		FlowPanel superSvrCompilePanel = new FlowPanel();

		// BuildInfo buildInfo = GWT.create(BuildInfo.class);
		// String bt=buildInfo.getTime();
		String bt = "" + GWT.getPermutationStrongName();

		//
		superSvrCompilePanel.add(new Label("ABBT: " + bt));

		final Button compile = new Button("compile");
		superSvrCompilePanel.add(compile);

		final Label url = new Label(superServerUrl);
		superSvrCompilePanel.add(url);
		superSvrCompilePanel.add(new Label(
				"(Use url parameter '" + GlobalConstants.URL_PARAMETER_GWT_SUPERSVR + "' to overwrite url!)"));

		final Label label = new Label();
		label.setVisible(false);
		label.getElement().getStyle().setColor("#ff0000");
		superSvrCompilePanel.add(label);

		// !!!
		final String _superServerUrl = superServerUrl;

		compile.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent clickEvent) {

				compile.setText("Compiling...");
				compile.setEnabled(false);
				label.setVisible(false);

				NodeList<Element> nl = Document.get().getElementsByTagName("script");
				for (int i = 0; i < nl.getLength(); i++) {
					ScriptElement e = (ScriptElement) nl.getItem(i);
					if (e.getSrc().contains("/dev_mode_on.js")) {
						e.removeFromParent();
					}

				}

				//
				final String u = _superServerUrl + "/dev_mode_on.js";

				// !!!
				setBookmarkletParams(_superServerUrl + "/", GWT.getModuleName());

				// !!! note the getWin
				ScriptInjector.fromUrl(u).setCallback(new Callback<Void, Exception>() {
					public void onFailure(Exception e) {

						showCompileError("Connection to server failed: " + u);
					}

					public void onSuccess(Void result) {

						compile.setEnabled(true);
					}
				}).setWindow(getWin()).inject();
			}

		});

		//
		if (superServerUrl != null) {
			SwissKnifeDev.superSvrCompilePanel = superSvrCompilePanel;
		}
		return superSvrCompilePanel;
	}

	static public Widget drawPixel(int x, int y) {
		while(pixels.size() >= 5) {
			// remove from dom
			pixels.get(0).removeFromParent();
			
			// remove from stack
			pixels.remove(0);
		}
		//
		for (FlowPanel op : pixels) {
			op.getElement().getStyle().setBackgroundColor("brown");
		}

		//
		FlowPanel p = new FlowPanel();
		p.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		p.getElement().getStyle().setLeft(x - 2, Style.Unit.PX);
		p.getElement().getStyle().setTop(y - 2, Style.Unit.PX);
		p.getElement().getStyle().setWidth(5, Style.Unit.PX);
		p.getElement().getStyle().setHeight(5, Style.Unit.PX);
		p.getElement().getStyle().setBackgroundColor("red");
		p.getElement().getStyle().setZIndex(Integer.MAX_VALUE);
		RootPanel.get().add(p);

		//
		pixels.add(p);
		return p;
	}

	/*
	 * ??? on success function reloadInDevMode(module_name, codeserver_url) { var
	 * key = '__gwtDevModeHook:' + module_name; sessionStorage[key] = codeserver_url
	 * + module_name + '/' + module_name + '.nocache.js'; window.location.reload();
	 * }
	 */

	/*
	 * ??? use to call compile json function callJsonp(url_prefix, callback) { var
	 * callback_id = 'c' + globals.callback_counter++;
	 * globals.callbacks[callback_id] = function(json) { delete
	 * globals.callbacks[callback_id]; callback(json); };
	 * 
	 * var url = url_prefix + '_callback=__gwt_bookmarklet_globals.callbacks.' +
	 * callback_id;
	 * 
	 * var script = document.createElement('script'); script.src = url;
	 * document.getElementsByTagName('head')[0].appendChild(script);
	 * 
	 * then check status }
	 */

	static public String getDevHost() {
		if (devHost != null)
			return devHost;

		// ??? use only for super dev (expect fixes argument as parameter for server
		// e.g. darwinillo)
		String svr = Window.Location.getParameter(GlobalConstants.URL_PARAMETER_GWT_DEVSVR);
		if (svr != null && svr.length() > 0) {
			if (!svr.startsWith("http")) {
				svr = "http://" + svr;
			}
		} else {

			String host = Window.Location.getHostName();
			if (host == null || host.length() == 0) {
				host = "undefined.host";
			}

			//
			String portStr = Window.Location.getPort();
			if (portStr == null || portStr.length() == 0)
				portStr = "8888";
			portStr = ":" + portStr;

			//
			String protocol = Window.Location.getProtocol();
			if (!protocol.equals("http") && !protocol.equals("https"))
				protocol = "http";

			svr = protocol + "://" + host + portStr;
		}

		//
		devHost = svr;
		log.warning("DEV SERVER HOST: " + devHost);
		return devHost;
	}

	public static native String getModuleBindings(String moduleName) /*-{

		// from dev_mode_on.js
		function encodePair(key, value) {
			return encodeURIComponent(key) + '=' + encodeURIComponent(value);
		}

		//

		// !!! from call to compile(...) to in runBookmarklet in dev_mode_on.js
		function get_prop_map() {
			var active_modules = $wnd.__gwt_activeModules;

			if (active_modules) {
				var module = active_modules[moduleName];
				if (module) {
					//
					return module.bindings();

				} else {
					$wnd.alert("Module not found: " + moduleName);
				}
			} else {
				$wnd.alert("Super dev mode not on!");
			}

			return null;
		}

		// !!! from getBindingParameters in dev_mode_on.js
		var session_key = '__gwtDevModeSession:' + moduleName;

		var prop_map = get_prop_map();
		var props = [];
		if (prop_map) {
			for ( var key in prop_map) {
				//noinspection JSUnfilteredForInLoop
				props.push(encodePair(key, prop_map[key]));
			}
		}

		if (!props.length) {
			// There is only one permutation, maybe because we're in dev mode already.
			// Use the cached value if present.
			var cached = $wnd.sessionStorage[session_key];
			return cached || '';
		}

		var encoded = props.join('&') + '&';
		// Cache it for the next recompile.
		$wnd.sessionStorage[session_key] = encoded;
		return encoded;

	}-*/;

	static public String getSuperDevHost() {
		if (superDevHost != null)
			return superDevHost;

		String superServerUrl = Window.Location.getParameter(GlobalConstants.URL_PARAMETER_GWT_SUPERSVR);
		if (superServerUrl != null && superServerUrl.length() > 0) {

			String protocol = Window.Location.getProtocol();
			if (!protocol.equals("http") && !protocol.equals("https"))
				protocol = "http";

			if (!superServerUrl.startsWith("http"))
				superServerUrl = protocol + "://" + superServerUrl;
		} else {
			superServerUrl = getDevHost();
			superServerUrl = superServerUrl.substring(0, superServerUrl.indexOf(":", superServerUrl.indexOf(":") + 1))
					+ ":9876";
		}

		//
		SwissKnifeDev.superDevHost = superServerUrl;
		return superServerUrl;
	}

	native private static JavaScriptObject getWin() /*-{
		return $wnd;
	}-*/;

	static public boolean isDevMode() {
		String superServerUrl = Window.Location.getParameter(GlobalConstants.URL_PARAMETER_GWT_SUPERSVR);
		return superServerUrl != null && superServerUrl.length() > 0;
	}

	native private static void setBookmarkletParams(String server, String moduleName) /*-{

		$wnd.__gwt_bookmarklet_params = {
			server_url : server,
			module_name : moduleName
		};
	}-*/;

	private static void showCompileError(String e) {
		Button compile = (Button) superSvrCompilePanel.getWidget(1);
		Label label = (Label) superSvrCompilePanel.getWidget(2);

		label.setVisible(true);
		label.setText(e);

		//
		compile.setText("compile");
		compile.setEnabled(true);
	}

	private SwissKnifeDev() {

	}
}
