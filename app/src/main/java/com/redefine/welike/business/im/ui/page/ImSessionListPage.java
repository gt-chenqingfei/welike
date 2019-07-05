package com.redefine.welike.business.im.ui.page;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.commonui.dialog.CommonConfirmDialog;
import com.redefine.commonui.dialog.MenuItem;
import com.redefine.commonui.dialog.MenuItemIdConstant;
import com.redefine.commonui.dialog.OnMenuItemClickListener;
import com.redefine.commonui.dialog.SimpleMenuDialog;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.im.room.AccountSetting;
import com.redefine.im.room.MessageSession;
import com.redefine.im.room.SESSION;
import com.redefine.welike.R;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.vm.MessageFragmentViewModel;
import com.redefine.welike.business.im.IMHelper;
import com.redefine.welike.business.im.model.SessionModel;
import com.redefine.welike.business.im.ui.adapter.SessionListAdapter;
import com.redefine.welike.business.im.ui.holder.SessionItemViewHolder;
import com.redefine.welike.business.im.ui.vm.SessionListViewModel1;
import com.redefine.welike.business.im.ui.widget.SpaceLeftItemDecoration;
import com.redefine.welike.business.user.management.BlockUserManager;
import com.redefine.welike.frameworkmvvm.BaseLifecycleFragmentPage;
import com.redefine.welike.frameworkmvvm.BaseLifecyclePageStackManager;
import com.redefine.welike.frameworkmvvm.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Created by nianguowang on 2018/6/8
 */
@PageName("ImSessionListPage")
public class ImSessionListPage extends BaseLifecycleFragmentPage implements SessionItemViewHolder.IDeleteSessionCallback, SessionItemViewHolder.OnSessionLongClickListener, BlockUserManager.BlockUserCallback {

    private RecyclerView mRecyclerView;
    private final SessionListAdapter mAdapter;
    private SessionListViewModel1 mSessionListViewModel;
    private MessageFragmentViewModel mMessageViewModel;
    private LoadingDlg mLoadingDlg;

    public ImSessionListPage(BaseLifecyclePageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
        mAdapter = new SessionListAdapter(stackManager);
        mAdapter.setSessionDeleteCallback(this);
        mAdapter.setSessionLongClickListener(this);
        BlockUserManager.getInstance().register(this);
        mSessionListViewModel = ViewModelProviders.of(this).get(SessionListViewModel1.class);
        mMessageViewModel = ViewModelProviders.of(this).get(MessageFragmentViewModel.class);
    }

    @NonNull
    @Override
    protected View createView(ViewGroup container, Bundle saveState) {
        return LayoutInflater.from(container.getContext()).inflate(R.layout.im_session_list_layout, null);
    }

