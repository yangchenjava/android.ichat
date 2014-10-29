package com.yangc.ichat.volley.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jakewharton.disklrucache.DiskLruCache;

public class LruImageDiskCache {

	private DiskLruCache diskLruCache;

	public LruImageDiskCache(File cacheDir, int appVersion, long maxSize) {
		try {
			this.diskLruCache = DiskLruCache.open(cacheDir, appVersion, 1, maxSize);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Bitmap get(String key) {
		DiskLruCache.Snapshot snapshot = null;
		try {
			snapshot = this.diskLruCache.get(key);
			if (snapshot != null) {
				InputStream stream = snapshot.getInputStream(0);
				if (stream != null) {
					Bitmap bitmap = BitmapFactory.decodeStream(new BufferedInputStream(stream));
					stream.close();
					return bitmap;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (snapshot != null) snapshot.close();
		}
		return null;
	}

	public void put(String key, Bitmap bitmap) {
		DiskLruCache.Editor editor = null;
		try {
			editor = this.diskLruCache.edit(key);
			if (editor != null) {
				OutputStream stream = new BufferedOutputStream(editor.newOutputStream(0));
				if (this.writeBitmapToFile(bitmap, stream)) {
					editor.commit();
				} else {
					editor.abort();
				}
			}
			this.diskLruCache.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (editor != null) editor.abort();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean remove(String key) {
		try {
			return this.diskLruCache.remove(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean containsKey(String key) {
		DiskLruCache.Snapshot snapshot = null;
		try {
			snapshot = this.diskLruCache.get(key);
			return snapshot != null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (snapshot != null) snapshot.close();
		}
		return false;
	}

	public void clear() {
		try {
			this.diskLruCache.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean writeBitmapToFile(Bitmap bitmap, OutputStream stream) throws IOException {
		boolean result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		stream.flush();
		stream.close();
		return result;
	}

}
