package edu.test.wac;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import edu.test.wac.client.MsgStack;

public class TestMsgStack {

	@Test
	public void testOverlap() {
		MsgStack msgLast=new MsgStack(true);
		msgLast.add(true, "Hallo", "12:00");
		msgLast.add(true, "Wie geht's?", "12:05");
		msgLast.add(false, "Hi!", "12:07");
		msgLast.add(false, "Geht gut.", "12:10");

		MsgStack msgNew=msgLast.clone();
		msgNew.add(true, "Der Grund...", "12:12");
		msgNew.add(true, "warum ich dich anschreibe...", "12:13");
		
		
		MsgStack target=new MsgStack(true);
		target.add(true, "Der Grund...", "12:12");
		target.add(true, "warum ich dich anschreibe...", "12:13");
		
		//
		MsgStack diff=msgLast.getNew(msgNew, 2, -1);
		assertEquals(target, diff);
	}

	@Test
	public void testOneMore() {
		MsgStack msgLast=new MsgStack(true);
		msgLast.add(true, "Hallo", "12:00");
		msgLast.add(true, "Wie geht's?", "12:05");
		msgLast.add(false, "Hi!", "12:07");
		msgLast.add(false, "Geht gut.", "12:10");

		MsgStack msgNew=new MsgStack(true);
		msgNew.add(true, "Hallo", "12:00");
		msgNew.add(true, "Wie geht's?", "12:05");
		msgNew.add(false, "Hi!", "12:07");
		msgNew.add(false, "Geht gut.", "12:10");
		msgNew.add(true, "warum ich dich anschreibe...", "12:13");
		
		
		MsgStack target=new MsgStack(true);
		target.add(true, "warum ich dich anschreibe...", "12:13");
		
		//
		MsgStack diff=msgLast.getNew(msgNew, -1, 5);
		assertEquals(target, diff);
	}

	
	@Test
	public void testRepeatedOverlap() {
		MsgStack msgLast=new MsgStack(true);
		msgLast.add(true, "Hallo", "12:00");
		msgLast.add(true, "Wie geht's?", "12:05");
		msgLast.add(false, "Hi!", "12:07");
		msgLast.add(false, "Geht gut.", "12:10");
		msgLast.add(true, "Hallo", "12:00");
		msgLast.add(true, "Wie geht's?", "12:05");
		msgLast.add(false, "Hi!", "12:07");
		msgLast.add(false, "Geht gut.", "12:10");

		MsgStack msgNew=new MsgStack(true);
		msgNew.add(true, "Hallo", "12:00");
		msgNew.add(true, "Wie geht's?", "12:05");
		msgNew.add(false, "Hi!", "12:07");
		msgNew.add(false, "Geht gut.", "12:10");

		//
		msgNew.add(true, "Der Grund...", "12:12");
		msgNew.add(true, "warum ich dich anschreibe...", "12:13");
		
		
		MsgStack target=new MsgStack(true);
		target.add(true, "Der Grund...", "12:12");
		target.add(true, "warum ich dich anschreibe...", "12:13");
		
		//
		MsgStack diff=msgLast.getNew(msgNew, 2, -1);
		assertEquals(target, diff);
	}

	@Test
	public void testEarlierOverlap() {
		MsgStack msgLast=new MsgStack(true);
		msgLast.add(true, "Hallo", "12:00");
		msgLast.add(true, "Wie geht's?", "12:05");
		msgLast.add(true, "NA???", "12:05");
		msgLast.add(true, "Wie geht's?", "12:05");
		
		MsgStack msgNew=new MsgStack(true);
		msgNew.add(true, "Wie geht's?", "12:05");
		msgNew.add(false, "Hi!", "12:07");
		
		MsgStack target=new MsgStack(true);
		target.add(false, "Hi!", "12:07");
		
		//
		MsgStack diff=msgLast.getNew(msgNew, 1, -1);
		assertEquals(target, diff);
	}

	@Test
	public void testTooLittleOverlap() {
		MsgStack msgLast=new MsgStack(true);
		msgLast.add(true, "Hallo", "12:00");
		msgLast.add(true, "Wie geht's?", "12:05");

		MsgStack msgNew=new MsgStack(true);
		msgNew.add(true, "Wie geht's?", "12:05");
		msgNew.add(false, "Hi!", "12:07");
		
		//
		assertThrows(RuntimeException.class, new Executable() {
			
			@Override
			public void execute() throws Throwable {
				msgLast.getNew(msgNew, 2, -1);		
			}
		});
	}
	
	
}
