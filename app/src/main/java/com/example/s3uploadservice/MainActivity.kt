package com.example.s3uploadservice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rktech.s3manager.S3Manager
import com.rktech.s3manager.interfaces.S3MultipleTransferListener
import com.rktech.s3manager.interfaces.S3SingleTransferListener
import com.rktech.s3manager.model.S3Config
import java.io.File

class MainActivity : AppCompatActivity() {

    private val s3Manager by lazy {
        S3Manager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //You can also check if credentials set or not
        if (s3Manager.isCredentialsAvailable()) {
            //Now you can used

            //for single file
            uploadFile()

            //for multiple files
            uploadFiles()

        } else {
            //You need to call setCredentials()
            setCredentials()
        }

    }

    private fun setCredentials() {
        s3Manager.setCredentials(
            S3Config(
                "YOUR_ACCESS_KEY_HERE",
                "YOUR_SECRET_KEY_HERE",
                "YOUR_REGION_HERE",
                "YOUR_BUCKET_HERE",
            )
        )
    }

    private fun uploadFile() {
        s3Manager.uploadFile(
            file = File("demo.jpg"), // File need to upload
            filename = "uploadFileName", //if not pass argument then it has by default random name
            directory = "customer/user/profile", // full path of upload directory
            s3SingleTransferListener = object :
                S3SingleTransferListener { //file uploading status listener

                override fun onCompleted(fileName: String) {
                    //Do something on file uploading successfully
                }

                override fun onError(id: Int, exception: Exception?) {
                    //Do something on got any error file uploading
                }

                override fun onProgress(progress: Int) {
                    //Do something on file uploading progress
                }

            }

        )
    }

    private fun uploadFiles() {
        s3Manager.uploadFiles(
            files = arrayListOf(File("demo.jpg")), // array of file need to upload
            directory = "customer/user/profile", // full path of upload directory
            s3MultipleTransferListener = object :
                S3MultipleTransferListener { //file uploading status listener

                override fun onCompleted(listOfName: ArrayList<String>) {
                    //Do something on files uploading successfully
                }

                override fun onError(id: Int, exception: Exception?) {
                    //Do something on got any error file uploading
                }

                override fun onProgress(progress: Int) {
                    //Do something on file uploading progress
                }

                override fun filesUploadedCount(totalFiles: Int, pendingFile: Int) {
                    //Do something on file uploading count
                }

            }
        )
    }
}