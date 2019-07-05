package com.redefine.multimedia.photoselector.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.activity.BaseActivity;
import com.redefine.multimedia.R;
import com.redefine.multimedia.photoselector.adapter.PreviewBottomAdapter;
import com.redefine.multimedia.photoselector.config.ImagePickConfig;
import com.redefine.multimedia.photoselector.constant.ImagePickConstant;
import com.redefine.multimedia.photoselector.entity.IncapableCause;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.photoselector.entity.MimeType;
import com.redefine.multimedia.photoselector.loader.AlbumMediaCache;
import com.redefine.multimedia.photoselector.model.SelectedItemCollection;
import com.redefine.multimedia.picturelooker.config.PictureConfig;
import com.redefine.multimedia.picturelooker.listener.OnCallBackActivity;
import com.redefine.multimedia.picturelooker.listener.OnImageLookedChangedListener;
import com.redefine.multimedia.picturelooker.widget.PicturePreView;
import com.redefine.welike.base.constant.CommonRequestCode;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PhotoSelectorPreviewActivity extends BaseActivity implements OnCallBackActivity, OnImageLookedChangedListener {

    private final ArrayList<Item> mItems = new ArrayList<>();
    private PicturePreView pictruePreView;
    private View mBackBtn;
    private TextView mTitleView;
    private int mCount;
    private TextView mCheckBtn;
    private int mCurrentPosition;
    private TextView mConfirm;
    private SelectedItemCollection mSelectedCollection;
    private ImagePickConfig mConfig;
    private View mCheckLayout;


    private RecyclerView mBottomRecyclerView;
    PreviewBottomAdapter previewBottomAdapter;

    View mTitleLayoutView;
    View mTopHolderView;
    View mRecyclerViewHolderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_selector_preview);
        parseBundle(getIntent(), savedInstanceState);
        if (mItems.size() == 0) {
            finish();
            return;
        }
        mRecyclerViewHolderView = findViewById(R.id.image_pick_preview_bottom_recycler_holder);
        mBottomRecyclerView = findViewById(R.id.image_pick_preview_bottom_recycler_view);
        mTitleLayoutView = findViewById(R.id.title_layout);
        mTopHolderView = findViewById(R.id.image_pick_preview_top_holder_view);

        pictruePreView = findViewById(R.id.image_pick_preview);
        mBackBtn = findViewById(R.id.common_back_btn);
        mTitleView = findViewById(R.id.common_title_view);
        mCheckBtn = findViewById(R.id.image_pick_check);
        mCheckLayout = findViewById(R.id.image_pick_check_layout);
        mConfirm = findViewById(R.id.photo_selector_confirm);
        mSelectedCollection = new SelectedItemCollection(this);
        mSelectedCollection.onCreate(getIntent().getExtras(), savedInstanceState, mConfig);


        previewBottomAdapter = new PreviewBottomAdapter(mSelectedCollection, mItems, new PreviewBottomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, @NotNull Item item, View ItemView) {
                moveToMiddle(position, ItemView);
                pictruePreView.bindData(mItems, position, PhotoSelectorPreviewActivity.this);
            }
        });

        mBottomRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mBottomRecyclerView.setAdapter(previewBottomAdapter);


        pictruePreView.bindData(mItems, mCurrentPosition, this);
        pictruePreView.setOnchangedListener(this);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResult(false);
            }
        });
        mCheckLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckViewClicked(mItems.get(mCurrentPosition));
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResult(true);
            }
        });

        onLookedChanged(mCurrentPosition, mItems.get(mCurrentPosition));
        notifyCheckStateChanged(mItems.get(mCurrentPosition));
    }

    private void onResult(boolean isNext) {
        Intent intent = new Intent();
        intent.putExtras(mSelectedCollection.getDataWithBundle());
        intent.putExtra(ImagePickConstant.EXTRA_RESULT_IS_NEXT, isNext);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        onResult(false);
    }

    private boolean assertAddSelection(Context context, Item item) {
        IncapableCause cause = mSelectedCollection.isAcceptable(item);
        IncapableCause.handleCause(context, cause);
        return cause == null;
    }

    public void onCheckViewClicked(Item item) {
        if (mConfig.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(item);
            if (checkedNum == -1) {
                if (assertAddSelection(this, item)) {
                    mSelectedCollection.add(item);
                    notifyCheckStateChanged(item);
                }
            } else {
                mSelectedCollection.remove(item);
                notifyCheckStateChanged(item);
            }
        } else {
            if (mSelectedCollection.isSelected(item)) {
                mSelectedCollection.remove(item);
                notifyCheckStateChanged(item);
            } else {
                if (assertAddSelection(this, item)) {
                    mSelectedCollection.add(item);
                    notifyCheckStateChanged(item);
                }
            }
        }
    }

    private void notifyCheckStateChanged(Item item) {
        setCheckStatus(item, mSelectedCollection, mConfig);
//        mConfirm.setText(String.format(ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR
//                , "picture_done_front_num"), mSelectedCollection.count(), mSelectedCollection.currentMaxSelectable(mConfig)));

    }


    private void parseBundle(Intent intent, Bundle savedInstanceState) {
        if (intent == null || intent.getExtras() == null) {
            finish();
            return;
        }
        List<Item> items = AlbumMediaCache.getInstance().getCacheItems();
        ImagePickConfig config = intent.getExtras().getParcelable(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG);
        int position = intent.getExtras().getInt(PictureConfig.EXTRA_POSITION, -1);
        if (config == null && savedInstanceState != null) {
            config = savedInstanceState.getParcelable(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG);
        }

        if (position == -1 && savedInstanceState != null) {
            position = savedInstanceState.getInt(PictureConfig.EXTRA_POSITION, 0);
        }

        if (position == -1) {
            position = 0;
        }

        mConfig = config;
        mItems.clear();
        mItems.addAll(items);
        mCount = mItems.size();
        mCurrentPosition = position;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectedCollection.onSaveInstanceState(outState);
        outState.putInt(PictureConfig.EXTRA_POSITION, mCurrentPosition);
        outState.putParcelable(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG, mConfig);
    }


    public static void launch(Context mContext, ArrayList<Item> list, int mCurrentPosition) {
        Intent intent = new Intent(mContext, PhotoSelectorPreviewActivity.class);
        Bundle bundle = new Bundle();
        AlbumMediaCache.getInstance().setCacheItems(list);
        bundle.putInt(PictureConfig.EXTRA_POSITION, mCurrentPosition);
        bundle.putParcelable(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG, ImagePickConfig.get().setMimeTypeSet(MimeType.ofAll())
                .setShowSingleMediaType(false).setIsCutPhoto(false).setCapture(true).setCountable(true));

        bundle.putParcelableArrayList(SelectedItemCollection.STATE_SELECTION, list);
        bundle.putInt(SelectedItemCollection.STATE_COLLECTION_TYPE, SelectedItemCollection.COLLECTION_IMAGE);
        intent.setClass(mContext, PhotoSelectorPreviewActivity.class);
        intent.putExtras(bundle);
        ((Activity) mContext).startActivityForResult(intent, CommonRequestCode.IMAGE_PICK_PREVIEW);
        ((Activity) mContext).overridePendingTransition(com.redefine.multimedia.R.anim.preview_in, 0);
    }



    public static void launch(Activity context, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(context, PhotoSelectorPreviewActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.preview_in, 0);
    }


    @Override
    public void onActivityBackPressed() {
        // 这个事件是预览图片的点击事件 之前的操作是返回到上一层级， 现在的界面是点击是否收起其他View
//             onResult();();
        if (isShowScreenModel) {
            showStatusView();
        } else {
            hideStatusView();
        }
    }

    boolean isShowScreenModel = false;

    private void hideStatusView() {
        isShowScreenModel = true;
        mTitleLayoutView.setVisibility(View.GONE);
        mTopHolderView.setVisibility(View.GONE);
        mBottomRecyclerView.setVisibility(View.GONE);
        mCheckLayout.setVisibility(View.GONE);
        mRecyclerViewHolderView.setVisibility(View.GONE);
    }

    private void showStatusView() {
        isShowScreenModel = false;
        mTitleLayoutView.setVisibility(View.VISIBLE);
        mTopHolderView.setVisibility(View.VISIBLE);
        mBottomRecyclerView.setVisibility(View.VISIBLE);
        mCheckLayout.setVisibility(View.VISIBLE);
        mRecyclerViewHolderView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLookedChanged(int pos, @NotNull Item item) {
        mCurrentPosition = pos;
        moveToMiddle(pos, null);
        previewBottomAdapter.setSelectPosition(mCurrentPosition);
        mTitleView.setText((pos + 1) + "/" + mCount);
        setCheckStatus(item, mSelectedCollection, mConfig);
    }

    private void setCheckStatus(Item item, SelectedItemCollection selectedCollection, ImagePickConfig config) {
        if (config.countable) {
            int checkedNum = selectedCollection.checkedNumOf(item);
            if (checkedNum > 0) {
                mCheckLayout.setSelected(true);
            } else {
                if (mSelectedCollection.maxSelectableReached(config)) {
                    mCheckLayout.setSelected(false);
                } else {
                    mCheckLayout.setSelected(false);
                }
            }
        } else {
            boolean selected = mSelectedCollection.isSelected(item);
            if (selected) {
                mCheckLayout.setSelected(true);
            } else {
                if (mSelectedCollection.maxSelectableReached(config)) {
                    mCheckLayout.setSelected(false);
                } else {
                    mCheckLayout.setSelected(false);
                }
            }
        }
    }

    /**
     * 滚动到中间位置
     *
     * @param clkView 被点击的View
     */
    public void moveToMiddle(int position, View clkView) {
        View childAt = clkView;
        if (childAt == null) {
            childAt = mBottomRecyclerView.getChildAt(position);
        }
        if (childAt == null) {
            return;
        }
        int itemWidth = childAt.getWidth();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int scrollWidth = childAt.getLeft() - (screenWidth / 2 - itemWidth / 2);
        mBottomRecyclerView.scrollBy(scrollWidth, 0);
    }
}
