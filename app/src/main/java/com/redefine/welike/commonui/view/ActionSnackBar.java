package com.redefine.welike.commonui.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.statistical.EventLog;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by honglin on 2018/7/11.
 */

public class ActionSnackBar {

    private static ActionSnackBar instance;

    public static ActionSnackBar getInstance() {
        if (instance == null) {
            instance = new ActionSnackBar();
        }
        return instance;
    }

    private Snackbar snackbarLogin, snackbar;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    if (snackbarLogin != null && snackbarLogin.isShown())
                        snackbarLogin.dismiss();
                }
            }, 0, TimeUnit.MILLISECONDS);
        }
    };

    public void showLoginSnackBar(@NotNull View view, String title, String action, long dur, final ActionBtnClickListener listener) {

        showLoginSnackBar(view, title, action, dur, 0, listener);

    }

    public void showLoginSnackBar(@NotNull View view, String title, String action, long dur, int bottomMargin, final ActionBtnClickListener listener) {


        if (snackbarLogin != null && snackbarLogin.isShown()) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, dur);
            return;
        }

        snackbarLogin = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);

        Snackbar.SnackbarLayout barlayout = (Snackbar.SnackbarLayout) snackbarLogin.getView();

        barlayout.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.transparent));

        barlayout.findViewById(R.id.snackbar_text).setVisibility(View.INVISIBLE);

        View add_view = LayoutInflater.from(view.getContext()).inflate(R.layout.snackbar_item, null);

        TextView titleTv = add_view.findViewById(R.id.tv_snackbar_text);
        TextView actionTv = add_view.findViewById(R.id.tv_snackbar_login);

        titleTv.setText(title);
        actionTv.setText(action);

        barlayout.addView(add_view);

        actionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbarLogin.dismiss();
                listener.onActionClick();
                handler.removeCallbacks(runnable);
                EventLog.UnLogin.report17(StartEventManager.getInstance().getActionType() > 5 ? 6 : StartEventManager.getInstance().getActionType(), StartEventManager.getInstance().getFrom_page());
            }
        });

        if (snackbarLogin != null) {
            ViewGroup.LayoutParams params = snackbarLogin.getView().getLayoutParams();
            ((ViewGroup.MarginLayoutParams) params).setMargins(0, 0, 0, bottomMargin);
            snackbarLogin.getView().setLayoutParams(params);
        }

        snackbarLogin.show();
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, dur);

    }

    public void showLoginSnackBar(@NotNull View view, String title, String action, final ActionBtnClickListener listener) {

        if (snackbar != null && snackbar.isShown()) return;
        snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);

        Snackbar.SnackbarLayout barlayout = (Snackbar.SnackbarLayout) snackbar.getView();

        barlayout.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.transparent));

        barlayout.findViewById(R.id.snackbar_text).setVisibility(View.INVISIBLE);

        View add_view = LayoutInflater.from(view.getContext()).inflate(R.layout.snackbar_item, null);
        add_view.findViewById(R.id.ll_snackbar).setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.transparent));
        TextView titleTv = add_view.findViewById(R.id.tv_snackbar_text);
        TextView actionTv = add_view.findViewById(R.id.tv_snackbar_login);

        titleTv.setVisibility(View.GONE);
        actionTv.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtils.dip2Px(view.getContext(), 190), ScreenUtils.dip2Px(view.getContext(), 40)));
        actionTv.setText(action);

        barlayout.addView(add_view);

        actionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onActionClick();

            }
        });
        snackbar.show();
    }

    public void showLoginLightSnackBar(@NotNull View view, String title, String action, long dur, final ActionBtnClickListener listener) {


        if (snackbarLogin != null && snackbarLogin.isShown()) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, dur);
            return;
        }

        snackbarLogin = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);

        Snackbar.SnackbarLayout barlayout = (Snackbar.SnackbarLayout) snackbarLogin.getView();

        barlayout.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.transparent));

        barlayout.findViewById(R.id.snackbar_text).setVisibility(View.INVISIBLE);

        View add_view = LayoutInflater.from(view.getContext()).inflate(R.layout.snackbar_item, null);

        View container = add_view.findViewById(R.id.ll_snackbar);
        TextView titleTv = add_view.findViewById(R.id.tv_snackbar_text);
        TextView actionTv = add_view.findViewById(R.id.tv_snackbar_login);

        container.setBackgroundResource(R.drawable.common_light_snackbar_bg);
        titleTv.setText(title);
        titleTv.setTextColor(Color.parseColor("#000000"));
        actionTv.setText(action);

        barlayout.addView(add_view);

        actionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbarLogin.dismiss();
                listener.onActionClick();
                handler.removeCallbacks(runnable);
                EventLog.UnLogin.report17(StartEventManager.getInstance().getActionType() > 5 ? 6 : StartEventManager.getInstance().getActionType(), StartEventManager.getInstance().getFrom_page());
            }
        });

        snackbarLogin.show();
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, dur);

    }


    public void showLoginSnackBar(Activity context, String title, String action, final ActionBtnClickListener listener) {

        View view = context.getWindow().getDecorView().findViewById(android.R.id.content);

        if (snackbar != null && snackbar.isShown()) return;
        snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);

        Snackbar.SnackbarLayout barlayout = (Snackbar.SnackbarLayout) snackbar.getView();

        barlayout.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.transparent));

        barlayout.findViewById(R.id.snackbar_text).setVisibility(View.INVISIBLE);

        View add_view = LayoutInflater.from(view.getContext()).inflate(R.layout.snackbar_item, null);
        add_view.findViewById(R.id.ll_snackbar).setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.transparent));
        TextView titleTv = add_view.findViewById(R.id.tv_snackbar_text);
        TextView actionTv = add_view.findViewById(R.id.tv_snackbar_login);

        titleTv.setVisibility(View.GONE);
        actionTv.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtils.dip2Px(view.getContext(), 190), ScreenUtils.dip2Px(view.getContext(), 40)));
        actionTv.setText(action);

        barlayout.addView(add_view);

        actionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onActionClick();

            }
        });
        snackbar.show();
    }

    public void dissmissSnackbar() {
        if (snackbar != null && snackbar.isShown())
            snackbar.dismiss();
    }

    public boolean isLoginSnackBarShown() {
        return (snackbar != null && snackbar.isShown());
    }


    public interface ActionBtnClickListener {
        void onActionClick();
    }

}
