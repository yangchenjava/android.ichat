package com.yangc.ichat.activity;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.yangc.ichat.R;
import com.yangc.ichat.activity.adapter.ChatActivityChatAdapter;
import com.yangc.ichat.activity.adapter.ChatActivityEmojiAdapter;
import com.yangc.ichat.bean.EmojiBean;
import com.yangc.ichat.comm.bean.ChatBean;
import com.yangc.ichat.comm.bean.FileBean;
import com.yangc.ichat.comm.bean.ResultBean;
import com.yangc.ichat.database.bean.TIchatAddressbook;
import com.yangc.ichat.database.bean.TIchatHistory;
import com.yangc.ichat.service.CallbackManager;
import com.yangc.ichat.service.PushService;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.DatabaseUtils;
import com.yangc.ichat.utils.EmojiUtils;
import com.yangc.ichat.utils.VoiceUtils;
import com.yangc.ichat.widget.ResizeLayout;

public class ChatActivity extends Activity implements CallbackManager.OnChatListener {

	private Vibrator vibrator;
	private SoundPool soundPool;
	private int afterUploadVoice;

	// top
	private TextView tvChatNickname;
	// center
	private ListView lvChat;
	private ChatActivityChatAdapter chatAdapter;
	// bottom
	private TextView tvChatMode;
	private TextView tvChatPlus;
	private Button btnChatSend;
	private EditText etChatContent;
	private TextView tvChatFace;
	private Button btnChatRecord;
	// emoji
	private RelativeLayout rlChatEmoji;
	private ViewPager vpChatEmoji;
	private LinearLayout llChatEmojiNavi;
	// record status
	private int[] volResIds = { R.drawable.record_vol_1, R.drawable.record_vol_2, R.drawable.record_vol_3, R.drawable.record_vol_4, R.drawable.record_vol_5, R.drawable.record_vol_6,
			R.drawable.record_vol_7 };
	private RelativeLayout rlRecordStatus;
	private ImageView ivRecordSpeaking;
	private ImageView ivRecordVol;
	private ImageView ivRecordCancel;
	private ImageView ivRecordShort;
	private TextView tvRecordText;

	private String username;
	private TIchatAddressbook addressbook;
	private List<TIchatHistory> chatList;

