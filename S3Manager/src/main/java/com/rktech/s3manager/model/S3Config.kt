package com.rktech.s3manager.model

data class S3Config(
    val accessKey: String?,
    val secretKey: String?,
    val region: String?,
    val bucket: String?
)