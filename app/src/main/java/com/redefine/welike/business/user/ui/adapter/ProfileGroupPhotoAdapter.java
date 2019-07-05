package com.redefine.welike.business.user.ui.adapter;

import android.support.v7.util.AdapterListUpdateCallback;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.user.management.bean.GroupedPhoto;
import com.redefine.welike.business.user.ui.viewholder.ProfileGroupPhotoViewHolder;
import com.redefine.welike.business.user.ui.viewholder.ProfilePhotoViewHolder;
import com.redefine.welike.business.videoplayer.management.bean.AttachmentBase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by nianguowang on 2018/10/10
 */
public class ProfileGroupPhotoAdapter extends LoadMoreFooterRecyclerAdapter {

    private List<GroupedPhoto> mAttachments = new ArrayList<>();

    private ProfilePhotoAdapter.OnAttachmentClickListener mListener;

    public void setAttachmentClickListener(ProfilePhotoAdapter.OnAttachmentClickListener listener) {
        mListener = listener;
    }

    public void setData(final List<GroupedPhoto> attachmentBases) {
        mAttachments.clear();
        mAttachments.addAll(attachmentBases);
        notifyDataSetChanged();
    }

    /**
     * Group photo according to post created time by month.
     * @param attachmentBases
     * @return
     */
    public List<GroupedPhoto> groupImageByMonth(List<AttachmentBase> attachmentBases) {
        if (CollectionUtil.isEmpty(attachmentBases)) {
            return new ArrayList<>();
        }

        LinkedHashMap<String, List<AttachmentBase>> map = new LinkedHashMap();
        for (AttachmentBase attachmentBase : attachmentBases) {
            long postCreatedTime = attachmentBase.getPostCreatedTime();
            Date date = new Date(postCreatedTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            String showTime = month + "-" + year;
            List<AttachmentBase> list = map.get(showTime);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(attachmentBase);
            map.put(showTime, list);
        }

        List<GroupedPhoto> groupedPhotos = new ArrayList<>();
        Set<Map.Entry<String, List<AttachmentBase>>> entries = map.entrySet();
        for (Map.Entry<String, List<AttachmentBase>> entry : entries) {
            String key = entry.getKey();
            List<AttachmentBase> value = entry.getValue();
            GroupedPhoto photo = new GroupedPhoto();
            photo.setShowTime(key);
            photo.setAttachments(value);
            String[] time = key.split("-");
            photo.setMonth(Integer.valueOf(time[0]));
            photo.setYear(Integer.valueOf(time[1]));
            groupedPhotos.add(photo);
        }
        return groupedPhotos;
    }

    public List<AttachmentBase> getAttachmentList() {
        if (CollectionUtil.isEmpty(mAttachments)) {
            return new ArrayList<>();
        }
        List<AttachmentBase> attachmentBases = new ArrayList<>();
        for (GroupedPhoto attachment : mAttachments) {
            List<AttachmentBase> attachments = attachment.getAttachments();
            if (!CollectionUtil.isEmpty(attachments)) {
                attachmentBases.addAll(attachments);
            }
        }
        return attachmentBases;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new ProfileGroupPhotoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_group_photo_item, parent, false));
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProfileGroupPhotoViewHolder) {
            ((ProfileGroupPhotoViewHolder) holder).setAttachmentClickListener(mListener);
            ((ProfileGroupPhotoViewHolder) holder).bindView(mAttachments.get(position));
        }
    }

    @Override
    protected int getRealItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRealItemCount() {
        return CollectionUtil.getCount(mAttachments);
    }

    @Override
    protected Object getRealItem(int position) {
        return mAttachments.get(position);
    }
}
