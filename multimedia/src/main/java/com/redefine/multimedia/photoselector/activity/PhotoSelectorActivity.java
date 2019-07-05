package com.redefine.multimedia.photoselector.activity;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.multimedia.R;
import com.redefine.multimedia.photoselector.adapter.AlbumMediaAdapter;
import com.redefine.multimedia.photoselector.adapter.AlbumsAdapter;
import com.redefine.multimedia.photoselector.config.ImagePickConfig;
import com.redefine.multimedia.photoselector.constant.ImagePickConstant;
import com.redefine.multimedia.photoselector.entity.Album;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.photoselector.entity.MimeType;
import com.redefine.multimedia.photoselector.loader.AlbumMediaCache;
import com.redefine.multimedia.photoselector.model.SelectedItemCollection;
import com.redefine.multimedia.photoselector.util.PathUtils;
import com.redefine.multimedia.photoselector.util.PhotoMetadataUtils;
import com.redefine.multimedia.photoselector.viewmodel.ImagePickViewModel;
import com.redefine.multimedia.photoselector.widget.AlbumsSpinner;
import com.redefine.multimedia.photoselector.widget.MediaGridInset;
import com.redefine.multimedia.photoselector.widget.PhotoPopupWindow;
import com.redefine.multimedia.player.VideoPlayerActivity;
import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.multimedia.recorder.activity.VideoRecorderActivity;
import com.redefine.multimedia.recorder.constant.VideoRecorderConstant;
import com.redefine.multimedia.snapshot.PhotoSnapShotActivity;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.constant.CommonRequestCode;
import com.redefine.welike.base.constant.PermissionRequestCode;
import com.redefine.welike.base.io.WeLikeFileManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by liwenbo on 2018/2/26.
 */

