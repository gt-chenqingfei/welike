package com.redefine.commonui.h5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.redefine.foundation.framework.Event;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.resource.ResourceTool;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liwenbo on 2018/3/22.
 */

public class WeLikeJsBridge {


    public static final String TAG = "WeLikeJsBridge";

    private Context mContext;
    public WeLikeJsBridge(Context context) {
        mContext = context;
    }

    public void callWebView(WebView webView, String jsCall) {
        webView.loadUrl(jsCall);
    }

    @JavascriptInterface
    public void closePage() {
        EventBus.getDefault().post(new Event(EventIdConstant.POP_TOP_PAGE, null));
        if (mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    }

    @JavascriptInterface
    public void share(String title, String shareUrl) {
        if(mContext == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, title + "\t" + shareUrl);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void startMission(int type) {
        MissionDelegate.INSTANCE.startMission(type);
    }

}
