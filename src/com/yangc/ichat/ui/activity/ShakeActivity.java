package com.yangc.ichat.ui.activity;

import java.io.IOException;

import android.app.Activity;
import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yangc.ichat.R;

public class ShakeActivity extends Activity {

	private static final float ALPHA = 0.8F;
	private static final int VALUE = 12; // 界限值
	private static final int UPTATE_INTERVAL_TIME = 3000; // 判断每次晃动的间隔时间
	private final float[] gravity = new float[3];
	private long lastUpdateTime = 0L;
	private SensorManager sensorManager;

	private SoundPool soundPool;
	private int shakeSoundMale;

	private int colorShakeTabNormal;
	private int colorShakeTabSelect;

	private TextView tvShakeTitle;
	private LinearLayout llShakeTabPerson;
	private LinearLayout llShakeTabSong;
	private LinearLayout llShakeTabTv;
	private ImageView ivShakeUp;
	private ImageView ivShakeDown;
	private ImageView ivShakeBorderUp;
	private ImageView ivShakeBorderDown;

	private Animation animationShakeUp;
	private Animation animationShakeDown;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_shake);

		this.sensorManager = (SensorManager) this.getSystemService(Service.SENSOR_SERVICE);

		// 音效
		this.soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 5);
		try {
			this.shakeSoundMale = this.soundPool.load(this.getAssets().openFd("sound/shake_sound_male.mp3"), 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.colorShakeTabNormal = this.getResources().getColor(R.color.shake_tab_normal);
		this.colorShakeTabSelect = this.getResources().getColor(R.color.shake_tab_select);

		((ImageView) this.findViewById(R.id.iv_shake_backspace)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		this.tvShakeTitle = (TextView) this.findViewById(R.id.tv_shake_title);
		this.llShakeTabPerson = (LinearLayout) this.findViewById(R.id.ll_shake_tab_person);
		this.llShakeTabSong = (LinearLayout) this.findViewById(R.id.ll_shake_tab_song);
		this.llShakeTabTv = (LinearLayout) this.findViewById(R.id.ll_shake_tab_tv);

		this.llShakeTabPerson.setOnClickListener(this.clickListener);
		this.llShakeTabSong.setOnClickListener(this.clickListener);
		this.llShakeTabTv.setOnClickListener(this.clickListener);

		this.ivShakeUp = (ImageView) this.findViewById(R.id.iv_shake_up);
		this.ivShakeDown = (ImageView) this.findViewById(R.id.iv_shake_down);
		this.ivShakeBorderUp = (ImageView) this.findViewById(R.id.iv_shake_border_up);
		this.ivShakeBorderDown = (ImageView) this.findViewById(R.id.iv_shake_border_down);

		this.animationShakeUp = AnimationUtils.loadAnimation(this, R.anim.shake_up);
		this.animationShakeUp.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				ivShakeBorderUp.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				ivShakeUp.clearAnimation();
				ivShakeBorderUp.clearAnimation();
				ivShakeBorderUp.setVisibility(View.GONE);
			}
		});
		this.animationShakeDown = AnimationUtils.loadAnimation(this, R.anim.shake_down);
		this.animationShakeDown.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				ivShakeBorderDown.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				ivShakeDown.clearAnimation();
				ivShakeBorderDown.clearAnimation();
				ivShakeBorderDown.setVisibility(View.GONE);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.sensorManager.registerListener(this.sensorEventListener, this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		this.switchTab(R.id.ll_shake_tab_person);
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.sensorManager.unregisterListener(sensorEventListener);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.soundPool.release();
		this.soundPool = null;
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switchTab(v.getId());
		}
	};

	private void switchTab(int tabId) {
		ImageView ivShakeTabPerson = (ImageView) this.llShakeTabPerson.findViewById(R.id.iv_shake_tab_person);
		TextView tvShakeTabPerson = (TextView) this.llShakeTabPerson.findViewById(R.id.tv_shake_tab_person);
		ImageView ivShakeTabSong = (ImageView) this.llShakeTabSong.findViewById(R.id.iv_shake_tab_song);
		TextView tvShakeTabSong = (TextView) this.llShakeTabSong.findViewById(R.id.tv_shake_tab_song);
		ImageView ivShakeTabTv = (ImageView) this.llShakeTabTv.findViewById(R.id.iv_shake_tab_tv);
		TextView tvShakeTabTv = (TextView) this.llShakeTabTv.findViewById(R.id.tv_shake_tab_tv);

		// reset
		ivShakeTabPerson.setImageResource(R.drawable.shake_tab_person_normal);
		tvShakeTabPerson.setTextColor(this.colorShakeTabNormal);
		ivShakeTabSong.setImageResource(R.drawable.shake_tab_song_normal);
		tvShakeTabSong.setTextColor(this.colorShakeTabNormal);
		ivShakeTabTv.setImageResource(R.drawable.shake_tab_tv_normal);
		tvShakeTabTv.setTextColor(this.colorShakeTabNormal);

		switch (tabId) {
		case R.id.ll_shake_tab_person:
			this.tvShakeTitle.setText(R.string.shake_title_bar_person);
			ivShakeTabPerson.setImageResource(R.drawable.shake_tab_person_select);
			tvShakeTabPerson.setTextColor(this.colorShakeTabSelect);
			break;
		case R.id.ll_shake_tab_song:
			this.tvShakeTitle.setText(R.string.shake_title_bar_song);
			ivShakeTabSong.setImageResource(R.drawable.shake_tab_song_select);
			tvShakeTabSong.setTextColor(this.colorShakeTabSelect);
			break;
		case R.id.ll_shake_tab_tv:
			this.tvShakeTitle.setText(R.string.shake_title_bar_tv);
			ivShakeTabTv.setImageResource(R.drawable.shake_tab_tv_select);
			tvShakeTabTv.setTextColor(this.colorShakeTabSelect);
			break;
		}
	}

	// 摇晃监听
	private SensorEventListener sensorEventListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * event.values[0];
			gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * event.values[1];
			gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * event.values[2];

			float x = event.values[0] - gravity[0]; // x轴方向的重力加速度，向右为正
			float y = event.values[1] - gravity[1]; // y轴方向的重力加速度，向上为正
			float z = event.values[2] - gravity[2]; // z轴方向的重力加速度，向前为正

			if ((Math.abs(x) > VALUE || Math.abs(y) > VALUE || Math.abs(z) > VALUE) && System.currentTimeMillis() - lastUpdateTime > UPTATE_INTERVAL_TIME) {
				ivShakeUp.startAnimation(animationShakeUp);
				ivShakeDown.startAnimation(animationShakeDown);
				ivShakeBorderUp.startAnimation(animationShakeUp);
				ivShakeBorderDown.startAnimation(animationShakeDown);
				soundPool.play(shakeSoundMale, 1, 1, 0, 0, 1);
				lastUpdateTime = System.currentTimeMillis();
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

}
