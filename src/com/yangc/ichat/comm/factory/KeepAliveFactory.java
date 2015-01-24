package com.yangc.ichat.comm.factory;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

import com.yangc.ichat.comm.protocol.ProtobufMessage;

public class KeepAliveFactory implements KeepAliveMessageFactory {

	@Override
	public boolean isRequest(IoSession session, Object message) {
		return false;
	}

	@Override
	public boolean isResponse(IoSession session, Object message) {
		return message instanceof Byte;
	}

	@Override
	public Object getRequest(IoSession session) {
		return ProtobufMessage.Heart.newBuilder().build();
	}

	@Override
	public Object getResponse(IoSession session, Object request) {
		return null;
	}

}