public class PhotoSelectorActivity extends BaseActivity implements AdapterView.OnItemSelectedListener
        , AlbumMediaAdapter.CheckStateListener, AlbumMediaAdapter.OnMediaClickListener
        , EasyPermissions.PermissionCallbacks, PhotoPopupWindow.OnItemClickListener {


    private RecyclerView mRecyclerView;
    private LoadingView mLoading;
    private EmptyView mEmptyView;
    private View mBackBtn;
    private TextView mCommonTitleView;
    private TextView mConfirmBtn;

    private ImagePickConfig mConfig;

    private ImagePickViewModel mViewModel;

    private AlbumsAdapter mAlbumsAdapter;
    private AlbumMediaAdapter mAdapter;
    private AlbumsSpinner mAlbumsSpinner;

    private Bundle mSaveInstanceState;
    private View mCommonTitleRootView;


    private TextView mPreviewView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_selector_layout);
        parseBundle(getIntent(), savedInstanceState);
        mSaveInstanceState = savedInstanceState;
        mRecyclerView = findViewById(R.id.photo_selector_recycler);
        mLoading = findViewById(R.id.common_loading_view);
        mEmptyView = findViewById(R.id.common_empty_view);
        mBackBtn = findViewById(R.id.common_back_btn);
        mCommonTitleView = findViewById(R.id.common_title_view);
        mCommonTitleRootView = findViewById(R.id.title_layout);
        mConfirmBtn = findViewById(R.id.photo_selector_confirm);
        mPreviewView = findViewById(R.id.photo_selector_preview);

        mAlbumsAdapter = new AlbumsAdapter();
        mAlbumsSpinner = new AlbumsSpinner(this);
        mAlbumsSpinner.setOnItemSelectedListener(this);
        mAlbumsSpinner.setSelectedTextView(mCommonTitleView);
        mAlbumsSpinner.setPopupAnchorView(mCommonTitleRootView);
        mAlbumsSpinner.setAdapter(mAlbumsAdapter);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                PhotoSelectorActivity.this.overridePendingTransition(R.anim.sliding_to_left_in, R.anim.sliding_right_out);
            }
        });


        mPreviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Item> items = mViewModel.getSelectedCollection().asList();
                if (items != null && !items.isEmpty()) {
                    Item item = items.get(0);
                    if (item.isImage()) {
                        Bundle bundle = new Bundle();
                        AlbumMediaCache.getInstance().setCacheItems(items);
                        bundle.putAll(mViewModel.getSelectedCollection().getDataWithBundle());
                        bundle.putParcelable(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG, mConfig);
                        Intent intent = new Intent();
                        intent.setClass(PhotoSelectorActivity.this, PhotoSelectorPreviewActivity.class);
                        intent.putExtras(bundle);
                        PhotoSelectorActivity.this.startActivityForResult(intent, CommonRequestCode.IMAGE_PICK_PREVIEW);
                        PhotoSelectorActivity.this.overridePendingTransition(R.anim.preview_in, 0);
                    } else if (item.isVideo()) {
                        String path = PhotoMetadataUtils.getPath(PhotoSelectorActivity.this.getContentResolver(), item.uri);
                        Intent intent = new Intent(PhotoSelectorActivity.this, VideoPlayerActivity.class);
                        intent.putExtra(PlayerConstant.MEDIA_PLAYER_VIDEO_PATH, path);
                        intent.putExtra(PlayerConstant.MEDIA_PLAYER_VIDEO_SOURCE, PlayerConstant.VIDEO_SITE_DEFAULT);
                        PhotoSelectorActivity.this.overridePendingTransition(com.redefine.commonui.R.anim.sliding_right_in,
                                com.redefine.commonui.R.anim.sliding_to_left_out);
                        PhotoSelectorActivity.this.startActivity(intent);
                    }
                }
            }
        });

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConfig.onlyShowImages() && mConfig.isCutPhoto) {
                    ArrayList<Item> items = mViewModel.getSelectedCollection().asList();
                    if (!CollectionUtil.isEmpty(items)) {
                        startCrop(items.get(0).filePath);
                    }
                } else {
                    Intent result = new Intent();
                    ArrayList<Item> items = mViewModel.getSelectedCollection().asList();
                    result.putParcelableArrayListExtra(ImagePickConstant.EXTRA_RESULT_SELECTION_ITEMS, items);
                    if (getIntent() != null) {
                        result.putExtras(getIntent());
                    }
                    onResultOk(result);
                }

            }
        });

        int spacing = ScreenUtils.dip2Px(2);

        mRecyclerView.addItemDecoration(new MediaGridInset(mConfig.spanCount, spacing, false));
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Fresco.getImagePipeline().resume();
                } else {
                    Fresco.getImagePipeline().pause();
                }
            }
        });

        mViewModel = ViewModelProviders.of(this).get(ImagePickViewModel.class);
        Bundle bundle = getIntent() == null ? new Bundle() : getIntent().getExtras();
        mViewModel.getSelectedCollection().onCreate(bundle, savedInstanceState, mConfig);
        mAdapter = new AlbumMediaAdapter(this, mViewModel.getSelectedCollection(), mConfig);
        mAdapter.registerCheckStateListener(this);
        mAdapter.registerOnMediaClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, mConfig.spanCount);
