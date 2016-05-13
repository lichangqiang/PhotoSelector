package com.magicwork.photoablumlib;

import java.io.File;
import java.util.ArrayList;

import com.magicwork.photoablumlib.ImageDir.Type;
import com.magicwork.photoablumlib.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class PhotoSelectorAdapter extends BaseAdapter {

	ImageDir imageDir;
	Activity context;
	LayoutInflater inflator;
	LocalImageLoader localImageLoad;
	onItemCheckedChangedListener itemCheckListener;

	public interface onItemCheckedChangedListener {
		public void onItemCheckChanged(CompoundButton chBox, boolean isCheced, ImageDir iamgeDir, String path);

		public void onTakePicture(ImageDir imageDir);

		public void onShowPicture(String path);
	}

	public void setOnItemCheckdedChangedListener(onItemCheckedChangedListener listener) {
		this.itemCheckListener = listener;
	}

	public PhotoSelectorAdapter(Activity context, ImageDir imageDir) {
		this.imageDir = imageDir;
		this.context = context;
		this.inflator = LayoutInflater.from(context);
		localImageLoad = new LocalImageLoader();
	}

	@Override
	public int getCount() {
		return imageDir.getFiles().size() + 1;
	}

	@Override
	public String getItem(int position) {
		return imageDir.getFiles().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHodler viewHolder;
		if (convertView == null) {
			convertView = inflator.inflate(R.layout.grid_item_photo, null);
			viewHolder = new ViewHodler();
			viewHolder.chSelect = (CheckBox) convertView.findViewById(R.id.ch_photo_select);
			viewHolder.photoView = (ImageView) convertView.findViewById(R.id.img_photo);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHodler) convertView.getTag();
		}

		if (position == 0) {
			viewHolder.photoView.setImageResource(R.drawable.compose_photo_photograph);
			viewHolder.photoView.setScaleType(ScaleType.CENTER_INSIDE);
			viewHolder.chSelect.setVisibility(View.GONE);
		} else {
			viewHolder.photoView.setScaleType(ScaleType.CENTER_CROP);
			viewHolder.chSelect.setVisibility(View.VISIBLE);
			viewHolder.chSelect.setTag(position - 1);
			String path = getItem(position - 1);
			viewHolder.chSelect.setOnCheckedChangeListener(null);
			viewHolder.chSelect.setChecked(imageDir.selectedFiles.contains(path));
			viewHolder.chSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					itemCheckListener.onItemCheckChanged(buttonView, isChecked, imageDir, getItem(position - 1));
				}
			});

			if (imageDir.getType() == Type.VEDIO) {
				viewHolder.photoView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(path, Thumbnails.MINI_KIND));
				/*viewHolder.photoView
						.setImageBitmap(ImageUtils.getVedioThubnailPath(imageDir.getIds().get(position - 1), context));*/
			} else {
				localImageLoad.displayImage(viewHolder.photoView, getItem(position - 1));
			}

		}

		viewHolder.photoView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (position != 0) {
					itemCheckListener.onShowPicture(getItem(position - 1));
				} else {
					itemCheckListener.onTakePicture(imageDir);
				}
			}
		});
		return convertView;
	}

	public static class ViewHodler {
		ImageView photoView;
		CheckBox chSelect;
	}

}
