package com.redefine.welike.base.uploading;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.http.HttpManager;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.URLCenter;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by liubin on 2018/3/17.
 */

public class BlockUploadRequest implements BlockProgressRequestBody.BlockProgressRequestBodyListener {
    private static final String RESPONSE_KEY_URL = "url";
    private final int blockSize;
    private Call call;
    private Context context;
    private BlockUploadRequestCallback callback;

    interface BlockUploadRequestCallback {

        void onBlockUploadRequestSuccessed(BlockUploadRequest request, String url);
        void onBlockUploadRequestProgress(BlockUploadRequest request, int offset);
        void onBlockUploadRequestFailed(BlockUploadRequest request, int errCode);

    }

    public BlockUploadRequest(int blockSize, Context context) {
        this.blockSize = blockSize;
        this.context = context;
    }

    public void upload(String fileName, String objectKey, int num, int total, BlockUploadRequestCallback callback) {
        Account account = AccountManager.getInstance().getAccount();
//        if (account == null) {
//            if (callback != null) {
//                callback.onBlockUploadRequestFailed(null, ErrorCode.ERROR_NETWORK_TOKEN_INVALID);
//            }
//            return;
//        }

        byte[] blockData = getBlock(num - 1, new File(fileName), blockSize);
        this.callback = callback;
        if (blockData != null) {
            try {
                RequestBody filePart = new BlockProgressRequestBody(blockData, "application/octet-stream", this);
                MultipartBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", objectKey, filePart)
                        .addFormDataPart("partNumber", Integer.toString(num))
                        .addFormDataPart("total", Integer.toString(total))
                        .build();


                String url;
                Request request;
                if (account != null &&
                        account.getCompleteLevel() >= Account.PROFILE_COMPLETE_LEVEL_MAIN_DONE &&
                        account.isLogin()) {
                    url = URLCenter.getHost() + "file/upload?" + "token=" + account.getAccessToken() + "&welikeParams=" + BaseRequest.baseParamsBlock(context);
                    request = new Request.Builder().url(url).post(requestBody).header("idtoken", account.getUid()).build();
                } else {
                    url = URLCenter.getHostUpload1() + "upload";
                    request = new Request.Builder().url(url).post(requestBody).build();
                }
                call = HttpManager.getInstance().getHttpUploadOldClient().newCall(request);
                call.enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (BlockUploadRequest.this.callback != null) {
                            BlockUploadRequest.this.callback.onBlockUploadRequestFailed(BlockUploadRequest.this, ErrorCode.networkExceptionToErrCode(e));
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        byte[] body = null;
                        try {
                            ResponseBody respBody = response.body();
                            if (respBody != null) {
                                body = respBody.bytes();
                            }
                            if (body != null && body.length > 0) {
                                String json = new String(body, "UTF-8");
                                JSONObject jsonObj = JSON.parseObject(json);
                                if (jsonObj != null) {
                                    if (jsonObj.containsKey(BaseRequest.RESPONSE_KEY_CODE)) {
                                        int errCode = jsonObj.getIntValue(BaseRequest.RESPONSE_KEY_CODE);
                                        if (errCode == ErrorCode.ERROR_NETWORK_SUCCESS) {
                                            JSONObject res = jsonObj.getJSONObject(BaseRequest.RESPONSE_KEY_DATA);
                                            String url = null;
                                            if (res.containsKey(RESPONSE_KEY_URL)) {
                                                url = res.getString(RESPONSE_KEY_URL);
                                            }
                                            if (BlockUploadRequest.this.callback != null) {
                                                BlockUploadRequest.this.callback.onBlockUploadRequestSuccessed(BlockUploadRequest.this, url);
                                            }
                                        } else {
                                            if (BlockUploadRequest.this.callback != null) {
                                                BlockUploadRequest.this.callback.onBlockUploadRequestFailed(BlockUploadRequest.this, errCode);
                                            }
                                        }
                                    } else {
                                        if (BlockUploadRequest.this.callback != null) {
                                            BlockUploadRequest.this.callback.onBlockUploadRequestFailed(BlockUploadRequest.this, ErrorCode.ERROR_NETWORK_RESP_INVALID);
                                        }
                                    }
                                } else {
                                    if (BlockUploadRequest.this.callback != null) {
                                        BlockUploadRequest.this.callback.onBlockUploadRequestFailed(BlockUploadRequest.this, ErrorCode.ERROR_NETWORK_RESP_INVALID);
                                    }
                                }
                            } else {
                                if (BlockUploadRequest.this.callback != null) {
                                    BlockUploadRequest.this.callback.onBlockUploadRequestFailed(BlockUploadRequest.this, ErrorCode.ERROR_NETWORK_RESP_INVALID);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (BlockUploadRequest.this.callback != null) {
                                BlockUploadRequest.this.callback.onBlockUploadRequestFailed(BlockUploadRequest.this, ErrorCode.networkExceptionToErrCode(e));
                            }
                        }
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                if (this.callback != null) {
                    this.callback.onBlockUploadRequestFailed(this, ErrorCode.networkExceptionToErrCode(e));
                }
            }
        } else {
            if (this.callback != null) {
                this.callback.onBlockUploadRequestFailed(this, ErrorCode.ERROR_UPLOADING_OFFSET_OUT);
            }
        }
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
        call = null;
        callback = null;
    }

    @Override
    public void onTransferred(int offset) {
        if (callback != null) {
            callback.onBlockUploadRequestProgress(this, offset);
        }
    }

    private static byte[] getBlock(int idx, File file, int blockSize) {
        int begin = idx * blockSize;
        byte[] result = new byte[blockSize];
        int readSize = -1;
        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(file, "r");
            accessFile.seek(begin);
            readSize = accessFile.read(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (readSize == blockSize) {
            return result;
        } else if (readSize == -1) {
            return null;
        } else {
            byte[] tmpByte = new byte[readSize];
            System.arraycopy(result, 0, tmpByte, 0, readSize);
            return tmpByte;
        }
    }

}
