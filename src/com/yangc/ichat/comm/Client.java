package com.yangc.ichat.comm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.yangc.ichat.comm.bean.ChatBean;
import com.yangc.ichat.comm.bean.FileBean;
import com.yangc.ichat.comm.bean.ResultBean;
import com.yangc.ichat.comm.bean.UserBean;
import com.yangc.ichat.comm.factory.DataCodecFactory;
import com.yangc.ichat.comm.factory.KeepAliveFactory;
import com.yangc.ichat.comm.handler.ClientHandler;
import com.yangc.ichat.comm.protocol.ProtobufMessage;
import com.yangc.ichat.service.PushService;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.Md5Utils;

public class Client {

	private static final String TAG = Client.class.getName();

	private static Client client;

	private Context context;
	private ExecutorService executorService;

	private NioSocketConnector connector;
	private IoSession session;

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

	/**
	 * @功能: 初始化连接
	 * @作者: yangc
	 * @创建日期: 2014年12月7日 下午6:19:01
	 */
	private void init() {
		Log.i(TAG, "init");
		this.connector = new NioSocketConnector();
		this.connector.setConnectTimeoutMillis(8000);
		this.connector.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, Constants.TIMEOUT);
		this.connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new DataCodecFactory()));
		KeepAliveFilter keepAliveFilter = new KeepAliveFilter(new KeepAliveFactory(), IdleStatus.WRITER_IDLE, KeepAliveRequestTimeoutHandler.NOOP, Constants.INTERVAL, Constants.TIMEOUT);
		keepAliveFilter.setForwardEvent(true);
		this.connector.getFilterChain().addLast("heartBeat", keepAliveFilter);
		this.connector.setHandler(new ClientHandler(this.context));
	}

	/**
	 * @功能: 连接服务器
	 * @作者: yangc
	 * @创建日期: 2014年12月7日 下午6:24:06
	 */
	public synchronized void connect() {
		Log.i(TAG, "connect");
		try {
			if (!AndroidUtils.checkNetwork(this.context)) {
				throw new RuntimeException("No networks are available");
			}
			Future<?> future = this.executorService.submit(new Runnable() {
				@Override
				public void run() {
					ConnectFuture connectFuture = connector.connect(new InetSocketAddress(Constants.IP, Constants.PORT)).awaitUninterruptibly();
					if (connectFuture.isConnected()) {
						session = connectFuture.getSession();
					} else {
						throw new RuntimeException("The server does not startup");
					}
				}
			});
			future.get();
		} catch (Exception e) {
			e.printStackTrace();
			Intent intent = new Intent(this.context, PushService.class);
			intent.putExtra(Constants.EXTRA_ACTION, Constants.ACTION_NETWORK_ERROR);
			this.context.startService(intent);
		}
	}

	/**
	 * @功能: 关闭会话,连接
	 * @作者: yangc
	 * @创建日期: 2014年12月7日 下午6:25:12
	 */
	public void destroy() {
		Log.i(TAG, "destroy");
		if (this.session != null) {
			this.session.close(true).awaitUninterruptibly();
			this.session = null;
		}
		// if (this.connector != null) {
		// this.connector.dispose();
		// this.connector = null;
		// }
	}

	/**
	 * @功能: 重新连接服务器
	 * @作者: yangc
	 * @创建日期: 2014年12月7日 下午6:26:36
	 */
	public void reconnect() {
		Log.i(TAG, "reconnect");
		this.destroy();
		// this.init();
		this.connect();
	}

	/**
	 * @功能: TCP登录
	 * @作者: yangc
	 * @创建日期: 2014年12月7日 下午6:29:16
	 * @param user
	 */
	public void login(final UserBean user) {
		this.executorService.execute(new Runnable() {
			@Override
			public void run() {
				if (session != null && session.isConnected()) {
					Log.i(TAG, "login");
					ProtobufMessage.Login.Builder builder = ProtobufMessage.Login.newBuilder();
					builder.setUuid(user.getUuid());
					builder.setUsername(user.getUsername());
					builder.setPassword(user.getPassword());
					session.write(builder.build());
				}
			}
		});
	}

	/**
	 * @功能: 发送文本
	 * @作者: yangc
	 * @创建日期: 2014年12月7日 下午6:29:43
	 * @param chat
	 */
	public void sendChat(final ChatBean chat) {
		this.executorService.execute(new Runnable() {
			@Override
			public void run() {
				if (session != null && session.isConnected()) {
					Log.i(TAG, "sendChat");
					ProtobufMessage.Chat.Builder builder = ProtobufMessage.Chat.newBuilder();
					builder.setUuid(chat.getUuid());
					builder.setFrom(chat.getFrom());
					builder.setTo(chat.getTo());
					builder.setData(chat.getData());
					session.write(builder.build());
				} else {
					ResultBean result = new ResultBean();
					result.setUuid(chat.getUuid());
					result.setSuccess(false);
					result.setData("fail");

					Intent intent = new Intent(context, PushService.class);
					intent.putExtra(Constants.EXTRA_ACTION, Constants.ACTION_RECEIVE_RESULT);
					intent.putExtra(Constants.EXTRA_RESULT, result);
					context.startService(intent);
				}
			}
		});
	}

	/**
	 * @功能: 发送文件
	 * @作者: yangc
	 * @创建日期: 2014年12月16日 下午1:06:18
	 * @param file
	 */
	public void sendFile(final FileBean file) {
		this.executorService.execute(new Runnable() {
			@Override
			public void run() {
				if (session != null && session.isConnected()) {
					Log.i(TAG, "sendFile");
					File dir = AndroidUtils.getStorageDir(context, Constants.APP + "/" + Constants.CACHE_VOICE + "/" + file.getTo());
					File targetFile = new File(dir, file.getFileName());
					if (targetFile.exists()) {
						ProtobufMessage.File.Builder builder = ProtobufMessage.File.newBuilder();
						builder.setUuid(file.getUuid());
						builder.setFrom(file.getFrom());
						builder.setTo(file.getTo());
						builder.setFileName(file.getFileName());
						builder.setFileSize(targetFile.length());
						builder.setFileMd5(Md5Utils.getMD5String(targetFile));

						int offset = -1;
						byte[] data = new byte[8192];
						BufferedInputStream bis = null;
						try {
							bis = new BufferedInputStream(new FileInputStream(targetFile));
							while ((offset = bis.read(data)) != -1) {
								builder.setOffset(offset);
								builder.setData(ByteString.copyFrom(data));
								session.write(builder.build());
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							try {
								if (bis != null) bis.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} else {
					ResultBean result = new ResultBean();
					result.setUuid(file.getUuid());
					result.setSuccess(false);
					result.setData("fail");

					Intent intent = new Intent(context, PushService.class);
					intent.putExtra(Constants.EXTRA_ACTION, Constants.ACTION_RECEIVE_RESULT);
					intent.putExtra(Constants.EXTRA_RESULT, result);
					context.startService(intent);
				}
			}
		});
	}

	/**
	 * @功能: 发送结果
	 * @作者: yangc
	 * @创建日期: 2014年12月7日 下午6:29:58
	 * @param result
	 */
	public void sendResult(final ResultBean result) {
		this.executorService.execute(new Runnable() {
			@Override
			public void run() {
				if (session != null && session.isConnected()) {
					Log.i(TAG, "sendResult");
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
