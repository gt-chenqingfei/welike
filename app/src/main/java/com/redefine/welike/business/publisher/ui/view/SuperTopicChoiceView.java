package com.redefine.welike.business.publisher.ui.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.utils.InputMethodUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager;
import com.redefine.welike.business.publisher.management.PublisherEventManager;
import com.redefine.welike.business.publisher.ui.adapter.TopicSearchHistoryAdapter;
import com.redefine.welike.business.publisher.ui.adapter.TopicSearchSugAdapter;
import com.redefine.welike.business.publisher.ui.component.OnSearchBarListener;
import com.redefine.welike.business.publisher.ui.component.SearchBar;
import com.redefine.welike.business.publisher.ui.contract.ITopicChoiceContract;
import com.redefine.welike.business.publisher.ui.dialog.OnTopicChoiceListener;
import com.redefine.welike.statistical.EventLog;

/**
 * Created by liwenbo on 2018/4/10.
 */

public class SuperTopicChoiceView implements ITopicChoiceContract.ITopicChoiceView
        , BaseErrorView.IErrorViewClickListener, TextView.OnEditorActionListener,
        View.OnTouchListener, OnSearchBarListener {

    private ITopicChoiceContract.ITopicChoicePresenter mPresenter;
    private RecyclerView mRecyclerView;
    private EmptyView mEmptyView;
    private LoadingView mLoadingView;
    private ErrorView mErrorView;

    private SearchBar mSearchBar;
    boolean hasInput = false;
    private OnTopicChoiceListener listener;
    public SuperTopicChoiceView(SearchBar searchBar, OnTopicChoiceListener listener) {
        mSearchBar = searchBar;
        this.listener = listener;
    }

    @Override
    public void onCreate(View rootView, Bundle savedInstanceState) {

        mRecyclerView = rootView.findViewById(R.id.topic_choice_recycler_view);
        mEmptyView = rootView.findViewById(R.id.common_empty_view);
        mLoadingView = rootView.findViewById(R.id.common_loading_view);
        mErrorView = rootView.findViewById(R.id.common_error_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mEmptyView.showEmptyImageText(R.drawable.ic_common_empty,
                rootView.getContext().getString(R.string.topic_choice_empty_text));
        mErrorView.setOnErrorViewClickListener(this);
        mRecyclerView.setOnTouchListener(this);
        EventLog.Publish.report20(PublisherEventManager.INSTANCE.getSource(),
                PublisherEventManager.INSTANCE.getMain_source(),
                PublisherEventManager.INSTANCE.getPage_type(),
                PublisherEventManager.INSTANCE.getAt_source());


        if (PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel() != null) {
            PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel().getProxy().report18();
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void setPresenter(ITopicChoiceContract.ITopicChoicePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showErrorView() {
        mEmptyView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void showContentView() {
        mEmptyView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
    }

    private void showLoading() {
        mEmptyView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void setHistoryAdapter(TopicSearchHistoryAdapter mTopicHistory) {
        mRecyclerView.setAdapter(mTopicHistory);
    }

    @Override
    public void setSugAdapter(TopicSearchSugAdapter mTopicSugAdapter) {
        mRecyclerView.setAdapter(mTopicSugAdapter);
    }

    @Override
    public void onErrorViewClick() {
        showLoading();
        doSearch(mSearchBar.getText());
    }


    private void doSearch(String searchText) {
        String text = searchText.trim();
        mPresenter.onSearch(text);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        doSearch(s.toString());

        if(hasInput)
            return;
        hasInput = true;
        PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel().getProxy().report19();
        EventLog.Publish.report21(PublisherEventManager.INSTANCE.getSource(),
                PublisherEventManager.INSTANCE.getMain_source(),
                PublisherEventManager.INSTANCE.getPage_type(),
                PublisherEventManager.INSTANCE.getAt_source());
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null
                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            if (isShowContent()) {
                mPresenter.onClickImeOption();
            }
            return true;
        }
        return false;
    }

    private boolean isShowContent() {
        return mLoadingView.getVisibility() != View.VISIBLE && mErrorView.getVisibility()
                != View.VISIBLE && mEmptyView.getVisibility() != View.VISIBLE;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodUtil.hideInputMethod(mSearchBar);
        return false;
    }

    @Override
    public void onCancelClick() {
        if(listener != null) {
            listener.onTopicChoice(null, false);
        }
    }
}
