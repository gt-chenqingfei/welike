package com.redefine.welike.commonui.share;

import android.content.Context;
import android.widget.Toast;

import com.redefine.commonui.share.IShareCallbackListener;
import com.redefine.commonui.share.ShareManager;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.interceptor.AbstractInterceptor;
import com.redefine.commonui.share.interceptor.ShareApkInterceptor;
import com.redefine.commonui.share.request.ShareCountReportManager;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.commonui.share.sharemedel.SharePackageModel;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.LogEvent;
import com.redefine.welike.base.track.LogEventConstant;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.commonui.event.commonenums.ResultEnum;
import com.redefine.welike.commonui.event.helper.ShareEventHelper;
import com.redefine.welike.commonui.event.model.EventModel;
import com.redefine.welike.commonui.share.interceptor.CopyLinkInterceptor;
import com.redefine.welike.commonui.share.interceptor.ImageDownLoadInterceptor;
import com.redefine.welike.commonui.share.interceptor.ShareLinkInterceptor2;
import com.redefine.welike.commonui.share.interceptor.ShareTitleInterceptor;
import com.redefine.welike.commonui.share.interceptor.VideoDownloadInterceptor;
import com.redefine.welike.commonui.share.interceptor.WaterMarkInterceptor;
import com.redefine.welike.commonui.share.model.ShareTitleModel;
import com.redefine.welike.commonui.share.model.ShortLinkModel;
import com.redefine.welike.commonui.share.model.WaterMarkModel;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by nianguowang on 2018/6/20
 */
public class ShareManagerWrapper implements IShareCallbackListener {

    private Context mContext;
    private String mEntryType;
    private boolean mLoginShare;
    private boolean mShareVideo;
    private ShareModel mShareModel;
    private ShortLinkModel mShortLinkModel;
    private ShareTitleModel mShareTitleModel;
    private WaterMarkModel mWaterMarkModel;
    private EventModel mEventModel;
    private SharePackageFactory.SharePackage mSharePackage;
    private SharePackageModel[] mCustomMenus;
    private List<SharePackageModel> mAddedMenus;

    private ShareManagerWrapper(Builder builder) {
        mContext = builder.mContext;
        mEntryType = builder.mEntryType;
        mLoginShare = builder.mLoginShare;
        mShareVideo = builder.mShareVideo;
        mShareModel = builder.mShareModel;
        mEventModel = builder.mEventModel;
        mShortLinkModel = builder.mShortLinkModel;
        mShareTitleModel = builder.mShareTitleModel;
        mWaterMarkModel = builder.mWaterMarkModel;
        mSharePackage = builder.mSharePackage;
        mCustomMenus = builder.mCustomMenus;
        mAddedMenus = builder.mAddedMenus;
    }

    public void share() {
        ShareManager.getInstance().reset();
        ShareManager.getInstance().addInterceptor(new ShareLinkInterceptor2(mShortLinkModel));
        ShareManager.getInstance().addInterceptor(new ShareTitleInterceptor(mShareTitleModel));
        if (mShareVideo) {
            ShareManager.getInstance().addImageInterceptor(new VideoDownloadInterceptor(mContext/*, mWaterMarkModel*/));
        }
        ShareManager.getInstance().addImageInterceptor(new ImageDownLoadInterceptor(mContext));
        ShareManager.getInstance().addImageInterceptor(new WaterMarkInterceptor(mContext, mWaterMarkModel));
        if(mSharePackage == SharePackageFactory.SharePackage.WHATS_APP) {
            ShareManager.getInstance().addImageInterceptor(new CopyLinkInterceptor(mContext));
        }
        ShareManager.getInstance().addMenuList(mAddedMenus);
        //如果全部是自定义菜单，则不需要注册分享的callback。
        if (mCustomMenus != null) {
            ShareManager.getInstance().customMenu(mCustomMenus);
        }
        ShareManager.getInstance().registerShareCallback(this);
        ShareManager.getInstance().share(mContext, mShareModel, mSharePackage, mEntryType);
    }

    public void shareApk() {
        ShareManager.getInstance().reset();
        ShareManager.getInstance().addImageInterceptor(new ShareApkInterceptor(mContext));
        ShareManager.getInstance().registerShareCallback(this);
        ShareManager.getInstance().share(mContext, mShareModel, mSharePackage, mEntryType);
    }

    private void reportIfNeeded() {
        if (mShareModel != null && mShortLinkModel != null &&
                (mShareModel.getShareModelType() == ShareModel.SHARE_MODEL_TYPE_POST ||
                mShareModel.getShareModelType() == ShareModel.SHARE_MODEL_TYPE_VIDEO)) {
            ShareCountReportManager.getInstance().reportPostShare(mContext, mShortLinkModel.getShareId());
        }
    }

    public void addInteceptor(AbstractInterceptor interceptor) {
        ShareManager.getInstance().addInterceptor(interceptor);
    }

    public void addImageIntecepter(AbstractInterceptor interceptor) {
        ShareManager.getInstance().addImageInterceptor(interceptor);
    }

    public void registerShareCallback(IShareCallbackListener callback) {
        ShareManager.getInstance().registerShareCallback(callback);
    }

