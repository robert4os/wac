package edu.test.wac.client;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.linker.CrossSiteIframeLinker;

public class HttpsCrossSiteIframeLinker extends CrossSiteIframeLinker {
    @Override
    protected String getJsDevModeRedirectHookPermitted(LinkerContext context) {
        return "$wnd.location.protocol == \"http:\" || $wnd.location.protocol == \"file:\" "
                + "|| $wnd.location.protocol == \"https:\"";
    }
}