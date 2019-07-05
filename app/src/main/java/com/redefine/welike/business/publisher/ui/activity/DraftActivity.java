package com.redefine.welike.business.publisher.ui.activity;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.dialog.CommonConfirmDialog;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.publisher.management.DraftManager;
import com.redefine.welike.business.publisher.management.bean.DraftBase;
import com.redefine.welike.business.publisher.management.cache.DraftCacheWrapper;
import com.redefine.welike.business.publisher.room.Draft;
import com.redefine.welike.business.publisher.ui.adapter.DraftAdapter;
import com.redefine.welike.business.publisher.viewmodel.DraftViewModel;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Created by liwenbo on 2018/3/19.
 */

public class DraftActivity extends BaseActivity implements View.OnClickListener {
    private View mBackBtn;
    private TextView mTitleView;
    private RecyclerView mRecyclerView;
    private EmptyView mEmptyView;
    private View mClearView;
    private DraftAdapter mDraftAdapter;
    private DraftViewModel mDraftViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draft_layout);
        mDraftAdapter = new DraftAdapter();
        mBackBtn = findViewById(R.id.common_back_btn);
        mTitleView = findViewById(R.id.common_title_view);
        mClearView = findViewById(R.id.common_clear_btn);
        mRecyclerView = findViewById(R.id.draft_list);
        mEmptyView = findViewById(R.id.common_empty_view);

        mRecyclerView.setAdapter(mDraftAdapter);
        mTitleView.setText(R.string.publish_draft_title);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mBackBtn.setOnClickListener(this);
        mClearView.setOnClickListener(this);

        mEmptyView.showEmptyImageText(R.drawable.ic_common_empty, getString(R.string.draft_empty_text));
        registerObserver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            finish();
            overridePendingTransition(R.anim.sliding_to_left_in, R.anim.sliding_right_out);
        } else if (v == mClearView) {
            clear();
        }
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, DraftActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.sliding_right_in, R.anim.sliding_to_left_out);
    }

    public void clear() {
        CommonConfirmDialog.showCancelDialog(this
                , ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "clear_draft_title")
                , ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_cancel")
                , ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_ok")
                , new CommonConfirmDialog.IConfirmDialogListener() {

                    @Override
                    public void onClickCancel() {

                    }

                    @Override
                    public void onClickConfirm() {
                        DraftManager.getInstance().clearAll();
                    }
                });
    }


    private void registerObserver() {
        mDraftViewModel = ViewModelProviders.of(this).get(DraftViewModel.class);
        mDraftViewModel.getDraftList(new Function1<LiveData<List<Draft>>, Unit>() {
            @Override
            public Unit invoke(LiveData<List<Draft>> liveData) {
                liveData.observe(DraftActivity.this, new Observer<List<Draft>>() {
                    @Override
                    public void onChanged(@Nullable List<Draft> drafts) {

                        final List<DraftBase> convertList = new ArrayList();

                        for (Draft draft : drafts) {
                            DraftBase item = DraftCacheWrapper.Companion.convertDraftFromLocal(draft);
                            convertList.add(item);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (CollectionUtil.isEmpty(convertList)) {
                                    mRecyclerView.setVisibility(View.INVISIBLE);
                                    mEmptyView.setVisibility(View.VISIBLE);

                                } else {
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    mEmptyView.setVisibility(View.INVISIBLE);
                                    mDraftAdapter.setDrafts(convertList);
                                }
                            }
                        });

                    }
                });
                return null;
            }
        });
    }
}