    private void showToast(final String msg) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void shareCompleted(SharePackageFactory.SharePackage sharePackage) {
//                showToast("success");
        ShareManager.getInstance().unregisterShareCallback(this);
        reportIfNeeded();

        LogEvent logEvent = new LogEvent();
        logEvent.id = LogEventConstant.LOG_EVENT_SHARE_RESULT_SUCCESS;
        EventBus.getDefault().post(logEvent);

        if (mEventModel != null) {
            AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_SHARE);
            EventLog1.Share.report1(ShareEventHelper.convertSharePackage(sharePackage), mEventModel.contentType, mEventModel.postType,
                    mEventModel.videoPostType, mEventModel.shareFrom, mEventModel.popPage, ResultEnum.SUCCESS,
                    mEventModel.poolCode, mEventModel.operationType, mEventModel.source, mEventModel.postId, mEventModel.postLanguage, mEventModel.postTags,
                    mEventModel.postUid, mEventModel.rootPostId, mEventModel.rootPostUid, mEventModel.rootPostLanguage, mEventModel.rootPostTags, mEventModel.sequenceId,mEventModel.reclogs);
        }
    }

    @Override
    public void shareFailed(SharePackageFactory.SharePackage sharePackage) {
        ShareManager.getInstance().unregisterShareCallback(this);
//        showToast("Canceled");

        LogEvent logEvent = new LogEvent();
        logEvent.id = LogEventConstant.LOG_EVENT_SHARE_RESULT_FAIL;
        EventBus.getDefault().post(logEvent);

        if (mEventModel != null) {
            AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_SHARE);
            EventLog1.Share.report1(ShareEventHelper.convertSharePackage(sharePackage), mEventModel.contentType, mEventModel.postType,
                    mEventModel.videoPostType, mEventModel.shareFrom, mEventModel.popPage, ResultEnum.FAIL,
                    mEventModel.poolCode, mEventModel.operationType, mEventModel.source, mEventModel.postId, mEventModel.postLanguage, mEventModel.postTags,
                    mEventModel.postUid, mEventModel.rootPostId, mEventModel.rootPostUid, mEventModel.rootPostLanguage, mEventModel.rootPostTags, mEventModel.sequenceId,mEventModel.reclogs);
        }
    }

    @Override
    public void shareCanceled() {
        ShareManager.getInstance().unregisterShareCallback(this);
        LogEvent logEvent = new LogEvent();
        logEvent.id = LogEventConstant.LOG_EVENT_SHARE_RESULT_UNKUNOW;
        EventBus.getDefault().post(logEvent);

    }

    public static class Builder {
        private Context mContext;
        private String mEntryType;
        private ShareModel mShareModel;
        private ShortLinkModel mShortLinkModel;
        private ShareTitleModel mShareTitleModel;
        private WaterMarkModel mWaterMarkModel;
        private EventModel mEventModel;
        private boolean mLoginShare;
        private boolean mShareVideo;
        private SharePackageFactory.SharePackage mSharePackage;
        private SharePackageModel[] mCustomMenus;
        private List<SharePackageModel> mAddedMenus;

        public Builder with(Context context) {
            mContext = context;
            return this;
        }

        /**
         * 用户是登陆分享还是未登陆分享，会影响分享的链接。
         * @param login
         * @return
         */
        public Builder login(boolean login) {
            mLoginShare = login;
            return this;
        }

        /**
         * 需要分享的Model
         * @param shareModel
         * @return
         */
        public Builder shareModel(ShareModel shareModel) {
            mShareModel = shareModel;
            return this;
        }

        /**
         * 生成短链的Model
         * @param shortLinkModel
         * @return
         */
        public Builder shortLinkModel(ShortLinkModel shortLinkModel) {
            mShortLinkModel = shortLinkModel;
            return this;
        }

        /**
         * 分享标题的Model
         * @param shareTitleModel
         * @return
         */
        public Builder shareTitleModel(ShareTitleModel shareTitleModel) {
            mShareTitleModel = shareTitleModel;
            return this;
        }

        /**
         * 图片及视频添加水印的Model
         * @param waterMarkModel
         * @return
         */
        public Builder waterMarkModel(WaterMarkModel waterMarkModel) {
            mWaterMarkModel = waterMarkModel;
            return this;
        }

        public Builder eventModel(EventModel eventModel) {
            mEventModel = eventModel;
            return this;
        }

        /**
         * 如果需要直接不需要吊起分享页面，直接分享到某个渠道的话，传入渠道对应的SharePackage
         * @param sharePackage
         * @return
         */
        public Builder sharePackage(SharePackageFactory.SharePackage sharePackage) {
            mSharePackage = sharePackage;
            return this;
        }

        public Builder shareCustomMenus(SharePackageModel... sharePackageModels) {
            this.mCustomMenus = sharePackageModels;
            return this;
        }

        public Builder addMenuList(List<SharePackageModel> menus) {
            this.mAddedMenus = menus;
            return this;
        }

        public Builder shareVideo(boolean shareVideo) {
            this.mShareVideo = shareVideo;
            return this;
        }

        /**
         * 分享来源，可选。
         * @param entryType
         * @return
         */
        public Builder entryType(String entryType) {
            mEntryType = entryType;
            return this;
        }

        public ShareManagerWrapper build() {
            return new ShareManagerWrapper(this);
        }
    }
}
