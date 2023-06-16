
# S3Manager - aws files uploading library

This library is created to make files uploading and downloading on Aws easier

## Features

- Easy to use
- Single/multiple file upload on Aws s3

## How to Add
Project level gradle file


```bash
   allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

App level gradle file
```bash
	dependencies {
	         implementation 'com.github.rajesh-k-21:S3Manager:v1.2'
	}

```

## Deployment

How to use this amazing lib and save your time

```bash
    private val s3Manager by lazy {
           S3Manager(this)
       }
```

```bash
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
```

## setCredentials
```bash
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
```

## Upload files
```bash
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
```

## Contributing

Contributions are always welcome!

See `contributing.md` for ways to get started.

Please adhere to this project's `code of conduct`.


## Authors

- [@rkahir21](https://github.com/rkahir21)


## Happy Coding | Made with ❤ | Made in 🇮🇳 ... :)
