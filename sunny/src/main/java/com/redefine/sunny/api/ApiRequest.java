package com.redefine.sunny.api;

import android.text.TextUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Model for Request.
 * Created by ningdai on 16/7/22.
 */
public class ApiRequest {

    public String action;
    public HttpMethod method;

    public HashMap<String, Object> params = new HashMap<>();

    public ArrayList<FileParam> fileParams = new ArrayList<>();

    public HashMap<String,String> headers = new HashMap<>();

    private ApiRequest(String action, HttpMethod method) {
        this.action = action;
        this.method = method;
    }

    public static ApiRequest c(String action, HttpMethod method) {
        return new ApiRequest(action, method);
    }

    public ApiRequest add(Annotation annotation, Object arg) throws Exception {
        if (annotation instanceof PARAM) {
            PARAM parameter = (PARAM) annotation;
            String value = parameter.value();
            if (TextUtils.isEmpty(value)){
                throw new NullPointerException("@PARAM value() is empty");
            }
            Boolean optional = parameter.optional();
            if (arg != null) {
                params.put(value, arg);
            } else if (!optional) {
                arg = "";
            } else {
                return this;
            }
            params.put(value, arg);
        } else if (annotation instanceof FILE) {
            FILE parameter = (FILE) annotation;
            String value = parameter.value();
            String fileType = parameter.fileType();
            Boolean optional = parameter.optional();
            if (TextUtils.isEmpty(value)){
                throw new NullPointerException("@PARAM value() is empty");
            }
            if (arg != null) {
                if (!checkType(arg.getClass(), File.class)) {
                    throw new Exception("@FILE should target File");
                }
                fileParams.add(getFileParam(value, (File) arg, fileType));
            } else if (!optional) {
                throw new NullPointerException("Parameter == null");
            } else {
                return this;
            }
        }
        return this;
    }

    public static FileParam getFileParam(String key, File file, String fileType) {
        final String filename = (file != null && file.exists()) ? file.getName() : "";
        FileParam param = new FileParam();
        param.key = key;
        param.file = file;
        param.fileType = fileType;
        param.filename = filename;
        return param;
    }

    public static boolean checkType(Type type, Class<?> clazz) {
        if (type == null) throw new NullPointerException("type == null");
        if (type instanceof Class<?>) {
            Class inClazz = (Class<?>) type;
            return clazz.isAssignableFrom(inClazz);
        }
        return false;
    }
}
