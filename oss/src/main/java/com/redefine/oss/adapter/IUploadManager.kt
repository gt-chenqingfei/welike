package com.redefine.oss.adapter

import com.redefine.oss.upload.IUploadTaskCallback

/**
 *
 * Name: IUploadManager
 * Author: weber
 * Email:
 * Comment: //TODO
 * Date: 2018-12-25 22:44
 *
 */

interface IUploadManager {
    fun setEnv(uploadConfig: IUploadConfig)
    fun stopAll()
    fun upload(objFileName: String, extName: String, objectType: String, callback: IUploadTaskCallback?): String

}