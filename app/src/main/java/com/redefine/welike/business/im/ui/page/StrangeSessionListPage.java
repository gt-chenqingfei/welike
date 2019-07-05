package com.redefine.welike.business.im.ui.page;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.dialog.CommonConfirmDialog;
import com.redefine.commonui.dialog.MenuItem;
import com.redefine.commonui.dialog.MenuItemIdConstant;
import com.redefine.commonui.dialog.OnMenuItemClickListener;
import com.redefine.commonui.dialog.SimpleMenuDialog;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.im.room.MessageSession;
import com.redefine.im.room.SESSION;
import com.redefine.welike.R;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.im.IMHelper;
import com.redefine.welike.business.im.ui.adapter.StrangerSessionListAdapter;
import com.redefine.welike.business.im.ui.holder.SessionGreetItemViewHolder;
import com.redefine.welike.business.im.ui.vm.SessionListViewModel;
import com.redefine.welike.business.im.ui.widget.SpaceLeftItemDecoration;
import com.redefine.welike.business.user.management.BlockUserManager;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Created by nianguowang on 2018/6/8
 */
@Route(path = RouteConstant.STRANGE_SESSION_ROUTE_PATH)
public class StrangeSessionListPage extends BaseActivity implements View.OnClickListener, SessionGreetItemViewHolder.IDeleteSessionCallback, SessionGreetItemViewHolder.OnSessionLongClickListener, BlockUserManager.BlockUserCallback {

    private StrangerSessionListAdapter mAdapter;
    private LoadingDlg mLoadingDlg;

    private SessionListViewModel mSessionViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_strange_session_list_layout);

        mAdapter = new StrangerSessionListAdapter();
        mAdapter.setSessionDeleteCallback(this);
        mAdapter.setSessionLongClickListener(this);
        BlockUserManager.getInstance().register(this);
        mSessionViewModel = ViewModelProviders.of(this).get(SessionListViewModel.class);

        initView();
    }

    private void initView() {
        View backBtn = findViewById(R.id.common_back_btn);
        TextView titleView = findViewById(R.id.common_title_view);
        RecyclerView recyclerView = findViewById(R.id.im_session_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        SpaceLeftItemDecoration decoration = new SpaceLeftItemDecoration(this, DividerItemDecoration.VERTICAL
                , ScreenUtils.dip2Px(this, 74.5f));
        decoration.setDrawable(getResources().getDrawable(R.drawable.chat_item_divider_1dp));
        recyclerView.addItemDecoration(decoration);
        if (recyclerView.getItemAnimator() != null) {
            recyclerView.getItemAnimator().setChangeDuration(0);
            recyclerView.getItemAnimator().setAddDuration(0);
            recyclerView.getItemAnimator().setMoveDuration(0);
            recyclerView.getItemAnimator().setRemoveDuration(0);
        }

        backBtn.setOnClickListener(this);
        titleView.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "session_greetings"));

        recyclerView.setAdapter(mAdapter);
        mSessionViewModel.loadSessions(new SessionListViewModel.OnSessionLoadListener() {
            @Override
            public void onSessionLoaded(LiveData<List<MessageSession>> sessionLiveData) {
                sessionLiveData.observe(StrangeSessionListPage.this, new Observer<List<MessageSession>>() {
                    @Override
                    public void onChanged(@Nullable final List<MessageSession> sessions) {
                        mAdapter.setData(mSessionViewModel.filterUnStrangeSessions(sessions));
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
