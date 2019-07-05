package com.redefine.welike.business.user.ui.page;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.constant.WebViewConstant;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.language.LocalizationManager;
import com.redefine.im.room.SESSION;
import com.redefine.welike.BuildConfig;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;
import com.redefine.welike.business.im.IMHelper;
import com.redefine.welike.business.im.ui.constant.ImConstant;
import com.redefine.welike.business.user.management.bean.DeactivateReasonBean;
import com.redefine.welike.business.user.ui.adapter.ReasonAdapter;
import com.redefine.welike.business.user.ui.listener.IReasonItemClickListener;
import com.redefine.welike.business.user.ui.vm.ReasonCallBackViewModel;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.event.RouteDispatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Created by honglin on 2018/5/8.
 */
@Route(path = RouteConstant.PATH_USER_DEACT_REASON)
public class DeactivateAccountReasonActivity extends BaseActivity {

    private ImageView ivBack;

    private TextView tvPageTitle, btnNext, tvProblem;


    private RecyclerView rvSeason;

    private ErrorView errorView;

    private LoadingView loadingView;

    private ReasonCallBackViewModel reasonViewModel;

    private ReasonAdapter adapter;

    private boolean isLock = false;

//    public DeactivateAccountReasonActivity(BaseLifecyclePageStackManager stackManager, PageConfig config, PageCache cache) {
//        super(stackManager, config, cache);
//    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.page_deactivate_reason);

        reasonViewModel = ViewModelProviders.of(this).get(ReasonCallBackViewModel.class);

        EventBus.getDefault().register(this);

        initView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {

        if (EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGOUT == event.id
                || EventIdConstant.CLEAR_STACK_LAUNCH_MAIN_MINE == event.id) {

            finish();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected void initView() {

        tvPageTitle = findViewById(R.id.tv_common_title);
        ivBack = findViewById(R.id.iv_common_back);
        btnNext = findViewById(R.id.btn_next);
        tvProblem = findViewById(R.id.tv_problem);
        rvSeason = findViewById(R.id.rv_reason);

        loadingView = findViewById(R.id.common_loading_view);
        errorView = findViewById(R.id.common_error_view);

        tvPageTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_deactivate_account"));
        btnNext.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.REGISTER, "regist_next_btn"));
        tvProblem.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_deactivate_info_2"));


        setEvent();

        setOnclick();

        reasonViewModel.getReason();

    }

    private void setEvent() {


        adapter = new ReasonAdapter();
        adapter.setClickListener(clickListener);
        rvSeason.setAdapter(adapter);

        rvSeason.setLayoutManager(new LinearLayoutManager(this));

        reasonViewModel.getmReasons().observe(this, new Observer<List<DeactivateReasonBean>>() {
            @Override
            public void onChanged(@Nullable List<DeactivateReasonBean> deactivateReasonBeans) {

                adapter.setDataList(deactivateReasonBeans);
            }
        });

        reasonViewModel.getmPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {
                if (pageStatusEnum == null) return;
                switch (pageStatusEnum) {
                    case EMPTY:
                        loadingView.setVisibility(View.GONE);
                        errorView.setVisibility(View.GONE);
                        break;
                    case ERROR:
                        loadingView.setVisibility(View.GONE);
                        errorView.setVisibility(View.VISIBLE);
                        break;
                    case CONTENT:
                        loadingView.setVisibility(View.GONE);
                        errorView.setVisibility(View.GONE);
                        break;
                    case LOADING:
                        loadingView.setVisibility(View.VISIBLE);
                        errorView.setVisibility(View.GONE);
                        break;
                }
            }
        });

    }

    private void setOnclick() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();

                bundle.putIntegerArrayList("reasonIds", adapter.getCheckedList());

                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_DEACTIVATE_RECALL_PAGE, bundle));

            }
        });
        btnNext.setClickable(false);

        errorView.setOnErrorViewClickListener(new BaseErrorView.IErrorViewClickListener() {
            @Override
            public void onErrorViewClick() {
                reasonViewModel.getReason();
            }
        });
    }


    private IReasonItemClickListener clickListener = new IReasonItemClickListener() {
        @Override
        public void onItemClick(int pos, String router) {

            if (isLock) return;

            isLock = true;

            if (pos == 3) {
                // TODO: 2018/5/17 feedback
                Account account = AccountManager.getInstance().getAccount();
                String dn = account.getUid();
                String av = BuildConfig.VERSION_NAME;
                String to = account.getAccessToken();
                String la = LocalizationManager.getInstance().getCurrentLanguage();
                String lo = account.isLogin()?"1":"0";
                String url = String.format(RouteConstant.HELPANDFEEDBACK,dn,av,to,la,lo);

                Bundle bundle = new Bundle();
                bundle.putString("url", url.toString());
                bundle.putString(WebViewConstant.KEY_FROM, DefaultUrlRedirectHandler.FROM_FEEDBACK);
                bundle.putBoolean(WebViewConstant.KEY_SHOW_TITLE,true);
                bundle.putString(WebViewConstant.KEY_TITLE_TEXT,ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_feed_back_text"));
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_WEB_VIEW, bundle));
//                IMHelper.INSTANCE.getCustomerSession(new Function1<SESSION, Unit>() {
//                    @Override
//                    public Unit invoke(final SESSION session) {
//                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
//                            @Override
//                            public void run() {
//                                Bundle bundle = new Bundle();
//                                bundle.putParcelable(ImConstant.IM_SESSION_KRY, session);
//                                bundle.putBoolean(ImConstant.IM_SESSION_CUSTOMER, true);
//                                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CHAT_EVENT, bundle));
//                            }
//                        });
//                        return null;
//                    }
//                }, new Function1<Integer, Unit>() {
//                    @Override
//                    public Unit invoke(Integer integer) {
//                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "create_chat_failed"));
//                            }
//                        });
//                        return null;
//                    }
//                });
            } else if (!TextUtils.isEmpty(router)) {
                Uri uri = Uri.parse(router);
                boolean validUri = RouteDispatcher.validUri(uri);

                if (validUri) {
//                    StartManager.getInstance().start(uri);
                    RouteDispatcher.routeDispatcher(router, DeactivateAccountReasonActivity.this);
                }
            }

            isLock = false;
        }

        @Override
        public void onItemChecked(boolean check) {

            btnNext.setClickable(check);
            if (check) {
                btnNext.setTextColor(ContextCompat.getColor(DeactivateAccountReasonActivity.this, R.color.white));
                btnNext.setBackgroundResource(R.drawable.common_appcolor_btn_bg);
            } else {
                btnNext.setTextColor(ContextCompat.getColor(DeactivateAccountReasonActivity.this, R.color.main_grey));
                btnNext.setBackgroundResource(R.drawable.common_gray_btn_bg);
            }
        }
    };

}