    @Override
    protected void initView(ViewGroup container, Bundle saveState) {
        super.initView(container, saveState);
        mRecyclerView = getView().findViewById(R.id.im_session_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        SpaceLeftItemDecoration decoration = new SpaceLeftItemDecoration(container.getContext(), DividerItemDecoration.VERTICAL
                , ScreenUtils.dip2Px(container.getContext(), 74.5f));
        decoration.setDrawable(container.getResources().getDrawable(R.drawable.chat_item_divider_1dp));
        mRecyclerView.addItemDecoration(decoration);
        if (mRecyclerView.getItemAnimator() != null) {
            mRecyclerView.getItemAnimator().setChangeDuration(0);
            mRecyclerView.getItemAnimator().setAddDuration(0);
            mRecyclerView.getItemAnimator().setMoveDuration(0);
            mRecyclerView.getItemAnimator().setRemoveDuration(0);
        }

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                if (mAdapter != null) {
                    mAdapter.playAnimation();
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });
        mSessionListViewModel.loadAll(new SessionListViewModel1.OnSessionLoadListener() {
            @Override
            public void onSessionLoaded(LiveData<List<MessageSession>> sessionLiveData) {
                sessionLiveData.observe(ImSessionListPage.this, new Observer<List<MessageSession>>() {
                    @Override
                    public void onChanged(@Nullable final List<MessageSession> sessions) {
//                        mAdapter.setData(mSessionListViewModel.filterStrangeSessions(sessions));
                        mSessionListViewModel.updateSingleSessions(mSessionListViewModel.filterStrangeSessions(sessions));
                    }
                });
            }
        });
        mMessageViewModel.loadMessageCount(new Function1<LiveData<AccountSetting>, Unit>() {
            @Override
            public Unit invoke(LiveData<AccountSetting> accountSettingLiveData) {
                accountSettingLiveData.observe(ImSessionListPage.this, new Observer<AccountSetting>() {
                    @Override
                    public void onChanged(@Nullable AccountSetting accountSetting) {
                        if (accountSetting != null) {
                            Integer commentCount = accountSetting.getCommentCount();
                            Integer likeCount = accountSetting.getLikeCount();
                            Integer mentionCount = accountSetting.getMentionCount();
                           // Integer pushCount = accountSetting.getPushCount();
                            mSessionListViewModel.updateMCLSessions(mentionCount, commentCount, likeCount,0);
                        }
                    }
                });
                return null;
            }
        });
        mSessionListViewModel.getSessionLiveData().observe(this, new Observer<List<SessionModel>>() {
            @Override
            public void onChanged(@Nullable List<SessionModel> sessionModels) {
                mAdapter.setData(sessionModels);
            }
        });

    }

    @Override
    public void destroy(ViewGroup container) {
        super.destroy(container);
        BlockUserManager.getInstance().unregister(this);
    }

    @Override
    public void onDeleteSession(MessageSession imSession) {
        IMHelper.INSTANCE.removeSession(imSession.getSSid());
    }

    @Override
    public void onSessionLongClick(final View v, final MessageSession imSession) {
        List<MenuItem> list = new ArrayList<>();
        MenuItem nickNameItem = new MenuItem(MenuItemIdConstant.MENU_ITEM_NICK_NAME
                , GlobalConfig.AT + imSession.getSName(), false
                , v.getContext().getResources().getColor(com.redefine.welike.R.color.common_text_color_afb0b1));
        MenuItem blockItem = new MenuItem(MenuItemIdConstant.MENU_ITEM_BLOCK
                , ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "block") + GlobalConfig.AT + imSession.getSName());
        MenuItem deleteItem = new MenuItem(MenuItemIdConstant.MENU_ITEM_DELETE
                , ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_delete_confirm")
                , v.getContext().getResources().getColor(com.redefine.welike.R.color.common_menu_delete_text_color));

        list.add(nickNameItem);
        list.add(blockItem);
        list.add(deleteItem);
        SimpleMenuDialog.show(v.getContext(), list, new OnMenuItemClickListener() {
            @Override
            public void onMenuClick(MenuItem menuItem) {
                if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_DELETE) {
                    onDeleteSession(imSession);
                } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_BLOCK) {
                    doBlockUser(v.getContext(), imSession);
                }
            }
        });
    }

    private void doBlockUser(final Context context, final MessageSession imSession) {
        String title = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "block_user_confirm_title");

        CommonConfirmDialog.showCancelDialog(context, String.format(title, GlobalConfig.AT + imSession.getSName()), new CommonConfirmDialog.IConfirmDialogListener() {
            @Override
            public void onClickCancel() {

            }

            @Override
            public void onClickConfirm() {
                mLoadingDlg = new LoadingDlg((Activity) context);
                mLoadingDlg.show();
                IMHelper.INSTANCE.getSession(imSession.getSSid(), new Function1<SESSION, Unit>() {
                    @Override
                    public Unit invoke(final SESSION session) {
                        BlockUserManager.getInstance().block(session.getSessionUid());
                        return null;
                    }
                });
            }
        });
    }

    @Override
    public void onBlockCompleted(String uid, int errCode) {
        if(mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
    }

    @Override
    public void onUnBlockCompleted(String uid, int errCode) {
        if(mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
    }
}