	private VoiceUtils voice;
	private MyHandler myHandler;
	private int timing; // 录音计时
	private String fileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_chat);

		// 添加震动效果
		this.vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		// 音效
		this.soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 5);
		try {
			this.afterUploadVoice = soundPool.load(this.getAssets().openFd("sound/after_upload_voice.mp3"), 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.voice = VoiceUtils.getInstance();
		this.myHandler = new MyHandler(this);

		// top
		((ResizeLayout) this.findViewById(R.id.rl_chat)).setOnResizeListener(this.layoutResizeListener);
		((ImageView) this.findViewById(R.id.iv_chat_backspace)).setOnClickListener(this.backspaceListener);
		this.tvChatNickname = (TextView) this.findViewById(R.id.tv_chat_nickname);
		((ImageView) this.findViewById(R.id.iv_title_bar_friend)).setOnClickListener(this.friendInfoListener);
		// center
		this.lvChat = (ListView) this.findViewById(R.id.lv_chat);
		this.lvChat.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true, this.scrollListener));
		// bottom
		this.tvChatMode = (TextView) this.findViewById(R.id.tv_chat_mode);
		this.tvChatMode.setOnClickListener(this.modeListener);
		this.tvChatPlus = (TextView) this.findViewById(R.id.tv_chat_plus);
		this.btnChatSend = (Button) this.findViewById(R.id.btn_chat_send);
		this.btnChatSend.setOnClickListener(this.sendListener);
		this.etChatContent = (EditText) this.findViewById(R.id.et_chat_content);
		this.etChatContent.setOnKeyListener(this.delKeyListener);
		this.etChatContent.setOnTouchListener(this.editTextTouchListener);
		this.etChatContent.addTextChangedListener(this.textChangedListener);
		this.tvChatFace = (TextView) this.findViewById(R.id.tv_chat_face);
		this.tvChatFace.setOnClickListener(this.faceListener);
		this.btnChatRecord = (Button) this.findViewById(R.id.btn_chat_record);
		this.btnChatRecord.setOnTouchListener(this.recordListener);
		// emoji
		this.rlChatEmoji = (RelativeLayout) this.findViewById(R.id.rl_chat_emoji);
		this.vpChatEmoji = (ViewPager) this.findViewById(R.id.vp_chat_emoji);
		this.vpChatEmoji.setOffscreenPageLimit(2);
		this.vpChatEmoji.setOnPageChangeListener(this.pageChangeListener);
		this.vpChatEmoji.setAdapter(this.pagerAdapter);
		this.llChatEmojiNavi = (LinearLayout) this.findViewById(R.id.ll_chat_emoji_navi);
		this.setEmojiNavi(0);
		// record status
		this.rlRecordStatus = (RelativeLayout) this.findViewById(R.id.rl_record_status);
		this.ivRecordSpeaking = (ImageView) this.findViewById(R.id.iv_record_speaking);
		this.ivRecordVol = (ImageView) this.findViewById(R.id.iv_record_vol);
		this.ivRecordCancel = (ImageView) this.findViewById(R.id.iv_record_cancel);
		this.ivRecordShort = (ImageView) this.findViewById(R.id.iv_record_short);
		this.tvRecordText = (TextView) this.findViewById(R.id.tv_record_text);
	}

	@Override
	protected void onResume() {
		super.onResume();
		CallbackManager.registerChatListener(this);

		this.username = this.getIntent().getStringExtra("username");
		// 当前对话的用户
		Constants.CHATTING_USERNAME = this.username;
		this.addressbook = DatabaseUtils.getAddressbookByUsername(this, this.username);

		this.tvChatNickname.setText(this.addressbook.getNickname());
		this.chatList = DatabaseUtils.getHistoryListByUsername_page(this, this.username, 0L);
		this.chatAdapter = new ChatActivityChatAdapter(this, this.chatList, this.username, DatabaseUtils.getMe(this).getPhoto(), this.addressbook.getPhoto());
		this.lvChat.setAdapter(this.chatAdapter);
		this.lvChat.setSelection(this.chatList.size() == 0 ? 0 : this.chatList.size() - 1);

		// 取消所有通知
		Intent intent = new Intent(this, PushService.class);
		intent.putExtra(Constants.EXTRA_ACTION, Constants.ACTION_CANCEL_NOTIFICATION);
		this.startService(intent);

		// 更新文本消息为已读
		DatabaseUtils.updateHistoryByUsername(this, this.username);
	}

	@Override
	protected void onPause() {
		super.onPause();
		CallbackManager.unregisterChatListener(this);
		this.voice.stopRecord();
		this.voice.stopPlay();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.soundPool.release();
		this.soundPool = null;
	}

	@Override
	public void onBackPressed() {
		if (this.rlChatEmoji.getVisibility() == View.VISIBLE) {
			this.rlChatEmoji.setVisibility(View.GONE);
		} else {
			this.goToMain();
		}
	}

	@Override
	public void onChatReceived(TIchatHistory history) {
		if (this.chatList != null && history.getUsername().equals(this.username)) {
			synchronized (this.chatList) {
				this.chatList.add(history);
				this.chatAdapter.notifyDataSetChanged();
				this.lvChat.setSelection(this.chatList.size() - 1);
			}
		}
	}

	@Override
	public void onResultReceived(ResultBean result) {
		synchronized (this.chatList) {
			for (int i = this.chatList.size() - 1; i >= 0; i--) {
				TIchatHistory history = this.chatList.get(i);
				if (history.getChatStatus() == 1 && history.getUuid().equals(result.getUuid())) {
					history.setTransmitStatus(result.isSuccess() ? 2L : 1L);
					break;
				}
			}
		}
		this.chatAdapter.notifyDataSetChanged();
	}

	@Override
	public void onNetworkError() {
		AndroidUtils.alertToast(this, R.string.error_network);
	}

	// 回到主页面
	private void goToMain() {
		AndroidUtils.hideSoftInput(this);
		MainActivity.CURRENT_TAB_ID = R.id.ll_tab_wechat;
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}

	// 发送语音
	private void sendRecord() {
		FileBean file = new FileBean();
		file.setUuid(this.fileName + "_" + this.timing);
		file.setFrom(Constants.USERNAME);
		file.setTo(this.username);
		file.setFileName(this.fileName);

		TIchatHistory history = new TIchatHistory();
		history.setUuid(file.getUuid());
		history.setUsername(this.username);
		history.setChat(Constants.VOICE);
		history.setType(1L);
		history.setChatStatus(1L);
		history.setTransmitStatus(0L);
		history.setDate(new Date());
		DatabaseUtils.saveHistory(this, history);

		synchronized (this.chatList) {
			this.chatList.add(history);
			this.chatAdapter.notifyDataSetChanged();
			this.lvChat.setSelection(chatList.size() - 1);
		}

		Intent intent = new Intent(ChatActivity.this, PushService.class);
		intent.putExtra(Constants.EXTRA_ACTION, Constants.ACTION_SEND_FILE);
		intent.putExtra(Constants.EXTRA_FILE, file);
		this.startService(intent);

		this.soundPool.play(this.afterUploadVoice, 1, 1, 0, 0, 1);
	}

	/** ----------------------------------------- top ------------------------------------------- */

	// 布局大小变化监听
	private ResizeLayout.OnResizeListener layoutResizeListener = new ResizeLayout.OnResizeListener() {
		@Override
		public void onResize(int w, int h, int oldw, int oldh) {
			if (h < oldh) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						lvChat.setSelection(chatList.size() == 0 ? 0 : chatList.size() - 1);
					}
				}, 200);
			}
		}
	};

	// 后退监听
	private View.OnClickListener backspaceListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			goToMain();
		}
	};

	// 好友信息监听
	private View.OnClickListener friendInfoListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(ChatActivity.this, FriendActivity.class);
			Bundle bundle = new Bundle(6);
			bundle.putString("nickname", addressbook.getNickname());
			bundle.putLong("sex", addressbook.getSex());
			bundle.putString("phone", addressbook.getPhone());
			bundle.putString("photo", addressbook.getPhoto());
			bundle.putString("signature", addressbook.getSignature());
			bundle.putString("username", addressbook.getUsername());
			intent.putExtra("addressbook", bundle);
			ChatActivity.this.startActivity(intent);
		}
	};

	/** ----------------------------------------- center ------------------------------------------- */

	// listview滚动监听
	private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
		private Dialog progressDialog;
		private int firstVisibleItem;

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (this.firstVisibleItem == 0) {
				if (chatList != null && !chatList.isEmpty()) {
					synchronized (chatList) {
						List<TIchatHistory> historyList = DatabaseUtils.getHistoryListByUsername_page(ChatActivity.this, username, chatList.get(0).getId());
						if (historyList != null && !historyList.isEmpty()) {
							this.progressDialog = AndroidUtils.showProgressDialog(ChatActivity.this, getResources().getString(R.string.text_load), true, true);
							chatList.addAll(0, historyList);
							chatAdapter.notifyDataSetChanged();
							lvChat.setSelection(historyList.size() - 1);
						}
					}
					if (this.progressDialog != null) {
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								progressDialog.dismiss();
							}
						}, 200);
					}
				}
			}
			AndroidUtils.hideSoftInput(ChatActivity.this);
			rlChatEmoji.setVisibility(View.GONE);
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			this.firstVisibleItem = firstVisibleItem;
		}
	};

	/** ----------------------------------------- bottom ------------------------------------------- */

	// 模式切换监听
	private View.OnClickListener modeListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (btnChatRecord.getVisibility() == View.GONE) {
				tvChatMode.setBackgroundResource(R.drawable.selector_text);
				tvChatPlus.setVisibility(View.VISIBLE);
				btnChatSend.setVisibility(View.GONE);
				etChatContent.setVisibility(View.GONE);
				tvChatFace.setVisibility(View.GONE);
				btnChatRecord.setVisibility(View.VISIBLE);
				AndroidUtils.hideSoftInput(ChatActivity.this);
				rlChatEmoji.setVisibility(View.GONE);
			} else {
				tvChatMode.setBackgroundResource(R.drawable.selector_voice);
				if (etChatContent.length() > 0) {
					tvChatPlus.setVisibility(View.GONE);
					btnChatSend.setVisibility(View.VISIBLE);
				} else {
					tvChatPlus.setVisibility(View.VISIBLE);
					btnChatSend.setVisibility(View.GONE);
				}
				etChatContent.setVisibility(View.VISIBLE);
				tvChatFace.setVisibility(View.VISIBLE);
				btnChatRecord.setVisibility(View.GONE);
			}
		}
	};

	//  发送监听
	private View.OnClickListener sendListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Editable editable = etChatContent.getText();

			ChatBean chat = new ChatBean();
			chat.setUuid(UUID.randomUUID().toString());
			chat.setFrom(Constants.USERNAME);
			chat.setTo(username);
			chat.setData(editable.toString());

			TIchatHistory history = new TIchatHistory();
			history.setUuid(chat.getUuid());
			history.setUsername(username);
			history.setChat(chat.getData());
			history.setType(0L);
			history.setChatStatus(1L);
			history.setTransmitStatus(0L);
			history.setDate(new Date());
			DatabaseUtils.saveHistory(ChatActivity.this, history);

			synchronized (chatList) {
				chatList.add(history);
				chatAdapter.notifyDataSetChanged();
				lvChat.setSelection(chatList.size() - 1);
			}

			Intent intent = new Intent(ChatActivity.this, PushService.class);
			intent.putExtra(Constants.EXTRA_ACTION, Constants.ACTION_SEND_CHAT);
			intent.putExtra(Constants.EXTRA_CHAT, chat);
			ChatActivity.this.startService(intent);

			// 清空EditText
			ImageSpan[] imageSpans = editable.getSpans(0, editable.length(), ImageSpan.class);
			for (ImageSpan imageSpan : imageSpans) {
				editable.removeSpan(imageSpan);
			}
			editable.clear();
		}
	};

	// 软键盘退格键监听
	private View.OnKeyListener delKeyListener = new View.OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
				int selection = etChatContent.getSelectionStart();
				if (selection > 0) {
					Editable editable = etChatContent.getText();
					String content = editable.toString();
					if (content.substring(selection - 1, selection).equals("]")) {
						int start = content.substring(0, selection).lastIndexOf("[");
						if (EmojiUtils.getEmojiResId(content.substring(start, selection)) != null) {
							editable.removeSpan(editable.getSpans(start, selection, ImageSpan.class)[0]);
							editable.delete(start, selection);
						} else {
							editable.delete(selection - 1, selection);
						}
					} else {
						editable.delete(selection - 1, selection);
					}
					return true;
				}
			}
			return false;
		}
	};

	//  编辑框触碰监听
	private View.OnTouchListener editTextTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (rlChatEmoji.getVisibility() == View.VISIBLE) {
					rlChatEmoji.setVisibility(View.GONE);
				}
				v.performClick();
			}
			return false;
		}
	};

	// 编辑内容监听
	private TextWatcher textChangedListener = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (TextUtils.isEmpty(s)) {
				tvChatPlus.setVisibility(View.VISIBLE);
				btnChatSend.setVisibility(View.GONE);
			} else {
				tvChatPlus.setVisibility(View.GONE);
				btnChatSend.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	//  表情按钮监听
	private View.OnClickListener faceListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (rlChatEmoji.getVisibility() == View.GONE) {
				AndroidUtils.hideSoftInput(ChatActivity.this);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						rlChatEmoji.setVisibility(View.VISIBLE);
					}
				}, 100);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						lvChat.setSelection(chatList.size() == 0 ? 0 : chatList.size() - 1);
					}
				}, 200);
			}
		}
	};

	// 录制监听
	private View.OnTouchListener recordListener = new View.OnTouchListener() {
		private boolean isInside;
		private float y;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				this.isInside = true;
				this.y = event.getY();

				// [静止时长, 震动时长, 静止时长, 震动时长...] 时长的单位是毫秒, 0重复 -1不重复
				vibrator.vibrate(new long[] { 10, 30, 10, 30 }, -1);

				Dialog progressDialog = AndroidUtils.showProgressDialog(ChatActivity.this, getResources().getString(R.string.text_load), true, true);
				File dir = AndroidUtils.getStorageDir(ChatActivity.this, Constants.APP + "/" + Constants.CACHE_VOICE + "/" + username);
				fileName = UUID.randomUUID().toString();
				voice.startRecord(new File(dir, fileName));
				progressDialog.dismiss();

				rlRecordStatus.setVisibility(View.VISIBLE);
				recordStatus("speaking");
				new Thread(timer).start();
				new Thread(volume).start();
				break;
			}
			case MotionEvent.ACTION_UP: {
				if (voice.isRecording()) {
					if (timing < 1) {
						recordStatus("short");
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								rlRecordStatus.setVisibility(View.GONE);
							}
						}, 800);
						isInside = false;
					} else {
						rlRecordStatus.setVisibility(View.GONE);
					}
					voice.stopRecord();
					if (isInside) {
						sendRecord();
					} else {
						File dir = AndroidUtils.getStorageDir(ChatActivity.this, Constants.APP + "/" + Constants.CACHE_VOICE + "/" + username);
						File file = new File(dir, fileName);
						if (file.exists()) file.delete();
					}
					timing = 0;
				}
				v.performClick();
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				if (voice.isRecording()) {
					if (this.y - event.getY() > 100 || this.y - event.getY() < -10) {
						this.isInside = false;
						if (ivRecordCancel.getVisibility() == View.GONE) {
							recordStatus("cancel");
						}
					} else {
						this.isInside = true;
						if (ivRecordCancel.getVisibility() == View.VISIBLE) {
							recordStatus("speaking");
						}
					}
				}
				break;
			}
			}
			return false;
		}
	};

	private static class MyHandler extends Handler {
		private WeakReference<ChatActivity> weakReference;

		private MyHandler(ChatActivity activity) {
			this.weakReference = new WeakReference<ChatActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			ChatActivity activity = this.weakReference.get();
			if (activity != null && activity.voice.isRecording()) {
				switch (msg.what) {
				case 1: {
					if (++activity.timing < 60) {
						new Thread(activity.timer).start();
					} else {
						activity.rlRecordStatus.setVisibility(View.GONE);
						activity.voice.stopRecord();
						activity.sendRecord();
						activity.timing = 0;
					}
					break;
				}
				case 2: {
					if (activity.ivRecordVol.getVisibility() == View.VISIBLE) {
						activity.ivRecordVol.setBackgroundResource(activity.volResIds[activity.voice.getVolume()]);
					}
					new Thread(activity.volume).start();
					break;
				}
				}
			}
		}
	}

	private Runnable timer = new Runnable() {
		@Override
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			myHandler.sendEmptyMessage(1);
		}
	};

	private Runnable volume = new Runnable() {
		@Override
		public void run() {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			myHandler.sendEmptyMessage(2);
		}
	};

	/** ----------------------------------------- emoji ------------------------------------------- */

	// 填充表情
	private void setEmojiNavi(int position) {
		this.llChatEmojiNavi.removeAllViews();
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(5, 0, 5, 0);
		for (int i = 0; i < EmojiUtils.PAGE_COUNT; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			if (i == position) {
				imageView.setImageResource(R.drawable.emoji_page_select);
			} else {
				imageView.setImageResource(R.drawable.emoji_page_normal);
			}
			this.llChatEmojiNavi.addView(imageView, layoutParams);
		}
	}

	// viewpager滑动监听
	private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		// 状态有三个: 0空闲, 1正在滑行中, 2目标加载完毕
		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onPageSelected(int position) {
			setEmojiNavi(position);
		}
	};

	private PagerAdapter pagerAdapter = new PagerAdapter() {
		@Override
		public int getCount() {
			return EmojiUtils.PAGE_COUNT;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = getLayoutInflater().inflate(R.layout.activity_chat_emoji, container, false);
			GridView gvChatEmoji = (GridView) view.findViewById(R.id.gv_chat_emoji);
			List<EmojiBean> emojiList = EmojiUtils.getEmojiList(position);
			emojiList.add(new EmojiBean(R.drawable.selector_emoji_remove, null, null));
			gvChatEmoji.setAdapter(new ChatActivityEmojiAdapter(ChatActivity.this, emojiList));
			gvChatEmoji.setOnItemClickListener(this.gridViewItemClickListener);
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		private AdapterView.OnItemClickListener gridViewItemClickListener = new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Editable editable = etChatContent.getText();
				int selection = etChatContent.getSelectionStart();
				EmojiBean emoji = (EmojiBean) parent.getItemAtPosition(position);
				if (emoji.getResId() == R.drawable.selector_emoji_remove) {
					if (selection > 0) {
						String content = editable.toString();
						if (content.substring(selection - 1, selection).equals("]")) {
							int start = content.substring(0, selection).lastIndexOf("[");
							if (EmojiUtils.getEmojiResId(content.substring(start, selection)) != null) {
								editable.removeSpan(editable.getSpans(start, selection, ImageSpan.class)[0]);
								editable.delete(start, selection);
							} else {
								editable.delete(selection - 1, selection);
							}
						} else {
							editable.delete(selection - 1, selection);
						}
					}
				} else {
					Drawable drawable = ChatActivity.this.getResources().getDrawable(emoji.getResId());
					drawable.setBounds(0, 0, 34, 34);
					SpannableString spannableString = new SpannableString(emoji.getContent());
					spannableString.setSpan(new ImageSpan(drawable), 0, spannableString.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
					editable.insert(selection, spannableString);
				}
			}
		};
	};

	/** ----------------------------------------- record status ------------------------------------------- */

	private void recordStatus(String status) {
		if (status.equals("speaking")) {
			this.ivRecordSpeaking.setVisibility(View.VISIBLE);
			this.ivRecordVol.setVisibility(View.VISIBLE);
			this.ivRecordCancel.setVisibility(View.GONE);
			this.ivRecordShort.setVisibility(View.GONE);
			this.tvRecordText.setText(R.string.record_upglide);
			this.tvRecordText.setBackgroundResource(0);
		} else if (status.equals("cancel")) {
			this.ivRecordSpeaking.setVisibility(View.GONE);
			this.ivRecordVol.setVisibility(View.GONE);
			this.ivRecordCancel.setVisibility(View.VISIBLE);
			this.ivRecordShort.setVisibility(View.GONE);
			this.tvRecordText.setText(R.string.record_loosen);
			this.tvRecordText.setBackgroundResource(R.drawable.shape_bkg_red);
		} else if (status.equals("short")) {
			this.ivRecordSpeaking.setVisibility(View.GONE);
			this.ivRecordVol.setVisibility(View.GONE);
			this.ivRecordCancel.setVisibility(View.GONE);
			this.ivRecordShort.setVisibility(View.VISIBLE);
			this.tvRecordText.setText(R.string.record_short);
			this.tvRecordText.setBackgroundResource(0);
		}
	}

}
