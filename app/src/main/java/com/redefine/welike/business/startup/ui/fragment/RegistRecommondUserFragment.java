package com.redefine.welike.business.startup.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.startup.management.UsersSuggester;
import com.redefine.welike.business.startup.management.bean.RecommondUser;
import com.redefine.welike.business.startup.management.request.ReferrerInfo;
import com.redefine.welike.business.startup.ui.adapter.RegisterRecommondUserAdapter2;
import com.redefine.welike.business.startup.ui.viewmodel.RegisteredViewModel;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.widget.VipAvatar;
import com.redefine.welike.hive.AppsFlyerManager;

import java.util.List;

/**
 * Created by gongguan on 2018/2/7.
 */

public class RegistRecommondUserFragment extends Fragment implements UsersSuggester.UsersSuggesterCallback {
    public static final String RECOMMOND_USER_FRAGMENT_TAG = "recommond_user_fragment";
    private RecyclerView mRecycler;
    private TextView next, tv_recommondUser_title, tv_regist_recommond_sub_title;
    private RegisterRecommondUserAdapter2 registRecommondUserAdapter;
    private UsersSuggester usersSuggester;
    private LoadingDlg mGetDataLoading;
    //referrer 相关。
    private View clDefault, clReferrer;
    private TextView tvReferrerName, tvReferrerToast;
    private VipAvatar ivReferrerAvator;

    private RegisteredViewModel registeredViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regist_recommond_user, container, false);
        registeredViewModel = ViewModelProviders.of(getActivity()).get(RegisteredViewModel.class);
        next = view.findViewById(R.id.tv_regist_recommond_next);
        tvReferrerName = view.findViewById(R.id.tv_referrer_name);
        tvReferrerToast = view.findViewById(R.id.tv_referrer_toast);
        ivReferrerAvator = view.findViewById(R.id.iv_referrer_icon);

        clDefault = view.findViewById(R.id.cl_type_default);
        clReferrer = view.findViewById(R.id.cl_type_referrer);

        tv_recommondUser_title = view.findViewById(R.id.tv_regist_interests_title);
        tv_regist_recommond_sub_title = view.findViewById(R.id.tv_regist_interests_sub_title);
        tv_recommondUser_title.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.REGISTER, "regist_suggest_persons_title"));
        tv_regist_recommond_sub_title.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.REGISTER, "regist_suggest_persons_title_sub"));

        next.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.REGISTER, "regist_jion_weLike"));

        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                List<String> selectedUids = registRecommondUserAdapter.getSelectedUids();
                registeredViewModel.tryFollowUsers(selectedUids);
                AppsFlyerManager.getInstance().report();

                StartEventManager.getInstance().setFollowing(selectedUids == null ? 0 : selectedUids.size());
                StartEventManager.getInstance().setFollow_list(registRecommondUserAdapter.getAllUsersCount());
            }
        });

        mRecycler = view.findViewById(R.id.lv_regist_recommond_user);

        InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im != null) {
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        LinearLayoutManager layoutParams = new LinearLayoutManager(getActivity());
        layoutParams.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(layoutParams);

        registRecommondUserAdapter = new RegisterRecommondUserAdapter2();
        mRecycler.setAdapter(registRecommondUserAdapter);

        mGetDataLoading = new LoadingDlg(getActivity());
        mGetDataLoading.show();

        usersSuggester = new UsersSuggester();
        usersSuggester.setListener(this);
        usersSuggester.refresh();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        TrackerUtil.getPageTracker().setScreenName(TrackerConstant.PAGE_USER_RECOMMEND_USER);
//        TrackerUtil.getPageTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        if (mGetDataLoading != null) {
            mGetDataLoading.dismiss();
            mGetDataLoading = null;
        }
        usersSuggester = null;
    }

    @Override
    public void onRefreshUserSuggestions(List<RecommondUser> users, int errCode, ReferrerInfo info) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            registRecommondUserAdapter.setData(users);
            if (mGetDataLoading != null) {
                mGetDataLoading.dismiss();
            }
            if (info != null) {
                setReferrerView(info);
            }
        } else {
            if (mGetDataLoading != null) {
                mGetDataLoading.dismiss();
            }
        }
    }

    private void setReferrerView(ReferrerInfo info) {
        clDefault.setVisibility(View.GONE);
        clReferrer.setVisibility(View.VISIBLE);
        tvReferrerName.setText(info.name);
        tvReferrerToast.setText(info.toast);
//        HeadUrlLoader.getInstance().loadHeaderUrl(ivReferrerAvator, info.avatar);
        VipUtil.set(ivReferrerAvator, info.avatar, info.vip);
    }

    @Override
    public void onHisUserSuggestions(List<RecommondUser> users, boolean last, int errCode) {
    }
}
