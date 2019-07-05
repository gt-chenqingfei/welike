package com.redefine.welike.commonui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.dialog.MenuItem;
import com.redefine.commonui.dialog.OnMenuItemClickListener;
import com.redefine.commonui.dialog.SimpleMenuDialog;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;

import java.util.List;

/**
 * Created by liwenbo on 2018/3/4.
 */

public class FeedDeleteDialog extends SimpleMenuDialog {
    public FeedDeleteDialog(@NonNull Context context) {
        super(context);
    }

    public FeedDeleteDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected FeedDeleteDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected View initTitleView(Context context) {
        TextView view = new TextView(context);
        view.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_delete_query"));
        view.setGravity(Gravity.CENTER);
        view.setTextColor(context.getResources().getColor(R.color.feed_delete_dialog_title_text_color));
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.feed_delete_dialog_title_text_size));
        return view;
    }

    public static FeedDeleteDialog show(Context context, List<MenuItem> menuItemList, OnMenuItemClickListener onMenuItemClickListener) {
        FeedDeleteDialog deleteDialog = new FeedDeleteDialog(context);
        deleteDialog.setCanceledOnTouchOutside(true);
        deleteDialog.setMenuItems(menuItemList, onMenuItemClickListener);
        deleteDialog.show();
        return deleteDialog;
    }
}
