package com.redefine.im.service.socket;

import java.util.List;

public interface ConnectionManagerCallback {

    void onConnectionChannelReady(String channelId);
    void onConnectionMessagesSentResult(String channelId, List<SocketData> socketDataList);
    void onConnectionMessageSentFailed(String channelId, SocketData socketData, int errCode);
    void onConnectionTokenInvalid();
    void onConnectionChannelReceived(String channelId, List<SocketData> socketDataList);

}
