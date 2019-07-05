package top.zibin.luban;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Luban implements Handler.Callback {
    private static final String TAG = "Luban";
    private static final String DEFAULT_DISK_CACHE_DIR = "luban_disk_cache";

    private static final int MSG_COMPRESS_SUCCESS = 0;
    private static final int MSG_COMPRESS_START = 1;
    private static final int MSG_COMPRESS_ERROR = 2;

    private final int mCompressQuality;
    private final String mTargetDir;
    private final int mLeastCompressSize;
    private final OnRenameListener mRenameListener;
    private final OnCompressListener mCompressListener;
    private final CompressionPredicate mCompressionPredicate;
    private final List<InputStreamProvider> mStreamProviders;
    private final boolean ignoreRotate;

    private Handler mHandler;

    private Luban(Builder builder) {
        this.mTargetDir = builder.mTargetDir;
        this.mRenameListener = builder.mRenameListener;
        this.mStreamProviders = builder.mStreamProviders;
        this.mCompressListener = builder.mCompressListener;
        this.mLeastCompressSize = builder.mLeastCompressSize;
        this.mCompressionPredicate = builder.mCompressionPredicate;
        this.mCompressQuality = builder.compressQuality;
        this.ignoreRotate = builder.ignoreRotate;
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    /**
     * Returns a file with a cache image name in the private cache directory.
     *
     * @param context A context.
     */
    private File getImageCacheFile(Context context, String suffix) {
        if (TextUtils.isEmpty(mTargetDir)) {
            throw new IllegalArgumentException("you must call setTargetDir()");
        }

        String cacheBuilder = mTargetDir + File.separator +
                System.currentTimeMillis() +
                (int) (Math.random() * 1000) +
                (TextUtils.isEmpty(suffix) ? ".jpg" : suffix);

        return new File(cacheBuilder);
    }


    private File getImageCustomFile(Context context, String filename) {
        if (TextUtils.isEmpty(mTargetDir)) {
            throw new IllegalArgumentException("you must call setTargetDir()");
        }

        String cacheBuilder = mTargetDir + File.separator + filename;

        return new File(cacheBuilder);
    }

    /**
     * start asynchronous compress thread
     */
    @UiThread
    private void launch(final Context context) {
        if (mStreamProviders == null || mStreamProviders.size() == 0 && mCompressListener != null) {
            mCompressListener.onError(new NullPointerException("image file cannot be null"));
        }

        Iterator<InputStreamProvider> iterator = mStreamProviders.iterator();

        while (iterator.hasNext()) {
            final InputStreamProvider path = iterator.next();

            AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START));

                        WrapSizeFile result = compress(context, path);

                        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_SUCCESS, result));
                    } catch (IOException e) {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, e));
                    }
                }
            });

            iterator.remove();
        }
    }

    /**
     * start compress and return the mFile
     */
    @WorkerThread
    private WrapSizeFile get(InputStreamProvider path, Context context) throws IOException {

        if (Checker.SINGLE.needCompress(mLeastCompressSize, path.getPath())) {
            File outFile = getImageCacheFile(context, ".jpg");
            return new Engine(path, outFile, ignoreRotate).compress(mCompressQuality);
        } else {
            File outFile;
            if (path.getPath().endsWith(".tmp")) {
                String des = path.getPath().replace(".tmp", "");
                outFile = getImageCacheFile(context, Checker.SINGLE.extSuffix(des));
            } else {
                outFile = getImageCacheFile(context, Checker.SINGLE.extSuffix(path.getPath()));
            }
            return new WrapSizeFile(copyFileToFile(new File(path.getPath()), outFile));
        }
    }

    @WorkerThread
    private List<WrapSizeFile> get(Context context) throws IOException {
        List<WrapSizeFile> results = new ArrayList<>();
        Iterator<InputStreamProvider> iterator = mStreamProviders.iterator();

        while (iterator.hasNext()) {
            results.add(compress(context, iterator.next()));
            iterator.remove();
        }

        return results;
    }

    private WrapSizeFile compress(Context context, InputStreamProvider path) throws IOException {
        WrapSizeFile result;



        if (mCompressionPredicate != null) {
            if (mCompressionPredicate.apply(path.getPath())
                    && Checker.SINGLE.needCompress(mLeastCompressSize, path.getPath())) {
                File outFile = getImageCacheFile(context, ".jpg");

                if (mRenameListener != null) {
                    String filename = mRenameListener.rename(path.getPath());
                    outFile = getImageCustomFile(context, filename);
                }
                result = new Engine(path, outFile, ignoreRotate).compress(mCompressQuality);
            } else {
                File outFile;
                if (path.getPath().endsWith(".tmp")) {
                    String des = path.getPath().replace(".tmp", "");
                    outFile = getImageCacheFile(context, Checker.SINGLE.extSuffix(des));
                } else {
                    outFile = getImageCacheFile(context, Checker.SINGLE.extSuffix(path.getPath()));
                }

                if (mRenameListener != null) {
                    String filename = mRenameListener.rename(path.getPath());
                    outFile = getImageCustomFile(context, filename);
                }
                result = new WrapSizeFile(copyFileToFile(new File(path.getPath()), outFile));
            }
        } else {
            if (Checker.SINGLE.needCompress(mLeastCompressSize, path.getPath()) ) {
                File outFile = getImageCacheFile(context, ".jpg");

                if (mRenameListener != null) {
                    String filename = mRenameListener.rename(path.getPath());
                    outFile = getImageCustomFile(context, filename);
                }
                result = new Engine(path, outFile, ignoreRotate).compress(mCompressQuality);
            } else {
                File outFile;
                if (path.getPath().endsWith(".tmp")) {
                    String des = path.getPath().replace(".tmp", "");
                    outFile = getImageCacheFile(context, Checker.SINGLE.extSuffix(des));
                } else {
                    outFile = getImageCacheFile(context, Checker.SINGLE.extSuffix(path.getPath()));
                }

                if (mRenameListener != null) {
                    String filename = mRenameListener.rename(path.getPath());
                    outFile = getImageCustomFile(context, filename);
                }
                result = new WrapSizeFile(copyFileToFile(new File(path.getPath()), outFile));
            }
        }

        return result;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (mCompressListener == null) return false;

        switch (msg.what) {
            case MSG_COMPRESS_START:
                mCompressListener.onStart();
                break;
            case MSG_COMPRESS_SUCCESS:
                mCompressListener.onSuccess((File) msg.obj);
                break;
            case MSG_COMPRESS_ERROR:
                mCompressListener.onError((Throwable) msg.obj);
                break;
        }
        return false;
    }

    public static class Builder {
        private Context context;
        private String mTargetDir;
        private int mLeastCompressSize = 0;
        private OnRenameListener mRenameListener;
        private OnCompressListener mCompressListener;
        private CompressionPredicate mCompressionPredicate;
        private List<InputStreamProvider> mStreamProviders;
        private int compressQuality;
        private boolean ignoreRotate = false;

        Builder(Context context) {
            this.context = context;
            this.mStreamProviders = new ArrayList<>();
        }

        private Luban build() {
            return new Luban(this);
        }

        public Builder load(InputStreamProvider inputStreamProvider) {
            mStreamProviders.add(inputStreamProvider);
            return this;
        }

        public Builder load(final File file) {
            mStreamProviders.add(new InputStreamProvider() {
                @Override
                public InputStream open() throws IOException {
                    return new FileInputStream(file);
                }

                @Override
                public String getPath() {
                    return file.getAbsolutePath();
                }
            });
            return this;
        }

        public Builder load(final String string) {
            mStreamProviders.add(new InputStreamProvider() {
                @Override
                public InputStream open() throws IOException {
                    return new FileInputStream(string);
                }

                @Override
                public String getPath() {
                    return string;
                }
            });
            return this;
        }

        public <T> Builder load(List<T> list) {
            for (T src : list) {
                if (src instanceof String) {
                    load((String) src);
                } else if (src instanceof File) {
                    load((File) src);
                } else if (src instanceof Uri) {
                    load((Uri) src);
                } else {
                    throw new IllegalArgumentException("Incoming data type exception, it must be String, File, Uri or Bitmap");
                }
            }
            return this;
        }

        public Builder load(final Uri uri) {
            mStreamProviders.add(new InputStreamProvider() {
                @Override
                public InputStream open() throws IOException {
                    return context.getContentResolver().openInputStream(uri);
                }

                @Override
                public String getPath() {
                    return uri.getPath();
                }
            });
            return this;
        }

        public Builder putGear(int gear) {
            return this;
        }

        public Builder setRenameListener(OnRenameListener listener) {
            this.mRenameListener = listener;
            return this;
        }

        public Builder setCompressListener(OnCompressListener listener) {
            this.mCompressListener = listener;
            return this;
        }

        public Builder setTargetDir(String targetDir) {
            this.mTargetDir = targetDir;
            return this;
        }

        /**
         * do not compress when the origin image file size less than one value
         *
         * @param size the value of file size, unit KB, default 100K
         */
        public Builder ignoreBy(int size) {
            this.mLeastCompressSize = size;
            return this;
        }

        /**
         * do compress image when return value was true, otherwise, do not compress the image file
         *
         * @param compressionPredicate A predicate callback that returns true or false for the given input path should be compressed.
         */
        public Builder filter(CompressionPredicate compressionPredicate) {
            this.mCompressionPredicate = compressionPredicate;
            return this;
        }


        /**
         * begin compress image with asynchronous
         */
        public void launch() {
            build().launch(context);
        }

        public WrapSizeFile get(final String path) throws Throwable {
            return build().get(new InputStreamProvider() {
                @Override
                public InputStream open() throws IOException {
                    return new FileInputStream(path);
                }

                @Override
                public String getPath() {
                    return path;
                }
            }, context);
        }

        /**
         * begin compress image with synchronize
         *
         * @return the thumb image file list
         */
        public List<WrapSizeFile> get() throws Throwable {
            return build().get(context);
        }

        public Builder setCompressQuality(int quality) {
            this.compressQuality = quality;
            return this;
        }

        public Builder ignoreRotate(boolean b) {
            this.ignoreRotate = b;
            return this;
        }
    }

    public File copyFileToFile(File source, File target) {
        if (source == null || target == null || !source.exists()) {
            return target;
        }

        //获得原文件流
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            File parent = target.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean isSuccess = parent.mkdirs();
                if (!isSuccess) {
                    return target;
                }
            }
            if (!target.exists()) {
                boolean isSuccess = target.createNewFile();
                if (!isSuccess) {
                    return target;
                }
            }
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(target);
            byte[] data = new byte[1024];
            while (inputStream.read(data) != -1) {
                outputStream.write(data);
            }
            close(inputStream);
            close(outputStream);
            return target;
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
            close(outputStream);
        }
        return target;
    }

    public void close(Closeable inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Throwable e) {
                e.printStackTrace();
                // do nothing
            }
        }
    }

}