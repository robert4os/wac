package edu.test.wac.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.test.wac.client.ObjectDictionary;
import edu.test.wac.client.ServerStorageEvent;
import edu.test.wac.client.ServerStorageEventResponse;
import edu.test.wac.client.ServerStorageEventService;

public class ServerStorageServiceImpl extends RemoteServiceServlet implements ServerStorageEventService {

	private static final long serialVersionUID = 5344121975017168597L;

	static String dbfile="c:\\wac\\wac-derby";
	
	private static boolean createDB=true;
	
	static private Map<String, String> id2str=new HashMap<String, String>();
    static {
    	id2str.put(ObjectDictionary.INT_VP_ORIGIN_X, "0");
		id2str.put(ObjectDictionary.INT_VP_ORIGIN_Y, "96");
		id2str.put(ObjectDictionary.INT_LOCAL_SERVER, "1");
		id2str.put(ObjectDictionary.E_CHAT_AREA, "/HTML[(contains(@class, 'js') and contains(@class, 'serviceworker') and contains(@class, 'adownload') and contains(@class, 'cssanimations') and contains(@class, 'csstransitions') and contains(@class, 'no-webp') and contains(@class, 'wf-opensans-n4-inactive') and contains(@class, 'wf-opensans-n6-inactive') and contains(@class, 'wf-roboto-n3-inactive') and contains(@class, 'wf-roboto-n4-inactive') and contains(@class, 'wf-roboto-n5-inactive') and contains(@class, 'wf-inactive'))]/BODY[(contains(@class, 'web'))]/DIV[ @id='app']/DIV[(contains(@class, '_1FKgS') and contains(@class, 'app-wrapper-web') and contains(@class, 'bFqKf'))]/DIV[(contains(@class, 'app') and contains(@class, '_3dqpi') and contains(@class, 'two'))]/DIV[(contains(@class, '_3q4NP') and contains(@class, '_1Iexl'))]/DIV[ @id='main' and (contains(@class, '_1GX8_'))]/DIV[(contains(@class, '_3zJZ2'))]/DIV[(contains(@class, 'copyable-area'))]/DIV[(contains(@class, '_2nmDZ'))]");
		
		id2str.put(ObjectDictionary.E_CONTACT_NAME_WITH_INDICATOR, "/HTML/BODY/DIV[ @id='app']/DIV[@class='app-wrapper app-wrapper-web app-wrapper-main' ]/DIV[@class='app _3dqpi two' ]/DIV[@class='_3q4NP k1feT' ]/DIV[ @id='side' and @class='swl8g' ]/DIV[ @id='pane-side' and @class='_1NrpZ' ]/DIV/DIV/DIV[@class='RLfQR' ]/DIV[@class='_2wP_Y' ]/DIV/DIV[@class='_2EXPL CxUIE' ]/DIV[@class='_3j7s9' ]/DIV[@class='_2FBdJ' ]/DIV[@class='_25Ooe' ]/SPAN[@class='_1wjpf' ]");
		id2str.put(ObjectDictionary.E_CONTACT_NAME_WITHOUT_INDICATOR, "/HTML/BODY/DIV[ @id='app']/DIV[@class='app-wrapper app-wrapper-web app-wrapper-main' ]/DIV[@class='app _3dqpi two' ]/DIV[@class='_3q4NP k1feT' ]/DIV[ @id='side' and @class='swl8g' ]/DIV[ @id='pane-side' and @class='_1NrpZ' ]/DIV/DIV/DIV[@class='RLfQR' ]/DIV[@class='_2wP_Y' ]/DIV/DIV[@class='_2EXPL' ]/DIV[@class='_3j7s9' ]/DIV[@class='_2FBdJ' ]/DIV[@class='_25Ooe' ]/SPAN[@class='_1wjpf' ]");
		id2str.put(ObjectDictionary.E_INDICATOR, "/HTML/BODY/DIV[ @id='app']/DIV[@class='app-wrapper app-wrapper-web app-wrapper-main' ]/DIV[@class='app _3dqpi two' ]/DIV[@class='_3q4NP k1feT' ]/DIV[ @id='side' and @class='swl8g' ]/DIV[ @id='pane-side' and @class='_1NrpZ' ]/DIV/DIV/DIV[@class='RLfQR' ]/DIV[@class='_2wP_Y' ]/DIV/DIV[@class='_2EXPL CxUIE' ]/DIV[@class='_3j7s9' ]/DIV[@class='_1AwDx' ]/DIV[@class='_3Bxar' ]/SPAN/DIV[@class='_15G96' ]/SPAN[@class='OUeyt _3zmhL' ]");
		id2str.put(ObjectDictionary.E_TEXT_INPUT, "/HTML[(contains(@class, 'js') and contains(@class, 'serviceworker') and contains(@class, 'adownload') and contains(@class, 'cssanimations') and contains(@class, 'csstransitions') and contains(@class, 'no-webp') and contains(@class, 'wf-roboto-n4-active') and contains(@class, 'wf-opensans-n4-active') and contains(@class, 'wf-opensans-n6-active') and contains(@class, 'wf-roboto-n3-active') and contains(@class, 'wf-roboto-n5-inactive') and contains(@class, 'wf-active'))]/BODY[(contains(@class, 'web'))]/DIV[ @id='app']/DIV[(contains(@class, '_1FKgS') and contains(@class, 'app-wrapper-web') and contains(@class, 'bFqKf'))]/DIV[(contains(@class, 'app') and contains(@class, '_3dqpi') and contains(@class, 'two'))]/DIV[(contains(@class, '_3q4NP') and contains(@class, '_1Iexl'))]/DIV[ @id='main' and (contains(@class, '_1GX8_'))]/FOOTER[(contains(@class, '_2jVLL'))]/DIV[(contains(@class, '_3oju3'))]/DIV[(contains(@class, '_2bXVy'))]/DIV[(contains(@class, '_3F6QL') and contains(@class, '_2WovP'))]/DIV[(contains(@class, '_2S1VP') and contains(@class, 'copyable-text') and contains(@class, 'selectable-text'))]");
		
		id2str.put(ObjectDictionary.E_CHAT_MSG_FROM_OTHER, "/HTML[(contains(@class, 'js') and contains(@class, 'serviceworker') and contains(@class, 'adownload') and contains(@class, 'cssanimations') and contains(@class, 'csstransitions') and contains(@class, 'no-webp') and contains(@class, 'wf-roboto-n4-active') and contains(@class, 'wf-roboto-n5-active') and contains(@class, 'wf-opensans-n4-active') and contains(@class, 'wf-roboto-n3-active') and contains(@class, 'wf-opensans-n6-active') and contains(@class, 'wf-active'))]/BODY[(contains(@class, 'web'))]/DIV[ @id='app']/DIV[(contains(@class, '_1FKgS') and contains(@class, 'app-wrapper-web') and contains(@class, 'bFqKf'))]/DIV[(contains(@class, 'app') and contains(@class, '_3dqpi') and contains(@class, 'two'))]/DIV[(contains(@class, '_3q4NP') and contains(@class, '_1Iexl'))]/DIV[ @id='main' and (contains(@class, '_1GX8_'))]/DIV[(contains(@class, '_3zJZ2'))]/DIV[(contains(@class, 'copyable-area'))]/DIV[(contains(@class, '_2nmDZ'))]/DIV[(contains(@class, '_9tCEa'))]/DIV[(contains(@class, 'vW7d1'))]/DIV[(contains(@class, '_3_7SH') and contains(@class, '_3DFk6') and contains(@class, 'message-in'))]/DIV[(contains(@class, 'Tkt2p'))]/DIV[(contains(@class, 'copyable-text'))]/DIV[(contains(@class, '_3zb-j') and contains(@class, 'ZhF0n'))]/SPAN[(contains(@class, 'selectable-text') and contains(@class, 'invisible-space') and contains(@class, 'copyable-text'))]");
		id2str.put(ObjectDictionary.E_CHAT_MSG_FROM_YOU,   "/HTML/BODY/DIV[ @id='app']/DIV[@class='app-wrapper app-wrapper-web app-wrapper-main' ]/DIV[@class='app _3dqpi two' ]/DIV[@class='_3q4NP _1Iexl' ]/DIV[ @id='main' and @class='_1GX8_' ]/DIV[@class='_3zJZ2' ]/DIV[@class='copyable-area' ]/DIV[@class='_2nmDZ' ]/DIV[@class='_9tCEa' ]/DIV[@class='msg msg-continuation' ]/DIV[@class='message message-chat message-out message-chat' ]/DIV/DIV[@class='Tkt2p' ]/DIV[@class='copyable-text' ]/DIV[@class='_3zb-j ZhF0n' ]/SPAN[@class='selectable-text invisible-space copyable-text' ]");
		id2str.put(ObjectDictionary.CLASS_MSG_OUT, "message-out");
		id2str.put(ObjectDictionary.CLASS_MSG_IN, "message-in");
		//id2str.put(ObjectDictionary.XXX, null);
		
		id2str.put(ObjectDictionary.E_CHAT_MSG_FROM_OTHER_TIME, " /HTML/BODY/DIV[ @id='app']/DIV[@class='app-wrapper app-wrapper-web app-wrapper-main' ]/DIV[@class='app _3dqpi two' ]/DIV[@class='_3q4NP _1Iexl' ]/DIV[ @id='main' and @class='_1GX8_' ]/DIV[@class='_3zJZ2' ]/DIV[@class='copyable-area' ]/DIV[@class='_2nmDZ' ]/DIV[@class='_9tCEa' ]/DIV[@class='msg' ]/DIV[@class='message message-chat message-in tail message-chat' ]/DIV/DIV[@class='Tkt2p' ]/DIV[@class='_2f-RV' ]/DIV[@class='_1DZAH' ]/SPAN[@class='_3EFt_' ]");
		//id2str.put(ObjectDictionary.E_CHAT_DATE_MSG, "/HTML/BODY/DIV[ @id='app']/DIV[@class='app-wrapper app-wrapper-web app-wrapper-main' ]/DIV[@class='app _3dqpi two' ]/DIV[@class='_3q4NP _1Iexl' ]/DIV[ @id='main' and @class='_1GX8_' ]/DIV[@class='_3zJZ2' ]/DIV[@class='copyable-area' ]/DIV[@class='_2nmDZ' ]/DIV[@class='_9tCEa' ]/DIV[@class='msg msg-system' ]/DIV[@class='message message-system' ]/SPAN[@class='message-system-body' ]/SPAN");
									
		
		id2str.put(ObjectDictionary.E_CONTACT_IMAGE, "/HTML/BODY/DIV[ @id='app']/DIV[@class='app-wrapper app-wrapper-web app-wrapper-main' ]/DIV[@class='app _3dqpi two' ]/DIV[@class='_3q4NP k1feT' ]/DIV[ @id='side' and @class='swl8g' ]/DIV[ @id='pane-side' and @class='_1NrpZ' ]/DIV/DIV/DIV[@class='RLfQR' ]/DIV[@class='_2wP_Y' ]/DIV/DIV[@class='_2EXPL CxUIE' ]/DIV[@class='dIyEr' ]/DIV[@class='_1WliW' ]/IMG[@class='Qgzj8 gqwaM' ]");
		id2str.put(ObjectDictionary.E_CURRENT_CHAT_USER_IMAGE, "/HTML/BODY/DIV[ @id='app']/DIV[@class='app-wrapper app-wrapper-web app-wrapper-main' ]/DIV[@class='app _3dqpi two' ]/DIV[@class='_3q4NP k1feT' ]/DIV[ @id='side' and @class='swl8g' ]/DIV[ @id='pane-side' and @class='_1NrpZ' ]/DIV/DIV/DIV[@class='RLfQR' ]/DIV[@class='_2wP_Y' ]/DIV/DIV[@class='_2EXPL _1f1zm' ]/DIV[@class='dIyEr' ]/DIV[@class='_1WliW' ]/IMG[@class='Qgzj8 gqwaM' ]");
				
	
		id2str.put(ObjectDictionary.PARAM_USER_ID, "i");
		
		id2str.put(ObjectDictionary.STR_BOT__SECRET, "");
		id2str.put(ObjectDictionary.STR_TIME__START, "7:00");
		id2str.put(ObjectDictionary.STR_TIME__END, "19:00");
    }
	
