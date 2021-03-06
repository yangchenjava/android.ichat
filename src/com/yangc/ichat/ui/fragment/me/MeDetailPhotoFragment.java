package com.yangc.ichat.ui.fragment.me;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yangc.ichat.R;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;

public class MeDetailPhotoFragment extends Fragment {

	private DisplayImageOptions options;

	private PhotoView pvMeDetailPhoto;
	private ImageView ivLoadingPhoto;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).imageScaleType(ImageScaleType.NONE).bitmapConfig(Bitmap.Config.ARGB_8888).build();
		View view = inflater.inflate(R.layout.fragment_me_detail_photo, container, false);
		this.pvMeDetailPhoto = (PhotoView) view.findViewById(R.id.pv_me_detail_photo);
		this.ivLoadingPhoto = (ImageView) view.findViewById(R.id.iv_loading_photo);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		String photo = this.getArguments().getString("photo");
		if (!TextUtils.isEmpty(photo)) {
			this.ivLoadingPhoto.setVisibility(View.VISIBLE);
			this.ivLoadingPhoto.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), R.anim.rotate_loading));

			int i = photo.lastIndexOf(".");
			ImageLoader.getInstance().displayImage(Constants.SERVER_URL + photo.substring(0, i) + Constants.ORIGINAL_IMAGE + photo.substring(i), this.pvMeDetailPhoto, this.options,
					this.imageLoadingListener);
		}
	}

	private ImageLoadingListener imageLoadingListener = new SimpleImageLoadingListener() {
		@Override
		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			ivLoadingPhoto.clearAnimation();
			ivLoadingPhoto.setVisibility(View.GONE);
			Context context = getActivity();
			if (context != null) {
				AndroidUtils.alertToast(context, R.string.error_timeout);
			}
		}

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			ivLoadingPhoto.clearAnimation();
			ivLoadingPhoto.setVisibility(View.GONE);
		}
	};

}
