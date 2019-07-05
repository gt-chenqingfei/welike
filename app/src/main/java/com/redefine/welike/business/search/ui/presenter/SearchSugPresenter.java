package com.redefine.welike.business.search.ui.presenter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.foundation.framework.Event;
import com.redefine.frameworkmvp.presenter.MvpTitlePagePresenter;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.dao.welike.SearchHistory;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.feeds.management.HotFeedTopicManager;
import com.redefine.welike.business.feeds.management.SugManager;
import com.redefine.welike.business.feeds.management.bean.SugResult;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;
import com.redefine.welike.business.search.ui.adapter.SearchSugAdapter;
import com.redefine.welike.business.search.ui.bean.MovieSugBean;
import com.redefine.welike.business.search.ui.constant.SearchConstant;
import com.redefine.welike.business.search.ui.contract.ISearchSugContract;
import com.redefine.welike.business.search.ui.interfaces.ISearchSugOpListener;
import com.redefine.welike.business.topic.ui.constant.TopicConstant;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.manager.SearchEventManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liwenbo on 2018/3/13.
 */

public class SearchSugPresenter extends MvpTitlePagePresenter<ISearchSugContract.ISearchSugView> implements ISearchSugContract.ISearchSugPresenter
        , SugManager.SugManagerCallback, ISearchSugOpListener {
    private final SearchSugAdapter mAdapter;
    private final SugManager mModel;
    private final HotFeedTopicManager mTopicManager;
    private String mSearchQuery;
    private Disposable mDispose;

    public SearchSugPresenter(IPageStackManager stackManager, Bundle pageConfig) {
        super(stackManager, pageConfig);
        mAdapter = new SearchSugAdapter(this);
        mModel = new SugManager();
        mTopicManager = new HotFeedTopicManager();
        mTopicManager.setListener(mTopicCallback);
        mAdapter.setOnTopicClickListener(mTopicClickListener);
        mAdapter.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder, Object t) {
                SugResult sugResult = null;
                if (t instanceof SugResult) {
                    sugResult = (SugResult) t;
                }
                if (sugResult == null) {
                    return;
                }
                Object obj = sugResult.getObj();
                if (obj instanceof User) {
                    onBackPressed();
                    UserHostPage.launch(true, ((User) obj).getUid());
                } else if (obj instanceof MovieSugBean) {
                    goSearchResultPage(((MovieSugBean) obj).getSearchText());
                } else if (obj instanceof SearchHistory) {
                    goSearchResultPage(((SearchHistory) obj).getKeyword());
                } else if (obj instanceof String) {
                    goSearchResultPage((String) obj);
                }
            }
        });
    }

    @Override
    protected ISearchSugContract.ISearchSugView createPageView() {
        return ISearchSugContract.SearchSugFactory.createView();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        String searchQuery = null;
        if (mPageBundle != null) {
            searchQuery = mPageBundle.getString(SearchConstant.SEARCH_QUERY);
        }
        if (savedInstanceState != null && TextUtils.isEmpty(searchQuery)) {
            searchQuery = savedInstanceState.getString(SearchConstant.SEARCH_QUERY);
        }
        mView.setPresenter(this);
        mView.setAdapter(mAdapter);

        List<SugResult> list = mModel.listRecentKeywords();
        mAdapter.setSearchHistory(list, mModel.getAllHistoryCount() > GlobalConfig.SUG_HIS_SHOW_NUM);
        if (!TextUtils.isEmpty(searchQuery)) {
            mView.setText(searchQuery);
        }
        mTopicManager.request(AccountManager.getInstance().isLogin());
    }

    @Override
    public void onSearch(final String trim) {
        if (TextUtils.isEmpty(trim)) {
            // 显示历史
            mAdapter.showSearchHistory();
        } else {
            mAdapter.showSearchSugList(trim, null);
        }
        if (TextUtils.equals(trim, mSearchQuery)) {
            return;
        }
        mSearchQuery = trim;
        if (mDispose != null && !mDispose.isDisposed()) {
            mDispose.dispose();
        }
        mDispose = Schedulers.newThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                mModel.inputKeyword(trim, SearchSugPresenter.this, AccountManager.getInstance().isLogin());
            }
        }, 300, TimeUnit.MILLISECONDS);
    }

    @Override
    public void goSearchResultPage(String trim) {
        if (TextUtils.isEmpty(trim)) {
            return;
        }
        mView.hideInputMethod();
        final SugResult sugResult = new SugResult();
        sugResult.setCategory(SugResult.SUG_RESULT_CATEGORY_KEYWORD);
        sugResult.setType(SugResult.SUG_RESULT_TYPE_HIS);
        sugResult.setObj(trim);
        Schedulers.newThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                mModel.insert(sugResult);
            }
        });
        onBackPressed();//todo
        Bundle bundle = new Bundle();
        bundle.putString(SearchConstant.SEARCH_QUERY, trim);
        ARouter.getInstance().build(RouteConstant.PATH_SEARCH).with(bundle).navigation();
