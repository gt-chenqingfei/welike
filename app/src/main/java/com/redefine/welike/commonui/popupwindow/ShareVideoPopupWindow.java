package com.redefine.welike.commonui.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.scwang.smartrefresh.layout.util.DensityUtil;

/**
 * Created by nianguowang on 2018/9/3
 */
public class ShareVideoPopupWindow extends DPopupWindow {

    private static final int POPUP_WINDOW_WIDTH = DensityUtil.dp2px(300);
    private static final int POPUP_WINDOW_HEIGHT = DensityUtil.dp2px(65);

    private OnShareClickListener mListener;

    public ShareVideoPopupWindow(Context context, OnShareClickListener listener) {
//        View rootView = LayoutInflater.from(context).inflate(R.layout.share_video_popup_window, null)
        super(LayoutInflater.from(context).inflate(R.layout.share_video_popup_window, null), POPUP_WINDOW_WIDTH, POPUP_WINDOW_HEIGHT);
        this.mListener = listener;
        initView(getContentView());
    }

    public interface OnShareClickListener {

        void onShareVideo();

        void onShareLink();

    }

    private void initView(View contentView) {
//        contentView.measure(makeDropDownMeasureSpec(getWidth()), makeDropDownMeasureSpec(getHeight()));
        TextView shareVideoText = contentView.findViewById(R.id.share_video_text);
        TextView shareLinkText = contentView.findViewById(R.id.share_link_text);
        shareVideoText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "share_video"));
        shareLinkText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "share_link"));

        contentView.findViewById(R.id.share_video_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onShareVideo();
                }
                dismiss();
            }
        });
        contentView.findViewById(R.id.share_link_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onShareLink();
                }
                dismiss();
            }
        });
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable());
        setAnimationStyle(R.style.popupwindow_anim_translate);
    }

    public void show(View anchorView) {
        setFocusable(true);
        if (isShowing()) {
            dismiss();
            return;
        }
//        setClippingEnabled(false);
//        int popWidth = getContentView().getMeasuredWidth();
//        int popHeight = getContentView().getMeasuredHeight();
//        int offsetX = -popWidth;
//        int offsetY = -(popHeight + anchorView.getHeight()) / 2;
//        Log.d("DDAI","popWidth="+popWidth+";popHeight="+popHeight+";anchorView.getHeight()="+(anchorView.getHeight()));
//        Log.d("DDAI","offsetX="+offsetX+";offsetY="+offsetY);
//        PopupWindowCompat.showAsDropDown(this, anchorView, offsetX, offsetY, Gravity.START);
        showOnAnchor(anchorView, VerticalPosition.CENTER, HorizontalPosition.LEFT, false);
    }

    private int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorHeight = anchorView.getHeight();

        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        final int contentHeight = DensityUtil.dp2px(POPUP_WINDOW_HEIGHT);
        final int contentHeight = contentView.getMeasuredHeight();
        final int contentWidth = contentView.getMeasuredWidth();

        windowPos[0] = contentWidth;
//        windowPos[1] = (contentHeight/2) + (anchorHeight/2);
        windowPos[1] = anchorHeight;
        return windowPos;
    }

    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }

}