//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//
//            @Override
//            public int getSpanSize(int position) {
//                if (mAdapter.getItemCount() == position) {
//                    return 1;
//                } else {
//                    return mConfig.spanCount;
//                }
//            }
//        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mViewModel.getAlbumMediaCursor().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable List<Item> items) {
                mAdapter.setItems(items);
                mRecyclerView.scrollToPosition(0);
                if (CollectionUtil.isEmpty(items)) {
                    showEmpty();
                } else {
                    showContent();
                }
            }
        });

        mViewModel.getAlbumCursor().observe(this, new Observer<List<Album>>() {
            @Override
            public void onChanged(@Nullable List<Album> albums) {
                if (CollectionUtil.isEmpty(albums)) {
                    return;
                }
                mAlbumsAdapter.setmSelectAlbumValue(albums.get(mViewModel.getAlbumCollection().getCurrentSelection()).getDisplayName());
                mAlbumsAdapter.setAlbums(albums);
                mAlbumsSpinner.setSelection(mViewModel.getAlbumCollection().getCurrentSelection());
                onAlbumSelected(albums.get(mViewModel.getAlbumCollection().getCurrentSelection()));
            }
        });

        onUpdate();

        boolean hasPermission = EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasPermission) {
            loadAlbums();
        } else {
            EasyPermissions.requestPermissions(this, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "sd_read_permission")
                    , PermissionRequestCode.IMAGE_PICK_PERMISSION_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void parseBundle(Intent intent, Bundle savedInstanceState) {
        if (intent != null && intent.getExtras() != null) {
            mConfig = intent.getExtras().getParcelable(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG);
        }

        if (mConfig == null && savedInstanceState != null) {
            mConfig = savedInstanceState.getParcelable(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG);
        }

        if (mConfig == null) {
            mConfig = ImagePickConfig.get();
        }
    }

    private void loadAlbums() {
        showLoading();
        mViewModel.onCreate(this, mSaveInstanceState, mConfig);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void showLoading() {
        mLoading.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
    }

    private void showContent() {
        mLoading.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);
    }


    private void showEmpty() {
        mEmptyView.showEmptyText(ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "picture_empty_title"));
        mLoading.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == UCrop.REQUEST_CROP) {
                Uri uri = data.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI);
                int width = data.getIntExtra(UCrop.EXTRA_OUTPUT_IMAGE_WIDTH, 0);
                int height = data.getIntExtra(UCrop.EXTRA_OUTPUT_IMAGE_HEIGHT, 0);
                Item item = new Item(uri, PathUtils.getPath(this, uri), MimeType.JPEG.toString(), 0, 0, width, height);
                Intent result = new Intent();
                ArrayList<Item> items = new ArrayList<>();
                items.add(item);
                result.putParcelableArrayListExtra(ImagePickConstant.EXTRA_RESULT_SELECTION_ITEMS, items);
                if (getIntent() != null) {
                    result.putExtras(getIntent());
                }
                onResultOk(result);

            } else if (requestCode == CommonRequestCode.REQUEST_IMAGE_PICK_SNAPSHOT) {

                ArrayList<Item> items = mViewModel.getSelectedCollection().asList();
                String filePath = data.getStringExtra(VideoRecorderConstant.MEDIA_RESULT_PATH);
                Uri uri = data.getData();
                int width = data.getIntExtra(VideoRecorderConstant.MEDIA_WIDTH, 0);
                int height = data.getIntExtra(VideoRecorderConstant.MEDIA_HEIGHT, 0);
                Item item = new Item(uri, filePath, MimeType.JPEG.toString(), new File(filePath).length(), 0, width, height);


                if (mConfig.onlyShowImages() && mConfig.isCutPhoto) {
                    startCrop(item.filePath);
                } else {
                    if (new File(filePath).exists()) {
                        int maxSize = mViewModel.getSelectedCollection().currentMaxSelectable(mConfig);
                        if (items.size() >= maxSize) {
                            items = new ArrayList<>(items.subList(0, maxSize - 1));
                        }
                        items.add(item);
                    }
                    Intent result = new Intent();
                    result.putParcelableArrayListExtra(ImagePickConstant.EXTRA_RESULT_SELECTION_ITEMS, items);
                    if (getIntent() != null) {
                        result.putExtras(getIntent());
                    }
                    onResultOk(result);
                }

            } else if (requestCode == CommonRequestCode.REQUEST_IMAGE_PICK_VIDEO_RECORD) {
                Intent result = new Intent();
                ArrayList<Item> items = mViewModel.getSelectedCollection().asList();
                String filePath = data.getStringExtra(VideoRecorderConstant.MEDIA_RESULT_PATH);
                Uri uri = data.getData();
                int width = data.getIntExtra(VideoRecorderConstant.MEDIA_WIDTH, 0);
                int height = data.getIntExtra(VideoRecorderConstant.MEDIA_HEIGHT, 0);
                long duration = data.getLongExtra(VideoRecorderConstant.MEDIA_DURATION, 0);
                if (new File(filePath).exists()) {
                    int maxSize = mViewModel.getSelectedCollection().currentMaxSelectable(mConfig);
                    if (items.size() >= maxSize) {
                        items = new ArrayList<>(items.subList(0, maxSize - 1));
                    }
                    Item item = new Item(uri, filePath, MimeType.MP4.toString(), new File(filePath).length(), duration, width, height);
                    items.add(item);
                }
                result.putParcelableArrayListExtra(ImagePickConstant.EXTRA_RESULT_SELECTION_ITEMS, items);
                if (getIntent() != null) {
                    result.putExtras(getIntent());
                }
                onResultOk(result);
            } else if (requestCode == CommonRequestCode.IMAGE_PICK_PREVIEW) {
                boolean isNext = data.getBooleanExtra(ImagePickConstant.EXTRA_RESULT_IS_NEXT, false);
                if (isNext) {
                    Intent result = new Intent();
                    ArrayList<Item> items = data.getParcelableArrayListExtra(SelectedItemCollection.STATE_SELECTION);
                    result.putParcelableArrayListExtra(ImagePickConstant.EXTRA_RESULT_SELECTION_ITEMS, items);
                    if (getIntent() != null) {
                        result.putExtras(getIntent());
                    }
                    onResultOk(result);
                } else {
                    mViewModel.getSelectedCollection().onCreate(data.getExtras(), mSaveInstanceState, mConfig);
                    Album album = (Album) mAlbumsAdapter.getItem(mViewModel.getAlbumCollection().getCurrentSelection());
                    onAlbumSelected(album);
                    onUpdate();
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void onResultOk(Intent result) {
        setResult(RESULT_OK, result);
        finish();
        AlbumMediaCache.getInstance().clearCache();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mViewModel.getSelectedCollection().onSaveInstanceState(outState);
        mViewModel.getAlbumCollection().onSaveInstanceState(outState);
        if (mConfig != null) {
            outState.putParcelable(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG, mConfig);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mViewModel.getAlbumCollection().setStateCurrentSelection(position);
        Album album = (Album) mAlbumsAdapter.getItem(position);
        mAlbumsAdapter.setmSelectAlbumValue(album.getDisplayName());
        mAlbumsAdapter.notifyDataSetChanged();
        onAlbumSelected(album);
    }

    private void onAlbumSelected(Album album) {
        if (!album.isAll() && album.isEmpty()) {
            showEmpty();
        } else {
            showLoading();
            mViewModel.onLoadAlbumMedias(this, album, mConfig);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onUpdate() {
        if (mViewModel.getSelectedCollection().count() > 0) {
            mPreviewView.setEnabled(true);
            mPreviewView.setTextColor(getResources().getColor(R.color.app_color));
        } else {
            mPreviewView.setEnabled(false);
            mPreviewView.setTextColor(getResources().getColor(R.color.common_text_color_afb0b1));
        }

        mConfirmBtn.setText(String.format(ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR
                , "picture_done_front_num"), mViewModel.getSelectedCollection().count(), mViewModel.getSelectedCollection().currentMaxSelectable(mConfig)));
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        boolean hasPermission = EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasPermission) {
            loadAlbums();
        } else {
            Toast.makeText(this, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_permissions_denied"), Toast.LENGTH_SHORT).show();
            showEmpty();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_permissions_denied"), Toast.LENGTH_SHORT).show();
        showEmpty();
    }

    @Override
    public void onMediaClick(int position, Item item) {
        if (item.isCapture()) {
            // 点击拍照按钮
            if (mConfig.onlyShowImages() || mViewModel.getSelectedCollection().getCollectionType() == SelectedItemCollection.COLLECTION_IMAGE) {
                startOpenCamera();
            } else if (mConfig.onlyShowVideos() || mViewModel.getSelectedCollection().getCollectionType() == SelectedItemCollection.COLLECTION_VIDEO) {
                startOpenCameraVideo();
            } else {
                PhotoPopupWindow popupWindow = new PhotoPopupWindow(this);
                popupWindow.setOnItemClickListener(this);
                popupWindow.showAsDropDown(mCommonTitleRootView);
            }

        }
//        else if (item.isImage()) {
//            Bundle bundle = new Bundle();
//            ArrayList<Item> items = mAdapter.getItems();
//            if (!CollectionUtil.isEmpty(items)) {
//                if (items.get(0).isCapture()) {
//                    items = new ArrayList<>(items.subList(1, items.size()));
//                    position--;
//                }
//            }
//
//            AlbumMediaCache.getInstance().setCacheItems(items);
//            bundle.putAll(mViewModel.getSelectedCollection().getDataWithBundle());
//            bundle.putInt(PictureConfig.EXTRA_POSITION, position);
//            bundle.putParcelable(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG, mConfig);
//            Intent intent = new Intent();
//            intent.setClass(this, PhotoSelectorPreviewActivity.class);
//            intent.putExtras(bundle);
//            this.startActivityForResult(intent, CommonRequestCode.IMAGE_PICK_PREVIEW);
//            this.overridePendingTransition(R.anim.preview_in, 0);
//
//        } else if (item.isVideo()) {
//            String path = PhotoMetadataUtils.getPath(this.getContentResolver(), item.uri);
//            Intent intent = new Intent(this, VideoPlayerActivity.class);
//            intent.putExtra(PlayerConstant.MEDIA_PLAYER_VIDEO_PATH, path);
//            intent.putExtra(PlayerConstant.MEDIA_PLAYER_VIDEO_SOURCE, PlayerConstant.VIDEO_SITE_DEFAULT);
//            this.overridePendingTransition(com.redefine.commonui.R.anim.sliding_right_in, com.redefine.commonui.R.anim.sliding_to_left_out);
//            this.startActivity(intent);
//        }
    }

    /**
     * 去裁剪
     *
     * @param originalPath
     */
    protected void startCrop(String originalPath) {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(getResources().getColor(R.color.white));
        options.setStatusBarColor(getResources().getColor(R.color.white));
        options.setToolbarWidgetColor(getResources().getColor(R.color.common_text_black_31));
        options.setCircleDimmedLayer(false);
        options.setShowCropFrame(true);
        options.setShowCropGrid(true);
        options.setCompressionQuality(100);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        boolean isHttp = WeLikeFileManager.isHttp(originalPath);
        Uri uri = isHttp ? Uri.parse(originalPath) : Uri.fromFile(new File(originalPath));
        UCrop.of(uri, Uri.fromFile(WeLikeFileManager.getTempCacheFile(System.currentTimeMillis() + "." + WeLikeFileManager.parseTmpFileSuffix(originalPath) + GlobalConfig.PUBLISH_PIC_SUFFIX)))
                .withAspectRatio(mConfig.aspectRatioX, mConfig.aspectRatioY)
                .withMaxResultSize(500, 500)
                .withOptions(options)
                .start(this);
    }

    @Override
    public void onItemClick(int position) {
        if (position == PhotoPopupWindow.SNAPSHOT) {
            startOpenCamera();
        } else if (position == PhotoPopupWindow.RECORDER) {
            startOpenCameraVideo();
        }
    }

    private void startOpenCameraVideo() {
        Intent intent = new Intent(this, VideoRecorderActivity.class);
        intent.putExtra(VideoRecorderConstant.MEDIA_TYPE, VideoRecorderConstant.TYPE_VIDEO);
        overridePendingTransition(R.anim.sliding_right_in, R.anim.sliding_to_left_out);
        startActivityForResult(intent, CommonRequestCode.REQUEST_IMAGE_PICK_VIDEO_RECORD);
    }

    private void startOpenCamera() {
        Intent intent = new Intent(this, PhotoSnapShotActivity.class);
        intent.putExtra(VideoRecorderConstant.MEDIA_TYPE, VideoRecorderConstant.TYPE_IMAGE);
        overridePendingTransition(R.anim.sliding_right_in, R.anim.sliding_to_left_out);
        startActivityForResult(intent, CommonRequestCode.REQUEST_IMAGE_PICK_SNAPSHOT);
    }
}