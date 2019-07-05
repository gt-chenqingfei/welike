package com.redefine.sunny;

/**
 * Handle network task.
 * Created by ning.dai on 16/7/21.
 */
public abstract class Task<T> implements ITask<T> {
    //response type should be String, JSONObject, ParserModel;
//    static SThreadPool threadPool = SThreadPool.newThreadPool(10, 5, 2, true);
//
//    Type responseType;
//    ApiRequest request;
//
//    public Task(ApiRequest request, Type responseType) {
//        this.responseType = responseType;
//        this.request = request;
//    }
//
//    public void go(Callback<T> call) {
//        threadPool.put("Engine", new TaskImp(request, responseType, call), TaskPriority.UI_NORM);
//    }
//
//
//    class TaskImp implements IPriorityTask {
//        ApiRequest request;
//        Type responseType;
//        Callback<T> call;
//        HttpUtil.Brake brake = new HttpUtil.Brake();
//        protected volatile boolean hasCanceled = false;
//        Callback uiCallback;
//
//        public TaskImp(ApiRequest request, Type responseType, Callback<T> call) {
//            this.request = request;
//            this.responseType = responseType;
//            this.call = call;
//            Handler handler = new Handler(Looper.getMainLooper());
//            uiCallback = (Callback) Proxy.newProxyInstance(call.getClass().getClassLoader(), call
//                    .getClass().getInterfaces(), new UICallbackProxy(call, handler));
//        }
//
//        public synchronized void cancel() {
//            hasCanceled = true;
//            brake.stop();
//        }
//
//        @Override
//        public void run() {
//            try {
//                CacheManager.Key key = null;
//                if (call instanceof Callback.CacheCallback) {
//                    key = CacheManager.Key.from(request);
//                    try {
//                        CacheManager.Record record = CacheManager.getInstance().getContent(key);
//                        if (record != null) {
//                            JSONObject cacheJO = new JSONObject(record.content);
//                            Object data = cacheJO.get("data");
//                            Object result;
//                            if (data instanceof String) {
//                                result = data;
//                            } else {
//                                result = new Gson().fromJson(data.toString(), responseType);
//                            }
//                            ((Callback.CacheCallback) uiCallback).fromCache((T) result);
//                        }
//                    } catch (Exception ignored) {
//                        ignored.printStackTrace();
//                    }
//                }
//
//                Protocol p = Protocol.create(request.action);
////                if (Config.DEMO){
////                    request.params.put("test","1");
////                }
//                if (Config.DEBUG) {
//                    Log.e("DDAI", "Request = " + p.toURL());
//
//
//                }
//
//                p.map = request.params;
//                for (FileParam fp : request.fileParams) {
//                    p.addFile(fp.key, fp.fileType, fp.file);
//                }
//
//
//                HttpUtil.HttpPostRequest postRequest = HttpUtil.post(p.toURL());
//                postRequest.setParam(p.map);
//                postRequest.setFileParam(p.fileParams);
//
//                Log.e("DDAI", "params = " + p.map);
//                HttpUtil.Response response = postRequest.open();
//                if (call instanceof Callback.ProgressCallback) {
//                    response.setProgressListener(new com.sunny.net.Callback.ProgressCallback() {
//                        @Override
//                        public void onProgress(int percent) {
//                            if (uiCallback != null) {
//                                ((Callback.ProgressCallback) uiCallback).onProgress(percent);
//                            }
//                        }
//                    });
//                }
//                JSONObject resultJO = response.getJSONObject();
//                Log.e("x","resultJO    ==   "+resultJO);
//                int errorCode = resultJO.optInt("result", 0);
//                if (errorCode != 1) {
//                    if (uiCallback != null) {
//                        uiCallback.onError(errorCode, resultJO.optString("message"));
//                    }
//                    return;
//                }
//                if (key != null) {
//                    CacheManager.getInstance().saveContent(key, resultJO.toString());
//                }
//
//                Object data = resultJO.get("data");
//                Object result;
//                if (data instanceof String) {
//                    result = data;
//                } else {
//                    result = new Gson().fromJson(data.toString(), responseType);
//                }
//
//                if (call instanceof Callback.PrepareCallback) {
//                    if (!((Callback.PrepareCallback) call).prepare(data.toString(), (T) result)) {
//                        return;
//                    }
//                }
//                uiCallback.onFinish((T) result);
//            } catch (SocketTimeoutException ignored) {
//                ignored.printStackTrace();
//                if (uiCallback != null) {
//                    uiCallback.onNetworkError(true);
//                }
//            } catch (Exception ignored) {
//                ignored.printStackTrace();
//                if (uiCallback != null) {
//                    uiCallback.onNetworkError(false);
//                }
//            }
//        }
//
//        @Override
//        public String getToken() {
//            return String.valueOf(this.hashCode());
//        }
//
//        @Override
//        public boolean onRepeatPut(IPriorityTask newTask) {
//            return false;
//        }
//
//        @Override
//        public boolean unregisterListener(int taskId) {
//            return false;
//        }
//    }

}
