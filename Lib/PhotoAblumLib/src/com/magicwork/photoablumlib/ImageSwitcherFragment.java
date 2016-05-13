package com.magicwork.photoablumlib;

import java.util.HashSet;
import java.util.List;

import com.magicwork.photoablumlib.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ImageSwitcherFragment extends Fragment {
	private View rootView;
	private ViewPager viewPager;
	private TextView tvPage;

	List<String> paths;
	int currentPosition;

	LocalImageLoader imageLoader = new LocalImageLoader();
	HashSet<String> selectedPaths = new HashSet<String>();
	LayoutInflater inf;
	onImageSelectChangedListener imageSelectedListener;
	int maxCount = 3;
	int otherCount = 0;

	public interface onImageSelectChangedListener {
		public void onIamgeSelectChanged(HashSet<String> paths);

		public void onPageChanged(int index, int total);
	}

	public onImageSelectChangedListener getImageSelectedListener() {
		return imageSelectedListener;
	}

	public void setImageSelectedListener(onImageSelectChangedListener imageSelectedListener) {
		this.imageSelectedListener = imageSelectedListener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.fragment_image_switcher, null);
		inf = LayoutInflater.from(getContext());
		initData();
		initView();
		return rootView;
	}

	private void initData() {
		Bundle bundle = getArguments();
		paths = bundle.getStringArrayList("paths");
		maxCount = bundle.getInt("maxCount");
		otherCount = bundle.getInt("otherCount");
		String currentPath = bundle.getString("currentPath");
		String[] selectedPath = bundle.getStringArray("selectedPaths");

		for (int i = 0; i < selectedPath.length; i++) {
			selectedPaths.add(selectedPath[i]);
		}

		if (currentPath == null) {
			currentPosition = 0;
		} else {
			currentPosition = paths.indexOf(currentPath);
		}
	}

	private void initView() {
		viewPager = (ViewPager) rootView.findViewById(R.id.viewPager_image_switcher);
		tvPage = (TextView) rootView.findViewById(R.id.tv_currentPage);
		initViewPager();
		viewPager.setCurrentItem(currentPosition);
	}

	private void initViewPager() {
		viewPager.setAdapter(new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View view, Object obj) {
				return view == obj;
			}

			@Override
			public int getCount() {
				return paths.size();
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				final String path = paths.get(position);
				View view = inf.inflate(R.layout.item_image_switcher, null);
				ImageView imageView = (ImageView) view.findViewById(R.id.img_switcher_image);
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.ch_switcher_isSelected);
				checkBox.setChecked(selectedPaths.contains(paths.get(position)));

				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) {
							if (selectedPaths.size() + otherCount < maxCount) {
								selectedPaths.add(path);
							} else {
								buttonView.setChecked(false);
								Toast.makeText(getActivity(), "选择张数不能大于" + maxCount, Toast.LENGTH_LONG).show();
							}
						} else {
							selectedPaths.remove(path);
						}
						imageSelectedListener.onIamgeSelectChanged(selectedPaths);
					}
				});

				imageLoader.displayImage(imageView, paths.get(position));
				container.addView(view);
				return view;
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				// views.remove(position);
				container.removeView((View) object);
			}
		});

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int pos) {
				imageSelectedListener.onPageChanged(pos + 1, paths.size());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}
}
