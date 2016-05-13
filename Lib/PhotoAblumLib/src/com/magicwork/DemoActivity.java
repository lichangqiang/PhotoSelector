package com.magicwork;

import java.util.ArrayList;

import com.magicwork.photoablumlib.ImageDir;
import com.magicwork.photoablumlib.PhotoSelectorActivity;
import com.magicwork.photoablumlib.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DemoActivity extends Activity {
	private ArrayList<String> selectedImagesPaths = new ArrayList<String>();
	private ArrayList<String> selectedVedioPaths = new ArrayList<String>();

	private static final int REQUEST_CODE_GET_PHOTOS = 1000;
	private static final int REQUEST_CODE_GET_VEDIOS = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actitvity_demo);
	}

	/**
	 * 照片选择
	 * 
	 * @param view
	 */
	public void choosePhoto(View view) {
		Intent i = new Intent(this, PhotoSelectorActivity.class);
		
		i.putStringArrayListExtra("selectedPaths", selectedImagesPaths);//若传入已选中的路径则在选择页面会呈现选中状态
		startActivityForResult(i, REQUEST_CODE_GET_PHOTOS);
	}

	/**
	 * 视频选择
	 * 
	 * @param view
	 */
	public void chooseVideo(View view) {
		Intent i = new Intent(this, PhotoSelectorActivity.class);
		i.putStringArrayListExtra("selectedPaths", selectedVedioPaths);
		i.putExtra("loadType", ImageDir.Type.VEDIO.toString());
		i.putExtra("sizeLimit", 1 * 1024 * 1024);
		startActivityForResult(i, REQUEST_CODE_GET_VEDIOS);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_GET_PHOTOS:
			if (resultCode == RESULT_OK) {
				selectedImagesPaths = data.getStringArrayListExtra("selectPaths");
			}
			break;
		case REQUEST_CODE_GET_VEDIOS:
			if (resultCode == RESULT_OK) {
				selectedVedioPaths = data.getStringArrayListExtra("selectPaths");
			}
			break;
		}
	}

}