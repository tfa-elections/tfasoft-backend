package com.tfa.elections.tfasoft


import grails.rest.*
import grails.converters.*
import grails.rest.RestfulController
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartHttpServletRequest

class MediaController {

    static responseFormats = ['json']
    MediaService mediaService
    ResultService resultService

    def upload() {
        def user = User.findByUsername(request.getAttribute("authenticatedUsername"))
        def entry = ResultEntry.findBySubmittedBy(user)

        if (!entry) {
            respond([error: 'User has not submitted a result yet'], status: HttpStatus.BAD_REQUEST)
            return
        }

        if (!(request instanceof MultipartHttpServletRequest)) {
            respond([error: 'Expected multipart request'], status: HttpStatus.BAD_REQUEST)
            return
        }

        def files = ((MultipartHttpServletRequest) request).getFiles("files")
        def type = params.type  // expects 'image' or 'video'
        def deviceId = params.deviceId ?: 'unknown'

        try {
            def uploaded = mediaService.uploadMediaFiles(entry, files, deviceId, type)
            respond uploaded, status: HttpStatus.CREATED
        } catch (Exception e) {
            respond([error: e.message], status: HttpStatus.BAD_REQUEST)
        }
    }

    def completePendingUpload() {
        def user = User.findByUsername(request.getAttribute("authenticatedUsername"))
        def entry = ResultEntry.findBySubmittedBy(user)

        if (!entry) {
            respond([error: 'User has not submitted a result yet'], status: HttpStatus.BAD_REQUEST)
            return
        }

        if (!(request instanceof MultipartHttpServletRequest)) {
            respond([error: 'Expected multipart request'], status: HttpStatus.BAD_REQUEST)
            return
        }

        def localId = params.localMediaId
        def file = ((MultipartHttpServletRequest) request).getFile("file")

        def existing = MediaFile.findByResultEntryAndLocalMediaId(entry, localId)

        if (!existing) {
            respond([error: 'No registered pending upload for this localMediaId'], status: HttpStatus.NOT_FOUND)
            return
        }

        try {
            def key = "results/${entry.pollingStationCode}/${entry.id}/${System.currentTimeMillis()}_${file.originalFilename}"
            def url = mediaService.awsS3Service.uploadFile(file, key)

            existing.s3Key = key
            existing.s3Url = url
            existing.mimeType = file.contentType
            existing.fileSize = file.size
            existing.status = MediaFile.UploadStatus.COMPLETED
            existing.save(flush: true)

            respond existing, status: HttpStatus.OK
        } catch (Exception e) {
            respond([error: e.message], status: HttpStatus.BAD_REQUEST)
        }
    }

    def registerPendingUpload() {
        def user = User.findByUsername(request.getAttribute("authenticatedUsername"))
        def entry = ResultEntry.findBySubmittedBy(user)

        if (!entry) {
            respond([error: 'User has not submitted a result yet'], status: HttpStatus.BAD_REQUEST)
            return
        }

        def json = request.JSON
        def deviceId = json.deviceId ?: 'unknown'
        def type = json.type ?: 'IMAGE'
        def localMediaId = json.localMediaId ?: UUID.randomUUID().toString()

        try {
            def metadata = new MediaFile(
                    resultEntry: entry,
                    localDeviceId: deviceId,
                    originalFileName: json.originalFileName,
                    mimeType: json.mimeType,
                    type: MediaFile.MediaType.valueOf(type.toUpperCase()),
                    fileSize: json.fileSize ?: 0,
                    captureTimestamp: json.captureTimestamp ? new Date(json.captureTimestamp as long) : null,
                    latitude: json.latitude,
                    longitude: json.longitude,
                    localMediaId: localMediaId,
                    status: MediaFile.UploadStatus.PENDING
            )

            metadata.save(flush: true)
            respond metadata, status: HttpStatus.CREATED
        } catch (Exception e) {
            respond([error: e.message], status: HttpStatus.BAD_REQUEST)
        }
    }

    def listByResult() {
        def resultId = params.long("resultId")
        respond mediaService.getMediaForResult(resultId)
    }

    def getStatusByLocalMediaId() {
        def user = User.findByUsername(request.getAttribute("authenticatedUsername"))
        def entry = ResultEntry.findBySubmittedBy(user)
        def localId = params.localMediaId

        if (!entry || !localId) {
            respond([error: 'Missing parameters'], status: HttpStatus.BAD_REQUEST)
            return
        }

        def file = MediaFile.findByResultEntryAndLocalMediaId(entry, localId)

        if (!file) {
            respond([status: 'NOT_FOUND'], status: HttpStatus.NOT_FOUND)
        } else {
            respond([
                    status: file.status.name(),
                    s3Url: file.s3Url,
                    mediaType: file.type.name(),
                    originalFileName: file.originalFileName,
                    uploadedAt: file.createdAt
            ])
        }
    }
}
