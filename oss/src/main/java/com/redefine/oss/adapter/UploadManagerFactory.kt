package com.redefine.oss.adapter

import com.redefine.oss.upload.UploadManager

/**
 *
 * Name: UploadManagerFactory
 * Author: weber
 * Email:
 * Comment: //TODO
 * Date: 2018-12-25 22:51
 *
 */

object UploadManagerFactory {

    fun getUploadManager():IUploadManager {
        return UploadManager()
    }
}