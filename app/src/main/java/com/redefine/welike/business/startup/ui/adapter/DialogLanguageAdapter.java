package com.redefine.welike.business.startup.ui.adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.business.startup.management.bean.LanguageBean;

import java.util.ArrayList;

/**
 * Created by honglin on 2018/7/10.
 */

public class DialogLanguageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<LanguageBean> datas;

    private LanguageClickListener languageClickListener;

    public void setDatas(ArrayList<LanguageBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language_layout, null);

        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        LanguageViewHolder languageViewHolder = (LanguageViewHolder) holder;

        languageViewHolder.rlLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LanguageBean languageBean = datas.get(position);

                if (languageBean.isReady()) {

                    for (int i = 0; i < datas.size(); i++) {
                        if (datas.get(i).getChecked() && i != position && datas.get(i).isReady()) {
                            datas.get(i).setChecked(false);
                            notifyItemChanged(i);
                        }
                    }

                    languageBean.setChecked(true);

                    notifyItemChanged(position);

                    languageClickListener.onItemClick(position, true);
//                } else {
//                    languageClickListener.onItemClick(position, false);
                }

            }
        });

        languageViewHolder.tvChooseLanguageTitle.setImageResource(datas.get(position).getIconDrawable());

        languageViewHolder.ivCheckbox.setImageResource(datas.get(position).getChecked() && datas.get(position).isReady() ? R.drawable.choose_language_checked : R.drawable.regist_recommond_check);

        if (datas.get(position).isReady()) {
            languageViewHolder.ivActiveLocale.setImageResource(datas.get(position).getIconActive());
        } else {
            languageViewHolder.ivActiveLocale.setImageResource(datas.get(position).getIconUnActive());
        }
    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    public void setLanguageClickListener(LanguageClickListener languageClickListener) {
        this.languageClickListener = languageClickListener;
    }

    public interface LanguageClickListener {

        void onItemClick(int position, boolean canSelect);

    }

    class LanguageViewHolder extends RecyclerView.ViewHolder {


        private ImageView tvChooseLanguageTitle;
        private ImageView ivCheckbox;
        private ImageView ivActiveLocale;
        private ConstraintLayout clInfo;
        private RelativeLayout rlLanguage;


        public LanguageViewHolder(View itemView) {
            super(itemView);

            rlLanguage = itemView.findViewById(R.id.rl_language);
            tvChooseLanguageTitle = itemView.findViewById(R.id.tv_choose_language_title);
            ivCheckbox = itemView.findViewById(R.id.iv_choose_language);
            ivActiveLocale = itemView.findViewById(R.id.iv_language_icon);
            clInfo = itemView.findViewById(R.id.cl_info);
            clInfo.setVisibility(View.GONE);

            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) ivActiveLocale.getLayoutParams();

            rl.width = ScreenUtils.getSreenWidth(itemView.getContext()) * 91 / 100 / 5;

            rl.height = rl.width * 68 / 72;

            ivActiveLocale.setLayoutParams(rl);

        }


    }


}
