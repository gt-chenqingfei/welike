package com.redefine.welike.business.startup.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redefine.welike.R;

/**
 * Created by gongguan on 2018/3/30.
 */

public class RegistWeclomeFragment extends Fragment {
    public static final String WELCOME_USER_FRAGMENT_TAG = "welcome_user_fragment";
    private ConstraintLayout clParent;
    private AppCompatImageView ivTarget;
    private TextView tvTarget;
    private int imageType = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_contact_item, null);
        return view;
    }

    public void onFailed(int errCode) {
    }

}
