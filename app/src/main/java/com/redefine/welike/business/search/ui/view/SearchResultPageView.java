package com.redefine.welike.business.search.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.foundation.utils.InputMethodUtil;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.search.ui.bean.SearchMovieBean;
import com.redefine.welike.business.search.ui.constant.SearchConstant;
import com.redefine.welike.business.search.ui.contract.ISearchPageContract;
import com.redefine.welike.business.search.ui.page.SearchFragmentPageSwitcher;
import com.redefine.welike.common.util.DateTimeUtil;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.manager.SearchEventManager;

/**
 * Created by liwenbo on 2018/2/11.
 */

public class SearchResultPageView implements ISearchPageContract.ISearchResultPageView, View.OnClickListener {
    private final IPageStackManager mPageStackManager;
    private final Bundle mPageBundle;
    private View mCommonBackBtn;
    private TextView mEditText;
    private TextView mPostTab;
    private TextView mUserTab;
    private TextView mLatestTab;
    private View index1;
    private View index2;
    private View index3;
    private SearchFragmentPageSwitcher mSearchPageSwitcher;
    private ViewGroup mPageContainer;
    private final ISearchPageContract.ISearchResultPagePresenter mPresenter;
    private String mLastText;
    private String mSearchQuery;
//    private boolean mIsBrowse;

    private View movieCardLayout;
    private TextView movieCardFlag;
    private SimpleDraweeView movieCardCover;
    private TextView movieTitle;
    private TextView movieRate;
    private TextView movieTime;
    private TextView movieIntroduce;
    private AppCompatImageView mMovieFlag;


    public SearchResultPageView(IPageStackManager pageStackManager, ISearchPageContract.ISearchResultPagePresenter presenter, Bundle pageBundle) {
        mPageStackManager = pageStackManager;
        mPresenter = presenter;
        mPageBundle = pageBundle;
    }

