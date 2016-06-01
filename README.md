# PhotoSelector
仿新浪相册选择器，支持相册选择、视频选择</br>

</br>
<h3>一、使用方法</h3></br>
<h5>1、选择相册、支持拍照</h5>
启动PhotoSelectorActivity并传入已选照片路径List。若包含已选照片路径则被选照片会被选</br>
Intent i = new Intent(getActivity(), PhotoSelectorActivity.class);</br>
i.putStringArrayListExtra("selectedPaths", selectedImagesPaths);</br>
<h5>2、选择视频、支持视频</h5>
启动PhotoSelectorActivity并传入已选视频路径List。若包含已选视频路径则被选视频会被选</br>
传入加载类型为视频</br>
Intent i = new Intent(getActivity(), PhotoSelectorActivity.class);</br>
		i.putStringArrayListExtra("selectedPaths", selectedVedioPaths);</br>
		i.putExtra("loadType", ImageDir.Type.VEDIO.toString());</br>
		startActivityForResult(i, REQUEST_CODE_GET_VEDIOS);</br>
