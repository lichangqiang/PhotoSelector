package com.magicwork.photoablumlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore.Video.Thumbnails;

public class ImageUtils {

	public static Bitmap compressBitmap(String filePath, int with, int height) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		options.inSampleSize = calculateInSampleSize(options, with, height);
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
	
	public static Bitmap getVedioThubnailPath(String id,Context context){
		long vedioId=Long.valueOf(id);
		BitmapFactory.Options option=new  BitmapFactory.Options();
		return Thumbnails.getThumbnail(context.getContentResolver(), vedioId, Thumbnails.MINI_KIND, option);
	}
}
