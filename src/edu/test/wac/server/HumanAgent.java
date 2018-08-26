package edu.test.wac.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import edu.test.wac.client.HumanMouseEventResponse;

public class HumanAgent {

	String cmd="c:\\wac\\bin\\HumanMouse.exe";
	
	public HumanMouseEventResponse inputKeys(String txt, boolean enter) {
		StringBuilder out=new StringBuilder();
		StringBuilder err=new StringBuilder();
		
		int ec=execute(cmd+" 1 \""+txt+"\" "+(enter ? 1:0), out, err);
		return new HumanMouseEventResponse(ec, err.toString());	
	}

	private int execute(String cmd, StringBuilder out, StringBuilder err) {
		try {
		      String line;
		      Process p = Runtime.getRuntime().exec
		        (cmd);
		      
		      BufferedReader input =
		        new BufferedReader
		          (new InputStreamReader(p.getInputStream()));
		      while ((line = input.readLine()) != null) {
		        out.append(line);
		      }
		      input.close();
		      
		      BufferedReader einput =
				        new BufferedReader
				          (new InputStreamReader(p.getErrorStream()));
				      while ((line = einput.readLine()) != null) {
				        err.append(line);
				      }
				      einput.close();
				    
				      if(out.length()>0) {
				    	  System.out.println("HumanMouse output: "+out);
				      
				      }
				      
				      if(err.length()>0) {
				    	  System.out.println("HumanMouse error: "+err);
				      }
				            
		      //
		      return p.waitFor();
		     
		    }
		    catch (Exception ex) {
		      err.append(ex.getMessage());
		      return -3;
		    }
	}
	
	public HumanMouseEventResponse getMouse() {
		StringBuilder out=new StringBuilder();
		StringBuilder err=new StringBuilder();
		
		int ec=execute(cmd+" 2", out, err);
		 if(ec==0) {
		       String cs[]=out.toString().trim().split(" ");
		       int x=Integer.parseInt(cs[0]);
		       int y=Integer.parseInt(cs[1]);
		       return new HumanMouseEventResponse(ec, x, y);
		      }
		
		return new HumanMouseEventResponse(ec, err.toString());
	}
	
	public HumanMouseEventResponse moveMouse(int screenX, int screenY, boolean click) {
		
		StringBuilder out=new StringBuilder();
		StringBuilder err=new StringBuilder();
		
		int ec=execute(cmd+" 0 "+screenX+" "+screenY+" "+(click ? 1:0), out, err);
		return new HumanMouseEventResponse(ec, err.toString());
	}

}
