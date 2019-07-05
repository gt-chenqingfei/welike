package com.redefine.welike.commonui.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Toast提示信息工具类
 * Created by Gonguan on 2018/1/3.
 */
public class ToastUtils {
    private static Context mContext = MyApplication.getAppContext();

    private ToastUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(CharSequence message) {
        showMyLayoutToast(mContext, message, Toast.LENGTH_SHORT);
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(int message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    public static void showLong(CharSequence message) {
        showMyLayoutToast(mContext, message, Toast.LENGTH_LONG);
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    public static void showLong(int message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义Toast显示位置
     *
     * @param message
     * @param position
     */
    public static void showMyToast(String message, int position) {
        Toast toast = Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG);
        if (position == Gravity.TOP)
            toast.setGravity(position, 0, ScreenUtils.getScreenHeight(mContext) / 9);
        else if (position == Gravity.BOTTOM)
            toast.setGravity(position, 0, ScreenUtils.getScreenHeight(mContext) / 10);
        else if (position == Gravity.CENTER)
            toast.setGravity(position, 0, 0);
        toast.show();
    }

    /**
     * 带图片的Toast
     *
     * @param context
     * @param bg      背景图
     */
    public static void showImgToast(Context context, int bg) {
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastView = (LinearLayout) toast.getView();
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toastView.setLayoutParams(ll);
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(bg);
        imageView.setLayoutParams(ll);
        toastView.addView(imageView, 0);
        toast.show();
    }

    private static void showMyLayoutToast(final Context context, final CharSequence content, final int duration) {

        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = layoutInflater.inflate(R.layout.download_toast_bg, null);
                TextView textView = layout.findViewById(R.id.my_toast_content);
                textView.setText(content);
                Toast toast = new Toast(context);
                toast.setGravity(Gravity.BOTTOM, 0, ScreenUtils.getScreenHeight(mContext) / 4);
                toast.setDuration(duration);
                toast.setView(layout);
                toast.show();
            }
        });

    }

}
