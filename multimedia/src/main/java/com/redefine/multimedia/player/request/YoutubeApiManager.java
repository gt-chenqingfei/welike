package com.redefine.multimedia.player.request;

import android.content.Context;

import com.redefine.commonui.share.CommonListener;

import java.util.List;

public class YoutubeApiManager {


    public static void getYoutubeUrl(Context context, String id, final IRequestCallback commonListener) {
        YoutubeApiRequest request =  new YoutubeApiRequest();
        request.getYoutubeUrl(context, id, commonListener);

    }

    public static interface IRequestCallback {
        void onError(int errCode);
        void onSuccess(YoutubeVideoInfo result);
    }
}
