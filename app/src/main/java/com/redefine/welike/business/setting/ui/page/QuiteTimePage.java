package com.redefine.welike.business.setting.ui.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.widget.loopView.LoopView;
import com.redefine.commonui.widget.loopView.OnItemSelectedListener;
import com.redefine.welike.R;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.resource.ResourceTool;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by mengnan on 2018/5/8.
 **/
@Route(path = RouteConstant.PATH_QUITE_TIME)
public class QuiteTimePage extends BaseActivity implements View.OnClickListener {

    private View mBackBtn;
//    private View mView;
    private TextView mTitleView, mFromTv, mEndTv, mFromTextHoursTv, mFromTextminTv, mEndTextHoursTv, mEndTextminTv;
    private LinearLayout mFromLl, mEndLl;
    private LoopView hoursLoopView;
    private LoopView minutesLoopView;
    private boolean isFrom = true;
    private ArrayList<String> hoursList = new ArrayList<>();
    private ArrayList<String> minsList = new ArrayList<>();
    private String mFromHours="22";
    private String mFromMins="00";
    private String mEndHours="07";
    private String mEndMins="00";

//    public QuiteTimePage(BaseLifecyclePageStackManager stackManager, PageConfig config, PageCache cache) {
//        super(stackManager, config, cache);
//    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_quite_time);
        mBackBtn = findViewById(R.id.iv_common_back);
        mTitleView = findViewById(R.id.tv_common_title);

        mFromTv = findViewById(R.id.tv_mute_from_time);
        mEndTv = findViewById(R.id.tv_mute_end_time);
        mFromTextHoursTv = findViewById(R.id.tv_mute_from_hours_text);
        mFromTextminTv = findViewById(R.id.tv_mute_from_min_text);
        mEndTextHoursTv = findViewById(R.id.tv_mute_end_hours_text);
        mEndTextminTv = findViewById(R.id.tv_mute_end_min_text);

        mFromLl = findViewById(R.id.ll_mute_from_time);
        mEndLl = findViewById(R.id.ll_mute_end_time);

        hoursLoopView = findViewById(R.id.start_loopView);
        minutesLoopView = findViewById(R.id.end_loopView);

        mBackBtn.setOnClickListener(this);
        mFromLl.setOnClickListener(this);
        mEndLl.setOnClickListener(this);

        initTextView();
        initLoopView();
    }

//    @Override
//    protected View createPageView(ViewGroup container, Bundle saveState) {
//        mView = LayoutInflater.from(container.getContext()).inflate(R.layout.notification_quite_time, null);
//        return mView;
//    }

//    @Override
//    protected void initView(ViewGroup container, Bundle saveState) {
//        super.initView(container, saveState);
//
//
//    }


    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            onBackPressed();
        } else if (v == mFromLl) {
            changeFromTo(true);
        } else if (v == mEndLl) {
            changeFromTo(false);
        }


    }


    private void initTextView() {
        mTitleView.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "Quiet_Hours"));
        mFromTv.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "mute_start"));
        mEndTv.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "mute_end"));
        mFromTextHoursTv.setText("22");
        mFromTextminTv.setText("00");
        mEndTextHoursTv.setText("07");
        mEndTextminTv.setText("00");
    }

    private void initLoopView() {

        for (int i = 0; i < 24; i++) {
            hoursList.add(makeTime(i));
        }
        hoursLoopView.setItems(hoursList);
        for (int i = 0; i < 60; i++) {
            minsList.add(makeTime(i));
        }
        minutesLoopView.setItems(minsList);

        hoursLoopView.setInitPosition(22);
        minutesLoopView.setInitPosition(0);

        timeSelect();

    }

    private void timeSelect() {
        hoursLoopView.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                changeHoursTime(index);
            }
        });

        minutesLoopView.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                changeMinsTime(index);
            }
        });

    }

    private void changeFromTo(boolean isFrom) {
        this.isFrom = isFrom;
        mFromLl.setBackgroundColor(isFrom ? getResources().getColor(R.color.common_gray_f6) : getResources().getColor(R.color.White));
        mEndLl.setBackgroundColor(isFrom ? getResources().getColor(R.color.White) : getResources().getColor(R.color.common_gray_f6));


    }

    private String makeTime(int time) {
        DecimalFormat df = new DecimalFormat("00");
        return df.format(time);
    }

    private void changeHoursTime(int index) {
        if (isFrom) {
            mFromTextHoursTv.setText(hoursList.get(index));
            mFromHours=hoursList.get(index);
        } else {
            mEndTextHoursTv.setText(hoursList.get(index));
            mEndHours=hoursList.get(index);
        }

    }

    private void changeMinsTime(int index) {
        if (isFrom) {
            mFromTextminTv.setText(minsList.get(index));
            mFromMins=minsList.get(index);

        } else {
            mEndTextminTv.setText(minsList.get(index));
            mEndMins=minsList.get(index);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpManager.Setting.setNotificationStartTime(mFromHours+":"+mFromMins,getApplicationContext());
        SpManager.Setting.setNotificationEndTime(mEndHours+":"+mEndMins,getApplicationContext());
    }

//    @Override
//    public void destroy(ViewGroup container) {
//        super.destroy(container);
//
//    }

}
