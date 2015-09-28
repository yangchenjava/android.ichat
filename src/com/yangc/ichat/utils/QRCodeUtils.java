package com.yangc.ichat.utils;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeUtils {

	private static final Map<EncodeHintType, Object> ENCODE_HINTS = new EnumMap<EncodeHintType, Object>(EncodeHintType.class) {
		private static final long serialVersionUID = 1L;
		{
			// 编码格式(utf-8等单词必须小写!!)
			put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 容错级别(L--7%,M--15%,Q--25%,H--30%)
			put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			// 边框宽度
			put(EncodeHintType.MARGIN, 1);
		}
	};

	private static final Map<DecodeHintType, Object> DECODE_HINT = new EnumMap<DecodeHintType, Object>(DecodeHintType.class) {
		private static final long serialVersionUID = 1L;
		{
			// 编码格式(utf-8等单词必须小写!!)
			put(DecodeHintType.CHARACTER_SET, "utf-8");
		}
	};

	private QRCodeUtils() {
	}

	/**
	 * @功能: 生成二维码
	 * @作者: yangc
	 * @创建日期: 2014年6月6日 下午8:39:13
	 * @param contents
	 * @param width
	 * @param height
	 * @param imagePath
	 * @return
	 */
	public static boolean encode(String content, int width, int height, String imagePath) {
		String imageType = imagePath.substring(imagePath.lastIndexOf(".") + 1);
		if (!TextUtils.equals(imageType, "png")) {
			throw new IllegalArgumentException("image must be png");
		}

		try {
			BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, ENCODE_HINTS);
			File imageFile = new File(imagePath);
			MatrixToImageWriter.writeToFile(matrix, imageType, imageFile, new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF));
			return true;
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String decode(Bitmap bitmap) {
		if (bitmap != null) {
			try {
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				int[] pixels = new int[width * height];
				bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
				LuminanceSource source = new RGBLuminanceSource(width, height, pixels);
				BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
				Result result = new MultiFormatReader().decode(binaryBitmap, DECODE_HINT);
				return result.getText();
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
