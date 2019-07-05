package com.redefine.welike.statistical.pagename;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nianguowang on 2018/4/27
 */
public class ActivityNameParser implements INameParser {

    @Override
    public Map<String, String> parse(Context context) {
        Map<String, String> nameMap = new HashMap<>();
        XmlResourceParser xml = context.getResources().getXml(com.redefine.welike.base.R.xml.analytics_page);
        try {
            int event = xml.getEventType();
            while (XmlPullParser.END_DOCUMENT != event) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String name = xml.getName();
                        if(TextUtils.equals(name, "screenName")) {
                            String activityName = xml.getAttributeValue(null, "name");
                            String activityValue = xml.nextText();
                            nameMap.put(activityName, activityValue);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                    default:
                        break;
                }
                event = xml.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            xml.close();
        } catch (IOException e) {
            e.printStackTrace();
            xml.close();
        }
        return nameMap;
    }
}
