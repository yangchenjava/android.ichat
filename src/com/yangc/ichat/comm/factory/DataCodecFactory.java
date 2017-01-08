package com.yangc.ichat.comm.factory;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.yangc.ichat.comm.codec.ProtobufDecoderData;
import com.yangc.ichat.comm.codec.ProtobufEncoderData;

public class DataCodecFactory implements ProtocolCodecFactory {

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return new ProtobufEncoderData();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return new ProtobufDecoderData();
	}

}
