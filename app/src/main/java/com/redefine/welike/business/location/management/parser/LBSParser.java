package com.redefine.welike.business.location.management.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.business.location.management.bean.LBSUser;
import com.redefine.welike.business.location.management.bean.Location;
import com.redefine.welike.business.location.management.bean.PoiInfo;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.parser.UserParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2018/3/26.
 */

public class LBSParser {

    public static List<PoiInfo> parserPoiList(JSONObject result) {
        JSONArray placesJSON = null;
        try {
            placesJSON = result.getJSONArray("places");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (placesJSON != null && placesJSON.size() > 0) {
            List<PoiInfo> poiInfoList = new ArrayList<>();
            for (int i = 0; i < placesJSON.size(); i++) {
                JSONObject placeJSON = placesJSON.getJSONObject(i);
                try {
                    int feedCount = placeJSON.getIntValue("feedCount");
                    int userCount = placeJSON.getIntValue("userCount");
                    String name = placeJSON.getString("name");
                    String placeId = placeJSON.getString("placeId");
                    double lat = placeJSON.getDoubleValue("lat");
                    double lng = placeJSON.getDoubleValue("lng");

                    Location location = new Location();
                    location.setPlace(name);
                    location.setPlaceId(placeId);
                    location.setLatitude(lat);
                    location.setLongitude(lng);

                    PoiInfo poiInfo = new PoiInfo();
                    poiInfo.setLocation(location);
                    poiInfo.setFeedCount(feedCount);
                    poiInfo.setUserCount(userCount);
                    poiInfoList.add(poiInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return poiInfoList;
        }
        return null;
    }

    public static List<LBSUser> parserLBSUser(JSONObject result) {
        JSONArray usersJSON = null;
        try {
            usersJSON = result.getJSONArray("users");
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<LBSUser> lbsUsers = null;
        if (usersJSON != null && usersJSON.size() > 0) {
            lbsUsers = new ArrayList<>();
            for (int i = 0; i < usersJSON.size(); i++) {
                try {
                    JSONObject userJSON = usersJSON.getJSONObject(i);
                    JSONObject uJSON = userJSON.getJSONObject("user");
                    User u = UserParser.parseUser(uJSON);
                    if (u == null) {
                        continue;
                    }
                    long passTime = userJSON.getLongValue("passTime");
                    LBSUser user = new LBSUser();
                    user.setPassTime(passTime);
                    user.setUser(u);
                    lbsUsers.add(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return lbsUsers;
    }

}
