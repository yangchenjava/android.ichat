package com.yangc.ichat.utils;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class BitmapUtils {

	/**
	 * @功能: 直接获取图片
	 * @作者: yangc
	 * @创建日期: 2012-12-11 下午03:35:00
	 * @param pathName
	 * @return
	 */
	public static Bitmap getBitmap(String pathName) {
		return BitmapFactory.decodeFile(pathName);
	}

	/**
	 * @功能: 根据指定缩放比例获取图片
	 * @作者: yangc
	 * @创建日期: 2012-12-11 下午03:38:36
	 * @param pathName
	 * @param size
	 * @return
	 */
	public static Bitmap getBitmap(String pathName, int size) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = size;
		return BitmapFactory.decodeFile(pathName, opts);
	}

	/**
	 * @功能: 根据指定缩放比例获取图片
	 * @作者: yangc
	 * @创建日期: 2013-1-28 下午03:48:31
	 * @param is
	 * @param size
	 * @return
	 */
	public static Bitmap getBitmap(InputStream is, int size) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = size;
		return BitmapFactory.decodeStream(is, null, opts);
	}

	/**
	 * @功能: 按宽高压缩图片
	 * @作者: yangc
	 * @创建日期: 2012-12-11 下午03:49:15
	 * @param pathName
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmap(String pathName, int width, int height) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, opts);
		int xScale = opts.outWidth / width;
		int yScale = opts.outHeight / height;
		opts.inSampleSize = xScale > yScale ? xScale : yScale;
		opts.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, opts);
	}

	/**
	 * @功能: 缩放图片
	 * @作者: yangc
	 * @创建日期: 2014年10月26日 下午7:27:21
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = (float) width / w;
		float scaleHeight = (float) height / h;
		matrix.postScale(scaleWidht, scaleHeight);
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	}

	/**
	 * @功能: drawable转Bitmap
	 * @作者: yangc
	 * @创建日期: 2014年10月26日 下午8:01:47
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() == PixelFormat.OPAQUE ? Bitmap.Config.RGB_565 : Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * @功能: 获取圆形bitmap
	 * @作者: yangc
	 * @创建日期: 2014年10月26日 下午8:02:09
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getCircleBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(0xff424242);

		canvas.drawARGB(0, 0, 0, 0);
		// 绘制圆形
		canvas.drawCircle(width / 2, height / 2, width > height ? height / 2 : width / 2, paint);
		// 交集模式
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		// 绘制图片
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return output;
	}

	/**
	 * @功能: 获取圆角bitmap
	 * @作者: yangc
	 * @创建日期: 2014年10月26日 下午8:02:09
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Rect rect = new Rect(0, 0, width, height);
		RectF rectF = new RectF(rect);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(0xff424242);

		canvas.drawARGB(0, 0, 0, 0);
		// 绘制圆角矩形
		canvas.drawRoundRect(rectF, 30, 30, paint);
		// 交集模式
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		// 绘制图片
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * @功能: 获取带倒影的bitmap
	 * @作者: yangc
	 * @创建日期: 2014年10月26日 下午8:02:09
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getReflectionImageWithOriginBitmap(Bitmap bitmap) {
		// 图片与倒影间隔距离
		int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		// 图片缩放,x轴变为原来的1倍,y轴为-1倍,实现图片的反转
		matrix.preScale(1, -1);
		// 创建反转后的图片Bitmap对象,图片高是原图的一半。
		Bitmap reflectionBitmap = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);
		// 创建标准的Bitmap对象,宽和原图一致,高是原图的1.5倍.可以理解为这张图在屏幕上显示的是原图和倒影的合体
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, height + height / 2, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapWithReflection);

		// 绘制原始图片
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint paint = new Paint();
		// 绘制间隔矩形
		canvas.drawRect(0, height, width, height + reflectionGap, paint);
		// 绘制倒影图片
		canvas.drawBitmap(reflectionBitmap, 0, height + reflectionGap, null);
		// 实现倒影渐变效果
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, Shader.TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
		return bitmapWithReflection;
	}

}
