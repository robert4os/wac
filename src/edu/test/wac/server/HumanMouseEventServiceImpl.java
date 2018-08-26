package edu.test.wac.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.test.wac.client.HumanMouseEvent;
import edu.test.wac.client.HumanMouseEventResponse;
import edu.test.wac.client.HumanMouseEventService;

public class HumanMouseEventServiceImpl extends RemoteServiceServlet implements HumanMouseEventService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7914307632259605621L;

	@Override
	public HumanMouseEventResponse execHumanMouseEvent(HumanMouseEvent input) throws IllegalArgumentException {
		
		if(input.getType()==0) {
			
			return new HumanAgent().moveMouse(input.getX(),  input.getY(),  input.getClick());
		} else 
		if(input.getType()==1){
			return new HumanAgent().inputKeys(input.getTxt(), input.isEnter());	
		} 
		else
			if(input.getType()==2){
				return new HumanAgent().getMouse();	
			} 
		
		return new HumanMouseEventResponse(-2, "Unknown cmd: "+input.getType());
	}

	
}
