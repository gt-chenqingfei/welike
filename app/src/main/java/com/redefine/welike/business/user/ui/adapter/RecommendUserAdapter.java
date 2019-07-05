package com.redefine.welike.business.user.ui.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.management.bean.EmptyBean;
import com.redefine.welike.business.user.management.bean.RecommendTagBean;
import com.redefine.welike.business.user.management.bean.RecommendUser;
import com.redefine.welike.business.user.ui.viewholder.InterestEmptyViewHolder;
import com.redefine.welike.statistical.manager.InterestAndRecommendCardEventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by honglin on 2018/7/26.
 */

public class RecommendUserAdapter extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, Object> {
    private List contactsList;
    private List<Integer> tagList = new ArrayList<>();

    public RecommendUserAdapter() {
        contactsList = new ArrayList<>();
        setFooter(null);
    }

    public void addNewData(List<RecommendTagBean> mList) {
        contactsList.clear();
        tagList.clear();
        Account account = AccountManager.getInstance().getAccount();
        if (!CollectionUtil.isEmpty(mList)) {

            for (Object obj : mList) {

                if (obj instanceof RecommendTagBean) {
                    RecommendTagBean tagBean = (RecommendTagBean) obj;
                    if (tagBean.getList() != null) {
                        if (tagBean.getTag().isDefault() == 1
                                || account == null
                                || account.getIntrests() == null
                                || account.getIntrests().size() == 0)
                            for (RecommendUser user : tagBean.getList()) {
                                user.setSelect(true);
                            }
                        contactsList.add(tagBean);
                    }
                    tagList.add(tagBean.getTag().getLabelId());
                }
            }
        }

        contactsList.add(0, new EmptyBean(EmptyBean.TYE_INFO));

        contactsList.add(new EmptyBean(EmptyBean.TYE_EMPTY));

        notifyDataSetChanged();

        InterestAndRecommendCardEventManager.INSTANCE.report4();
    }

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0) {
            View mView = mInflater.inflate(R.layout.item_home_recommend_layout, parent, false);
            return new TagViewHolder(mView);
        } else if (viewType == 2) {
            View mView = mInflater.inflate(R.layout.item_empty_info_layout, parent, false);
            return new IntroViewHolder(mView);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest_empty, null);
            return new InterestEmptyViewHolder(view);
        }
    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, final int position) {

        int viewType = getRealItemViewType(position);

        if (viewType == 0) {

            final RecommendTagBean tagBean = (RecommendTagBean) contactsList.get(position);

            final TagViewHolder tagViewHolder = (TagViewHolder) holder;

            tagViewHolder.tvtitle.setText(tagBean.getTag().getTag());

            final RecommendUserSubAdapter userSubAdapter = new RecommendUserSubAdapter(tagViewHolder.recyclerView.getContext());
            setSelectImageViewStatus(tagViewHolder.ivSelect, checkIsAllSelected(tagBean.getList()));
            tagViewHolder.recyclerView.setAdapter(userSubAdapter);

            tagViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(tagViewHolder.recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));

            userSubAdapter.addNewData(tagBean.getList());

            userSubAdapter.setListener(new RecommendUserSubAdapter.OnDataSelectStatusChange() {
                @Override
                public void onChange(int position) {
                    tagBean.getList().get(position).setSelect(!tagBean.getList().get(position).getSelect());

                    userSubAdapter.notifyItemChanged(position);

                    setSelectImageViewStatus(tagViewHolder.ivSelect, checkIsAllSelected(tagBean.getList()));
                }
            });


            tagViewHolder.ivSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isAll = checkIsAllSelected(tagBean.getList());
                    setSelectStatus(!isAll, tagBean.getList());
                    userSubAdapter.notifyDataSetChanged();
                    setSelectImageViewStatus((ImageView) v, !isAll);
                }
            });

            if (position == getItemCount() - 2) {
                tagViewHolder.divider1.setVisibility(View.GONE);
            } else {
                tagViewHolder.divider1.setVisibility(View.VISIBLE);
            }
        } else if (viewType == 2) {
            IntroViewHolder tagViewHolder = (IntroViewHolder) holder;

            tagViewHolder.tvtitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_no_follow_title"));
        }
    }

    private void setSelectImageViewStatus(ImageView imageView, boolean isSelectAll) {

        imageView.setImageResource(isSelectAll ? R.drawable.regist_recommond_checked : R.drawable.regist_recommond_check);

    }

    @Override
    protected int getRealItemViewType(int position) {

        if (contactsList.get(position) instanceof EmptyBean) {

            if (((EmptyBean) contactsList.get(position)).getTye() == 0) {
                return 2;
            } else return 1;

        } else return 0;
    }

    @Override
    public int getRealItemCount() {
        return contactsList != null ? contactsList.size() : 0;
    }

    @Override
    protected Object getRealItem(int position) {
        return contactsList.get(position);
    }


    class TagViewHolder extends BaseRecyclerViewHolder {

        public TextView tvtitle;

        public ImageView ivSelect;

        public RecyclerView recyclerView;
        public View divider1;

        public TagViewHolder(View itemView) {
            super(itemView);


            tvtitle = itemView.findViewById(R.id.tv_tag_title);
            ivSelect = itemView.findViewById(R.id.iv_select);
            recyclerView = itemView.findViewById(R.id.rv_user_list);
            divider1 = itemView.findViewById(R.id.divider1);

        }
    }

    class IntroViewHolder extends BaseRecyclerViewHolder {

        public TextView tvtitle;

        public IntroViewHolder(View itemView) {
            super(itemView);

            tvtitle = itemView.findViewById(R.id.tv_intro_title);

        }
    }


    public ArrayList<String> getSelectUserIds() {

        ArrayList<String> ids = new ArrayList<>();

        for (Object obj : contactsList) {

            if (obj instanceof RecommendTagBean) {
                RecommendTagBean tagBean = (RecommendTagBean) obj;
                if (tagBean.getList() != null)
                    for (RecommendUser user : tagBean.getList()) {
                        if (user.getSelect())
                            ids.add(user.getUid());
                    }
            }
        }

        return ids;

    }

    public List<String> getSelectTagIds() {
        List<String> tags = new ArrayList<>();
        for (Object o : contactsList) {
            if (o instanceof RecommendTagBean) {
                RecommendTagBean tagBean = (RecommendTagBean) o;
                if (checkIsAllSelected(tagBean.getList())) {
                    tags.add(tagBean.getTag().getLabelId() + "");
                }
            }
        }
        return tags;
    }


    private void setSelectStatus(boolean isSelect, List<RecommendUser> list) {

        for (RecommendUser obj : list) {
            obj.setSelect(isSelect);
        }
        notifyDataSetChanged();
    }


    private boolean checkIsAllSelected(List<RecommendUser> list) {

        if (list == null) return false;

        boolean isAll = true;

        for (RecommendUser obj : list) {

            if (obj.getSelect()) continue;

            isAll = false;
        }

        return isAll;

    }

}