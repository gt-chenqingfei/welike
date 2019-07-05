package com.redefine.welike.business.setting.ui.page;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.dialog.CommonConfirmDialog;
import com.redefine.commonui.enums.PageLoadMoreStatusEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.InputMethodUtil;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.im.ui.widget.SpaceItemDecoration;
import com.redefine.welike.business.setting.ui.adapter.BlockSearchUserAdapter;
import com.redefine.welike.business.setting.ui.vm.BlockSearchUserViewModel;
import com.redefine.welike.business.user.management.BlockUserManager;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by nianguowang on 2018/5/15
 */
@PageName("BlockSearchUserPage")
@Route(path = RouteConstant.BLOCK_SEARCH_ROUTE_PATH)
public class BlockSearchUserPage extends BaseActivity implements BaseErrorView.IErrorViewClickListener, OnClickRetryListener,
        View.OnClickListener, ILoadMoreDelegate, BlockSearchUserAdapter.OnBlockUserOpCallback, BlockUserManager.BlockUserCallback, TextView.OnEditorActionListener, TextWatcher {

    private View mBackBtn;
    private TextView mTitleView;
    private RecyclerView mRecyclerView;
    private EditText mSearchView;
    private ImageView mClearText;
    private BlockSearchUserAdapter mAdapter;

    private LoadingView mLoadingView;
    private ErrorView mErrorView;
    private EmptyView mEmptyView;

    private BlockSearchUserViewModel mBlockUserViewModel;
    private LoadingDlg mLoadingDlg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.block_search_users_page);
        mBlockUserViewModel = ViewModelProviders.of(this).get(BlockSearchUserViewModel.class);
        initView();
    }

    protected void initView() {
        mBackBtn = findViewById(R.id.common_back_btn);
        mTitleView = findViewById(R.id.common_title_view);
        mRecyclerView = findViewById(R.id.block_users_list);
        mSearchView = findViewById(R.id.search_sug_page_edit);
        mClearText = findViewById(R.id.search_sug_edit_delete_btn);
        mErrorView = findViewById(R.id.common_error_view);
        mEmptyView = findViewById(R.id.common_empty_view);
        mLoadingView = findViewById(R.id.common_loading_view);

        mSearchView.requestFocus();
        InputMethodUtil.showInputMethod(mSearchView);

        mEmptyView.showEmptyImageText(R.drawable.block_user_empty, ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "empty_block_users"));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mErrorView.setOnErrorViewClickListener(this);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dip2Px(1)));

        mAdapter = new BlockSearchUserAdapter(this);
        mAdapter.setRetryLoadMoreListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mBackBtn.setOnClickListener(this);
        mSearchView.addTextChangedListener(this);
        mSearchView.setOnEditorActionListener(this);
        mClearText.setOnClickListener(this);

        mTitleView.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "block"));

        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));

        mBlockUserViewModel.getBlockUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                if (CollectionUtil.isEmpty(users)) {
                    showEmptyView();
                } else {
                    showContentView();
                    mAdapter.setData(users);
                }
            }
        });

        mBlockUserViewModel.getPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {
                if (pageStatusEnum == null) {
                    return ;
                }
                switch (pageStatusEnum) {
                    case EMPTY:
                        showEmptyView();
                        break;
                    case ERROR:
                        showErrorView();
                        break;
                    case CONTENT:
                        showContentView();
                        break;
                    case LOADING:
                        showLoading();
                        break;
                }
            }
        });

        mBlockUserViewModel.getLoadMoreStatus().observe(this, new Observer<PageLoadMoreStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageLoadMoreStatusEnum pageLoadMoreStatusEnum) {
                if (pageLoadMoreStatusEnum == null) {
                    return ;
                }
                switch (pageLoadMoreStatusEnum) {
                    case NONE:
                        mAdapter.clearFinishFlag();
                        break;
                    case FINISH:
                        mAdapter.finishLoadMore();
                        break;
                    case LOADING:
                        mAdapter.onLoadMore();
                        break;
                    case NO_MORE:
                        mAdapter.noMore();
                        break;
                    case LOAD_ERROR:
                        mAdapter.loadError();
                        break;
                }
            }
        });

        BlockUserManager.getInstance().register(this);

    }

    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    public void showErrorView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
    }

    public void showEmptyView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    public void showContentView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    private void onRefresh() {
        mBlockUserViewModel.refresh(mSearchView.getText().toString().trim());
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            InputMethodUtil.hideInputMethod(mSearchView);
            finish();
        } else if (v == mClearText) {
            mSearchView.setText("");
        }
    }
    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mBlockUserViewModel.loadMore();
    }

    @Override
    public void onRetryLoadMore() {
        if (canLoadMore()) {
            onLoadMore();
        }
    }

    @Override
    public void onBlock(final User user) {
        if (user == null) {
            return ;
        }
        InputMethodUtil.hideInputMethod(mSearchView);

        String title = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "block_user_confirm_title");

        CommonConfirmDialog.showCancelDialog(this, String.format(title, GlobalConfig.AT + user.getNickName()), new CommonConfirmDialog.IConfirmDialogListener() {
            @Override
            public void onClickCancel() {

            }

            @Override
            public void onClickConfirm() {
                mLoadingDlg = new LoadingDlg(BlockSearchUserPage.this);
                mLoadingDlg.show();
                BlockUserManager.getInstance().block(user.getUid());
                EventLog1.BlockUser.report1(user.getUid(), EventConstants.FEED_PAGE_SETTING_BLOCK, null, null, null, null);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
        InputMethodUtil.hideInputMethod(mSearchView);
        BlockUserManager.getInstance().unregister(this);
    }

    @Override
    public void onBlockCompleted(String uid, int errCode) {
        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
        if(errCode == ErrorCode.ERROR_SUCCESS) {
            mBlockUserViewModel.removeBlockUser(uid);
            Message message = Message.obtain();
            message.what = MessageIdConstant.MESSAGE_SYNC_BLOCK_USER;
            EventBus.getDefault().post(message);
            finish();
        }
    }

    @Override
    public void onUnBlockCompleted(String uid, int errCode) {
    }

    @Override
    public void onErrorViewClick() {
        onRefresh();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            InputMethodUtil.hideInputMethod(mSearchView);
            onRefresh();
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            mClearText.setVisibility(View.VISIBLE);
            onRefresh();
        } else {
            mClearText.setVisibility(View.GONE);
        }
    }
}
