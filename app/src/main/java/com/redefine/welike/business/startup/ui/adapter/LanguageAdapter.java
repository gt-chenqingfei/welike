package com.redefine.welike.business.startup.ui.adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.redefine.welike.R;
import com.redefine.welike.business.startup.management.bean.LanguageBean;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.manager.UnLoginEventManager;

import java.util.ArrayList;

/**
 * Created by honglin on 2018/7/10.
 */

public class LanguageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<LanguageBean> datas;

    private LanguageClickListener languageClickListener;

    private int selectPos = -1;

    private boolean clickLanguageFlag;

    public void setDatas(ArrayList<LanguageBean> datas) {
        this.datas = datas;
        this.datas.add(new LanguageBean(0, 0, 0, "", false, false));
        this.datas.add(new LanguageBean(0, 0, 0, "", false, false));
        this.datas.add(new LanguageBean(0, 0, 0, "", false, false));

        notifyDataSetChanged();
    }

    public ArrayList<LanguageBean> getDatas() {
        return datas;
    }

    public int getSelectPos() {
        return selectPos;
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

                if (!clickLanguageFlag) {
                    UnLoginEventManager.INSTANCE.setFirstClickLanguage(languageBean.getShortName());
                    EventLog.UnLogin.report5(languageBean.getShortName());
                    clickLanguageFlag = true;
                }

                if (languageBean.isReady()) {

                    for (int i = 0; i < datas.size(); i++) {
                        if (datas.get(i).getChecked() && i != position && datas.get(i).isReady()) {
                            datas.get(i).setChecked(false);
                            notifyItemChanged(i);
                        }
                    }

                    languageBean.setChecked(!languageBean.getChecked());

                    notifyItemChanged(position);

                    boolean checked = false;
                    selectPos = -1;
                    for (int i = 0; i < datas.size(); i++) {
                        if (datas.get(i).getChecked() && datas.get(i).isReady()) {
                            checked = true;
                            selectPos = i;
                            break;
                        }
                    }
                    languageClickListener.onItemListener(checked);
                } else {
                    if (TextUtils.isEmpty(languageBean.getShortName())) return;
                    if (languageBean.getChecked()) {
                        return;
                    } else {

                        languageClickListener.showLoading();

                        for (LanguageBean languageBean1 : datas) {
                            if (!languageBean1.isReady() && !TextUtils.isEmpty(languageBean1.getShortName()))
                                languageBean1.setChecked(true);
                        }
                    }
                }

            }
        });

        if (TextUtils.isEmpty(datas.get(position).getShortName())) {
            languageViewHolder.rlLanguage.setVisibility(View.INVISIBLE);
        } else {
            languageViewHolder.rlLanguage.setVisibility(View.VISIBLE);
        }


        languageViewHolder.tvChooseLanguageTitle.setImageResource(datas.get(position).getIconDrawable());

        languageViewHolder.ivCheckbox.setImageResource(datas.get(position).getChecked() && datas.get(position).isReady() ? R.drawable.choose_language_checked : R.drawable.ic_choose_language_un_check);

        if (datas.get(position).getChecked() && !datas.get(position).isReady()) {
            languageViewHolder.clInfo.setVisibility(View.VISIBLE);
            languageViewHolder.ivActiveLocale.setImageResource(datas.get(position).getIconUnActive());
        } else {
            languageViewHolder.clInfo.setVisibility(View.GONE);
            languageViewHolder.ivActiveLocale.setImageResource(datas.get(position).getIconActive());
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
        void onItemListener(Boolean checked);

        void showLoading();
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

        }


    }


}
