package com.redefine.welike.business.startup.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.startup.management.request.ReferrerInfo;
import com.redefine.welike.business.startup.ui.viewmodel.RegisteredViewModel;
import com.redefine.welike.business.user.management.IntrestsManager;
import com.redefine.welike.business.user.management.bean.Interest;
import com.redefine.welike.business.user.ui.adapter.UserInterestAdapter2;
import com.redefine.welike.common.Life;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.commonui.widget.VipAvatar;
import com.redefine.welike.hive.AppsFlyerManager;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * @author gongguan
 * @time 2018/1/8 下午8:46
 */
public class RegistInterestsFragment extends Fragment implements IntrestsManager.IntrestsSuggesterCallback {
    public static final String REGIST_INTEREST_TAG = "ChooseInterest";
    private View mView;
    private RecyclerView recycler_interest;
    private TextView next, tv_interest_title, tv_interest_sub_title;
    private UserInterestAdapter2 registInterestAdapter;
    private IntrestsManager intrestsSuggester;
    private LoadingDlg mGetDataLoading;

    //referrer 相关。
    private View clDefault, clReferrer;
    private TextView tvReferrerName, tvReferrerToast;
    private VipAvatar ivReferrerAvator;

    private RegisteredViewModel registeredViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_regist_interests, container, false);
        registeredViewModel = ViewModelProviders.of(getActivity()).get(RegisteredViewModel.class);
        initViews();

        initEvents();

        setOnclick();

        intrestsSuggester = new IntrestsManager();
        intrestsSuggester.setListener(this);
        intrestsSuggester.refresh();
        AppsFlyerManager.getInstance().report();
        Life.registerFinish();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
//        TrackerUtil.getPageTracker().setScreenName(TrackerConstant.PAGE_USER_INTEREST_SELECT);
//        TrackerUtil.getPageTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mGetDataLoading != null) {
            mGetDataLoading.dismiss();
            mGetDataLoading = null;
        }
        intrestsSuggester = null;
    }

    private void initViews() {
        recycler_interest = mView.findViewById(R.id.recycler_regist_interest);
        next = mView.findViewById(R.id.tv_regist_interests_next);
        tv_interest_title = mView.findViewById(R.id.tv_regist_interests_title);
        tv_interest_sub_title = mView.findViewById(R.id.tv_regist_interests_sub_title);
        tvReferrerName = mView.findViewById(R.id.tv_referrer_name);
        tvReferrerToast = mView.findViewById(R.id.tv_referrer_toast);
        ivReferrerAvator = mView.findViewById(R.id.iv_referrer_icon);

        clDefault = mView.findViewById(R.id.cl_type_default);
        clReferrer = mView.findViewById(R.id.cl_type_referrer);
    }

    private void initEvents() {
        tv_interest_title.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.REGISTER, "regist_suggest_interests_title", false));
        tv_interest_sub_title.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.REGISTER, "regist_suggest_interests_sub_title", false));
        next.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.REGISTER, "regist_next_btn"));

//        FlexboxLayoutManager manager = new FlexboxLayoutManager(getContext());
//        manager.setFlexDirection(FlexDirection.ROW);
//        manager.setFlexWrap(FlexWrap.WRAP);
//        manager.setAlignItems(AlignItems.STRETCH);
        recycler_interest.setLayoutManager(new LinearLayoutManager(getContext()));

        registInterestAdapter = new UserInterestAdapter2(mView.getContext(), new Function0<Unit>() {
            @Override
            public Unit invoke() {
                setNexButtonEnable();
                return null;
            }
        });
        recycler_interest.setAdapter(registInterestAdapter);

        mGetDataLoading = new LoadingDlg(getActivity());
        mGetDataLoading.show();
    }

    private void setOnclick() {
//        registInterestAdapter.setOnRecyclerViewItemClickListener(new UserInterestAdapter.OnRecyclerViewItemClickListener() {
//            @Override
//            public void onItemClick() {
//                setNexButtonEnable();
//            }
//        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<UserBase.Intrest> list = registInterestAdapter.getSelectInterest();
                int listCount = 0;
                if (list != null && list.size() > 0) {
                    listCount = list.size();
                }
                StartEventManager.getInstance().setVertical(listCount);
                if (listCount < 2) {
                    ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.REGISTER, "regist_suggest_interests_sub_title", false));
                    return;
                }
//                TrackerUtil.getEventTracker().send(new HitBuilders.EventBuilder()
//                        .setCategory(TrackerConstant.EVENT_EV_CT)
//                        .setAction(TrackerConstant.EVENT_REGISTER_SELECT_INTER)
//                        .setValue(listCount).build());
                registeredViewModel.tryUpdateUserInterest(list);
                AppsFlyerManager.getInstance().report();
            }
        });
    }

    private void setNexButtonEnable() {
        List<UserBase.Intrest> list = registInterestAdapter.getSelectInterest();
        Context context = getContext();
        if (context != null) {
            if (list != null && list.size() < 2) {
                next.setBackgroundResource(R.drawable.common_gray_btn_bg);
                next.setTextColor(ContextCompat.getColor(context, R.color.main_grey));
            } else {
                next.setBackgroundResource(R.drawable.common_appcolor_btn_new_bg);
                next.setTextColor(ContextCompat.getColor(context, R.color.white));
            }
        }
    }

    @Override
    public void onRefreshIntrestSuggestions(ArrayList<Interest> intrests, int errCode, ReferrerInfo info) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            registInterestAdapter.setData(intrests);
            setNexButtonEnable();
            StartEventManager.getInstance().setVertical_list(intrests == null ? 0 : intrests.size());
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
        VipUtil.set(ivReferrerAvator, info.avatar, info.vip);
    }


}
