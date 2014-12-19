package com.yangc.ichat.utils;

import java.io.File;
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

	public interface OnPlayingCompletionListener {
		public void onPlayingCompletion();
	}

	private VoiceUtils() {
	}

	public synchronized static VoiceUtils getInstance() {
		if (instance == null) {
			instance = new VoiceUtils();
		}
		return instance;
	}

	public void startRecord(File file) {
		try {
			if (file.exists()) file.delete();
			file.createNewFile();
			this.stopRecord();
			this.mediaRecorder = new MediaRecorder();
			this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			this.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			this.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			this.mediaRecorder.setOutputFile(file.getAbsolutePath());
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
		if (this.isRecording && this.mediaRecorder != null) {
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
		if (this.isRecording && this.mediaRecorder != null) {
			int volume = MAX_VOLUME * this.mediaRecorder.getMaxAmplitude() / 32768;
			return volume < MAX_VOLUME ? volume : MAX_VOLUME - 1;
		}
		return 0;
	}

	public void startPlay(File file, final VoiceUtils.OnPlayingCompletionListener playingCompletionListener) {
		try {
			if (file.exists()) {
				this.stopPlay();
				this.mediaPlayer = new MediaPlayer();
				this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						stopPlay();
						playingCompletionListener.onPlayingCompletion();
					}
				});
				this.mediaPlayer.setDataSource(file.getAbsolutePath());
				this.mediaPlayer.prepare();
				this.mediaPlayer.start();
				this.isPlaying = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopPlay() {
		if (this.isPlaying && this.mediaPlayer != null) {
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
