package edu.test.wac.client;

import java.util.ArrayList;
import java.util.List;

public class MsgStack {
 
	private boolean keepText;
	
	public MsgStack(boolean b) {
		keepText=b;
	}
	
	
	static public class Msg {
		protected boolean in;
		protected String txt;
		protected String time;
		
		protected String hc;

		public Msg(boolean in, String txt, String time) {
			super();
			this.in = in;
			this.txt = txt;
			this.time = time;
			//
			hc=(in ? "I" : "O") + "_" +txt.hashCode()+"_"+time;			
		}
		
		public boolean equals(Msg m) {
			return hc.equals(m.hc);
		}
		
		public Msg clone() {
			return new Msg(in, txt, time);
		}
		
		public String toString() {
			return (in ? "I" : "O") +": "+(txt!=null ? txt : hc)+" ["+time+"]";
		}
	}
	
	protected List<Msg> txths=new ArrayList<Msg>(); 
	
	final static int max=5; // we never know how many mssgs will be given by the whatsapp web cliebt 5 is good minimum
	
	long lastTimeMsgWasAdded=System.currentTimeMillis()-24*60*60*1000;
	
	//
	public void add(Msg msg) {
		txths.add(msg);
		if(!keepText) msg.txt=null;
		if(max>0 && txths.size()>max) txths.remove(0);	
		//
		lastTimeMsgWasAdded=System.currentTimeMillis();
	}
	
	public void removeLast() {
		txths.remove(txths.size()-1);
	}
	
	
	
	public void add(MsgStack msg) {
		for(int i=0; i<msg.size(); i++) {
			add(msg);
		}
	}
	
	public int getTimeInSecSinceLastMessage() {
		return (int)(System.currentTimeMillis()-lastTimeMsgWasAdded)/1000;
	}
	
	static public Msg createMsg(boolean in, String txt, String time) {
		return new Msg(in,  txt, time);
	}
	
	public void add(boolean in, String txt, String time) {
		Msg msg=new Msg(in,  txt, time);
		//
		add(msg);
	}
	
	public int size() {
		return txths.size();
	}
	
	public Msg getMsg(int index) {
		return txths.get(index);
	}
	
	public MsgStack getNew(MsgStack newMsg, int minMatchI, int maxMatchI) {
	
		MsgStack diffMsg=new MsgStack(true);
		
		//
		MsgStack oldMsg=this;
		
		//
		int maxCntMatchIn=-1;
			
		    //
			for(int o=oldMsg.size()-1; o>=0; o--) {
			
				int cntMatchIn=0;
				
				for(int n=0; n<newMsg.size(); n++) {
					
					int oi=o+n;
					int ni=n;
					if(oi>=oldMsg.size()) break;
					//if(ni>=newMsg.size()) break;
					
					//
					Msg om=oldMsg.getMsg(oi);
					Msg nm=newMsg.getMsg(ni);
					
					//System.out.println(""+om+"["+oi+"] =? "+nm+" ["+ni+"]");
					//
					if(!om.equals(nm)) break;
					//
					cntMatchIn++;
				}
				
				// get the earliest max match, thus <
				if(cntMatchIn>maxCntMatchIn) {
					maxCntMatchIn=cntMatchIn;
					//
					if(maxMatchI>=0 && maxCntMatchIn>=maxMatchI) break;
				}
			}	
				
		if(minMatchI>0 && maxCntMatchIn<minMatchI) throw new RuntimeException("Insufficient match of incoming messges: "+maxCntMatchIn+" < "+minMatchI);
		
		// if there was no match -> return all
		if(maxCntMatchIn<0) maxCntMatchIn=0;
		//
		for(int i=maxCntMatchIn; i<newMsg.size(); i++) {
			diffMsg.add(newMsg.getMsg(i).clone());
		}
		
		//
		
		return diffMsg;
	}
	
	public MsgStack clone() {
		MsgStack ms=new MsgStack(keepText);
		for(int i=0; i<size(); i++) {
			ms.add(getMsg(i));
		}
		
		return ms;
	}
	
	public boolean equals(Object o) {
		MsgStack ms=(MsgStack) o;
		if(ms.txths.size()!=txths.size()) return false;
		//
		for(int i=0; i<txths.size(); i++) {
			Msg a=txths.get(i);
			Msg b=ms.txths.get(i);
			if(!a.equals(b)) return false;
		}
		
		return true;
	}
	
	public String toString() {
	 StringBuilder s=new StringBuilder();
	 for(int i=0; i<size(); i++) {
		 if(s.length()>0) s.append("\r\n");
		 s.append(getMsg(i));
	 }
	 
	 return s.toString();
	}
}
