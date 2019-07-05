package com.redefine.multimedia.photoselector.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.redefine.multimedia.R;
import com.redefine.multimedia.photoselector.entity.Album;
import com.redefine.welike.base.resource.ResourceTool;

public class AlbumsSpinner {

    private static final int MAX_SHOWN_COUNT = 6;
    private BaseAdapter mAdapter;
    private TextView mSelected;
    private ListPopupWindow mListPopupWindow;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener;

    public AlbumsSpinner(@NonNull Context context) {
        mListPopupWindow = new ListPopupWindow(context);
        mListPopupWindow.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        mListPopupWindow.setModal(true);
                mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlbumsSpinner.this.onItemSelected(position);
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onItemSelected(parent, view, position, id);
                }
            }
        });
        mListPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mSelected.setCompoundDrawables(null, null, ResourceTool.getBoundDrawable(mSelected.getResources(), R.drawable.image_pick_arrow_down), null);
            }
        });
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
    }

    public void setSelection(int position) {
        mListPopupWindow.setSelection(position);
        onItemSelected(position);
    }

    private void onItemSelected(int position) {
        mListPopupWindow.dismiss();

        Album album = (Album) mAdapter.getItem(position);
        String displayName = album.getDisplayName();

        mSelected.setText(displayName);
        mSelected.setVisibility(View.VISIBLE);
        mSelected.setCompoundDrawables(null, null, ResourceTool.getBoundDrawable(mSelected.getResources(), R.drawable.image_pick_arrow_down), null);
    }

    public void setAdapter(BaseAdapter adapter) {
        mListPopupWindow.setAdapter(adapter);
        mAdapter = adapter;
    }

    public void setSelectedTextView(TextView textView) {
        mSelected = textView;
        // tint dropdown arrow icon

        mSelected.setVisibility(View.GONE);
        mSelected.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int itemHeight = v.getResources().getDimensionPixelSize(R.dimen.album_item_height);
                mListPopupWindow.setHeight(
                        mAdapter.getCount() > MAX_SHOWN_COUNT ? itemHeight * MAX_SHOWN_COUNT
                                : itemHeight * mAdapter.getCount());
                mListPopupWindow.show();
                mSelected.setCompoundDrawables(null, null, ResourceTool.getBoundDrawable(mSelected.getResources(), R.drawable.image_pick_arrow_up), null);
            }
        });
    }

    public void setPopupAnchorView(View view) {
        mListPopupWindow.setAnchorView(view);
    }

}
