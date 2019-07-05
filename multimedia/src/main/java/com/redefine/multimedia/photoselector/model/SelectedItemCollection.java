package com.redefine.multimedia.photoselector.model;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.photoselector.config.ImagePickConfig;
import com.redefine.multimedia.photoselector.entity.IncapableCause;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.photoselector.entity.SelectionSpec;
import com.redefine.multimedia.photoselector.util.PathUtils;
import com.redefine.multimedia.photoselector.util.PhotoMetadataUtils;
import com.redefine.welike.base.resource.ResourceTool;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class SelectedItemCollection {

    public static final String STATE_SELECTION = "state_selection";
    public static final String EXPORT_STATE_SELECTION = "export_state_selection";
    public static final String STATE_COLLECTION_TYPE = "state_collection_type";
    /**
     * Empty collection
     */
    public static final int COLLECTION_UNDEFINED = 0x00;
    /**
     * Collection only with images
     */
    public static final int COLLECTION_IMAGE = 0x01;
    /**
     * Collection only with videos
     */
    public static final int COLLECTION_VIDEO = 0x01 << 1;
    /**
     * Collection with images and videos.
     */
    public static final int COLLECTION_MIXED = COLLECTION_IMAGE | COLLECTION_VIDEO;
    private final Context mContext;
    private Set<Item> mItems;
    private int mCollectionType = COLLECTION_UNDEFINED;
    private ImagePickConfig mConfig;

    public SelectedItemCollection(Context context) {
        mContext = context;
    }

    public void onCreate(Bundle bundle, Bundle saveState, ImagePickConfig config) {
        mConfig = config;
        if (bundle == null) {
            mItems = new LinkedHashSet<>();
        } else {
            List<Item> saved = bundle.getParcelableArrayList(STATE_SELECTION);
            if (CollectionUtil.isEmpty(saved)) {
                mItems = new LinkedHashSet<>();
            } else {
                mItems = new LinkedHashSet<>(saved);
            }
            mCollectionType = bundle.getInt(STATE_COLLECTION_TYPE, COLLECTION_UNDEFINED);
            refineCollectionType();
        }

        if (CollectionUtil.isEmpty(mItems) && saveState != null) {
            List<Item> saved = saveState.getParcelableArrayList(STATE_SELECTION);
            if (CollectionUtil.isEmpty(saved)) {
                mItems = new LinkedHashSet<>();
            } else {
                mItems = new LinkedHashSet<>(saved);
            }
            mCollectionType = saveState.getInt(STATE_COLLECTION_TYPE, COLLECTION_UNDEFINED);
            refineCollectionType();
        }
    }

    public void setDefaultSelection(List<Item> uris) {
        mItems.addAll(uris);
    }

    public void onSaveInstanceState(Bundle outState) {
        if (!CollectionUtil.isEmpty(mItems)) {
            outState.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mItems));
        }
        outState.putInt(STATE_COLLECTION_TYPE, mCollectionType);
    }

    public Bundle getDataWithBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mItems));
        bundle.putInt(STATE_COLLECTION_TYPE, mCollectionType);
        return bundle;
    }

    public boolean add(Item item) {
        if (typeConflict(item)) {
            throw new IllegalArgumentException("Can't select images and videos at the same time.");
        }

        if (mCollectionType == COLLECTION_UNDEFINED) {
            if (item.isImage()) {
                mCollectionType = COLLECTION_IMAGE;
            } else if (item.isVideo()) {
                mCollectionType = COLLECTION_VIDEO;
            }
        } else if (mCollectionType == COLLECTION_IMAGE) {
            if (item.isVideo()) {
                return false;
            }
        } else if (mCollectionType == COLLECTION_VIDEO) {
            if (item.isImage()) {
                return false;
            }
        }
        return mItems.add(item);
    }

    public boolean remove(Item item) {
        boolean removed = false;
        for (Item i : mItems) {
            if (TextUtils.equals(i.filePath, item.filePath)) {
                mItems.remove(i);
                removed = true;
                break;
            }
        }
        if (removed) {
            if (mItems.size() == 0) {
                mCollectionType = COLLECTION_UNDEFINED;
            } else {
                if (mCollectionType == COLLECTION_MIXED) {
                    refineCollectionType();
                }
            }
        }
        return removed;
    }

    public void overwrite(ArrayList<Item> items, int collectionType) {
        if (items.size() == 0) {
            mCollectionType = COLLECTION_UNDEFINED;
        } else {
            mCollectionType = collectionType;
        }
        mItems.clear();
        mItems.addAll(items);
    }


    public ArrayList<Item> asList() {
        return new ArrayList<>(mItems);
    }

    public List<Uri> asListOfUri() {
        List<Uri> uris = new ArrayList<>();
        for (Item item : mItems) {
            uris.add(item.getContentUri());
        }
        return uris;
    }

    public List<String> asListOfString() {
        List<String> paths = new ArrayList<>();
        for (Item item : mItems) {
            paths.add(PathUtils.getPath(mContext, item.getContentUri()));
        }
        return paths;
    }

    public boolean isEmpty() {
        return mItems == null || mItems.isEmpty();
    }

    public Set<Item> getmItems() {
        return mItems;
    }

    /**
     * 由于老数据兼容，只能用path来判断
     *
     * @param item
     * @return
     */
    public boolean isSelected(Item item) {
        for (Item i : mItems) {
            if (TextUtils.equals(i.filePath, item.filePath)) {
                return true;
            }
        }
        return false;
    }

    public IncapableCause isAcceptable(Item item) {
        if (typeConflict(item)) {
            return new IncapableCause(ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "picture_rule"));
        } else if (item.isImage() && new File(item.filePath).length() > mConfig.maxPicFileSize) {
            int maxSelectable = currentMaxSelectable(mConfig);
            String cause = ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "video_too_large");
            cause = String.format(cause, maxSelectable);
            return new IncapableCause(cause);
        } else if (item.isVideo() && new File(item.filePath).length() > mConfig.maxVideoFileSize) {
            String cause = ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "video_too_large");
            return new IncapableCause(cause);
        } else if (maxSelectableReached(mConfig)) {
            int maxSelectable = currentMaxSelectable(mConfig);
            String cause = ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "picture_message_max_num");
            if (mCollectionType == COLLECTION_VIDEO) {
                cause = ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "video_message_max_num");
            }
            cause = String.format(cause, maxSelectable);
            return new IncapableCause(cause);
        }

        return PhotoMetadataUtils.isAcceptable(mContext, item, mConfig);
    }

    public boolean maxSelectableReached(ImagePickConfig config) {
        return mItems.size() == currentMaxSelectable(config);
    }

    // depends
    public int currentMaxSelectable(ImagePickConfig config) {
        if (mCollectionType == COLLECTION_IMAGE) {
            return config.maxImageSelectable;
        } else if (mCollectionType == COLLECTION_VIDEO) {
            return config.maxVideoSelectable;
        } else {
            return config.maxImageSelectable;
        }
    }

    public int getCollectionType() {
        return mCollectionType;
    }

    private void refineCollectionType() {
        boolean hasImage = false;
        boolean hasVideo = false;
        for (Item i : mItems) {
            if (i.isImage() && !hasImage) hasImage = true;
            if (i.isVideo() && !hasVideo) hasVideo = true;
        }
        if (hasImage && hasVideo) {
            mCollectionType = COLLECTION_MIXED;
        } else if (hasImage) {
            mCollectionType = COLLECTION_IMAGE;
        } else if (hasVideo) {
            mCollectionType = COLLECTION_VIDEO;
        }
    }

    /**
     * Determine whether there will be conflict media types. A user can only select images and videos at the same time
     * while {@link SelectionSpec#mediaTypeExclusive} is set to false.
     */
    public boolean typeConflict(Item item) {
        return mConfig.mediaTypeExclusive
                && ((item.isImage() && (mCollectionType == COLLECTION_VIDEO || mCollectionType == COLLECTION_MIXED))
                || (item.isVideo() && (mCollectionType == COLLECTION_IMAGE || mCollectionType == COLLECTION_MIXED)));
    }

    public int count() {
        return mItems.size();
    }

    public int checkedNumOf(Item item) {
        int index = 0;
        for (Item i : mItems) {
            index++;
            if (TextUtils.equals(i.filePath, item.filePath)) {
                return index;
            }
        }
        return -1;
    }
}
