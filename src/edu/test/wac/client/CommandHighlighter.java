package edu.test.wac.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Element;

abstract public class CommandHighlighter implements Command {

	Map<Element, String> e2oldborder=new HashMap<Element, String>();
	
	protected void highlight(Element e, String col) {
		e2oldborder.put(e, UiUtil.highlight(e, col));
	}
	
	protected void unhighlightAll() {
		for(Map.Entry<Element, String> m : e2oldborder.entrySet()) {
			UiUtil.resetBorder(m.getKey(), m.getValue());
		}
		
	}
}
