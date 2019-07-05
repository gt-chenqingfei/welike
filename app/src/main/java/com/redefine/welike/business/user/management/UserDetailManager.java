package com.redefine.welike.business.user.management;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.UserDetailRequest;
import com.redefine.welike.business.browse.management.request.UserDetailRequest2;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.parser.UserParser;
import com.redefine.welike.business.user.management.request.UserAlbumsRequest;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/1/21.
 */

public class UserDetailManager implements RequestCallback {
    private BaseRequest detailRequest;
    private UserAlbumsRequest albumsRequest;
    private UserDetailCallback detailListener;
    private UserAlbumsCallback albumsListener;
    private String albumsCursor;
    private int albumActionState = GlobalConfig.LIST_ACTION_NONE;

    public static class UserAlbumPic {
        private String thumbnail;
        private String url;
        private String id;

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

    public interface UserDetailCallback {

        void onContactDetailCompleted(UserDetailManager manager, User user, int errCode);

    }

    public interface UserAlbumsCallback {

        void onRefreshUserAlbums(UserDetailManager manager, List<UserAlbumPic> albums, int errCode);

        void onReceiveHisUserAlbums(UserDetailManager manager, List<UserAlbumPic> albums, boolean last, int errCode);

    }

    public void setDetailListener(UserDetailCallback listener) {
        detailListener = listener;
    }

    public void setAlbumsListener(UserAlbumsCallback listener) {
        albumsListener = listener;
    }

    public void loadContactDetail(String uid) {
        if (detailRequest != null) return;

        detailRequest = new UserDetailRequest(uid, MyApplication.getAppContext());
        try {
            detailRequest.req(this);
        } catch (Exception e) {
            detailRequest = null;
            e.printStackTrace();
            final int errCode = ErrorCode.networkExceptionToErrCode(e);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (detailListener != null) {
                        detailListener.onContactDetailCompleted(UserDetailManager.this, null, errCode);
                    }
                }

            });
        }
    }

    public void loadContactDetail4H5(String uid) {
        if (detailRequest != null || TextUtils.isEmpty(uid)) return;

        detailRequest = new UserDetailRequest2(uid, MyApplication.getAppContext());
        try {
            detailRequest.req(this);
        } catch (Exception e) {
            detailRequest = null;
            e.printStackTrace();
            final int errCode = ErrorCode.networkExceptionToErrCode(e);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (detailListener != null) {
                        detailListener.onContactDetailCompleted(UserDetailManager.this, null, errCode);
                    }
                }

            });
        }
    }

    public void refreshUserAlbums(String uid) {
        if (albumsRequest != null) return;

        albumActionState = GlobalConfig.LIST_ACTION_REFRESH;
        albumsCursor = null;
        albumsRequest = new UserAlbumsRequest(MyApplication.getAppContext());
        try {
            albumsRequest.refresh(uid, this);
        } catch (Exception e) {
            e.printStackTrace();
            final int errCode = ErrorCode.networkExceptionToErrCode(e);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (albumsListener != null) {
                        albumsListener.onRefreshUserAlbums(UserDetailManager.this, null, errCode);
                    }
                }

            });
        }
    }

    public void hisUserAlbums(String uid) {
        if (albumsRequest != null) return;

        albumActionState = GlobalConfig.LIST_ACTION_HIS;
        if (!TextUtils.isEmpty(albumsCursor)) {
            albumsRequest = new UserAlbumsRequest(MyApplication.getAppContext());
            try {
                albumsRequest.his(uid, albumsCursor, this);
            } catch (Exception e) {
                e.printStackTrace();
                final int errCode = ErrorCode.networkExceptionToErrCode(e);
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        if (albumsListener != null) {
                            albumsListener.onReceiveHisUserAlbums(UserDetailManager.this, null, false, errCode);
                        }
                    }

                });
            }
        } else {
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (albumsListener != null) {
                        albumsListener.onReceiveHisUserAlbums(UserDetailManager.this, null, true, ErrorCode.ERROR_SUCCESS);
                    }
                }

            });
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (detailRequest == request) {
            detailRequest = null;
            final int err = errCode;
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (detailListener != null) {
                        detailListener.onContactDetailCompleted(UserDetailManager.this, null, err);
                    }
                }

            });
        } else if (albumsRequest == request) {
            albumsRequest = null;
            final int err = errCode;
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (albumsListener != null) {
                        if (albumActionState == GlobalConfig.LIST_ACTION_HIS) {
                            albumsListener.onReceiveHisUserAlbums(UserDetailManager.this, null, false, err);
                        } else if (albumActionState == GlobalConfig.LIST_ACTION_REFRESH) {
                            albumsListener.onRefreshUserAlbums(UserDetailManager.this, null, err);
                        }
                    }
                }

            });
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (detailRequest == request) {
            detailRequest = null;
            final User user = UserParser.parseUser(result);
            Account account = AccountManager.getInstance().getAccount();
            //boolean needUpdatePosts = true;
            if (account == null) {
                //needUpdatePosts = false;
            } else {
                if (user != null) {//todo
                    if (TextUtils.equals(user.getUid(), account.getUid())) {
                        //needUpdatePosts = false;
                        account.setMyLikedPostsCount(user.getMyLikedPostsCount());
                        account.setLikedMyPostsCount(user.getLikedMyPostsCount());
                        account.setFollowedUsersCount(user.getFollowedUsersCount());
                        account.setFollowUsersCount(user.getFollowUsersCount());
                        account.setPostsCount(user.getPostsCount());
                        account.setVip(user.getVip());
                        account.setSex(user.getSex());
                        account.setIntroduction(user.getIntroduction());
                        account.setIntrests(user.getIntrests());
                        account.setAllowUpdateNickName(user.isAllowUpdateNickName());
                        account.setNextUpdateNickNameDate(user.getNextUpdateNickNameDate());
                        AccountManager.getInstance().updateAccount(account);
                    } else if (ContactsManager.getInstance().hasContact(user.getUid())) {
                        //needUpdatePosts = false;
                    }
                } else {
                    //needUpdatePosts = false;
                }
            }
//            if (needUpdatePosts) {
//                HotCache.getInstance().updatePostsStatus(user);
//            }
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (detailListener != null) {
                        detailListener.onContactDetailCompleted(UserDetailManager.this, user, ErrorCode.ERROR_SUCCESS);
                    }
                }

            });
        } else if (albumsRequest == request) {
            albumsRequest = null;
            final List<UserAlbumPic> albums = UserParser.parseUserAlbums(result);
            albumsCursor = UserParser.parseCursor(result);
            final boolean last = TextUtils.isEmpty(albumsCursor);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (albumsListener != null) {
                        if (albumActionState == GlobalConfig.LIST_ACTION_HIS) {
                            albumsListener.onReceiveHisUserAlbums(UserDetailManager.this, albums, last, ErrorCode.ERROR_SUCCESS);
                        } else if (albumActionState == GlobalConfig.LIST_ACTION_REFRESH) {
                            albumsListener.onRefreshUserAlbums(UserDetailManager.this, albums, ErrorCode.ERROR_SUCCESS);
                        }
                    }
                }

            });
        }
    }
}
