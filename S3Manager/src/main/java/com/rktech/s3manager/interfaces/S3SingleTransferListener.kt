package com.rktech.s3manager.interfaces

interface S3SingleTransferListener {
    fun onCompleted(fileName: String)
    fun onError(id: Int, exception: Exception?)
    fun onProgress(progress: Int)
}