package com.redefine.welike.business.feeds.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.base.track.TrackerUtil;
import com.redefine.welike.business.feeds.ui.contract.IMainHomeContract;
import com.redefine.welike.business.feeds.ui.listener.OnRequestPermissionCallback;
import com.redefine.welike.commonui.framework.PageStackManager;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * @author redefine honlin
 * @Date on 2018/10/25
 * @Description page ---- 》 fragment
 */

public class FollowFeedFragment extends Fragment implements OnRequestPermissionCallback, EasyPermissions.PermissionCallbacks {
    public static final String TAG = "home_page";
    private IMainHomeContract.IMainHomePresenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = IMainHomeContract.IMainHomeFactory.createPresenter(new PageStackManager(getActivity()));
        mPresenter.setRequestPermissionCallback(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPresenter.onCreate(savedInstanceState, savedInstanceState);
        View view = mPresenter.createView(inflater, container, savedInstanceState);
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                if (mPresenter != null) {
                    mPresenter.attach();
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (mPresenter != null) {
                    mPresenter.detach();
                }
            }
        });
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mPresenter == null) {
            return;
        }
        if (isVisibleToUser) {
            mPresenter.onFragmentShow();
        } else {
            mPresenter.onFragmentHide();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();

    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onActivityPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onActivityResume();
    }


    public void refresh() {
        if (mPresenter != null)
            mPresenter.autoRefresh();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onRequestPermission(@NotNull String message, int requestCode, @NotNull String permission) {
        EasyPermissions.requestPermissions(this, message, requestCode, permission);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("from", EventLog1.AddFriend.ButtonFrom.OTHER);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CONTACT, bundle));
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //do nothing
    }

}
