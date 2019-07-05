package com.redefine.oss.upload;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by liubin on 2018/3/17.
 */

public class BlockUploadRequest implements BlockProgressRequestBody.BlockProgressRequestBodyListener {
    private static final String RESPONSE_KEY_CODE = "code";
    private static final String RESPONSE_KEY_DATA = "result";
    private static final String RESPONSE_KEY_URL = "url";
    private final int blockSize;
    private int totalSize;
    private final String host;
    private Call call;
    private BlockUploadRequestCallback callback;
    private static final String TAG = "BlockUploadRequest";

    interface BlockUploadRequestCallback {

        void onBlockUploadRequestSuccessed(BlockUploadRequest request, String url);

        void onBlockUploadRequestProgress(BlockUploadRequest request, float progress);

        void onBlockUploadRequestFailed(BlockUploadRequest request, int errCode);

    }

    public BlockUploadRequest(int blockSize, String host) {
        this.blockSize = blockSize;
        this.host = host;
        totalSize = 0;
    }

    public void upload(OkHttpClient client, String accessToken, String fileName, String objectKey, int num, int total, BlockUploadRequestCallback callback) {
        if (TextUtils.isEmpty(accessToken)) {
            if (callback != null) {
                callback.onBlockUploadRequestFailed(null, ErrorCode.ERROR_UPLOAD_TOKEN_INVALID);
            }
            return;
        }

        byte[] blockData = getBlock(num - 1, new File(fileName), blockSize);
        this.callback = callback;
        if (blockData != null) {
            totalSize = blockData.length;
            try {
                RequestBody filePart = new BlockProgressRequestBody(blockData, "application/octet-stream", this);
                MultipartBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", objectKey, filePart)
                        .addFormDataPart("partNumber", Integer.toString(num))
                        .addFormDataPart("total", Integer.toString(total))
                        .build();

                String url = host + "file/upload?" + "token=" + accessToken;
                Request request = new Request.Builder().url(url).post(requestBody).build();
                call = client.newCall(request);
                call.enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "error:" + e.toString());
                        if (BlockUploadRequest.this.callback != null) {
                            BlockUploadRequest.this.callback.onBlockUploadRequestFailed(BlockUploadRequest.this, ErrorCode.ERROR_UPLOAD_NETWORK_INVALID);
                        } else {
                            Log.e(TAG, "callback == null," + e.toString());
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
                                    if (jsonObj.containsKey(RESPONSE_KEY_CODE)) {
                                        int errCode = jsonObj.getIntValue(RESPONSE_KEY_CODE);
                                        if (errCode == ErrorCode.ERROR_UPLOAD_SUCCESS) {
                                            JSONObject res = jsonObj.getJSONObject(RESPONSE_KEY_DATA);
                                            String url = null;
                                            if (res.containsKey(RESPONSE_KEY_URL)) {
                                                url = res.getString(RESPONSE_KEY_URL);
                                            }
                                            if (BlockUploadRequest.this.callback != null) {
                                                BlockUploadRequest.this.callback.onBlockUploadRequestSuccessed(BlockUploadRequest.this, url);
                                            }
                                        } else {
                                            if (BlockUploadRequest.this.callback != null) {
                                                Log.e(TAG, "error:" + errCode);
                                                BlockUploadRequest.this.callback.onBlockUploadRequestFailed(BlockUploadRequest.this, errCode);
                                            } else {
                                                Log.e(TAG, "callback == null:" + errCode);
                                            }
                                        }
                                    } else {
                                        if (BlockUploadRequest.this.callback != null) {
                                            Log.e(TAG, "error:" + ErrorCode.ERROR_UPLOAD_RESP_INVALID);
                                            BlockUploadRequest.this.callback.onBlockUploadRequestFailed(BlockUploadRequest.this, ErrorCode.ERROR_UPLOAD_RESP_INVALID);
                                        } else {
                                            Log.e(TAG, "callback == null" + ErrorCode.ERROR_UPLOAD_RESP_INVALID);
                                        }
                                    }
                                } else {
                                    if (BlockUploadRequest.this.callback != null) {
                                        Log.e(TAG, "error:" + ErrorCode.ERROR_UPLOAD_RESP_INVALID);
                                        BlockUploadRequest.this.callback.onBlockUploadRequestFailed(BlockUploadRequest.this, ErrorCode.ERROR_UPLOAD_RESP_INVALID);
                                    } else {
                                        Log.e(TAG, "callback == null" + ErrorCode.ERROR_UPLOAD_RESP_INVALID);
                                    }
                                }
                            } else {
                                if (BlockUploadRequest.this.callback != null) {
                                    Log.e(TAG, "error:" + ErrorCode.ERROR_UPLOAD_RESP_INVALID);
                                    BlockUploadRequest.this.callback.onBlockUploadRequestFailed(BlockUploadRequest.this, ErrorCode.ERROR_UPLOAD_RESP_INVALID);
                                } else {
                                    Log.e(TAG, "callback == null" + ErrorCode.ERROR_UPLOAD_RESP_INVALID);
                                }
                            }
                        } catch (Exception e) {

                            if (BlockUploadRequest.this.callback != null) {
                                Log.e(TAG, "error:" + ErrorCode.ERROR_UPLOAD_RESP_INVALID);
                                BlockUploadRequest.this.callback.onBlockUploadRequestFailed(BlockUploadRequest.this, ErrorCode.ERROR_UPLOAD_RESP_INVALID);
                            } else {
                                Log.e(TAG, "callback == null" + ErrorCode.ERROR_UPLOAD_NETWORK_INVALID);
                            }
                        }
                    }

                });
            } catch (Exception e) {
                if (this.callback != null) {
                    Log.e(TAG, "error:" + ErrorCode.ERROR_UPLOAD_NETWORK_INVALID);
                    this.callback.onBlockUploadRequestFailed(this, ErrorCode.ERROR_UPLOAD_NETWORK_INVALID);
                } else {
                    Log.e(TAG, "callback == null" + ErrorCode.ERROR_UPLOAD_NETWORK_INVALID);
                }
            }
        } else {

            if (this.callback != null) {
                Log.e(TAG, "error:" + ErrorCode.ERROR_UPLOAD_NETWORK_INVALID);
                this.callback.onBlockUploadRequestFailed(this, ErrorCode.ERROR_UPLOAD_OFFSET_OUT);
            } else {
                Log.e(TAG, "callback == null" + ErrorCode.ERROR_UPLOAD_NETWORK_INVALID);
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
            if (totalSize > 0) {
                float progress = ((float) offset / (float) totalSize) * 100f;
                callback.onBlockUploadRequestProgress(this, progress);
            }
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
            // do nothing
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close();
                } catch (Exception e) {
                    // do nothing
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
