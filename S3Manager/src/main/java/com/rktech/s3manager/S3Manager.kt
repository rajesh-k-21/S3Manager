package com.rktech.s3manager

import android.content.Context
import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.rktech.s3manager.exception.S3Exception
import com.rktech.s3manager.interfaces.S3MultipleTransferListener
import com.rktech.s3manager.interfaces.S3SingleTransferListener
import com.rktech.s3manager.model.S3Config
import com.rktech.s3manager.utils.S3PrefClass
import com.rktech.s3manager.utils.getRandomName
import java.io.File

class S3Manager(context: Context) {

    init {
        TransferNetworkLossHandler.getInstance(context)
    }

    private val s3PrefClass by lazy {
        S3PrefClass(context)
    }

    private val clientConfiguration by lazy {
        ClientConfiguration().apply {
            maxErrorRetry = 5
            connectionTimeout = CONNECTION_TIMEOUT
            socketTimeout = CONNECTION_TIMEOUT
            protocol = Protocol.HTTP
        }
    }

    private val awsCredentials by lazy {
        BasicAWSCredentials(
            s3PrefClass.getString(KEYS.ACCESS_KEY),
            s3PrefClass.getString(KEYS.SECRET_KEY)
        )
    }

    private val amazonS3 by lazy {
        if (isCredentialsAvailable()) {
            AmazonS3Client(
                awsCredentials, Region.getRegion(
                    s3PrefClass.getString(KEYS.REGION)
                ), clientConfiguration
            )
        } else {
            throw S3Exception("Credentials not set")
        }
    }

    private val transferUtility by lazy {
        TransferUtility.builder()
            .context(context)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(amazonS3)
            .build()
    }

    fun setCredentials(s3Config: S3Config) {
        s3PrefClass.apply {
            putString(KEYS.ACCESS_KEY, s3Config.accessKey)
            putString(KEYS.SECRET_KEY, s3Config.secretKey)
            putString(KEYS.REGION, s3Config.region)
            putString(KEYS.BUCKET, s3Config.bucket)
        }
    }

    fun isCredentialsAvailable(): Boolean {
        return !(s3PrefClass.getString(KEYS.ACCESS_KEY)
            .isEmpty() && s3PrefClass.getString(KEYS.SECRET_KEY)
            .isEmpty() && s3PrefClass.getString(KEYS.REGION)
            .isEmpty() && s3PrefClass.getString(KEYS.BUCKET).isEmpty())
    }


    fun uploadFile(
        file: File,
        filename: String = file.getRandomName()!!,
        directory: String,
        s3SingleTransferListener: S3SingleTransferListener
    ) {

        val transferListener = object : TransferListener {

            override fun onStateChanged(id: Int, state: TransferState?) {
                when (state) {
                    TransferState.COMPLETED -> {
                        s3SingleTransferListener.onCompleted(filename)
                    }
                    TransferState.CANCELED,
                    TransferState.FAILED,
                    TransferState.WAITING_FOR_NETWORK,
                    TransferState.UNKNOWN -> {
                        s3SingleTransferListener.onError(id, Exception(state.name))
                    }
                    else -> {

                    }
                }
            }

            override fun onProgressChanged(
                id: Int,
                bytesCurrent: Long,
                bytesTotal: Long
            ) {
                s3SingleTransferListener.onProgress((100 * bytesCurrent / bytesTotal).toInt())
            }

            override fun onError(id: Int, ex: Exception?) {
                s3SingleTransferListener.onError(id, ex)
            }
        }

        addTransfer(file, filename, directory, transferListener)
    }

    fun uploadFiles(
        files: ArrayList<File>,
        directory: String,
        s3MultipleTransferListener: S3MultipleTransferListener
    ) {

        val filesName = arrayListOf<String>()
        val uploadedFilesName = ArrayList<String>()

        files.forEach {
            val fileName = it.getRandomName()!!
            filesName.add(fileName)

            val transferListener = object : TransferListener {

                override fun onStateChanged(id: Int, state: TransferState?) {
                    when (state) {
                        TransferState.COMPLETED -> {
                            uploadedFilesName.add(fileName)

                            if (files.size == uploadedFilesName.size) {
                                s3MultipleTransferListener.onCompleted(filesName)
                            }

                            s3MultipleTransferListener.filesUploadedCount(
                                files.size,
                                (files.size - filesName.size)
                            )
                        }
                        TransferState.CANCELED,
                        TransferState.FAILED,
                        TransferState.WAITING_FOR_NETWORK,
                        TransferState.UNKNOWN -> {
                            s3MultipleTransferListener.onError(id, Exception(state.name))
                        }
                        else -> {

                        }
                    }
                }

                override fun onProgressChanged(
                    id: Int,
                    bytesCurrent: Long,
                    bytesTotal: Long
                ) {
                    s3MultipleTransferListener.onProgress((100 * bytesCurrent / bytesTotal).toInt())
                }

                override fun onError(id: Int, ex: Exception?) {
                    s3MultipleTransferListener.onError(id, ex)
                }
            }

            addTransfer(it, fileName, directory, transferListener)
        }


    }

    private fun addTransfer(
        file: File,
        fileName: String,
        directory: String,
        transferListener: TransferListener,
    ) {
        transferUtility.upload(
            "${s3PrefClass.getString(KEYS.BUCKET)}/$directory",
            fileName,
            file,
            ObjectMetadata(),
            CannedAccessControlList.PublicReadWrite,
            transferListener
        )
    }

    fun clearConfig() {
        s3PrefClass.clearAll()
    }

    companion object {
        const val CONNECTION_TIMEOUT = 600000

        object KEYS {
            const val ACCESS_KEY = "accessKey"
            const val SECRET_KEY = "secretKey"
            const val REGION = "region"
            const val BUCKET = "bucket"
        }
    }
}
