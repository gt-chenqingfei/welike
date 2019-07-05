package com.redefine.welike.business.setting.ui.page;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.dialog.CommonConfirmDialog;
import com.redefine.commonui.enums.PageLoadMoreStatusEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.commonui.loadmore.bean.CommonTextHeadBean;
import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.im.ui.widget.SpaceBottomItemDecoration;
import com.redefine.welike.business.setting.ui.adapter.BlockUserAdapter;
import com.redefine.welike.business.setting.ui.vm.BlockUserViewModel;
import com.redefine.welike.business.user.management.BlockUserManager;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
@Route(path = RouteConstant.BLOCK_USER_ROUTE_PATH)
public class BlockUsersPage extends BaseActivity implements View.OnClickListener, ILoadMoreDelegate
        , OnClickRetryListener, BlockUserAdapter.OnBlockUserOpCallback
        ,BlockUserManager.BlockUserCallback, BaseErrorView.IErrorViewClickListener {

    private View mBackBtn;
    private TextView mTitleView;
    private TextView mAddUserBtn;
    private View mAddUserContainer;
    private RecyclerView mRecyclerView;
    private BlockUserAdapter mAdapter;

    private LoadingView mLoadingView;
    private ErrorView mErrorView;
    private EmptyView mEmptyView;

    private BlockUserViewModel mBlockUserViewModel;
    private LoadingDlg mLoadingDlg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.block_users_page);
        mBlockUserViewModel = ViewModelProviders.of(this).get(BlockUserViewModel.class);
        initView();
        EventBus.getDefault().register(this);
    }

    protected void initView() {
        mBackBtn = findViewById(R.id.iv_common_back);
        mTitleView = findViewById(R.id.tv_common_title);
        mAddUserContainer = findViewById(R.id.add_user_container);
        mAddUserBtn = findViewById(R.id.add_block_user);
        mRecyclerView = findViewById(R.id.block_users_list);
        mErrorView = findViewById(R.id.common_error_view);
        mEmptyView = findViewById(R.id.common_empty_view);
        mLoadingView = findViewById(R.id.common_loading_view);

        mAddUserBtn.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "add_user"));
        mEmptyView.showEmptyImageText(R.drawable.block_user_empty, ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "empty_block_users"));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mErrorView.setOnErrorViewClickListener(this);
        mEmptyView.setClickable(false);
        mRecyclerView.addItemDecoration(new SpaceBottomItemDecoration(ScreenUtils.dip2Px(1)));

        mAdapter = new BlockUserAdapter(this);
        mAdapter.setRetryLoadMoreListener(this);
        mAdapter.setHeader(new CommonTextHeadBean(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "block_user_list_title")));
        mRecyclerView.setAdapter(mAdapter);

        mBackBtn.setOnClickListener(this);
        mAddUserContainer.setOnClickListener(this);

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

        onRefresh();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(Message message) {
        if(message.what == MessageIdConstant.MESSAGE_SYNC_BLOCK_USER) {
            mBlockUserViewModel.refresh();
        }
    }

    private void onRefresh() {
        mBlockUserViewModel.refresh();
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            finish();
        } else if (v == mAddUserContainer) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_BLOCK_FOLLOWING_PAGE));
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
    public void onUnBlock(final User user) {
        if (user == null) {
            return ;
        }

        String title = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "un_block_user_confirm_title");

        CommonConfirmDialog.showConfirmDialog(this, title, new CommonConfirmDialog.IConfirmDialogListener() {
            @Override
            public void onClickCancel() {

            }

            @Override
            public void onClickConfirm() {
                mLoadingDlg = new LoadingDlg(BlockUsersPage.this);
                mLoadingDlg.show();
                BlockUserManager.getInstance().unBlock(user.getUid());
                EventLog1.BlockUser.report2(user.getUid(), EventConstants.FEED_PAGE_SETTING_BLOCK, null, null, null, null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
        EventBus.getDefault().unregister(this);
        BlockUserManager.getInstance().unregister(this);
    }

    @Override
    public void onBlockCompleted(String uid, int errCode) {

    }

    @Override
    public void onUnBlockCompleted(String uid, int errCode) {
        if(errCode == ErrorCode.ERROR_SUCCESS) {
            mBlockUserViewModel.removeBlockUser(uid);
        }

        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
    }

    @Override
    public void onErrorViewClick() {
        onRefresh();
    }
}
