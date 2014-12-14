package com.yangc.ichat.utils;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

public class VoiceUtils {

	private static final int MAX_VOLUME = 7;

	private static VoiceUtils instance;

	private boolean isRecording;
	private MediaRecorder mediaRecorder;

	private boolean isPlaying;
	private MediaPlayer mediaPlayer;

	private VoiceUtils() {
	}

	public synchronized static VoiceUtils getInstance() {
		if (instance == null) {
			instance = new VoiceUtils();
		}
		return instance;
	}

	public void startRecord(String path) {
		try {
			this.stopRecord();
			this.mediaRecorder = new MediaRecorder();
			this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			this.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			this.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			this.mediaRecorder.setOutputFile(path);
			this.mediaRecorder.prepare();
			this.mediaRecorder.start();
			Thread.sleep(200);
			this.isRecording = true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized void stopRecord() {
		if (this.isRecording() && this.mediaRecorder != null) {
			this.mediaRecorder.stop();
			this.mediaRecorder.release();
			this.mediaRecorder = null;
			this.isRecording = false;
		}
	}

	public boolean isRecording() {
		return this.isRecording;
	}

	public synchronized int getVolume() {
		if (this.isRecording() && this.mediaRecorder != null) {
			return MAX_VOLUME * this.mediaRecorder.getMaxAmplitude() / 32768;
		}
		return 0;
	}

	public synchronized void startPlay(String path, MediaPlayer.OnCompletionListener completionListener) {
		try {
			this.stopPlay();
			this.mediaPlayer = new MediaPlayer();
			this.mediaPlayer.setOnCompletionListener(completionListener);
			this.mediaPlayer.setDataSource(path);
			this.mediaPlayer.prepare();
			this.mediaPlayer.start();
			this.isPlaying = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void stopPlay() {
		if (this.isPlaying() && this.mediaPlayer != null) {
			this.mediaPlayer.stop();
			this.mediaPlayer.release();
			this.mediaPlayer = null;
			this.isPlaying = false;
		}
	}

	public boolean isPlaying() {
		return this.isPlaying;
	}

}
