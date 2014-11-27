package com.yangc.ichat.comm;

import java.net.InetSocketAddress;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.yangc.ichat.comm.factory.DataCodecFactory;
import com.yangc.ichat.comm.handler.ClientHandler;
import com.yangc.ichat.utils.Constants;

public class Client {

	private static NioSocketConnector connector;
	private static IoSession session;

	private static Client client;

	private Client() {
		this.init();
	}

	public synchronized static Client getInstance() {
		if (client == null) {
			client = new Client();
		}
		return client;
	}

	private void init() {
		connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(30000);
		connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, Constants.TIMEOUT);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new DataCodecFactory()));
		connector.setHandler(new ClientHandler());
		session = connector.connect(new InetSocketAddress(Constants.IP, Constants.PORT)).awaitUninterruptibly().getSession();
	}

	private void destroy() {
		if (session != null) {
			session.close(true).awaitUninterruptibly();
			session = null;
		}
		if (connector != null) {
			connector.dispose();
			connector = null;
		}
	}

	public void reconnect() {
		this.destroy();
		this.init();
	}

}
