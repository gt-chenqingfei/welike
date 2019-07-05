package com.redefine.welike.business.startup.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

import org.json.JSONObject;

public class ThirdBindRequest extends BaseRequest {

    public ThirdBindRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);

        setHost("user/bind", true);
    }

    public void req(String type, String thirdToken, RequestCallback callback) throws Exception {

        JSONObject bodyJSON = new JSONObject();
        bodyJSON.put("type", type);
        bodyJSON.put("token", thirdToken);
        setBodyData(bodyJSON.toString());
        req(callback);
    }

    public void req(String type, String phone, String code, RequestCallback callback) throws Exception {

        JSONObject bodyJSON = new JSONObject();
        bodyJSON.put("type", type);
        bodyJSON.put("phone", phone);
        bodyJSON.put("valideCode", code);
        setBodyData(bodyJSON.toString());
        req(callback);
    }

    public void req(String type, String payload, String signature, String signatureAlgorithm, RequestCallback callback) throws Exception {

        JSONObject bodyJSON = new JSONObject();
        bodyJSON.put("type", type);
        bodyJSON.put("payload", payload);
        bodyJSON.put("signature", signature);
        bodyJSON.put("signatureAlgorithm", signatureAlgorithm);
        setBodyData(bodyJSON.toString());
        req(callback);
    }

}
