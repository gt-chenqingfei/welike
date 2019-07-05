package com.redefine.welike.business.user.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.ui.adapter.InterestCategoryAdapter;
import com.redefine.welike.business.user.ui.contract.IChoiceInterestContract;

/**
 * Created by liwenbo on 2018/3/21.
 */

public class ChoiceInterestView implements IChoiceInterestContract.IChoiceInterestView, View.OnClickListener, BaseErrorView.IErrorViewClickListener {

    private TextView mTitleView;
    private View mBackBtn;
    private IChoiceInterestContract.IChoiceInterestPresenter mPresenter;
    private TextView mConfirmBtn;
    private RecyclerView mRecyclerView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;

    @Override
    public View createView(Context context, Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.choice_category_page, null);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mTitleView = view.findViewById(R.id.common_title_view);

        mBackBtn = view.findViewById(R.id.common_back_btn);
        mConfirmBtn = view.findViewById(R.id.choice_category_confirm);
        mRecyclerView = view.findViewById(R.id.choice_category_recycler);
        mErrorView = view.findViewById(R.id.common_error_view);
        mLoadingView = view.findViewById(R.id.common_loading_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mBackBtn.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mBackBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
        mErrorView.setOnErrorViewClickListener(this);
        mTitleView.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "choice_interest_title"));
        mConfirmBtn.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_confirm"));
    }

    @Override
    public void attach() {

    }

    @Override
    public void detach() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void setAdapter(InterestCategoryAdapter mInterestCategoryAdapter) {
        mRecyclerView.setAdapter(mInterestCategoryAdapter);
    }

    @Override
    public void setPresenter(IChoiceInterestContract.IChoiceInterestPresenter choiceInterestPresenter) {
        mPresenter = choiceInterestPresenter;
    }

    @Override
    public void showLoading() {
        mErrorView.setVisibility(View.INVISIBLE);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showContent() {
        mErrorView.setVisibility(View.INVISIBLE);
        mLoadingView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showErrorView() {
        mErrorView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            mPresenter.onBackPressed();
        } else if (v == mConfirmBtn) {
            mPresenter.onConfirmClick();
        }
    }

    @Override
    public void onErrorViewClick() {
        mPresenter.onRefresh();
    }
}