	private Connection getConnection(String ws) {
		
		System.setProperty("derby.storage.pageCacheSize", "40");
		System.setProperty("derby.storage.pageSize", "4096");
		
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		
		String url="jdbc:derby:"+dbfile+(ws!=null ? "_"+ws : "")+";create=true";
		
		Connection con;
		try {
			con = DriverManager.getConnection(url, "sa", "");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		
		if(createDB) {
			createDB=false;
			
			//
			PreparedStatement st=null;
			try {
				st=con.prepareStatement("CREATE TABLE dict (k varchar(255) not null, v varchar(32672), primary key(k))");
				st.executeUpdate();
				
			
			} catch (SQLException e) {
				
			} finally {
				if(st!=null)
					try {
						st.close();
					} catch (SQLException e) {
					}
			}
		}
		
		return con;
	}
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
			
				shutdownDb();
			}
			
		});
	}
	
	static private void shutdownDb() {
		String url="jdbc:derby:"+dbfile;
		url+=";shutdown=true";
		
		try {
			DriverManager.getConnection(url, "sa", "");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	private void setKey(Connection con, String k, String v) throws SQLException {
	
		try {
		PreparedStatement st=con.prepareStatement("INSERT INTO dict VALUES (?, ?)");
		st.setString(1, k);
		st.setString(2, v);
		st.executeUpdate();	
		//
		st.close(); 
		}catch (DerbySQLIntegrityConstraintViolationException e) {
			
			System.out.println("UPATE: "+k+": "+v);
			PreparedStatement st=con.prepareStatement("UPDATE dict set v=? where k=?");
			
			st.setString(1, v);
			st.setString(2, k);
			st.executeUpdate();	
			//
			st.close(); 
			
		}
		
		
	}

	private String getKey(Connection con, String k) throws SQLException {

		PreparedStatement st=con.prepareStatement("SELECT v from dict where k=?");
		
		st.setString(1, k);
		ResultSet rs=st.executeQuery();
		String v=null;
		if(rs.next()) {
			v=rs.getString(1);
		}
		rs.close();
		st.close();
		
		if(v==null) v=id2str.get(k);
		
		return v;
	}

	
	@Override
	public ServerStorageEventResponse execServerStorageEvent(ServerStorageEvent input) throws IllegalArgumentException {
		
		ServerStorageEventResponse r=new ServerStorageEventResponse(input.getCmd());
		Connection con=getConnection(input.getWs());

		
		
		try {
		
			if(input.getCmd()==ServerStorageEvent.CMD_DICT_GET) {
				
				r.setV(getKey(con, input.getK()));
				return r;
			} else 
				if(input.getCmd()==ServerStorageEvent.CMD_DICT_SET) {
					
					setKey(con,input.getK(), input.getV());
					return r;
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ServerStorageEventResponse();
	}

	
}
