package com.rktech.s3manager.interfaces

interface S3MultipleTransferListener {
    fun onCompleted(listOfName: ArrayList<String>)
    fun onError(id: Int, exception: Exception?)
    fun onProgress(progress: Int)
    fun filesUploadedCount(totalFiles: Int, pendingFile: Int)
}