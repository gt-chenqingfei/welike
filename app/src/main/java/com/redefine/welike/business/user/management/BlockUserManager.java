package com.redefine.welike.business.user.management;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.user.management.request.BlockUserRequest;
import com.redefine.welike.business.user.management.request.UnBlockUserRequest;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class BlockUserManager extends BroadcastManagerBase implements RequestCallback {
    private final List<BlockUserRequest> blockRequests = new ArrayList<>();
    private final List<UnBlockUserRequest> unBlockRequests = new ArrayList<>();

    public interface BlockUserCallback {

        void onBlockCompleted(String uid, int errCode);
        void onUnBlockCompleted(String uid, int errCode);

    }

    private static class BlockUserManagerHolder {
        public static BlockUserManager instance = new BlockUserManager();
    }

    private BlockUserManager() {}

    public static BlockUserManager getInstance() { return BlockUserManagerHolder.instance; }

    public void register(BlockUserCallback listener) {
        super.register(listener);
    }

    public void unregister(BlockUserCallback listener) {
        super.unregister(listener);
    }

    public void block(String uid) {
        if (blockActionInPool(uid)) return;
        BlockUserRequest request = new BlockUserRequest(uid, MyApplication.getAppContext());
        try {
            request.req(this);
            synchronized (blockRequests) {
                blockRequests.add(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            broadcastBlockCompleted(uid, ErrorCode.networkExceptionToErrCode(e));
        }
    }

    public void unBlock(String uid) {
        if (unBlockActionInPool(uid)) return;

        UnBlockUserRequest request = new UnBlockUserRequest(uid, MyApplication.getAppContext());
        try {
            request.req(this);
            synchronized (unBlockRequests) {
                unBlockRequests.add(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            broadcastUnBlockCompleted(uid, ErrorCode.networkExceptionToErrCode(e));
        }
    }

    public boolean isBlockRequesting(String uid) {
        return blockActionInPool(uid);
    }

    public boolean isUnBlockRequesting(String uid) {
        return unBlockActionInPool(uid);
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request instanceof BlockUserRequest) {
            BlockUserRequest blockUserRequest = (BlockUserRequest)request;
            synchronized(blockRequests) {
                blockRequests.remove(blockUserRequest);
            }
            String uid = getBlockUidFromRequest(blockUserRequest);
            broadcastBlockCompleted(uid, errCode);
        } else if (request instanceof UnBlockUserRequest) {
            UnBlockUserRequest unBlockUserRequest = (UnBlockUserRequest)request;
            synchronized(unBlockRequests) {
                unBlockRequests.remove(unBlockUserRequest);
            }
            String uid = getUnBlockUidFromRequest(unBlockUserRequest);
            broadcastUnBlockCompleted(uid, errCode);
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request instanceof BlockUserRequest) {
            BlockUserRequest blockUserRequest = (BlockUserRequest)request;
            synchronized(blockRequests) {
                blockRequests.remove(blockUserRequest);
            }

            String uid = getBlockUidFromRequest(blockUserRequest);
            broadcastBlockCompleted(uid, ErrorCode.ERROR_SUCCESS);
        } else if (request instanceof UnBlockUserRequest) {
            UnBlockUserRequest unBlockUserRequest = (UnBlockUserRequest)request;
            synchronized(unBlockRequests) {
                unBlockRequests.remove(unBlockUserRequest);
            }
            String uid = getUnBlockUidFromRequest(unBlockUserRequest);
            broadcastUnBlockCompleted(uid, ErrorCode.ERROR_SUCCESS);
        }
    }

    private void broadcastBlockCompleted(final String uid, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof BlockUserCallback) {
                            BlockUserCallback listener = (BlockUserCallback)l;
                            listener.onBlockCompleted(uid, errCode);
                        }
                    }
                }
            }

        });
    }

    private void broadcastUnBlockCompleted(final String uid, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof BlockUserCallback) {
                            BlockUserCallback listener = (BlockUserCallback)l;
                            listener.onUnBlockCompleted(uid, errCode);
                        }
                    }
                }
            }

        });
    }

    private String getBlockUidFromRequest(BlockUserRequest request) {
        String uid = null;
        Object o = request.getUserInfo(BlockUserRequest.BLOCK_UID_KEY);
        if (o != null && o instanceof String) {
            uid = (String)o;
        }
        return uid;
    }

    private String getUnBlockUidFromRequest(UnBlockUserRequest request) {
        String uid = null;
        Object o = request.getUserInfo(UnBlockUserRequest.UN_BLOCK_UID_KEY);
        if (o != null && o instanceof String) {
            uid = (String)o;
        }
        return uid;
    }

    private boolean blockActionInPool(String uid) {
        synchronized (blockRequests) {
            for (int i = 0; i < blockRequests.size(); i++) {
                BlockUserRequest request = blockRequests.get(i);
                String ud = getBlockUidFromRequest(request);
                if (!TextUtils.isEmpty(ud)) {
                    if (TextUtils.equals(ud, uid)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean unBlockActionInPool(String uid) {
        synchronized (unBlockRequests) {
            for (int i = 0; i < unBlockRequests.size(); i++) {
                UnBlockUserRequest request = unBlockRequests.get(i);
                String ud = getUnBlockUidFromRequest(request);
                if (!TextUtils.isEmpty(ud)) {
                    if (TextUtils.equals(ud, uid)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
