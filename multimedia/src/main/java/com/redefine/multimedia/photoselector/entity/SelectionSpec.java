package com.redefine.multimedia.photoselector.entity;

import android.content.pm.ActivityInfo;
import android.support.annotation.StyleRes;

import com.redefine.multimedia.photoselector.filter.Filter;
import com.redefine.multimedia.photoselector.listener.OnCheckedListener;
import com.redefine.multimedia.photoselector.listener.OnSelectedListener;

import java.util.List;
import java.util.Set;

public final class SelectionSpec {

    public Set<MimeType> mimeTypeSet = MimeType.ofAll();
    public boolean mediaTypeExclusive = true;
    public boolean showSingleMediaType;
    public int orientation;
    public boolean countable = true;
    public int maxSelectable = 9;
    public int maxImageSelectable = 9;
    public int maxVideoSelectable = 1;
    public List<Filter> filters;
    public boolean capture = true;
    public int spanCount = 4;
    public int gridExpectedSize;
    public float thumbnailScale = 0.5f;
    public boolean hasInited;
    public boolean originalable;
    public int originalMaxSize;

    private SelectionSpec() {
    }

    public static SelectionSpec getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static SelectionSpec getCleanInstance() {
        SelectionSpec selectionSpec = getInstance();
        selectionSpec.reset();
        return selectionSpec;
    }

    private void reset() {
        mimeTypeSet = MimeType.ofAll();
        mediaTypeExclusive = true;
        showSingleMediaType = false;
        orientation = 0;
        countable = true;
        maxSelectable = 9;
        maxImageSelectable = 9;
        maxVideoSelectable = 1;
        filters = null;
        capture = true;
        spanCount = 4;
        gridExpectedSize = 0;
        thumbnailScale = 0.5f;
        hasInited = true;
        originalable = false;
        originalMaxSize = Integer.MAX_VALUE;
    }

    public boolean singleSelectionModeEnabled() {
        return !countable && (maxSelectable == 1 || (maxImageSelectable == 1 && maxVideoSelectable == 1));
    }

    public boolean needOrientationRestriction() {
        return orientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    public boolean onlyShowImages() {
        return showSingleMediaType && MimeType.ofImage().containsAll(mimeTypeSet);
    }

    public boolean onlyShowVideos() {
        return showSingleMediaType && MimeType.ofVideo().containsAll(mimeTypeSet);
    }

    private static final class InstanceHolder {
        private static final SelectionSpec INSTANCE = new SelectionSpec();
    }
}
