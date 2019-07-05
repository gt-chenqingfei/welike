package com.redefine.welike.commonui.event.expose;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.commonui.event.expose.base.IDataProvider;
import com.redefine.welike.commonui.event.expose.base.IItemVisibleCallback;
import com.redefine.welike.commonui.event.expose.base.IItemExposeManager;
import com.redefine.welike.commonui.event.expose.model.ExposeModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * create by honlin
 * post view time manager
 **/
public class ItemExposeManager implements IItemExposeManager {

    private RecyclerView mRecyclerView;
    private boolean mAttach, mResume, mShow;
    private Rect rect = new Rect();
    private Map<IItemVisibleCallback, List<ExposeModel>> mCurrentShowItemMap = new LinkedHashMap<>();
    private Map<IItemVisibleCallback, List<ExposeModel>> mTempShowItemMap = new LinkedHashMap<>();

    public ItemExposeManager(IItemVisibleCallback... itemVisibleCallbacks) {
        for (IItemVisibleCallback itemVisibleCallback : itemVisibleCallbacks) {
            mCurrentShowItemMap.put(itemVisibleCallback, new ArrayList<ExposeModel>());
            mTempShowItemMap.put(itemVisibleCallback, new ArrayList<ExposeModel>());
        }
    }

    /**
     * attach view
     */
    @Override
    public void attach(RecyclerView recyclerView, IDataProvider adapter, String oldSource) {
        if (recyclerView == null) {
            return;
        }
        mRecyclerView = recyclerView;
        Set<IItemVisibleCallback> callbacks = mCurrentShowItemMap.keySet();
        for (IItemVisibleCallback callback : callbacks) {
            callback.bindDataAndSource(adapter, oldSource);
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                startCalculate();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                startCalculate();
            }
        });
    }

    private void startCalculate() {
        if (mRecyclerView == null) {
            return;
        }
        if (!mShow || !mAttach || !mResume) {
            return;
        }
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            clearList(mTempShowItemMap);
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            for (int i = firstVisibleItemPosition ; i <= lastVisibleItemPosition ; i++) {
                View indexView = layoutManager.getChildAt(i - firstVisibleItemPosition);
                if (indexView == null) {
                    continue;
                }
                int height = indexView.getHeight();
                if (height <= 0) {
                    continue;
                }

                indexView.getLocalVisibleRect(rect);
                if (rect.bottom > 0) {
                    int showHeight = rect.bottom - rect.top;
                    float showRatio = showHeight * 1f / height;
                    Set<IItemVisibleCallback> callbacks = mTempShowItemMap.keySet();
                    for (IItemVisibleCallback callback : callbacks) {
                        if (showRatio >= callback.getRatioForExpose()) {
                            List<ExposeModel> models = mTempShowItemMap.get(callback);
                            models.add(new ExposeModel(i, showRatio, indexView));
                        }
                    }
                }
            }
            Set<IItemVisibleCallback> iItemVisibleCallbacks = mCurrentShowItemMap.keySet();
            for (IItemVisibleCallback callback : iItemVisibleCallbacks) {
                List<ExposeModel> tempShowItems = mTempShowItemMap.get(callback);
                List<ExposeModel> currentShowItems = mCurrentShowItemMap.get(callback);
                for (ExposeModel tempShowItem : tempShowItems) {
                    if (!currentShowItems.contains(tempShowItem)) {
                        callback.onItemVisible(tempShowItem.layoutPosition, tempShowItem.view, tempShowItem.showRatio);
                    }
                }
                for (ExposeModel currentShowItem : currentShowItems) {
                    if (!tempShowItems.contains(currentShowItem)) {
                        callback.onItemInvisible(currentShowItem.layoutPosition, currentShowItem.view);
                    }
                }
                currentShowItems.clear();
                currentShowItems.addAll(tempShowItems);
            }
        }
    }

    private void callbackInvisible() {
        Set<IItemVisibleCallback> callbacks = mCurrentShowItemMap.keySet();
        for (IItemVisibleCallback callback : callbacks) {
            List<ExposeModel> models = mCurrentShowItemMap.get(callback);
            for (ExposeModel model : models) {
                callback.onItemInvisible(model.layoutPosition, model.view);
            }
        }
        clearList(mCurrentShowItemMap);
        clearList(mTempShowItemMap);
    }

    private void clearList(Map<IItemVisibleCallback, List<ExposeModel>> map) {
        if (map == null) {
            return;
        }
        Set<Map.Entry<IItemVisibleCallback, List<ExposeModel>>> entries = map.entrySet();
        for (Map.Entry<IItemVisibleCallback, List<ExposeModel>> entry : entries) {
            List<ExposeModel> value = entry.getValue();
            value.clear();
        }
    }

    @Override
    public void updateData(List<PostBase> feeds) {
    }

    @Override
    public void setData(List<PostBase> feeds, boolean isShowHeader) {
    }

    @Override
    public void setSource(String source, String oldSource) {
        Set<IItemVisibleCallback> callbacks = mCurrentShowItemMap.keySet();
        for (IItemVisibleCallback callback : callbacks) {
            callback.updateSource(source, oldSource);
        }
    }

    @Override
    public void onDataLoaded() {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                startCalculate();
            }
        }, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onAttach() {
        mAttach = true;
        startCalculate();
    }

    @Override
    public void onDetach() {
        mAttach = false;
        if (mShow && mResume) {
            callbackInvisible();
        }
    }

    @Override
    public void onShow() {
        mShow = true;
        startCalculate();
    }

    @Override
    public void onHide() {
        mShow = false;
        if (mResume && mAttach) {
            callbackInvisible();
        }
    }

    @Override
    public void onPause() {
        mResume = false;
        if (mAttach && mShow) {
            callbackInvisible();
        }
    }

    @Override
    public void onResume() {
        mResume = true;
        startCalculate();
    }

    @Override
    public void onDestroy() {
        mShow = mAttach = mResume = false;
        clearList(mTempShowItemMap);
        clearList(mCurrentShowItemMap);
    }
}
