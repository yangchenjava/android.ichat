package com.yangc.ichat.comm;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.yangc.ichat.comm.bean.ResultBean;
import com.yangc.ichat.comm.bean.TextBean;
import com.yangc.ichat.comm.bean.UserBean;
import com.yangc.ichat.comm.factory.DataCodecFactory;
import com.yangc.ichat.comm.handler.ClientHandler;
import com.yangc.ichat.comm.protocol.ProtobufMessage;
import com.yangc.ichat.utils.Constants;

public class Client {

	private static NioSocketConnector connector;
	private static IoSession session;
	private static ScheduledExecutorService scheduledExecutorService;

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
		connector.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, Constants.TIMEOUT);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new DataCodecFactory()));
		connector.setHandler(new ClientHandler());
		session = connector.connect(new InetSocketAddress(Constants.IP, Constants.PORT)).awaitUninterruptibly().getSession();

		// 心跳
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				if (session != null && session.isConnected()) {
					session.write(ProtobufMessage.Heart.newBuilder().build());
				}
			}
		}, 5, 60, TimeUnit.SECONDS);
	}

	private void destroy() {
		if (scheduledExecutorService != null) {
			scheduledExecutorService.shutdownNow();
		}
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

	public void login(UserBean user) {
		if (session != null && session.isConnected()) {
			ProtobufMessage.Login.Builder builder = ProtobufMessage.Login.newBuilder();
			builder.setUuid(user.getUuid());
			builder.setUsername(user.getUsername());
			builder.setPassword(user.getPassword());
			session.write(builder.build());
		}
	}

	public void sendChat(TextBean text) {
		if (session != null && session.isConnected()) {
			ProtobufMessage.Chat.Builder builder = ProtobufMessage.Chat.newBuilder();
			builder.setUuid(text.getUuid());
			builder.setFrom(text.getFrom());
			builder.setTo(text.getTo());
			builder.setData(text.getData());
			session.write(builder.build());
		}
	}

	public void sendResult(ResultBean result) {
		if (session != null && session.isConnected()) {
			ProtobufMessage.Result.Builder builder = ProtobufMessage.Result.newBuilder();
			builder.setUuid(result.getUuid());
			builder.setSuccess(result.isSuccess());
			builder.setData(result.getData());
			session.write(builder.build());
		}
	}

}
