package com.redefine.oss.upload;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by liubin on 2018/3/19.
 */

class BlockProgressRequestBody extends RequestBody {
    private static final int SEGMENT_SIZE = 1024 * 20;
    private final byte[] blockData;
    private String contentType;
    private BlockProgressRequestBodyListener listener;

    interface BlockProgressRequestBodyListener {

        void onTransferred(int offset);

    }

    BlockProgressRequestBody(byte[] blockData, String contentType, BlockProgressRequestBodyListener listener) {
        this.blockData = blockData;
        this.contentType = contentType;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(contentType);
    }

    @Override
    public long contentLength() throws IOException {
        return blockData.length;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        int offset = 0;
        int chunk;
        int count = blockData.length / SEGMENT_SIZE + (blockData.length % SEGMENT_SIZE != 0 ? 1 : 0);
        for (int i = 0; i < count; i++) {
            if (i != (count - 1)) {
                chunk = SEGMENT_SIZE;
            } else {
                chunk = blockData.length - offset;
            }
            sink.buffer().write(blockData, offset, chunk);
            sink.buffer().flush();
            offset += chunk;
            if (listener != null) {
                listener.onTransferred(offset);
            }
        }
    }

}
