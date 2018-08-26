package edu.test.wac.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Text;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.UIObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UiUtil {

    static private Logger log = Logger.getLogger(UiUtil.class.getName());

    static private native int getWindowClientInnerWidth() /*-{

        return $wnd.innerWidth ? $wnd.innerWidth : $doc.documentElement.clientWidth;
    }-*/;

    static private native int getWindowClientInnerHeight() /*-{

        return $wnd.innerHeight ? $wnd.innerHeight : $doc.documentElement.clientHeight;
    }-*/;

    static native public Integer getNativeOrientation() /*-{
        // !!! note: not reliable in Android 2 browser
        return $wnd.orientation;
    }-*/;

    native static public int getScreenWidth() /*-{
        return $wnd.screen.width;
    }-*/;

    native static public int getScreenHeight() /*-{
        return $wnd.screen.height;
    }-*/;

    static public String getOrientation() {
        String result;

            // No JavaScript orientation support. Work it out.
            if(Document.get().getDocumentElement().getClientWidth() > Document.get().getDocumentElement().getClientHeight()) {
                if(DebugUtil.expensiveLog) log.warning( "getOrientation: (1)");
                result = "landscape";
            } else {
                if(DebugUtil.expensiveLog) log.warning( "getOrientation: (2)");
                result = "portrait";
            }

        return result;
    }

    static public double getScale() {
        {
            // Get viewport width
            int viewportWidth=Document.get().getDocumentElement().getClientWidth();
            int viewportHeight=Document.get().getDocumentElement().getClientHeight();

            //
            if(getScreenWidth() > viewportWidth) {
                if(DebugUtil.expensiveLog) log.warning( "getScale: getScreenWidth("+getScreenWidth()+") > viewportWidth("+viewportWidth+")");
                // ignore (it happens in Android 2)
            }

            //
            int width=viewportWidth;

                //
            // Get the orientation corrected screen width
            if(getOrientation().equals("portrait")) {
                // Take smaller of the two dimensions
                if(viewportWidth > viewportHeight) width=viewportHeight;
            } else {
                // Take larger of the two dimensions
                if(viewportWidth < viewportHeight) width=viewportHeight;
            }

            // Calculate viewport scale
            return (double)width/getWindowClientInnerWidth();
        }
    }

    private static int delayInMs=100;
    
    static public void setFocus(final Focusable focusable) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            public void execute() {
                focusable.setFocus(true);
            }
        });
    }

    public static class ScrollState {
        private List<ElementScrollPos> list;

        private ScrollState(List<ElementScrollPos> list) {
            this.list = list;
        }

        public void restore() {
            for(ElementScrollPos scrollPos : list) {
                scrollPos.restore();
            }
        }
    }

    private static class ElementScrollPos {
        private Element element;
        private int x, y;

        public ElementScrollPos() {
            this.element=null;
            x= Window.getScrollLeft();
            y=Window.getScrollTop();
        }

        public ElementScrollPos(Element element) {
            this.element = element;
            x=element.getScrollLeft();
            y=element.getScrollTop();
        }

        private void restore() {
            if(element!=null) {
                element.setScrollLeft(x);
                element.setScrollTop(y);
            } else {
                Window.scrollTo(x, y);
            }
        }
        //
    }

    public static ScrollState getScrollState(Element e) {
        final List<UiUtil.ElementScrollPos> scrollPoses=new ArrayList<ElementScrollPos>();

        DomUtil.walkUp(e, new DomUtil.NodeHandler() {
            public boolean returnRejectedNode() {
                return false;
            }

            public boolean onText(Text text) {
                return true;
            }

            public boolean onElement(Element element) {
                scrollPoses.add(new UiUtil.ElementScrollPos(element));
                return true;
            }
        }, true);

        //
        scrollPoses.add(new UiUtil.ElementScrollPos(e.getOwnerDocument().getDocumentElement()));
        scrollPoses.add(new UiUtil.ElementScrollPos());

        return new ScrollState(scrollPoses);
    }

    static public void setPasswordType(UIObject e) {
        e.getElement().setAttribute("type", "password");
    }

    private static Boolean showTimeIn24Format = null;
    public static boolean isTimeIn24HourFormat() {
        if (showTimeIn24Format == null) {
            showTimeIn24Format = _showTimeIn24Format();
        }
        return showTimeIn24Format;
    }

    static native private boolean _showTimeIn24Format() /*-{
        var date = new Date(Date.UTC(2012, 11, 12, 3, 0, 0));
        var dateString = date.toLocaleTimeString();

        $wnd.alert(dateString+" = "+date.toString())

        // !!! apparently toLocaleTimeString() has a bug in Chrome.
        // toString() however returns 12/24 hour formats. If one of two contains AM/PM execute 12 hour coding.
        return !(dateString.match(/am|pm/i) || date.toString().match(/am|pm/i));
    }-*/;

    private static Boolean html5InputTimeSupport = null;
    public static boolean hasInputTypeTimeSupport() {

        if (html5InputTimeSupport == null) {
            InputElement i = Document.get().createTextInputElement();
            i.setAttribute("type", "time");
            i.setValue("ttime");
            html5InputTimeSupport = !i.getValue().equals("ttime");
        }
        return html5InputTimeSupport;
    }

  private static Boolean html5InputDateSupport = null;
  public static boolean hasInputTypeDateSupport() {
    if (html5InputDateSupport == null) {
      InputElement i = Document.get().createTextInputElement();
      i.setAttribute("type", "date");
      i.setValue("ddate");
      html5InputDateSupport = !i.getValue().equals("ddate");
    }
    return html5InputDateSupport;
  }

  public static interface FocusSourceHandler extends EventHandler {

        public enum FocusSource {
            TAB,
            TAB_REVERS,
            MOUSE
        }

        public void onFocus(FocusSourceEvent focusSourceEvent);
    }

    public static class FocusSourceEvent extends GwtEvent<FocusSourceHandler> {

        public static final GwtEvent.Type<FocusSourceHandler> TYPE = new GwtEvent.Type<FocusSourceHandler>();

        private FocusSourceHandler.FocusSource focusSource;

        public FocusSourceEvent(FocusSourceHandler.FocusSource focusSource) {
            this.focusSource = focusSource;
        }

        public FocusSourceHandler.FocusSource getFocusSource() {
            return focusSource;
        }

        @Override
        public Type<FocusSourceHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(FocusSourceHandler focusSourceHandler) {
            focusSourceHandler.onFocus(this);
        }
    }

    static public native void resetBorder(Element e, String c) /*-{
	
  	e.style.border=c;
    }-*/;

   static public native String highlight(Element e, String col) /*-{
	
	var s=e.style.border;
	e.style.border="solid 1px "+col;
	return s;
   }-*/;

}