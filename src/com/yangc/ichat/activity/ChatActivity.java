package com.yangc.ichat.activity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
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
import com.yangc.ichat.comm.bean.ResultBean;
import com.yangc.ichat.database.bean.TIchatAddressbook;
import com.yangc.ichat.database.bean.TIchatHistory;
import com.yangc.ichat.service.CallbackManager;
import com.yangc.ichat.service.PushService;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.DatabaseUtils;
import com.yangc.ichat.utils.EmojiUtils;
import com.yangc.ichat.widget.ResizeLayout;

public class ChatActivity extends Activity implements CallbackManager.OnChatListener {

	// top
	private TextView tvChatNickname;
	// center
	private ListView lvChat;
	private ChatActivityChatAdapter chatAdapter;
	// bottom
	private TextView tvChatPlus;
	private Button btnChatSend;
	private EditText etChatContent;
	private TextView tvChatFace;
	// emoji
	private RelativeLayout rlChatEmoji;
	private ViewPager vpChatEmoji;
	private LinearLayout llChatEmojiNavi;

	private String username;
	private TIchatAddressbook addressbook;
	private List<TIchatHistory> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_chat);
		CallbackManager.registerChatListener(this);
		// top
		((ResizeLayout) this.findViewById(R.id.rl_chat)).setOnResizeListener(this.layoutResizeListener);
		((ImageView) this.findViewById(R.id.iv_chat_backspace)).setOnClickListener(this.backspaceListener);
		this.tvChatNickname = (TextView) this.findViewById(R.id.tv_chat_nickname);
		((ImageView) this.findViewById(R.id.iv_title_bar_friend)).setOnClickListener(this.friendInfoListener);
		// center
		this.lvChat = (ListView) this.findViewById(R.id.lv_chat);
		this.lvChat.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true, this.scrollListener));
		this.lvChat.setOnTouchListener(this.listViewTouchListener);
		// bottom
		this.tvChatPlus = (TextView) this.findViewById(R.id.tv_chat_plus);
		this.btnChatSend = (Button) this.findViewById(R.id.btn_chat_send);
		this.btnChatSend.setOnClickListener(this.sendListener);
		this.etChatContent = (EditText) this.findViewById(R.id.et_chat_content);
		this.etChatContent.setOnTouchListener(this.editTextTouchListener);
		this.etChatContent.addTextChangedListener(this.textChangedListener);
		this.tvChatFace = (TextView) this.findViewById(R.id.tv_chat_face);
		this.tvChatFace.setOnClickListener(this.faceListener);
		// emoji
		this.rlChatEmoji = (RelativeLayout) this.findViewById(R.id.rl_chat_emoji);
		this.vpChatEmoji = (ViewPager) this.findViewById(R.id.vp_chat_emoji);
		this.vpChatEmoji.setOffscreenPageLimit(2);
		this.vpChatEmoji.setOnPageChangeListener(this.pageChangeListener);
		this.vpChatEmoji.setAdapter(this.pagerAdapter);
		this.llChatEmojiNavi = (LinearLayout) this.findViewById(R.id.ll_chat_emoji_navi);
		this.setEmojiNavi(0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.username = this.getIntent().getStringExtra("username");
		this.addressbook = DatabaseUtils.getAddressbookByUsername(this, this.username);

		this.tvChatNickname.setText(this.addressbook.getNickname());
		this.list = DatabaseUtils.getHistoryListByUsername_page(this, this.username, 0L);
		this.chatAdapter = new ChatActivityChatAdapter(this, this.list, DatabaseUtils.getMe(this).getPhoto(), this.addressbook.getPhoto());
		this.lvChat.setAdapter(this.chatAdapter);
		this.lvChat.setSelection(this.list.size() == 0 ? 0 : this.list.size() - 1);

		Intent intent = new Intent(this, PushService.class);
		intent.putExtra(Constants.EXTRA_ACTION, Constants.ACTION_CANCEL_NOTIFICATION);
		this.startService(intent);
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
	public void onChatReceived(final TIchatHistory history) {
		if (this.list != null) {
			synchronized (this.list) {
				this.list.add(history);
				this.chatAdapter.notifyDataSetChanged();
				this.lvChat.setSelection(this.list.size() - 1);
			}
		}
	}

	@Override
	public void onResultReceived(ResultBean result) {
		// TODO
	}

	@Override
	public void onNetworkError() {
		AndroidUtils.alertToast(this, R.string.error_network);
	}

	private void goToMain() {
		AndroidUtils.hideSoftInput(this);
		MainActivity.CURRENT_TAB_ID = R.id.ll_tab_wechat;
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}

	private void setEmojiNavi(int position) {
		this.llChatEmojiNavi.removeAllViews();
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(5, 0, 5, 0);
		for (int i = 0; i < EmojiUtils.PAGE_COUNT; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setScaleType(ScaleType.FIT_CENTER);
			if (i == position) {
				imageView.setImageResource(R.drawable.emoji_page_select);
			} else {
				imageView.setImageResource(R.drawable.emoji_page_normal);
			}
			this.llChatEmojiNavi.addView(imageView, layoutParams);
		}
	}

	/** -----------------------------------------top------------------------------------------- */

	// 布局大小变化监听
	private ResizeLayout.OnResizeListener layoutResizeListener = new ResizeLayout.OnResizeListener() {
		@Override
		public void onResize(int w, int h, int oldw, int oldh) {
			if (h < oldh) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						lvChat.setSelection(list.size() == 0 ? 0 : list.size() - 1);
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

	/** -----------------------------------------center------------------------------------------- */

	// listview滚动监听
	private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
		private Dialog progressDialog;
		private int firstVisibleItem;

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (this.firstVisibleItem == 0) {
				if (list != null && !list.isEmpty()) {
					synchronized (list) {
						List<TIchatHistory> historyList = DatabaseUtils.getHistoryListByUsername_page(ChatActivity.this, username, list.get(0).getId());
						if (historyList != null && !historyList.isEmpty()) {
							this.progressDialog = AndroidUtils.showProgressDialog(ChatActivity.this, getResources().getString(R.string.text_load), true, true);
							list.addAll(0, historyList);
							chatAdapter.notifyDataSetChanged();
							lvChat.setSelection(historyList.size() - 1);
						}
					}
					if (this.progressDialog != null) {
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								progressDialog.dismiss();
								progressDialog = null;
							}
						}, 200);
					}
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			this.firstVisibleItem = firstVisibleItem;
		}
	};

	@SuppressLint("ClickableViewAccessibility")
	private View.OnTouchListener listViewTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				AndroidUtils.hideSoftInput(ChatActivity.this);
				rlChatEmoji.setVisibility(View.GONE);
			}
			return false;
		}
	};

	/** -----------------------------------------bottom------------------------------------------- */

	//  发送监听
	private View.OnClickListener sendListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ChatBean chat = new ChatBean();
			chat.setUuid(UUID.randomUUID().toString());
			chat.setFrom(Constants.USERNAME);
			chat.setTo(username);
			chat.setData(etChatContent.getText().toString());

			TIchatHistory history = new TIchatHistory();
			history.setUuid(chat.getUuid());
			history.setUsername(username);
			history.setChat(chat.getData());
			history.setChatStatus(1L);
			history.setTransmitStatus(0L);
			history.setDate(new Date());
			DatabaseUtils.saveHistory(ChatActivity.this, history);

			synchronized (list) {
				list.add(history);
				chatAdapter.notifyDataSetChanged();
				lvChat.setSelection(list.size() - 1);
			}

			Intent intent = new Intent(ChatActivity.this, PushService.class);
			intent.putExtra(Constants.EXTRA_ACTION, Constants.ACTION_SEND_CHAT);
			intent.putExtra(Constants.EXTRA_CHAT, chat);
			ChatActivity.this.startService(intent);

			etChatContent.setText("");
		}
	};

	//  编辑框触碰监听
	@SuppressLint("ClickableViewAccessibility")
	private View.OnTouchListener editTextTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (rlChatEmoji.getVisibility() == View.VISIBLE) {
					rlChatEmoji.setVisibility(View.GONE);
				}
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
			}
		}
	};

	/** -----------------------------------------emoji------------------------------------------- */

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
			List<EmojiBean> list = EmojiUtils.getEmojiList(position);
			list.add(new EmojiBean(R.drawable.selector_emoji_remove, null, null));
			gvChatEmoji.setAdapter(new ChatActivityEmojiAdapter(ChatActivity.this, list));
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

			}
		};
	};

}
