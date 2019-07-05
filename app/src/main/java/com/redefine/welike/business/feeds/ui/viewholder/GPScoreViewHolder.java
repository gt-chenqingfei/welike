package com.redefine.welike.business.feeds.ui.viewholder;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.redefine.commonui.constant.WebViewConstant;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
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
import com.redefine.welike.business.feeds.management.bean.GPScorePost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;
import com.redefine.welike.business.im.IMHelper;
import com.redefine.welike.business.im.ui.constant.ImConstant;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.startup.management.constant.RegisteredConstant;
import com.redefine.welike.common.ScoreManager;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.redefine.welike.statistical.manager.GPScoreEventManager;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Created by MR on 2018/1/30.
 */

public class GPScoreViewHolder extends BaseRecyclerViewHolder<PostBase> {


    private TextView title;

    private TextView description;

    private LottieAnimationView expression, editExp, expression1;

    private LinearLayout llStar;

    private TextView description1;

    private TextView feedback;

    private GPScorePost gpScorePost;


    private static final int starCount = 5;

    public GPScoreViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.tv_gp_score_title);
        description = itemView.findViewById(R.id.tv_gp_score_description);
        expression = itemView.findViewById(R.id.lv_gp_score_expression);
        expression1 = itemView.findViewById(R.id.lv_gp_score_expression1);
        editExp = itemView.findViewById(R.id.lv_gp_score_edit_expression);
        llStar = itemView.findViewById(R.id.ll_gp_score_star);
        description1 = itemView.findViewById(R.id.tv_gp_score_description1);
        feedback = itemView.findViewById(R.id.tv_gp_score_feedback);
    }

    @Override
    public void bindViews(final RecyclerView.Adapter adapter, PostBase data) {
        super.bindViews(adapter, data);

        gpScorePost = (GPScorePost) data;

        feedback.setText(ResourceTool.getString("feed_gp_score_feedback"));
        description1.setText(ResourceTool.getString("feed_gp_score_description1"));

        if (gpScorePost.getCurrentType() == 0) {
            GPScoreEventManager.INSTANCE.report1();
            title.setText(ResourceTool.getString("feed_gp_score_title"));
            description.setText(ResourceTool.getString("feed_gp_score_description"));

            if (gpScorePost.getCurrentSelect() < 0)
                expression.setAnimation("gp_satisfaction.json");
            else if (gpScorePost.getCurrentSelect() < 2) {
                description1.setVisibility(View.VISIBLE);
                expression1.setVisibility(View.VISIBLE);
                expression.setVisibility(View.INVISIBLE);
                expression1.setAnimation("gp_bad.json");
            } else {
                description1.setVisibility(View.VISIBLE);
                expression1.setVisibility(View.VISIBLE);
                expression.setVisibility(View.INVISIBLE);
                expression1.setAnimation("gp_good.json");
            }
            expression.playAnimation();
            expression.setRepeatCount(LottieDrawable.INFINITE);

            setLlStar(gpScorePost.getCurrentSelect());

        } else {
            GPScoreEventManager.INSTANCE.report6();
            title.setText(ResourceTool.getString("feed_gp_score_feedback_title"));
            description.setText(ResourceTool.getString("feed_gp_score_feedback_description"));

            description1.setVisibility(View.INVISIBLE);
            llStar.setVisibility(View.INVISIBLE);
            expression.setVisibility(View.INVISIBLE);
            expression1.setVisibility(View.GONE);

            feedback.setVisibility(View.VISIBLE);
            editExp.setVisibility(View.VISIBLE);

            editExp.setAnimation("gp_feedback.json");
            editExp.playAnimation();
            editExp.setRepeatCount(LottieDrawable.INFINITE);

        }


        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // TODO: 2018/8/22

                GPScoreEventManager.INSTANCE.report4();
                changeListener.dissmiss();

                if (AccountManager.getInstance().isLoginComplete()) {

                    if (AccountManager.getInstance().getAccount().getStatus() == Account.ACCOUNT_HALF) {

                        Bundle bundle = new Bundle();
                        bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.CHAT));
                        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_VERIFY_DIALOG, bundle));

                        return;
                    }
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
                } else {
                    HalfLoginManager.getInstancce().showLoginDialog(v.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER));
                }

            }
        });

    }


    @Override
    public void onAttachToWindow() {
        super.onAttachToWindow();
    }


    private void setLlStar(int selectPos) {

        AppCompatImageView view;

        llStar.removeAllViews();

        int index = 0;
        while (index < starCount) {

            view = (AppCompatImageView) LayoutInflater.from(llStar.getContext()).inflate(R.layout.item_gp_score_star, null);

            view.setTag(index);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();

                    reset(position);
                }
            });

            if (index <= selectPos) {
                view.setImageResource(R.drawable.ic_gp_score_selected);
            } else {
                view.setImageResource(R.drawable.ic_gp_score_not_selected);
            }

            llStar.addView(view);
            index++;

        }
    }

    private void reset(int position) {

        setLlStar(position);
        GPScoreEventManager.INSTANCE.setRate(position + 1);
        GPScoreEventManager.INSTANCE.onClickRate();
        if (position == 4) {
            ScoreManager.jumpToGooglePlay(llStar.getContext());
            ScoreManager.postToastStatus(llStar.getContext(), 5);
            changeListener.dissmiss();
            return;
        }

        if (gpScorePost.getCurrentSelect() < 0 && position < 4) {
            gpScorePost.setCurrentSelect(position);
            changeListener.dataChanged(gpScorePost);
            return;
        }
        ScoreManager.postToastStatus(llStar.getContext(), position + 1);
        gpScorePost.setCurrentType(1);
        changeListener.dataChanged(gpScorePost);

    }


    public interface GPDataChangeListener {
        void dataChanged(GPScorePost data);

        void dissmiss();
    }


    private GPDataChangeListener changeListener;

    public void setChangeListener(GPDataChangeListener changeListener) {
        this.changeListener = changeListener;
    }
}