    @Override
    public View createView(Context context, Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_page, null);
        initViews(view, savedInstanceState);
        return view;
    }

    private void initViews(View view, Bundle savedInstanceState) {
        mSearchQuery = "";
        if (mPageBundle != null) {
            mSearchQuery = mPageBundle.getString(SearchConstant.SEARCH_QUERY);
        }
        if (TextUtils.isEmpty(mSearchQuery) && savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(SearchConstant.SEARCH_QUERY);
        }
        mCommonBackBtn = view.findViewById(R.id.common_back_btn);
        mEditText = view.findViewById(R.id.search_edit);
        mEditText.setOnClickListener(this);
        mPostTab = view.findViewById(R.id.search_post_tab);
        mLatestTab = view.findViewById(R.id.search_latest_tab);
        mUserTab = view.findViewById(R.id.search_user_tab);

        movieCardLayout = view.findViewById(R.id.movie_card_layout);
        movieCardFlag = view.findViewById(R.id.movie_text);
        movieCardCover = view.findViewById(R.id.movie_cover);
        movieTitle = view.findViewById(R.id.movie_title);
        movieRate = view.findViewById(R.id.movie_percent);
        movieTime = view.findViewById(R.id.movie_time);
        movieIntroduce = view.findViewById(R.id.movie_introduce);
        mMovieFlag = view.findViewById(R.id.movie_like_flag);

        index1 = view.findViewById(R.id.indicator1);
        index2 = view.findViewById(R.id.indicator2);
        index3 = view.findViewById(R.id.indicator3);

        mPostTab.setText(MyApplication.getAppContext().getResources().getString(R.string.search_post_tab));
        mUserTab.setText(MyApplication.getAppContext().getResources().getString(R.string.search_user_tab));
        mLatestTab.setText(MyApplication.getAppContext().getResources().getString(R.string.search_latest_tab));


        mPostTab.measure(0, 0);
        index1.getLayoutParams().width = mPostTab.getMeasuredWidth();
        mUserTab.measure(0, 0);
        index2.getLayoutParams().width = mUserTab.getMeasuredWidth();
        mLatestTab.measure(0, 0);
        index3.getLayoutParams().width = mLatestTab.getMeasuredWidth();


        mPageContainer = view.findViewById(R.id.discover_fragment_page_container);
        mSearchPageSwitcher = new SearchFragmentPageSwitcher(mPageStackManager, mPresenter);
        mCommonBackBtn.setOnClickListener(this);
        mPostTab.setOnClickListener(this);
        mUserTab.setOnClickListener(this);
        mLatestTab.setOnClickListener(this);
        mEditText.setText(mSearchQuery);
        selectTab(mLatestTab);
        mPresenter.getMovie(mSearchQuery);

        SearchEventManager.INSTANCE.setSearch_key(mSearchQuery);
        SearchEventManager.INSTANCE.report4();

        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_SEARCH);
        EventLog1.Search.report2(mSearchQuery);
    }

    private void selectTab(View tab) {
        if (tab == mLatestTab) {
            index1.setVisibility(View.VISIBLE);
            index2.setVisibility(View.INVISIBLE);
            index3.setVisibility(View.INVISIBLE);
            mLatestTab.setTextColor(mCommonBackBtn.getContext().getResources().getColor(R.color.search_tab_text_color_pressed));
            mPostTab.setTextColor(mCommonBackBtn.getContext().getResources().getColor(R.color.search_tab_text_color_default));
            mUserTab.setTextColor(mCommonBackBtn.getContext().getResources().getColor(R.color.search_tab_text_color_default));
            mSearchPageSwitcher.setCurrentItem(mPageContainer, SearchFragmentPageSwitcher.FRAGMENT_LATEST_POSITION);
        } else if (tab == mPostTab) {
            index1.setVisibility(View.INVISIBLE);
            index2.setVisibility(View.VISIBLE);
            index3.setVisibility(View.INVISIBLE);

            mLatestTab.setTextColor(mCommonBackBtn.getContext().getResources().getColor(R.color.search_tab_text_color_default));
            mPostTab.setTextColor(mCommonBackBtn.getContext().getResources().getColor(R.color.search_tab_text_color_pressed));
            mUserTab.setTextColor(mCommonBackBtn.getContext().getResources().getColor(R.color.search_tab_text_color_default));
            mSearchPageSwitcher.setCurrentItem(mPageContainer, SearchFragmentPageSwitcher.FRAGMENT_POST_POSITION);
        } else if (tab == mUserTab) {
            index1.setVisibility(View.INVISIBLE);
            index2.setVisibility(View.INVISIBLE);
            index3.setVisibility(View.VISIBLE);

            mLatestTab.setTextColor(mCommonBackBtn.getContext().getResources().getColor(R.color.search_tab_text_color_default));
            mPostTab.setTextColor(mCommonBackBtn.getContext().getResources().getColor(R.color.search_tab_text_color_default));
            mUserTab.setTextColor(mCommonBackBtn.getContext().getResources().getColor(R.color.search_tab_text_color_pressed));
            mSearchPageSwitcher.setCurrentItem(mPageContainer, SearchFragmentPageSwitcher.FRAGMENT_USER_POSITION);
        }
        mLastText = "";
        doSearch();
    }

    @Override
    public void attach() {

    }

    @Override
    public void detach() {

    }

    @Override
    public void destroy() {
        mSearchPageSwitcher.destroy(mPageContainer);
    }

    @Override
    public void onClick(View v) {
        if (v == mCommonBackBtn) {
            mPresenter.onBackPressed();
        } else if (v == mPostTab) {
            selectTab(v);
        } else if (v == mUserTab) {
            selectTab(v);
        } else if (v == mLatestTab) {
            selectTab(v);
        } else if (v == mEditText) {
            mPresenter.goSugPage(mSearchQuery);
        }
    }

    @Override
    public void hideInputMethod() {
        InputMethodUtil.hideInputMethod(mEditText);
    }

    @Override
    public void switchToPeoplePage() {
        selectTab(mUserTab);
    }

    @Override
    public void initMovieCard(SearchMovieBean bean) {
        if (null != bean) {
            movieCardLayout.setVisibility(View.VISIBLE);
            movieCardFlag.setText("Movie");
            movieCardCover.setImageURI(bean.getPoster());
            movieTitle.setText(bean.getTitle());
            if (null != bean.getRating()) {
                mMovieFlag.setImageResource(R.drawable.movie_like_flag);
                String rate = Math.round(bean.getRating() * 10) + "%";
                movieRate.setText(rate);

            } else {
                mMovieFlag.setImageResource(R.drawable.movie_like_unflag);
                movieRate.setText("--");

            }
            if (null != bean.getRuntime()) {
                movieTime.setText(DateTimeUtil.INSTANCE.secToTime(bean.getRuntime()));
            } else {
                movieTime.setText("Duration: NA");

            }
            if (null != bean.getPlot() && !TextUtils.isEmpty(bean.getPlot())) {
                movieIntroduce.setText(bean.getPlot());

            } else {
                movieIntroduce.setText("It looks like we don't have any description for this movie yet.");

            }

        }

    }

    @Override
    public void onActivityResume() {
        mSearchPageSwitcher.onActivityResume();
    }

    @Override
    public void onActivityPause() {
        mSearchPageSwitcher.onActivityPause();
    }

    private void doSearch() {
        String currentText = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(currentText)) {
            return;
        }
        mLastText = currentText;
        mSearchPageSwitcher.doRealSearch(mLastText);
    }
}
