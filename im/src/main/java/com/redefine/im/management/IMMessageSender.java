package com.redefine.im.management;

import android.text.TextUtils;

import com.redefine.im.bean.IMAudioMessage;
import com.redefine.im.bean.IMSession;
import com.redefine.im.bean.IMMessageBase;
import com.redefine.im.bean.IMPicMessage;
import com.redefine.im.bean.IMVideoMessage;
import com.redefine.im.cache.IMMessageCache;
import com.redefine.im.service.IMService;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.uploading.UploadingManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.schedulers.Schedulers;

/**
 * Created by liubin on 2018/2/6.
 */

public class IMMessageSender implements IMMessageUploadTask.MessageUploadingTaskListener {
    private IMService imService;
    private IMMessageSenderCallback listener;
    private ConcurrentHashMap<String, UploadingPackage> messagesQueue = new ConcurrentHashMap<>();

    public interface IMMessageSenderCallback {

        void onUploadMessageAttachmentProcess(IMMessageBase message, final float process);
        void onUploadMessageAttachmentFailed(IMMessageBase message);
        void onSendMessageSessionUpdated(IMMessageBase message, String sid);
    }

    private class UploadingPackage {
        private IMMessageUploadTask task;
        private IMMessageBase message;
    }

    IMMessageSender(IMService imService) {
        this.imService = imService;
    }

    public void setListener(IMMessageSenderCallback listener) {
        this.listener = listener;
    }

    int sendMessage(final IMMessageBase message, final IMSession session) {
        if (session.getSingleUid()==null){ //Trash! sometime it will be null.
            return ErrorCode.ERROR_IM_SEND_MSG_RESOURCE_INVALID;
        }
        if (messagesQueue.get(message.getMid()) == null) {
            Account account = AccountManager.getInstance().getAccount();
            message.setSessionNickName(session.getNickName());
            message.setSessionHead(session.getHead());
            message.setSessionType(session.getSessionType());
            message.setRemoteUid(session.getSingleUid());
            message.setSenderNickName(account.getNickName());
            message.setSenderHead(account.getHeadUrl());
            IMMessageCache.getInstance().sendMessage(message, session, new IMMessageCache.IMMessageCacheSendCallback() {

                @Override
                public void onSendMessageInserted(IMMessageBase message, String sid) {
                    if (listener != null) {
                        listener.onSendMessageSessionUpdated(message, sid);
                    }
                }

            });

            byte msgType = message.getType();
            if (msgType == IMMessageBase.MESSAGE_TYPE_TXT) {
                if (imService != null) {
                    imService.sendMessage(message);
                    return ErrorCode.ERROR_SUCCESS;
                } else {
                    return ErrorCode.ERROR_IM_SERVICE_MISS;
                }
            } else if (msgType != IMMessageBase.MESSAGE_TYPE_UNKNOWN) {
                String mainFileName = null;
                String subFileName = null;
                String objectType = null;
                if (message.getType() == IMMessageBase.MESSAGE_TYPE_PIC) {
                    mainFileName = ((IMPicMessage)message).getLocalFileName();
                    objectType = UploadingManager.UPLOAD_TYPE_IMG;
                } else if (message.getType() == IMMessageBase.MESSAGE_TYPE_AUDIO) {
                    mainFileName = ((IMAudioMessage)message).getLocalFileName();
                } else if (message.getType() == IMMessageBase.MESSAGE_TYPE_VIDEO) {
                    mainFileName = ((IMVideoMessage)message).getLocalFileName();
                    subFileName = ((IMVideoMessage)message).getLocalThumbFileName();
                    objectType = UploadingManager.UPLOAD_TYPE_VIDEO;
                }
                IMMessageUploadTask task = new IMMessageUploadTask();
                if (task.upload(message.getMid(), mainFileName, subFileName, objectType)) {
                    task.setListener(this);
                    UploadingManager.getInstance().register(task);
                    UploadingPackage msgPackage = new UploadingPackage();
                    msgPackage.task = task;
                    msgPackage.message = message;
                    messagesQueue.put(message.getMid(), msgPackage);
                    return ErrorCode.ERROR_SUCCESS;
                } else {
                    return ErrorCode.ERROR_IM_SEND_MSG_RESOURCE_INVALID;
                }
            } else {
                return ErrorCode.ERROR_IM_MSG_NOT_SUPPORT;
            }
        } else {
            return ErrorCode.ERROR_IM_DUPLICATE_SEND_MSG;
        }
    }

    void cancelAllSendingMessages(String sid) {
        List<String> removeList = new ArrayList<>();
        for (ConcurrentHashMap.Entry<String, UploadingPackage> entry : messagesQueue.entrySet()) {
            UploadingPackage uploadingPackage = entry.getValue();
            if (uploadingPackage.message != null) {
                if (TextUtils.equals(uploadingPackage.message.getSid(), sid)) {
                    removeList.add(uploadingPackage.message.getMid());
                }
            }
        }
        if (removeList.size() > 0) {
            for (String mid : removeList) {
                messagesQueue.remove(mid);
            }
        }
    }

    void handleMessageSendResult(String oldMid, IMMessageBase message, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            IMMessageCache.getInstance().refreshNetworkMessage(oldMid, message.getMid(), message.getTime(), message.isGreet(), IMMessageBase.MESSAGE_STATUS_SENT);
        } else {
            messagesQueue.clear();
        }
    }

    @Override
    public void onMessageUploadingTaskProcess(String mid, float process) {
        UploadingPackage pkg = messagesQueue.get(mid);
        if (pkg != null) {
            if (listener != null) {
                listener.onUploadMessageAttachmentProcess(pkg.message, process);
            }
        }
    }

    @Override
    public void onMessageUploadingTaskEnd(String mid, boolean successed) {
        UploadingPackage pkg = messagesQueue.remove(mid);
        if (pkg != null) {
            IMMessageBase message = pkg.message;
            pkg.task.setListener(null);
            UploadingManager.getInstance().unregister(pkg.task);
            if (successed) {
                String mainUrl = pkg.task.getMainObjectUrl();
                String subUrl = pkg.task.getSubObjectUrl();
                boolean supportType = false;
                if (message instanceof IMPicMessage) {
                    IMPicMessage picMessage = (IMPicMessage)message;
                    picMessage.setPicLargeUrl(mainUrl);
                    picMessage.setPicUrl(mainUrl);
                    supportType = true;
                } else if (message instanceof IMAudioMessage) {
                    IMAudioMessage audioMessage = (IMAudioMessage)message;
                    audioMessage.setAudioUrl(mainUrl);
                    supportType = true;
                } else if (message instanceof IMVideoMessage) {
                    supportType = true;
                    IMVideoMessage videoMessage = (IMVideoMessage)message;
                    videoMessage.setVideo(mainUrl);
                    videoMessage.setThumb(subUrl);
                }
                if (imService != null && supportType) {
                    imService.sendMessage(message);
                } else {
                    if (listener != null) {
                        listener.onUploadMessageAttachmentFailed(message);
                    }
                }
            } else {
                if (listener != null) {
                    listener.onUploadMessageAttachmentFailed(message);
                }
            }
        }
    }

}
