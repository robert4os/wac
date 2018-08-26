package edu.test.wac.client;

import java.util.Date;
import java.util.HashSet;

public class State {
	public String xps_contactWithNewIndicator;
	public XPath xp_rel_newMessageIndicator;
	public XPath xp_rel_name;
	//XPath xp_rel_img;
	//
	public String chatAreaXpath;
	
	public XPath xp_rel_chatFromOtherText;
	public XPath xp_rel_chatFromOtherTime;
	public String xps_chat;
	//
	public String outClass;
	public String inClass;
	
	public String textInputXpath;	
	//
	public int vpOriginX;
	public int vpOriginY;
	//
	public String botSecret;
	public Date start;
	public Date end;
	public HashSet<String> ignoredUsers=new HashSet<String>();
}
