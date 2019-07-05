package com.redefine.welike.business.publisher.ui.view;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager;
import com.redefine.welike.business.publisher.management.PublisherEventManager;
import com.redefine.welike.business.publisher.ui.component.OnSearchBarListener;
import com.redefine.welike.business.publisher.ui.component.SearchBar;
import com.redefine.welike.business.publisher.ui.contract.IContactListContract;
import com.redefine.welike.business.user.management.ContactsManager;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.ui.adapter.ContactsListAdapter;
import com.redefine.welike.business.user.ui.adapter.ContactsSearchAdapter;
import com.redefine.welike.business.user.ui.viewholder.OnContactsBeanClickListener;
import com.redefine.welike.statistical.EventLog;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;

/**
 * Created by gongguan on 2018/1/17.
 */

public class ContactListView implements IContactListContract.IContactListView,
        IContactListContract.OnContactChoiceListener, OnSearchBarListener, OnClickRetryListener, ILoadMoreDelegate {
    private RecyclerView mRecyclerView;
    private ContactsListAdapter mAdapter;
    private ContactsSearchAdapter mSearchAdapter;
    private SearchBar mTvSearch;
    private RelativeLayout mRl_parent;
    private TextView mTvSearchOnline;
    private View view;

    private EmptyView mEmptyView;
    private LoadingView mLoadingView;

    private IContactListContract.OnContactChoiceListener mListener;
    private boolean hasSend;

    private IContactListContract.IContactListPresenter mContactPresenter;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    EndlessRecyclerOnScrollListener scrollListener = new EndlessRecyclerOnScrollListener(this);

    @Override
    public void onCreate(View rootView, Bundle savedInstanceState) {
        view = rootView;

        initView();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void setContactData(List<User> userAll, List<User> userRecent) {
        mAdapter.setData(userRecent, userAll);
    }

    @Override
    public void setOnContactChoiceListener(IContactListContract.OnContactChoiceListener listener) {
        mListener = listener;
    }

    @Override
    public void setPresenter(IContactListContract.IContactListPresenter presenter) {
        mContactPresenter = presenter;
    }

    @Override
    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSearchOnLineResult(List<User> contents, int searchType, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mSearchAdapter.addNewData(contents);
            mSearchAdapter.clearFinishFlag();
            int size = CollectionUtil.getCount(contents);
            if (size == 0 && mSearchAdapter.getRealItemCount() == 0) {
                showEmptyView();
            } else {
                showContent();
            }
        } else {
            if (mSearchAdapter.getRealItemCount() == 0) {
                showEmptyView();
            } else {
                showContent();
            }
        }
    }

    @Override
    public void onLoadMoreResult(List<User> contents, int searchType, boolean last, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mSearchAdapter.addHisData(contents);
            if (last) {
                mSearchAdapter.noMore();
            } else {
                mSearchAdapter.finishLoadMore();
            }
        } else {
            mSearchAdapter.loadError();
        }
    }

    private void initView() {

        final View dragView = view.findViewById(R.id.drag_view);
        mEmptyView = view.findViewById(R.id.common_empty_view);
        mLoadingView = view.findViewById(R.id.common_loading_view);

        mRecyclerView = view.findViewById(R.id.recycler_contacts_list);
        mTvSearch = view.findViewById(R.id.search_bar);
        mRl_parent = view.findViewById(R.id.contact_list_root_view);
        mTvSearchOnline = view.findViewById(R.id.contacts_search_online_text);
        mTvSearch.setOnSearchBarListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);

        initAdapter();
        initSearchAdapter();
        mRecyclerView.setAdapter(mAdapter);

        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        //监听软键盘状态
        mRl_parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                mRl_parent.getWindowVisibleDisplayFrame(rect);
                int height = mRl_parent.getRootView().getHeight();
                int heightDefere = height - rect.bottom;
                if (heightDefere > 200) {

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });


        mTvSearchOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mTvSearch.getText())) {
                    return;
                }
                doSearchOnline();
                EventLog.Publish.report8(PublisherEventManager.INSTANCE.getSource(),
                        PublisherEventManager.INSTANCE.getMain_source(),
                        PublisherEventManager.INSTANCE.getPage_type(),
                        PublisherEventManager.INSTANCE.getAt_source());
            }
        });


    }


    private void initAdapter() {
        mAdapter = new ContactsListAdapter(LayoutInflater.from(view.getContext()));
        mAdapter.setOnContactsBeanClickListener(new OnContactsBeanClickListener() {
            @Override
            public void onContactsBeanClicked(User user) {
                if (user != null) {
                    setResult(user);
                }
                EventLog.Publish.report9(PublisherEventManager.INSTANCE.getSource(),
                        PublisherEventManager.INSTANCE.getMain_source(),
                        PublisherEventManager.INSTANCE.getPage_type(),
                        PublisherEventManager.INSTANCE.getAt_source());
            }
        });

    }

    private void initSearchAdapter() {
        mSearchAdapter = new ContactsSearchAdapter(LayoutInflater.from(view.getContext()));
        mSearchAdapter.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder, Object t) {
                if (t != null) {
                    setResult((User) t);
                }
            }
        });

    }

    private void setResult(User user) {
        ContactsManager.getInstance().atContact(user);
        if (mListener != null) {
            mListener.onUserChoice(user);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mSearchAdapter.setSearchKey(s.toString());
        mTvSearchOnline.setEnabled(s.length() > 0);
        if (s.length() > 0) {

            List<User> users = ContactsManager.getInstance().searchContactsWithKeyword(String.valueOf(s));
            if (!CollectionUtil.isEmpty(users)) {
                for (int i = 0; i < users.size(); i++) {
                    User user = users.get(i);
                    if (user != null && user.getNickName() != null) {
                        if (user.getNickName().equalsIgnoreCase(String.valueOf(s))) {
                            users.remove(user);
                            users.add(0, user);
                            break;
                        }
                    }
                }
            }
            mSearchAdapter.setData(users);
            if (!CollectionUtil.isEmpty(users)) {
                mRecyclerView.setAdapter(mSearchAdapter);
            }
        } else {
            doSearchClear();
        }
        if (hasSend) {
            return;
        }
        EventLog.Publish.report7(PublisherEventManager.INSTANCE.getSource(),
                PublisherEventManager.INSTANCE.getMain_source(),
                PublisherEventManager.INSTANCE.getPage_type(),
                PublisherEventManager.INSTANCE.getAt_source());
        PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel().getProxy().report7();
        hasSend = true;
    }

    private void doSearchOnline() {
        mRecyclerView.setAdapter(mSearchAdapter);
        KeyboardUtil.hideKeyboard(mTvSearch);
        mContactPresenter.searchOnLine(mTvSearch.getText());
        mRecyclerView.addOnScrollListener(scrollListener);
    }

    private void doSearchClear() {
        mRecyclerView.setAdapter(mAdapter);
        KeyboardUtil.hideKeyboard(mTvSearch);
        mContactPresenter.onRefresh();
        mRecyclerView.removeOnScrollListener(scrollListener);
    }

    @Override
    public void onUserChoice(@Nullable User user) {
        setResult(user);
    }

    @Override
    public void onCancelClick() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        if (mListener != null) {
            mListener.onUserChoice(null);
        }
    }

    public void showContent() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
    }

    public void showEmptyView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.showEmptyImageText(R.drawable.ic_common_empty,
                ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH,
                        "publish_mention_online_list_empty"));
    }

    @Override
    public void onRetryLoadMore() {
        if (canLoadMore()) {
            mSearchAdapter.onLoadMore();
            mContactPresenter.loadMore();
        }
    }

    @Override
    public boolean canLoadMore() {
        return mSearchAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mSearchAdapter.onLoadMore();
        mContactPresenter.loadMore();
    }
}
