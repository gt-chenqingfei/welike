package com.redefine.welike.business.publisher.ui.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.dao.welike.TopicSearchHistory;
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager;
import com.redefine.welike.business.publisher.management.PublisherEventManager;
import com.redefine.welike.business.publisher.management.TopicSugManager;
import com.redefine.welike.business.publisher.management.bean.HotTopics;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;
import com.redefine.welike.business.publisher.ui.adapter.TopicSearchHistoryAdapter;
import com.redefine.welike.business.publisher.ui.adapter.TopicSearchSugAdapter;
import com.redefine.welike.business.publisher.ui.component.SearchBar;
import com.redefine.welike.business.publisher.ui.contract.ITopicChoiceContract;
import com.redefine.welike.business.publisher.ui.dialog.OnTopicChoiceListener;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liwenbo on 2018/4/10.
 */

public class TopicChoicePresenter implements ITopicChoiceContract.ITopicChoicePresenter, TopicSugManager.TopicSugManagerCallback, HeaderAndFooterRecyclerViewAdapter.OnItemClickListener {

    private final Activity mActivity;
    private final ITopicChoiceContract.ITopicChoiceView mView;
    private final TopicSearchHistoryAdapter mTopicHistoryAdapter;
    private final TopicSearchSugAdapter mTopicSugAdapter;
    private final TopicSugManager mModel;
    private boolean isShowHistory = true;
    private String mSearchQuery;
    private Disposable mDispose;
    private OnTopicChoiceListener listener;


    public TopicChoicePresenter(Activity activity, OnTopicChoiceListener listener, SearchBar searchBar) {
        this.listener = listener;
        mActivity = activity;
        mView = ITopicChoiceContract.TopicChoiceFactory.createView(searchBar, listener);
        mView.setPresenter(this);
        mTopicHistoryAdapter = new TopicSearchHistoryAdapter();
        mTopicSugAdapter = new TopicSearchSugAdapter();
        mTopicSugAdapter.setOnItemClickListener(this);
        mTopicHistoryAdapter.setOnItemClickListener(this);
        mModel = new TopicSugManager();
    }

    public TopicChoicePresenter(Activity activity, OnTopicChoiceListener listener) {
        this.listener = listener;
        mActivity = activity;
        mView = ITopicChoiceContract.TopicChoiceFactory.createView(listener);
        mView.setPresenter(this);
        mTopicHistoryAdapter = new TopicSearchHistoryAdapter();
        mTopicSugAdapter = new TopicSearchSugAdapter();
        mTopicSugAdapter.setOnItemClickListener(this);
        mTopicHistoryAdapter.setOnItemClickListener(this);
        mModel = new TopicSugManager();
    }

    @Override
    public void onCreate(View rootView, Bundle savedInstanceState) {
        mView.onCreate(rootView, savedInstanceState);
        mView.setHistoryAdapter(mTopicHistoryAdapter);
        isShowHistory = true;
        mTopicHistoryAdapter.setData(mModel.listAllHistory());
        if (mTopicHistoryAdapter.getItemCount() != 0) {
            mView.showContentView();
        } else {
            mView.showEmptyView();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    private void finishActivity() {
        mActivity.finish();
        mActivity.overridePendingTransition(R.anim.sliding_to_left_in, R.anim.sliding_right_out);
    }

    @Override
    public void onSearch(final String text) {
        boolean isNeedSearch = false;
        if (!TextUtils.isEmpty(text)) {
            // 显示历史
            isNeedSearch = true;
            mView.setSugAdapter(mTopicSugAdapter);
            isShowHistory = false;
            if (mTopicSugAdapter.getItemCount() > 0) {
                mView.showContentView();
            } else {
                mView.showEmptyView();
            }
        } else {
            isNeedSearch = false;
            mView.setHistoryAdapter(mTopicHistoryAdapter);
            isShowHistory = true;
            if (mTopicHistoryAdapter.getItemCount() > 0) {
                mView.showContentView();
            } else {
                mView.showEmptyView();
            }
        }
        mSearchQuery = text;
        if (mDispose != null && !mDispose.isDisposed()) {
            mDispose.dispose();
        }
        if (isNeedSearch) {
            mDispose = Schedulers.newThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    mModel.inputKeyword(text, TopicChoicePresenter.this);
                }
            }, 300, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void onClickImeOption() {
        if (isShowHistory) {
            return;
        }
        TopicSearchSugBean.TopicBean bean = mTopicSugAdapter.getNewTopic();
        if (bean == null) {
            return;
        }
        onResult(bean, false);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mView.onTextChanged(s, start, before, count);
    }

    private void onResult(TopicSearchSugBean.TopicBean bean, boolean isWithFlag) {
        mModel.insert(bean);
        listener.onTopicChoice(bean, isWithFlag);
        PublisherEventManager.INSTANCE.setAdd_topic_type(EventConstants.PUBLISH_ADD_TOPIC_SEARCH);
    }

    @Override
    public void onSugResult(String query, TopicSearchSugBean results, int errCode) {
        if (!TextUtils.equals(mSearchQuery, query)) {
            return;
        }

        if (errCode == ErrorCode.ERROR_SUCCESS) {
            if (results == null) {
                // 空数据
                mView.showEmptyView();
            } else {
                mView.showContentView();
                mTopicSugAdapter.setData(mSearchQuery, results);
            }
        } else {
            mView.showErrorView();
            mTopicSugAdapter.setData(query, results);
        }
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder, Object t) {
        if (t instanceof TopicSearchHistory) {
            TopicSearchSugBean.TopicBean bean = new TopicSearchSugBean.TopicBean();
            bean.name = ((TopicSearchHistory) t).getKeyword();
            bean.id = ((TopicSearchHistory) t).getId().toString();
            onResult(bean, false);
            EventLog.Publish.report22(PublisherEventManager.INSTANCE.getSource(),
                    PublisherEventManager.INSTANCE.getMain_source(),
                    PublisherEventManager.INSTANCE.getPage_type(),
                    PublisherEventManager.INSTANCE.getAt_source(),
                    bean.id);

            if (PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel() != null) {
                PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel().getProxy().report20();
            }
        } else if (t instanceof TopicSearchSugBean.TopicBean) {
            onResult((TopicSearchSugBean.TopicBean) t, false);
            if (PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel() != null) {
                PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel().getProxy().report21();
            }
        } else if (t instanceof HotTopics.HotTopic) {
            if (TextUtils.isEmpty(((HotTopics.HotTopic) t).topicName)) {
                return;
            }
            TopicSearchSugBean.TopicBean bean = new TopicSearchSugBean.TopicBean();
            bean.id = ((HotTopics.HotTopic) t).id;
            bean.name = ((HotTopics.HotTopic) t).topicName;
            onResult(bean, true);
            EventLog.Publish.report22(PublisherEventManager.INSTANCE.getSource(),
                    PublisherEventManager.INSTANCE.getMain_source(),
                    PublisherEventManager.INSTANCE.getPage_type(),
                    PublisherEventManager.INSTANCE.getAt_source(),
                    bean.id);

            if (PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel() != null) {
                PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel().getProxy().report20();
            }
        }
        else {
            EventLog.Publish.report21(PublisherEventManager.INSTANCE.getSource(),
                    PublisherEventManager.INSTANCE.getMain_source(),
                    PublisherEventManager.INSTANCE.getPage_type(),
                    PublisherEventManager.INSTANCE.getAt_source());

            if (PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel() != null) {
                PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel().getProxy().report20();
            }
        }
    }
}
