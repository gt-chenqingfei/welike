package com.redefine.commonui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.redefine.commonui.R;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhl on 2018/3/4.
 */

public class SimpleMenuAutoGravityDialog extends Dialog {

    private List<MenuItem> mMenuItems = new ArrayList<>();
    private OnMenuItemClickListener mMenuClickListener;
    private LinearLayout mLlSimple;
    private LinearLayout ll_short_dialog;
    private ViewGroup mTitleLayout;
    private ViewGroup mMenuItemLayout;
    private View mTitleRootLayout;
    private LayoutInflater mInflater;
    private String mTitleText;

    private int mRight = 0;
    private int mTop = 0;

    private View rootView;


    public SimpleMenuAutoGravityDialog(@NonNull Context context) {
        this(context, R.style.BaseAppTheme_Dialog1);
    }

    public SimpleMenuAutoGravityDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.simple_menu_short_dialog);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        mInflater = LayoutInflater.from(getContext());
        mTitleLayout = findViewById(R.id.simple_menu_dialog_title_layout);
        ll_short_dialog = findViewById(R.id.ll_short_dialog);
        mTitleRootLayout = findViewById(R.id.simple_menu_dialog_title_root_layout);
        View title = initTitleView(getContext());
        if (title == null) {
            mTitleRootLayout.setVisibility(View.GONE);
        } else {
            int padding = ScreenUtils.dip2Px(15);
            title.setPadding(padding, padding, padding, padding);
            mTitleLayout.addView(title, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        mMenuItemLayout = findViewById(R.id.simple_menu_dialog_content_layout);
        if (!CollectionUtil.isEmpty(mMenuItems)) {
            mMenuItemLayout.removeAllViews();
        }
        initMenuItemViews(mMenuItems);

        ll_short_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (rootView != null) {
            setDialogPos();
            //auto position
            Window dialogWindow = getWindow();
            if (dialogWindow == null) return;
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            //default setting
            lp.width = ScreenUtils.dip2Px(getContext(), 175);
//            lp.x = mRight - lp.width; // 新位置X坐标
            lp.y = mTop - ScreenUtils.getStateBarHeight(getContext()); // 新位置Y坐标
            lp.x = ScreenUtils.dip2Px(getContext(), 3);
            lp.dimAmount = .2f;
            dialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP);
            dialogWindow.setAttributes(lp);
        }


    }

    public void setDismissTitle(boolean isDismissTitle) {
        mTitleRootLayout.setVisibility(isDismissTitle ? View.GONE : View.VISIBLE);
    }

    private View initTitleView(Context context) {
        if (TextUtils.isEmpty(mTitleText)) {
            return null;
        }
        TextView view = new TextView(context);
        view.setText(mTitleText);
        view.setGravity(Gravity.CENTER);
        view.setTextColor(context.getResources().getColor(R.color.common_menu_title_text_color));
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.common_menu_title_text_size));
        return view;
    }


    private void setMenuItems(List<MenuItem> items, OnMenuItemClickListener listener) {
        mMenuItems = items;
        mMenuClickListener = listener;
    }

    private void initMenuItemViews(List<MenuItem> mMenuItems) {
        if (CollectionUtil.isEmpty(mMenuItems)) {
            return;
        }
        View view;
        int index = 0;
        for (MenuItem menuItem : mMenuItems) {

            view = initMenuItemView(menuItem, index == mMenuItems.size() - 1);
            mMenuItemLayout.addView(view);
            index++;
        }
    }

    private View initMenuItemView(final MenuItem menuItem, boolean last) {
        View rootView = mInflater.inflate(R.layout.simple_menu_short_item_layout, null);
        TextView textView = rootView.findViewById(R.id.simple_menu_item_text);
        textView.setText(menuItem.menuText);
        if (menuItem.menuTextSize != 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuItem.menuTextSize);
        }
        if (menuItem.menuTextColor != 0) {
            textView.setTextColor(menuItem.menuTextColor);
        }

        if (last) {
            rootView.findViewById(R.id.dv).setVisibility(View.GONE);
        }

        if (menuItem.isClickable) {
            textView.setBackgroundColor(getContext().getResources().getColor(R.color.color_f4));
        } else {
            textView.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMenuClickListener != null) {
                    mMenuClickListener.onMenuClick(menuItem);
                    dismiss();
                }
            }
        });
        return rootView;
    }

    public void setRootView(View rootView) {

        try {

            this.rootView = rootView;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setDialogPos() {

        try {
            if (rootView == null) return;
            int[] location2 = new int[2];
            rootView.getLocationOnScreen(location2);
            this.mRight = location2[0] + rootView.getWidth();
            int height = ScreenUtils.getScreenHeight(getContext());
            if ((height - location2[1]) < ScreenUtils.dip2Px(getContext(), 155)) {
                int vw = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                int vh = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                ll_short_dialog.measure(vw, vh);
                this.mTop = location2[1] - ll_short_dialog.getMeasuredHeight();
            } else
                this.mTop = location2[1] + rootView.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static SimpleMenuAutoGravityDialog show(Context context, List<MenuItem> menuItemList, OnMenuItemClickListener onMenuItemClickListener) {
        SimpleMenuAutoGravityDialog deleteDialog = new SimpleMenuAutoGravityDialog(context);
        deleteDialog.setCanceledOnTouchOutside(true);
        deleteDialog.setMenuItems(menuItemList, onMenuItemClickListener);
        deleteDialog.show();
        return deleteDialog;
    }

    public static SimpleMenuAutoGravityDialog show(Context context, List<MenuItem> menuItemList, View rootView, OnMenuItemClickListener onMenuItemClickListener) {
        SimpleMenuAutoGravityDialog deleteDialog = new SimpleMenuAutoGravityDialog(context);
        deleteDialog.setCanceledOnTouchOutside(true);
        deleteDialog.setMenuItems(menuItemList, onMenuItemClickListener);
        deleteDialog.setRootView(rootView);
        deleteDialog.show();
        return deleteDialog;
    }

    public static SimpleMenuAutoGravityDialog show(Context context, String titleText, List<MenuItem> menuItemList, OnMenuItemClickListener onMenuItemClickListener) {
        SimpleMenuAutoGravityDialog deleteDialog = new SimpleMenuAutoGravityDialog(context);
        deleteDialog.setCanceledOnTouchOutside(true);
        deleteDialog.setTitleText(titleText);
        deleteDialog.setMenuItems(menuItemList, onMenuItemClickListener);
        deleteDialog.show();
        return deleteDialog;
    }

    private void setTitleText(String titleText) {
        mTitleText = titleText;
    }
}
