package com.redefine.commonui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.redefine.commonui.R;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.ScreenUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/3/4.
 */

public class SimpleMenuDialog extends Dialog {

    private List<MenuItem> mMenuItems = new ArrayList<>();
    private OnMenuItemClickListener mMenuClickListener;
    private ViewGroup mTitleLayout;
    private ViewGroup mMenuItemLayout;
    private View mTitleRootLayout;
    private LayoutInflater mInflater;
    private String mTitleText;

    public SimpleMenuDialog(@NonNull Context context) {
        this(context, R.style.BaseAppTheme_Dialog);
    }

    public SimpleMenuDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.simple_menu_dialog);

    }

    protected SimpleMenuDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setContentView(R.layout.simple_menu_dialog);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        mInflater = LayoutInflater.from(getContext());
        mTitleLayout = findViewById(R.id.simple_menu_dialog_title_layout);
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
    }

    public void setDismissTitle(boolean isDismissTitle) {
        mTitleRootLayout.setVisibility(isDismissTitle ? View.GONE : View.VISIBLE);
    }

    protected View initTitleView(Context context) {
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


    public void setMenuItems(List<MenuItem> items, OnMenuItemClickListener listener) {
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
            mMenuItemLayout.addView(view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dip2Px(48)));
            index++;
        }
    }

    private View initMenuItemView(final MenuItem menuItem, boolean isLast) {
        View rootView = mInflater.inflate(R.layout.simple_menu_item_layout, null);
        TextView textView = rootView.findViewById(R.id.simple_menu_item_text);
        View div = rootView.findViewById(R.id.simple_menu_item_div);
        textView.setText(menuItem.menuText);
        if (menuItem.menuTextSize != 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuItem.menuTextSize);
        }
        if (menuItem.menuTextColor != 0) {
            textView.setTextColor(menuItem.menuTextColor);
        }

        if (isLast) {
            div.setVisibility(View.INVISIBLE);
        } else div.setVisibility(View.VISIBLE);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMenuClickListener != null && menuItem.isClickable) {
                    mMenuClickListener.onMenuClick(menuItem);
                    dismiss();
                }
            }
        });
        return rootView;
    }

    public static SimpleMenuDialog show(Context context, List<MenuItem> menuItemList, OnMenuItemClickListener onMenuItemClickListener) {
        SimpleMenuDialog deleteDialog = new SimpleMenuDialog(context);
        deleteDialog.setCanceledOnTouchOutside(true);
        deleteDialog.setMenuItems(menuItemList, onMenuItemClickListener);
        deleteDialog.show();
        return deleteDialog;
    }

    public static SimpleMenuDialog show(Context context, String titleText, List<MenuItem> menuItemList, OnMenuItemClickListener onMenuItemClickListener) {
        SimpleMenuDialog deleteDialog = new SimpleMenuDialog(context);
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
