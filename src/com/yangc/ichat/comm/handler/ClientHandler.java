package com.yangc.ichat.comm.handler;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class ClientHandler extends IoHandlerAdapter {

	@Override
	public void sessionClosed(IoSession session) throws Exception {

	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		if (status.equals(IdleStatus.BOTH_IDLE)) {
			session.close(true);
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {

	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {

	}

}
