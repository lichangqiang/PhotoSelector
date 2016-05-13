package com.magicwork.photoablumlib;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.magicwork.photoablumlib.ImageDir.Type;
import com.magicwork.photoablumlib.PhotoSelectorAdapter.onItemCheckedChangedListener;
import com.magicwork.photoablumlib.R;

import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoSelectorActivity extends Activity {
	private HashMap<String, ImageDir> imageDirsMap = new HashMap<String, ImageDir>();

	GridView gvPhotos;
	ImageDir currentDir;
	ImageFolderPopWindow popDir;
	View lyTopBar;
	TextView tvTitle;
	Button btnNext;
	int maxPicSize;

	private int maxCount = 3;

	private File cameraFile;
	public static final int REQUEST_CODE_CAMERA = 1000;
	public static final int REQUEST_CODE_VEDIO = 2000;
	public static final int REQUEST_CODE_IMAGE_SWITCHER = 2000;
	private ArrayList<String> selectedFath;
	private Type loadType = Type.IMAGE;
	private long sizeLimit=5*1024*1024;//5m
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_selector);
		initData();
		init();
	}

	private void initData() {
		selectedFath = getIntent().getStringArrayListExtra("selectedPaths");
		if (getIntent().hasExtra("loadType")) {
			loadType = Type.valueOf(getIntent().getStringExtra("loadType"));
		}
		if(getIntent().hasExtra("sizeLimit")){
			sizeLimit=getIntent().getLongExtra("sizeLimit", 1024);
		}
	}
	
	private boolean isImageType(){
		return loadType==Type.IMAGE;
	}
	
	private boolean isVedioType(){
		return loadType==Type.VEDIO;
	}

	private void init() {
		gvPhotos = (GridView) findViewById(R.id.gv_photos);
		tvTitle = (TextView) findViewById(R.id.tv_top_bar_title);
		btnNext = (Button) findViewById(R.id.btn_next);
		if(isImageType()){
			loadImagesList();
		}
		
		if(isVedioType()){
			loadVedioImagesList();
		}
		
		popDir = new ImageFolderPopWindow(getApplicationContext(),
				PhoneStateUtils.getScreenWidth(getApplicationContext()),
				PhoneStateUtils.getScreenHeight(getApplicationContext()) / 2);
		popDir.setOutsideTouchable(true);
		lyTopBar = findViewById(R.id.ly_top_bar);
		popDir.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				tvTitle.setSelected(false);
			}
		});
		popDir.setOnPopClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageDir dir = (ImageDir) v.getTag();
				currentDir = dir;
				tvTitle.setText(dir.dirName);
				loadImages(currentDir);
				popDir.dismiss();
			}
		});
	}

	/**
	 * 锟斤拷取图片锟叫憋拷
	 */
	private void loadImagesList() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						new String[] { MediaStore.Images.Media.DATA, MediaStore.Images.Media.MIME_TYPE },
						"" + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
				while (cursor.moveToNext()) {
					String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					File imageFile = new File(filePath);
					ImageDir dir = addToDir(imageFile);
					// 锟揭碉拷锟侥硷拷锟斤拷锟斤拷锟侥硷拷锟斤拷锟斤拷为锟斤拷前目录
					if (dir.files.size() > maxPicSize) {
						maxPicSize = dir.files.size();
						currentDir = dir;
					}

					if (selectedFath.contains(filePath)) {
						dir.selectedFiles.add(filePath);
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						loadImages(currentDir);
					}
				});
			}
		}).start();
	}

	private void loadVedioImagesList() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
						new String[] { MediaStore.Video.Media.DATA, MediaStore.Video.Media.MIME_TYPE,MediaStore.Video.Media._ID }, null, null,
						MediaStore.Images.Media.DATE_MODIFIED);
				while (cursor.moveToNext()) {
					String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					String id=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
					File imageFile = new File(filePath);
					ImageDir dir = addToDir(imageFile);
					//dir.ids.add(id+"");
					dir.setType(Type.VEDIO);
					if (dir.files.size() > maxPicSize) {
						maxPicSize = dir.files.size();
						currentDir = dir;
					}

					if (selectedFath.contains(filePath)) {
						dir.selectedFiles.add(filePath);
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						//loadImages(currentDir);
						loadVedioImages(currentDir);
					}
				});
			}
		}).start();
	}
	
	

	/**
	 * 锟斤拷锟侥硷拷锟斤拷拥锟侥柯�
	 * 
	 * @param imageFile
	 */
	private ImageDir addToDir(File imageFile) {
		ImageDir imageDir;
		File parentDirFile = imageFile.getParentFile();
		String parentFilePath = parentDirFile.getPath();
		if (!imageDirsMap.containsKey(parentFilePath)) {
			imageDir = new ImageDir(parentFilePath);

			imageDir.dirName = parentDirFile.getName();
			imageDirsMap.put(parentFilePath, imageDir);
			imageDir.firstImagePath = imageFile.getPath();
			imageDir.addFile(imageFile.toString());
		} else {
			imageDir = imageDirsMap.get(parentFilePath);
			imageDir.addFile(imageFile.toString());
		}

		return imageDir;
	}

	/**
	 * 锟斤拷锟斤拷图片锟斤拷Gallary
	 * 
	 * @param imageDir
	 */
	private void loadImages(final ImageDir imageDir) {
		PhotoSelectorAdapter adapter = new PhotoSelectorAdapter(PhotoSelectorActivity.this, imageDir);
		gvPhotos.setAdapter(adapter);
		adapter.setOnItemCheckdedChangedListener(new onItemCheckedChangedListener() {

			@Override
			public void onItemCheckChanged(CompoundButton chBox, boolean isCheced, ImageDir imageDir, String path) {
				if (isCheced) {
					if (getSelectedPictureCont() >= maxCount) {
						Toast.makeText(PhotoSelectorActivity.this, "不能选择超过" + maxCount, Toast.LENGTH_SHORT).show();
						chBox.setChecked(false);
						imageDir.selectedFiles.remove(path);
					} else {
						imageDir.selectedFiles.add(path);
					}
				} else {
					imageDir.selectedFiles.remove(path);
				}

				updateNext();

			}

			@Override
			public void onTakePicture(ImageDir imageDir) {
				takePicture(imageDir);
			}

			@Override
			public void onShowPicture(String path) {
				showImage(path);
			}
		});
	}
	
	private void loadVedioImages(final ImageDir imageDir) {
		PhotoSelectorAdapter adapter = new PhotoSelectorAdapter(PhotoSelectorActivity.this, imageDir);
		gvPhotos.setAdapter(adapter);
		adapter.setOnItemCheckdedChangedListener(new onItemCheckedChangedListener() {

			@Override
			public void onItemCheckChanged(CompoundButton chBox, boolean isCheced, ImageDir imageDir, String path) {
				if (isCheced) {
					if (getSelectedPictureCont() >= maxCount) {
						Toast.makeText(PhotoSelectorActivity.this, "不能选择超过" + maxCount, Toast.LENGTH_SHORT).show();
						chBox.setChecked(false);
						imageDir.selectedFiles.remove(path);
					} else {
						imageDir.selectedFiles.add(path);
					}
				} else {
					imageDir.selectedFiles.remove(path);
				}

				updateNext();

			}

			@Override
			public void onTakePicture(ImageDir imageDir) {
				takeVedio(imageDir);
			}

			@Override
			public void onShowPicture(String path) {
				//showImage(path);
			}
		});
	}
	
	

	public void updateNext() {
		if (getSelectedPictureCont() > 0) {
			btnNext.setSelected(true);
			btnNext.setText("下一步(" + getSelectedPictureCont() + ")");
			btnNext.setTextColor(Color.WHITE);
		} else {
			btnNext.setSelected(false);
			btnNext.setText("下一步");
			btnNext.setTextColor(Color.BLACK);
		}
	}

	public void popImageDir(View view) {
		if (popDir.isShowing()) {
			popDir.dismiss();
			view.setSelected(false);
		} else {
			popDir.popWindow(imageDirsMap, lyTopBar);
			view.setSelected(true);
		}
	}

	public void goNext(View view) {
		Intent intent = new Intent();
		intent.putExtra("selectPaths", getSelectedPicture());
		setResult(RESULT_OK, intent);
		finish();
	}

	public void goBack(View view) {
		finish();
	}
	
	public void updateGalleray(String path){
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(path);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_CODE_CAMERA) {
			if (cameraFile != null && cameraFile.exists()) {
				updateGalleray(cameraFile.getPath());
				currentDir.selectedFiles.add(cameraFile.getPath());
				currentDir.files.add(0, cameraFile.getPath());
				loadImages(currentDir);
				updateNext();
			}
		}
		if (requestCode == REQUEST_CODE_VEDIO) {
			if (cameraFile != null && cameraFile.exists()) {
				updateGalleray(cameraFile.getPath());
				currentDir.selectedFiles.add(cameraFile.getPath());
				currentDir.files.add(0, cameraFile.getPath());
				//loadVedioImages(currentDir);
				loadVedioImagesList();
				updateNext();
			}
		}
		
		if (requestCode == REQUEST_CODE_IMAGE_SWITCHER) {
			String[] paths = data.getStringArrayExtra("selectPaths");
			for (int i = 0; i < paths.length; i++) {
				currentDir.selectedFiles.add(paths[i]);
			}
			loadImages(currentDir);
			updateNext();
		}
	}

	/**
	 * 拍照
	 * 
	 * @param view
	 */
	public void takePicture(ImageDir imageDir) {
		if (getSelectedPictureCont() >= maxCount) {
			Toast.makeText(this, "拍照前被选中照片张数不能大于" + maxCount, Toast.LENGTH_LONG).show();
			return;
		}
		cameraFile = new File(imageDir.dirPath, System.currentTimeMillis() + ".jpg");
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				REQUEST_CODE_CAMERA);
	}
	
	public void takeVedio(ImageDir imageDir){
		cameraFile=new File(imageDir.dirPath, System.currentTimeMillis() + ".mp4");
		Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
		//intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, sizeLimit);
		//intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		
		startActivityForResult(intent,REQUEST_CODE_CAMERA);
	}

	public int getSelectedPictureCont() {
		int count = 0;
		for (String name : imageDirsMap.keySet()) {
			count += imageDirsMap.get(name).selectedFiles.size();
		}

		return count;
	}

	public ArrayList<String> getSelectedPicture() {
		ArrayList<String> paths = new ArrayList<String>();
		for (String name : imageDirsMap.keySet()) {
			paths.addAll(imageDirsMap.get(name).selectedFiles);
		}

		return paths;
	}

	public void showImage(String filePath) {
		ImageDir imageDir = currentDir;
		Bundle b = new Bundle();
		b.putStringArrayList("paths", (ArrayList<String>) imageDir.getFiles());
		b.putString("currentPath", filePath);
		b.putInt("otherCount", getSelectedPictureCont() - imageDir.selectedFiles.size());
		b.putStringArray("selectedPaths", imageDir.selectedFiles.toArray(new String[] {}));
		b.putInt("maxCount", maxCount);
		Intent intent = new Intent(this, ImageSwitcherActivity.class);
		intent.putExtra("params", b);
		startActivityForResult(intent, REQUEST_CODE_IMAGE_SWITCHER);
	}
}
