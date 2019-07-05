package com.redefine.welike.business.search.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.InputMethodUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.search.ui.adapter.SearchSugAdapter;
import com.redefine.welike.business.search.ui.contract.ISearchSugContract;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liwenbo on 2018/3/13.
 */

public class SearchSugView implements ISearchSugContract.ISearchSugView, TextWatcher, View.OnClickListener, TextView.OnEditorActionListener {

    private RecyclerView mRecyclerView;
    private EditText mSearchEdit;
   // private ImageView mSearchCancel;
    private ISearchSugContract.ISearchSugPresenter mPresenter;
    private View mEditTextDelete;
    private View mBack;

    @Override
    public View createView(Context context, Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_sug_layout, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.search_sug_list);
        mEditTextDelete = view.findViewById(R.id.search_clear);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mSearchEdit = view.findViewById(R.id.location_search_edit);
        mBack = view.findViewById(R.id.common_back_btn);
        mSearchEdit.setFocusable(true);
       // mSearchCancel = view.findViewById(R.id.location_search_edit_clear);
       // mSearchCancel.setOnClickListener(this);
        mEditTextDelete.setOnClickListener(this);
        mBack.setOnClickListener(this);
       // mSearchCancel.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_cancel"));
        mSearchEdit.addTextChangedListener(this);
        mSearchEdit.setOnEditorActionListener(this);
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                mSearchEdit.requestFocus();
                InputMethodUtil.showInputMethod(mSearchEdit);
            }
        }, 10, TimeUnit.MILLISECONDS);
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
    public void setAdapter(SearchSugAdapter mAdapter) {
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setPresenter(ISearchSugContract.ISearchSugPresenter searchSugPresenter) {
        mPresenter = searchSugPresenter;
    }

    @Override
    public void setText(String sug) {
        mSearchEdit.setText(sug);
        mSearchEdit.setSelection(CollectionUtil.getCount(sug));
    }

    @Override
    public void hideInputMethod() {
        InputMethodUtil.hideInputMethod(mSearchEdit);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString().trim();
        if (s.length() > 0) {
            mEditTextDelete.setVisibility(View.VISIBLE);
        } else {
            mEditTextDelete.setVisibility(View.GONE);
        }
        mPresenter.onSearch(text);
    }

    @Override
    public void onClick(View v) {
       /* if (v == mSearchCancel) {
            mPresenter.onBackPressed();
        } else*/ if (v == mEditTextDelete) {
            mSearchEdit.setText("");
        }else if(v == mBack){
            mPresenter.onBackPressed();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            mPresenter.goSearchResultPage(mSearchEdit.getText().toString().trim());
            return true;
        }
        return false;
    }
}
