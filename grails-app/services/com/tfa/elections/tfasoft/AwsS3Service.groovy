package com.tfa.elections.tfasoft

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.web.multipart.MultipartFile

class AwsS3Service {

    AmazonS3 s3Client

    def grailsApplication

    void init() {
        def accessKey = grailsApplication.config.tfasoft.aws.s3.accessKey
        def secretKey = grailsApplication.config.tfasoft.aws.s3.secretKey
        def region = grailsApplication.config.tfasoft.aws.s3.region ?: 'us-east-1'

        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey)
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .build()
    }

    String uploadFile(MultipartFile file, String key) {
        String bucketName = grailsApplication.config.tfasoft.aws.s3.bucket

        ObjectMetadata metadata = new ObjectMetadata()
        metadata.contentType = file.contentType
        metadata.contentLength = file.size

        PutObjectRequest request = new PutObjectRequest(
                bucketName,
                key,
                file.inputStream,
                metadata
        ).withCannedAcl(CannedAccessControlList.PublicRead)

        s3Client.putObject(request)

        return "${grailsApplication.config.tfasoft.aws.s3.baseUrl}/${key}"
    }

    InputStream downloadFile(String key) {
        String bucketName = grailsApplication.config.tfasoft.aws.s3.bucket
        return s3Client.getObject(bucketName, key).objectContent
    }

    boolean deleteFile(String key) {
        String bucketName = grailsApplication.config.tfasoft.aws.s3.bucket
        s3Client.deleteObject(bucketName, key)
        return true
    }
}
