package com.tfa.elections.tfasoft

import grails.gorm.annotation.Entity
import groovy.transform.ToString

@Entity
@ToString(includeNames = true, includePackage = false)
class MediaFile {

    enum MediaType { IMAGE, VIDEO }
    enum UploadStatus { PENDING, COMPLETED, FAILED }

    ResultEntry resultEntry
    String s3Key
    String s3Url
    String localDeviceId
    String originalFileName
    MediaType type
    long fileSize
    String mimeType
    UploadStatus status = UploadStatus.PENDING
    String localMediaId
    Date createdAt = new Date()
    Date captureTimestamp
    String latitude
    String longitude

    static constraints = {
        s3Key blank: false, unique: true
        s3Url blank: false
        localDeviceId blank: false
        originalFileName nullable: true
        mimeType nullable: true
        latitude nullable: true
        longitude nullable: true
        captureTimestamp nullable: true
        localMediaId nullable: true, unique: ['resultEntry']
    }
}
