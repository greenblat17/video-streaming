package com.rbi.config

import io.minio.MinioClient

class MinioConfig {

    companion object {
        fun init(): MinioClient {
            return MinioClient.builder().endpoint(MinioProperties.URL)
                .credentials(MinioProperties.ACCESS_KEY, MinioProperties.SECRET_KEY).build()
        }
    }


}