package edu.test.wac.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Element;

public class ObjectDictionary {

	public static String INT_LOCAL_SERVER="INT_LOCAL_SERVER"; 
	public static String INT_VP_ORIGIN_X="INT_VP_ORIGIN_X"; 
	public static String INT_VP_ORIGIN_Y="INT_VP_ORIGIN_Y";
	public static String E_CONTACT_NAME_WITH_INDICATOR="E_CONTACT_NAME_WITH_INDICATOR";
	public static String E_CONTACT_NAME_WITHOUT_INDICATOR="E_CONTACT_NAME_WITHOUT_INDICATOR";
	public static String E_INDICATOR="E_INDICATOR";
	public static String E_CHAT_MSG_FROM_OTHER="E_CHAT_MSG_FROM_OTHER";
	//public static String E_CHAT_AREA="E_CHAT_AREA";
	public static String E_CHAT_MSG_FROM_OTHER_TIME="E_CHAT_MSG_FROM_OTHER_TIME";
	//public static String E_CHAT_DATE_MSG="E_CHAT_DATE_MSG";
	public static String CLASS_MSG_OUT="CLASS_MSG_OUT";
	public static String CLASS_MSG_IN="CLASS_MSG_IN";
	public static String E_CONTACT_IMAGE="E_CONTACT_IMAGE";
	public static String E_CURRENT_CHAT_USER_IMAGE="E_CURRENT_CHAT_USER_IMAGE";
	public static String PARAM_USER_ID="PARAM_USER_ID";
	public static String E_TEXT_INPUT="E_TEXT_INPUT";
	
	public static String E_CHAT_MSG_FROM_YOU="E_CHAT_MSG_FROM_YOU";
	public static String E_CHAT_AREA="E_CHAT_AREA";
	
	public static String STR_BOT__SECRET="STR_BOT__SECRET";
	public static String STR_TIME__START="STR_TIME__START";
	public static String STR_TIME__END="STR_TIME__END";
	public static String STR_USERS_TO_IGNORE="STR_USERS_TO_IGNORE";
	
	private Map<String, Integer> id2int=new HashMap<String, Integer>();
	private Map<String, Element> id2element=new HashMap<String, Element>();
	private Map<String, String> id2string=new HashMap<String, String>();

	public void put(String id, int i) {
		id2int.put(id,  i);
	}
	
	public void put(String id, Element i) {
		id2element.put(id,  i);
	}
	
	public Element getElement(String id) {
		return id2element.get(id);
	}
	
	public Integer getInt(String id) {
		return id2int.get(id);
	}

	public Iterable<Map.Entry<String, Integer>> getId2int() {
		return id2int.entrySet();
	}

	public Iterable<Map.Entry<String, Element>> getId2element() {
		return id2element.entrySet();
	}
	
	public void put(String id, String i) {
		id2string.put(id,  i);
	}
	
	public String getString(String id) {
		return id2string.get(id);
	}
	
	public Iterable<Map.Entry<String, String>> getId2string() {
		return id2string.entrySet();
	}
	
}
