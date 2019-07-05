package com.redefine.welike.business.assignment.management;

import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by nianguowang on 2018/5/8
 */
public class HtmlParser {

    private static final String META_ATTR_VALUE_TITLE = "wk-title";
    private static final String META_ATTR_VALUE_DESCRIPTION = "wk-description";
    private static final String META_ATTR_VALUE_ICON = "wk-icon";
    private static final String META_ATTR_VALUE_SHARELINK = "wk-sharelink";

    private static final String META_ATTR_CONTENT = "content";
    private static final String META_ATTR_NAME = "name";

    public interface OnParseCompleteCallback {
        void onComplete(boolean success, String title, String url, String summary, String imagePath);
    }

    public void parseHtml(final String url, final OnParseCompleteCallback callback) {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                String title = "", shareUrl = "", summary = "", imagePath = "";
                try {
                    Document document = Jsoup.connect(url).get();
                    Elements meta = document.getElementsByTag("meta");
                    for (Element element : meta) {
                        String wkTitle = element.attr(META_ATTR_NAME);
                        if(TextUtils.equals(wkTitle, META_ATTR_VALUE_TITLE)) {
                            title = element.attr(META_ATTR_CONTENT);
                            continue;
                        }

                        String wkDescription = element.attr(META_ATTR_NAME);
                        if(TextUtils.equals(wkDescription, META_ATTR_VALUE_DESCRIPTION)) {
                            summary = element.attr(META_ATTR_CONTENT);
                            continue;
                        }

                        String wkIcon = element.attr(META_ATTR_NAME);
                        if(TextUtils.equals(wkIcon, META_ATTR_VALUE_ICON)) {
                            imagePath = element.attr(META_ATTR_CONTENT);
                            continue;
                        }

                        String wkUrl = element.attr(META_ATTR_NAME);
                        if(TextUtils.equals(wkUrl, META_ATTR_VALUE_SHARELINK)) {
                            shareUrl = element.attr(META_ATTR_CONTENT);
                        }
                    }
                    if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(shareUrl)) {
                        notifyListener(callback, true, title, shareUrl, summary, imagePath);
                    } else {
                        notifyListener(callback, false, title, shareUrl, summary, imagePath);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    notifyListener(callback, false, title, shareUrl, summary, imagePath);
                }
            }
        });

    }

    private void notifyListener(final OnParseCompleteCallback callback, final boolean success, final String title, final String shareUrl, final String summary, final String imagePath) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                if(callback != null) {
                    callback.onComplete(success, title, shareUrl, summary, imagePath);
                }
            }
        });
    }
}
