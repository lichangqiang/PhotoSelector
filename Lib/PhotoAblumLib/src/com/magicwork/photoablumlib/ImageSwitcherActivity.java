package com.magicwork.photoablumlib;

import java.util.ArrayList;
import java.util.HashSet;

import com.magicwork.photoablumlib.ImageSwitcherFragment.onImageSelectChangedListener;
import com.magicwork.photoablumlib.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ImageSwitcherActivity extends FragmentActivity{

	FrameLayout lyImageSwitcher;
	ImageSwitcherFragment fragment;
	HashSet<String> selectPaths=new HashSet<String>();
	TextView tvTitle;
	Button btnNext;
	private ArrayList<String> paths;
	private int currentPosition;
	private int otherCount;
	private int maxCount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_switcher);
		initData();
		initView();
	}
	
	public void initData(){
		Bundle bundle=getIntent().getBundleExtra("params");
		paths=bundle.getStringArrayList("paths");
		String currentPath=bundle.getString("currentPath");
		String[] selectedPath=bundle.getStringArray("selectedPaths");
		maxCount=bundle.getInt("maxCount");
		otherCount=bundle.getInt("otherCount");
		for(int i=0;i<selectedPath.length;i++){
			selectPaths.add(selectedPath[i]);
		}
		
		if(currentPath==null){
			currentPosition=0;
		}else{
			currentPosition=paths.indexOf(currentPath);
		}
	}
	
	public void initView(){
		lyImageSwitcher=(FrameLayout) findViewById(R.id.ly_img_switcher);
		tvTitle=(TextView) findViewById(R.id.tv_top_bar_title);
		btnNext=(Button) findViewById(R.id.btn_next);
		fragment=new ImageSwitcherFragment();
		Bundle bundle=getIntent().getBundleExtra("params");
		fragment.setArguments(bundle);
		fragment.setImageSelectedListener(new onImageSelectChangedListener() {
			
			@Override
			public void onIamgeSelectChanged(HashSet<String> paths) {
				selectPaths=paths;
				updateNextButton();
			}

			@Override
			public void onPageChanged(int index, int total) {
				tvTitle.setText(index+"/"+total);
			}
		});
		FragmentTransaction trans=getSupportFragmentManager().beginTransaction();
		trans.add(R.id.ly_img_switcher, fragment);
		trans.commit();
		updateNextButton();
	}
	
	public void goBack(View view){
		finish();
	}
	
	public void goNext(View view){
		Intent intent=new Intent();
		intent.putExtra("selectPaths", selectPaths.toArray(new String[]{}));
		setResult(RESULT_OK, intent);
		finish();
	}
	
	
	public void  updateNextButton(){
		if(selectPaths.size()+otherCount>0){
			btnNext.setSelected(true);
			btnNext.setText("下一步("+(selectPaths.size()+otherCount)+")");
			btnNext.setTextColor(Color.WHITE);
		}else{
			btnNext.setSelected(false);
			btnNext.setText("下一步");
			btnNext.setTextColor(Color.BLACK);
		}
	}
	
	
}
