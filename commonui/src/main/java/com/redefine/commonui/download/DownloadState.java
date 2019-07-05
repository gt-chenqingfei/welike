package com.redefine.commonui.download;

import com.liulishuo.okdownload.StatusUtil;

public enum DownloadState {
    IDLE, PENDING, RUNNING, UNKNOWN, PROCESSING, COMPLETED;

    public static DownloadState valueOf(StatusUtil.Status status) {
        switch (status) {
            case COMPLETED:
                return DownloadState.COMPLETED;
            case RUNNING:
                return DownloadState.RUNNING;
            case PENDING:
                return DownloadState.PENDING;
            case IDLE:
                return DownloadState.IDLE;
            case UNKNOWN:
            default:
                return DownloadState.UNKNOWN;
        }
    }
}
