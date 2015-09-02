package com.yangc.ichat.ui.component.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Listener-helper for {@linkplain RecyclerView recycler view} which can {@linkplain ImageLoader#pause() pause ImageLoader's tasks} while recycler view is scrolling (touch scrolling and/or fling). It
 * prevents redundant loadings.<br />
 * Set it to your recycler view's {@link RecyclerView#setOnScrollListener(OnScrollListener) setOnScrollListener(...)}.<br />
 * This listener can wrap your custom {@linkplain OnScrollListener listener}.
 *
 * @since 1.7.0
 */
public class PauseOnScrollListener extends OnScrollListener {

	private ImageLoader imageLoader;

	private final boolean pauseOnScroll;
	private final boolean pauseOnFling;
	private final OnScrollListener externalListener;

	/**
	 * Constructor
	 *
	 * @param imageLoader {@linkplain ImageLoader} instance for controlling
	 * @param pauseOnScroll Whether {@linkplain ImageLoader#pause() pause ImageLoader} during touch scrolling
	 * @param pauseOnFling Whether {@linkplain ImageLoader#pause() pause ImageLoader} during fling
	 */
	public PauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
		this(imageLoader, pauseOnScroll, pauseOnFling, null);
	}

	/**
	 * Constructor
	 *
	 * @param imageLoader {@linkplain ImageLoader} instance for controlling
	 * @param pauseOnScroll Whether {@linkplain ImageLoader#pause() pause ImageLoader} during touch scrolling
	 * @param pauseOnFling Whether {@linkplain ImageLoader#pause() pause ImageLoader} during fling
	 * @param customListener Your custom {@link OnScrollListener} for {@linkplain RecyclerView recycler view} which also will be get scroll events
	 */
	public PauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling, OnScrollListener customListener) {
		this.imageLoader = imageLoader;
		this.pauseOnScroll = pauseOnScroll;
		this.pauseOnFling = pauseOnFling;
		externalListener = customListener;
	}

	@Override
	public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
		switch (newState) {
		case RecyclerView.SCROLL_STATE_IDLE:
			imageLoader.resume();
			break;
		case RecyclerView.SCROLL_STATE_DRAGGING:
			if (pauseOnScroll) {
				imageLoader.pause();
			}
			break;
		case RecyclerView.SCROLL_STATE_SETTLING:
			if (pauseOnFling) {
				imageLoader.pause();
			}
			break;
		}
		if (externalListener != null) {
			externalListener.onScrollStateChanged(recyclerView, newState);
		}
	}

	@Override
	public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
		if (externalListener != null) {
			externalListener.onScrolled(recyclerView, dx, dy);
		}
	}

}
