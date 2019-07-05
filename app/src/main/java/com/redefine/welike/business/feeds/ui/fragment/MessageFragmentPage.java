package com.redefine.welike.business.feeds.ui.fragment;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BasePage;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.commonui.dialog.CommonConfirmDialog;
import com.redefine.commonui.dialog.MenuItem;
import com.redefine.commonui.dialog.MenuItemIdConstant;
import com.redefine.commonui.dialog.OnMenuItemClickListener;
import com.redefine.commonui.dialog.SimpleMenuDialog;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.im.room.AccountSetting;
import com.redefine.im.room.MessageSession;
import com.redefine.im.room.SESSION;
import com.redefine.welike.R;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
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
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.manager.IMEventManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 18/1/8
 * <p>
 * Description : TODO
 * <p>
 * Creation    : 18/1/8
 * Author      : liwenbo0328@163.com
 * History     : Creation, 18/1/8, liwenbo, Create the file
 * ****************************************************************************
 */
@PageName("MessageFragmentPage")
public class MessageFragmentPage extends BaseLifecycleFragmentPage implements SessionItemViewHolder.IDeleteSessionCallback, SessionItemViewHolder.OnSessionLongClickListener, BlockUserManager.BlockUserCallback {
    public static final String TAG = "message_page";
    private TextView mMainTitleView;

    private RecyclerView mRecyclerView;
    private final SessionListAdapter mAdapter;
    private SessionListViewModel1 mSessionListViewModel;
    private MessageFragmentViewModel mMessageViewModel;
    private LoadingDlg mLoadingDlg;

    public MessageFragmentPage(BaseLifecyclePageStackManager stackManager, PageConfig config, PageCache cache) {
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
        View view = mPageStackManager.getLayoutInflater().inflate(R.layout.main_message_fragment, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mMainTitleView = view.findViewById(R.id.common_title_view);
        mMainTitleView.setText(ResourceTool.getString("main_tab_message"));

        view.findViewById(R.id.common_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("from", EventLog1.AddFriend.ButtonFrom.MESSAGE);
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CONTACT, bundle));
            }
        });

        mRecyclerView = view.findViewById(R.id.im_session_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        SpaceLeftItemDecoration decoration = new SpaceLeftItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL
                , ScreenUtils.dip2Px(view.getContext(), 74.5f));
        decoration.setDrawable(view.getResources().getDrawable(R.drawable.chat_item_divider_1dp));
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
                sessionLiveData.observe(MessageFragmentPage.this, new Observer<List<MessageSession>>() {
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
                accountSettingLiveData.observe(MessageFragmentPage.this, new Observer<AccountSetting>() {
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
    public void attach(ViewGroup container) {
        super.attach(container);
//        TrackerUtil.getPageTracker().setScreenName(TrackerConstant.PAGE_MAIN_MESSAGE);
//        TrackerUtil.getPageTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void detach(ViewGroup container) {
        super.detach(container);
    }

    @Override
    public void destroy(ViewGroup container) {
        super.destroy(container);
        BlockUserManager.getInstance().unregister(this);
    }

    @Override
    public void onPageStateChanged(int oldPageState, int pageState) {
        super.onPageStateChanged(oldPageState, pageState);
        if (pageState == BasePage.PAGE_STATE_SHOW) {
            IMEventManager.INSTANCE.report1();
        }
    }

    @Override
    public void onDeleteSession(MessageSession imSession) {
        IMHelper.INSTANCE.removeSession(imSession.getSSid());
    }

    @Override
    public void onSessionLongClick(final View v, final MessageSession imSession) {

        if (AccountManager.getInstance().getAccount() == null ||
                AccountManager.getInstance().getAccount().getStatus() == Account.ACCOUNT_HALF) {
            return;
        }

        List<MenuItem> list = new ArrayList<>();
        MenuItem nickNameItem = new MenuItem(MenuItemIdConstant.MENU_ITEM_NICK_NAME
                , GlobalConfig.AT + imSession.getSName(), false
                , v.getContext().getResources().getColor(com.redefine.welike.R.color.common_text_color_afb0b1));
        MenuItem blockItem = new MenuItem(MenuItemIdConstant.MENU_ITEM_BLOCK
                , ResourceTool.getString( "block") + GlobalConfig.AT + imSession.getSName());
        MenuItem deleteItem = new MenuItem(MenuItemIdConstant.MENU_ITEM_DELETE
                , ResourceTool.getString("feed_delete_confirm")
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
        String title = ResourceTool.getString("block_user_confirm_title");

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