//        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_SEARCH_RESULT_EVENT, bundle));
    }

    @Override
    public void hideInputMethod() {
        mView.hideInputMethod();
    }

    @Override
    public void onSugResult(String keyword, List<SugResult> results, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            if (!TextUtils.equals(mSearchQuery, keyword)) {
                return;
            }
            mAdapter.showSearchSugList(keyword, results);
        } else {
            mAdapter.showSearchSugList(keyword, null);
        }
    }

    @Override
    public void deleteHistory(String searchHistory) {
        mModel.deleteHistory(searchHistory);
        mAdapter.delete(searchHistory);
        List<SugResult> list = mModel.listAllHistory();
        mAdapter.setSearchHistory(list, mAdapter.getShowAllHistory());
    }

    @Override
    public void copyToEdit(String sug) {
        mView.setText(sug);
    }

    @Override
    public void clearAllHistory() {
        mModel.cleanAllHistory();
        mAdapter.setSearchHistory(null, false);
    }

    private HotFeedTopicManager.HotFeedTopicCallback mTopicCallback = new HotFeedTopicManager.HotFeedTopicCallback() {
        @Override
        public void onHotFeedTopicCallback(List<TopicSearchSugBean.TopicBean> topicBeans, int errCode) {
            mAdapter.setTopicData(topicBeans);
        }
    };

    private SearchSugAdapter.OnTopicClickListener mTopicClickListener = new SearchSugAdapter.OnTopicClickListener() {
        @Override
        public void onTopicClick(TopicSearchSugBean.TopicBean topicBean) {
            if (TextUtils.isEmpty(topicBean.id)) return;
            Bundle bundle = new Bundle();
            bundle.putSerializable(TopicConstant.BUNDLE_KEY_TOPIC, topicBean);
//            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_TOPIC_LANDING_PAGE, bundle));
            ARouter.getInstance().build(RouteConstant.PATH_TOPIC_LANDING).with(bundle).navigation();
            SearchEventManager.INSTANCE.setFrom_page(EventConstants.SEARCH_FROM_PAGE_SEARCH_TOPIC);
            SearchEventManager.INSTANCE.setTopic_name(topicBean.name);
            SearchEventManager.INSTANCE.report2();
        }

        @Override
        public void onMoreTopicClick() {
            Bundle bundle = new Bundle();
            bundle.putBoolean(TopicConstant.BUNDLE_KEY_IS_BROWSE, !AccountManager.getInstance().isLogin());
//            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_PAGE_TOPIC_LIST, bundle));
            ARouter.getInstance().build(RouteConstant.PATH_TOPIC_LIST).with(bundle).navigation();
        }

        @Override
        public void onMoreHistoryClick() {
            mAdapter.setSearchHistory(mModel.listAllHistory(), false);
        }
    };
}
