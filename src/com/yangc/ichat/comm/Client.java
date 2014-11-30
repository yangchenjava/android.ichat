package com.yangc.ichat.comm;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import android.content.Context;
import android.content.Intent;

import com.yangc.ichat.comm.bean.ChatBean;
import com.yangc.ichat.comm.bean.ResultBean;
import com.yangc.ichat.comm.bean.UserBean;
import com.yangc.ichat.comm.factory.DataCodecFactory;
import com.yangc.ichat.comm.handler.ClientHandler;
import com.yangc.ichat.comm.protocol.ProtobufMessage;
import com.yangc.ichat.service.PushService;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;

public class Client {

	private static Client client;

	private Context context;
	private ExecutorService executorService;

	private NioSocketConnector connector;
	private IoSession session;
	private ScheduledExecutorService scheduledExecutorService;

	private Client() {
	}

	private Client(Context context) {
		this.context = context;
		this.executorService = Executors.newFixedThreadPool(3);
		this.init();
	}

	public synchronized static Client getInstance(Context context) {
		if (client == null) {
			client = new Client(context);
		}
		return client;
	}

	private void init() {
		this.connector = new NioSocketConnector();
		this.connector.setConnectTimeoutMillis(30000);
		this.connector.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, Constants.TIMEOUT);
		this.connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new DataCodecFactory()));
		this.connector.setHandler(new ClientHandler(this.context));
	}

	public synchronized void connect() {
		try {
			if (!AndroidUtils.checkNetwork(this.context)) {
				throw new RuntimeException();
			}
			Future<?> future = this.executorService.submit(new Runnable() {
				@Override
				public void run() {
					session = connector.connect(new InetSocketAddress(Constants.IP, Constants.PORT)).awaitUninterruptibly().getSession();
				}
			});
			future.get();

			// 心跳
			this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
			this.scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					if (session != null && session.isConnected()) {
						session.write(ProtobufMessage.Heart.newBuilder().build());
					}
				}
			}, 5, 30, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
			Intent intent = new Intent(this.context, PushService.class);
			intent.putExtra(Constants.EXTRA_ACTION, Constants.ACTION_NETWORK_ERROR);
			this.context.startService(intent);
		}
	}

	public void destroy() {
		if (this.scheduledExecutorService != null) {
			this.scheduledExecutorService.shutdownNow();
		}
		if (this.session != null) {
			this.session.close(true).awaitUninterruptibly();
			this.session = null;
		}
		if (this.connector != null) {
			this.connector.dispose();
			this.connector = null;
		}
	}

	public void reconnect() {
		this.destroy();
		this.init();
		this.connect();
	}

	public void login(final UserBean user) {
		this.executorService.execute(new Runnable() {
			@Override
			public void run() {
				if (session != null && session.isConnected()) {
					ProtobufMessage.Login.Builder builder = ProtobufMessage.Login.newBuilder();
					builder.setUuid(user.getUuid());
					builder.setUsername(user.getUsername());
					builder.setPassword(user.getPassword());
					session.write(builder.build());
				}
			}
		});
	}

	public void sendChat(final ChatBean chat) {
		this.executorService.execute(new Runnable() {
			@Override
			public void run() {
				if (session != null && session.isConnected()) {
					ProtobufMessage.Chat.Builder builder = ProtobufMessage.Chat.newBuilder();
					builder.setUuid(chat.getUuid());
					builder.setFrom(chat.getFrom());
					builder.setTo(chat.getTo());
					builder.setData(chat.getData());
					session.write(builder.build());
				}
			}
		});
	}

	public void sendResult(final ResultBean result) {
		this.executorService.execute(new Runnable() {
			@Override
			public void run() {
				if (session != null && session.isConnected()) {
					ProtobufMessage.Result.Builder builder = ProtobufMessage.Result.newBuilder();
					builder.setUuid(result.getUuid());
					builder.setSuccess(result.isSuccess());
					builder.setData(result.getData());
					session.write(builder.build());
				}
			}
		});
	}

}