package com.redefine.welike.business.user.ui.page;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.fresco.loader.HeadUrlLoader;
import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.management.bean.DeactivateInfoBean;
import com.redefine.welike.business.user.ui.vm.FinalCallBackViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by honglin on 2018/5/16.
 */
@Route(path = RouteConstant.PATH_USER_DEACT_FINAL)
public class DeactivateFinalCallBackActivity extends BaseActivity {


    private ImageView ivBack;

    private TextView tvPageTitle, btnNext, tvLater, tvFollower, tvPost;

    private TextView tvRecallInfo0, tvRecallInfo1;

    private LinearLayout llLinkUser, llContent, llPage;

    private ErrorView errorView;

    private LoadingView loadingView;

    private FinalCallBackViewModel finalCallBackViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.page_deactivate_recall);
        finalCallBackViewModel = ViewModelProviders.of(this).get(FinalCallBackViewModel.class);
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
        llContent = findViewById(R.id.ll_content);
        llLinkUser = findViewById(R.id.ll_link_user);
        tvLater = findViewById(R.id.btn_later);
        tvFollower = findViewById(R.id.tv_follow);
        tvPost = findViewById(R.id.tv_post);
        tvRecallInfo0 = findViewById(R.id.tv_recall_info0);
        tvRecallInfo1 = findViewById(R.id.tv_recall_info1);
        llPage = findViewById(R.id.ll_page);
        tvPageTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_deactivate_account"));
        btnNext.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_continue"));
        tvLater.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "later"));
        tvRecallInfo1.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_deactivate_info_4"));

        loadingView = findViewById(R.id.common_loading_view);
        errorView = findViewById(R.id.common_error_view);

        setEvent();

        setOnclick();

        finalCallBackViewModel.getUserInfo();

    }

    private void setEvent() {

        finalCallBackViewModel.getDeactivateInfoBeanMutableLiveData().observe(this, new Observer<DeactivateInfoBean>() {
            @Override
            public void onChanged(@Nullable DeactivateInfoBean deactivateInfoBean) {
                if (deactivateInfoBean != null) {
                    resetViews(deactivateInfoBean);
                }
            }
        });

        finalCallBackViewModel.getmPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {
                if (pageStatusEnum == null) return;
                switch (pageStatusEnum) {
                    case EMPTY:
                        llContent.setVisibility(View.GONE);
                        loadingView.setVisibility(View.GONE);
                        errorView.setVisibility(View.GONE);
                        break;
                    case ERROR:
                        llPage.setBackgroundColor(ContextCompat.getColor(DeactivateFinalCallBackActivity.this, R.color.common_loading_bg_color));
                        llContent.setVisibility(View.GONE);
                        loadingView.setVisibility(View.GONE);
                        errorView.setVisibility(View.VISIBLE);
                        break;
                    case CONTENT:
                        llPage.setBackgroundColor(ContextCompat.getColor(DeactivateFinalCallBackActivity.this, R.color.white));
                        llContent.setVisibility(View.VISIBLE);
                        loadingView.setVisibility(View.GONE);
                        errorView.setVisibility(View.GONE);
                        break;
                    case LOADING:
                        llPage.setBackgroundColor(ContextCompat.getColor(DeactivateFinalCallBackActivity.this, R.color.common_loading_bg_color));
                        llContent.setVisibility(View.GONE);
                        loadingView.setVisibility(View.VISIBLE);
                        errorView.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    private void resetViews(@Nullable DeactivateInfoBean deactivateInfoBean) {
        tvRecallInfo0.setText(deactivateInfoBean.getDetentionDesc());

        tvFollower.setText(highlightKeyword(deactivateInfoBean.getRelationShipDesc()));

        tvPost.setText(highlightKeyword(deactivateInfoBean.getActiveDesc()));

        if (deactivateInfoBean.getFollowings() != null) {

            llLinkUser.removeAllViews();


            int screenWidth = ScreenUtils.getSreenWidth(this);

            int canInclue = (screenWidth - ScreenUtils.dip2Px(this, 22)) / ScreenUtils.dip2Px(this, 48);

            int max = Math.min(canInclue, deactivateInfoBean.getFollowings().size());

            for (int i = 0; i < max; i++) {

                View view = LayoutInflater.from(this).inflate(R.layout.avatar_item, null);

                SimpleDraweeView simplePic = view.findViewById(R.id.simple_reason_pic);

                HeadUrlLoader.getInstance().loadHeaderUrl2(simplePic, deactivateInfoBean.getFollowings().get(i));

                llLinkUser.addView(view);
            }

        } else {
            llLinkUser.setVisibility(View.GONE);
        }


    }

    /**
     * 高亮某个关键字，如果有多个则全部高亮
     */
    private SpannableString highlightKeyword(String str) {

        SpannableString sp = new SpannableString(str);

        Pattern p = Pattern.compile("[0-9]+");
        Matcher m = p.matcher(str);

        while (m.find()) {  //通过正则查找，逐个高亮
            int start = m.start();
            int end = m.end();
            sp.setSpan(new ForegroundColorSpan(Color.parseColor("#FF9A00")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return sp;
    }


    private void setOnclick() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        tvLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus.getDefault().post(new Event(EventIdConstant.CLEAR_STACK_LAUNCH_MAIN_MINE));

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_DEACTIVATE_CONFIRM_PAGE, getIntent().getExtras()));
            }
        });

        errorView.setOnErrorViewClickListener(new BaseErrorView.IErrorViewClickListener() {
            @Override
            public void onErrorViewClick() {
                finalCallBackViewModel.getUserInfo();
            }
        });

        tvRecallInfo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
