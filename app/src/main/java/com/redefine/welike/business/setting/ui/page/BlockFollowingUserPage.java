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
import com.pekingese.pagestack.framework.page.PageName;
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
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.im.ui.widget.SpaceBottomItemDecoration;
import com.redefine.welike.business.setting.ui.adapter.BlockFollowingUserAdapter;
import com.redefine.welike.business.setting.ui.vm.BlockFollowingUserViewModel;
import com.redefine.welike.business.user.management.BlockUserManager;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by nianguowang on 2018/5/15
 */
@PageName("BlockFollowingUserPage")
@Route(path = RouteConstant.BLOCK_FOLLOW_ROUTE_PATH)
public class BlockFollowingUserPage extends BaseActivity implements BaseErrorView.IErrorViewClickListener,
        OnClickRetryListener, ILoadMoreDelegate,  View.OnClickListener,
         BlockFollowingUserAdapter.OnBlockUserOpCallback, BlockUserManager.BlockUserCallback {

    private View mBackBtn;
    private TextView mTitleView;
    private RecyclerView mRecyclerView;
    private TextView mSearchUser;
    private BlockFollowingUserAdapter mAdapter;

    private LoadingView mLoadingView;
    private ErrorView mErrorView;
    private EmptyView mEmptyView;

    private BlockFollowingUserViewModel mBlockUserViewModel;
    private LoadingDlg mLoadingDlg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.block_following_users_page);
        mBlockUserViewModel = ViewModelProviders.of(this).get(BlockFollowingUserViewModel.class);
        initView();
    }

    protected void initView() {
        mBackBtn = findViewById(R.id.common_back_btn);
        mTitleView = findViewById(R.id.common_title_view);
        mRecyclerView = findViewById(R.id.block_users_list);
        mSearchUser = findViewById(R.id.block_user_search_title);
        mErrorView = findViewById(R.id.common_error_view);
        mEmptyView = findViewById(R.id.common_empty_view);
        mLoadingView = findViewById(R.id.common_loading_view);

        mSearchUser.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.SEARCH, "discover_search_default"));
        mEmptyView.showEmptyImageText(R.drawable.block_user_empty, ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "empty_block_users"));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mErrorView.setOnErrorViewClickListener(this);
        mRecyclerView.addItemDecoration(new SpaceBottomItemDecoration(ScreenUtils.dip2Px(1)));

        mAdapter = new BlockFollowingUserAdapter(this);
        mAdapter.setHeader(new CommonTextHeadBean(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "block_following_user")));
        mAdapter.setRetryLoadMoreListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mBackBtn.setOnClickListener(this);
        mSearchUser.setOnClickListener(this);

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

    private void onRefresh() {
        mBlockUserViewModel.refresh();
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            finish();
        } else if (v == mSearchUser) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_BLOCK_SEARCH_PAGE));
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

        String title = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "block_user_confirm_title");

        CommonConfirmDialog.showCancelDialog(this, String.format(title, GlobalConfig.AT + user.getNickName()), new CommonConfirmDialog.IConfirmDialogListener() {
            @Override
            public void onClickCancel() {

            }

            @Override
            public void onClickConfirm() {
                mLoadingDlg = new LoadingDlg(BlockFollowingUserPage.this);
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

}
