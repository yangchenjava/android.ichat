package com.yangc.ichat.comm.handler;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yangc.ichat.comm.bean.ChatBean;
import com.yangc.ichat.comm.bean.FileBean;
import com.yangc.ichat.comm.bean.ResultBean;
import com.yangc.ichat.service.PushService;
import com.yangc.ichat.utils.Constants;

public class ClientHandler extends IoHandlerAdapter {

	private static final String TAG = ClientHandler.class.getName();

	private Context context;

	public ClientHandler(Context context) {
		this.context = context;
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		Log.i(TAG, "sessionClosed");
		this.context.startService(this.getIntent(Constants.ACTION_RECONNECT));
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		Log.i(TAG, "sessionIdle");
		if (status.equals(IdleStatus.READER_IDLE)) {
			session.close(true);
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		Log.e(TAG, "exceptionCaught -> " + cause.getMessage(), cause);
		session.close(true);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		Log.i(TAG, "messageReceived");
		if (message instanceof ChatBean) {
			Intent intent = this.getIntent(Constants.ACTION_RECEIVE_CHAT);
			intent.putExtra(Constants.EXTRA_CHAT, (ChatBean) message);
			this.context.startService(intent);
		} else if (message instanceof FileBean) {
			Intent intent = this.getIntent(Constants.ACTION_RECEIVE_FILE);
			intent.putExtra(Constants.EXTRA_FILE, (FileBean) message);
			this.context.startService(intent);
		} else if (message instanceof ResultBean) {
			Intent intent = this.getIntent(Constants.ACTION_RECEIVE_RESULT);
			intent.putExtra(Constants.EXTRA_RESULT, (ResultBean) message);
			this.context.startService(intent);
		}
	}

	private Intent getIntent(int extra) {
		Intent intent = new Intent(this.context, PushService.class);
		intent.putExtra(Constants.EXTRA_ACTION, extra);
		return intent;
	}

}
