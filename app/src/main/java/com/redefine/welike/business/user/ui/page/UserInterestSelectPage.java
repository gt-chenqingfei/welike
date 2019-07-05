package com.redefine.welike.business.user.ui.page;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.management.bean.Interest;
import com.redefine.welike.business.user.ui.adapter.UserInterestAdapter;
import com.redefine.welike.business.user.ui.constant.UserConstant;
import com.redefine.welike.business.user.ui.vm.EditInterestViewModel;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.statistical.EventLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by honglin on 2018/6/29.
 */
@Route(path = RouteConstant.EDIT_INTEREST_ROUTE_PATH)
public class UserInterestSelectPage extends BaseActivity implements AccountManager.AccountCallback {

    private RecyclerView recyclerInterest;
    private TextView mNextBtn, tvInterestTitle, tvInterestSubTitle;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;
    private View clReferrer, commonActionBar;
    private TextView mTitleView;
    private View mBackBtn;

    private UserInterestAdapter interestAdapter;

    private LoadingDlg mLoadingDlg;
    private int type;
    private int from;

    private EditInterestViewModel viewModel;

    private boolean hasChanged = false;


    /**
     * launch current page.
     *
     * @param from 1 引导选兴趣横条，2 个人信息页点select your interest，3 profile页点edit
     */
    public static void launch(int from) {
        launch(from, UserConstant.USER_INTEREST_TYPE_PROFILE);
    }


    public static void launch(int from, int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("from_type", type);
        bundle.putInt("source", from);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_USER_INTEREST, bundle));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_select_interest);
        
        parseBundle();
        initView();
        setViewEvent();
        setEvent();
        AccountManager.getInstance().register(this);
        EventLog.InterestPage.report1(from);
    }

    private void parseBundle() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        type = extras.getInt("from_type", UserConstant.USER_INTEREST_TYPE_PROFILE);
        from = extras.getInt("source", 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AccountManager.getInstance().unregister(this);
    }

    private void initView() {

        clReferrer = findViewById(R.id.referrer_top);
        commonActionBar = findViewById(R.id.common_action_bar);
        tvInterestTitle = findViewById(R.id.tv_regist_interests_title);
        tvInterestSubTitle = findViewById(R.id.tv_regist_interests_sub_title);
        mTitleView = findViewById(R.id.tv_common_title);
        mBackBtn = findViewById(R.id.iv_common_back);
        mNextBtn = findViewById(R.id.tv_common_interests_next);
        recyclerInterest = findViewById(R.id.recycler_common_interest);
        mErrorView = findViewById(R.id.common_error_view);
        mLoadingView = findViewById(R.id.common_loading_view);
    }

    private void setViewEvent() {

        if (type == UserConstant.USER_INTEREST_TYPE_PROFILE) {
            clReferrer.setVisibility(View.GONE);
            commonActionBar.setVisibility(View.VISIBLE);
            mTitleView.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "select_interest"));
            mNextBtn.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_user_host_personal_edit_name_save"));
        } else {
            clReferrer.setVisibility(View.VISIBLE);
            commonActionBar.setVisibility(View.GONE);
            tvInterestTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.REGISTER, "regist_suggest_interests_title", false));
            tvInterestSubTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.REGISTER, "regist_suggest_interests_sub_title", false));
            mNextBtn.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.REGISTER, "regist_next_btn"));
        }

        FlexboxLayoutManager manager = new FlexboxLayoutManager(this);
        manager.setFlexDirection(FlexDirection.ROW);
        manager.setFlexWrap(FlexWrap.WRAP);
        manager.setAlignItems(AlignItems.STRETCH);
        recyclerInterest.setLayoutManager(manager);
        interestAdapter = new UserInterestAdapter(this);
        recyclerInterest.setAdapter(interestAdapter);
        interestAdapter.setOnRecyclerViewItemClickListener(new UserInterestAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick() {
                hasChanged = true;
            }
        });
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mPageStackManager.onBackPressed();
                EventLog.InterestPage.report3(from);
                if (hasChanged)
                    doSave();
                else finish();
            }
        });


        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventLog.InterestPage.report2(from);
                if (hasChanged)
                    doSave();
                else finish();
            }
        });

        mErrorView.setOnErrorViewClickListener(new BaseErrorView.IErrorViewClickListener() {
            @Override
            public void onErrorViewClick() {
                viewModel.getInterest();
            }
        });
    }

    private void setEvent() {
        viewModel = ViewModelProviders.of(this).get(EditInterestViewModel.class);
        viewModel.getInterest();

        viewModel.getmPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {
                if (pageStatusEnum == null) {
                    return;
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

        viewModel.getmInterst().observe(this, new Observer<ArrayList<Interest>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Interest> intrests) {
                interestAdapter.setItems(intrests);
            }
        });


    }

    private void doSave() {
        List<UserBase.Intrest> list = interestAdapter.getSelectInterest();
        if (list.size() < 1) {
            ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_interest_info_title"));
        } else {


            mLoadingDlg = new LoadingDlg(this);
            mLoadingDlg.show();
            if (type == UserConstant.USER_INTEREST_TYPE_PROFILE) {

//                Account account = AccountManager.getInstance().getAccount().copy();
//                account.setIntrests(list);
//                AccountManager.getInstance().modifyAccount(account);

                AccountManager.getInstance().modifyAccountInterest(list);
            } else {

            }
        }
    }

    private void showEmptyView() {
        mErrorView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.INVISIBLE);
    }

    private void showErrorView() {
        mErrorView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.INVISIBLE);
    }

    private void showContentView() {
        mErrorView.setVisibility(View.INVISIBLE);
        mLoadingView.setVisibility(View.INVISIBLE);
    }

    private void showLoading() {

        mErrorView.setVisibility(View.INVISIBLE);
        mLoadingView.setVisibility(View.VISIBLE);

    }


    @Override
    public void onModified() {

        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "select_interest_toast"));
            }
        });

        finish();
        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
    }

    @Override
    public void onModifyFailed(int errCode) {
        ToastUtils.showShort(ErrorCode.showErrCodeText(errCode));
        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
    }

}
