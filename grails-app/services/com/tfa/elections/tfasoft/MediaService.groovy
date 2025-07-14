package com.tfa.elections.tfasoft

import org.springframework.web.multipart.MultipartFile
import grails.gorm.transactions.Transactional

@Transactional
class MediaService {

    def awsS3Service

    static final int MAX_MEDIA_PER_RESULT = 10

    List<MediaFile> uploadMediaFiles(ResultEntry entry, List<MultipartFile> files, String deviceId, String mediaTypeStr) {
        if (!entry) throw new IllegalArgumentException("ResultEntry is required")

        def currentCount = MediaFile.countByResultEntry(entry)
        if (currentCount + files.size() > MAX_MEDIA_PER_RESULT) {
            throw new IllegalStateException("You can upload a maximum of $MAX_MEDIA_PER_RESULT files per result entry.")
        }

        def type = MediaFile.MediaType.valueOf(mediaTypeStr?.toUpperCase())
        def uploaded = []

        files.eachWithIndex { MultipartFile file, int idx ->
            def key = "results/${entry.pollingStationCode}/${entry.id}/${System.currentTimeMillis()}_${idx}_${file.originalFilename}"
            def url = awsS3Service.uploadFile(file, key)

            def metadata = new MediaFile(
                    resultEntry: entry,
                    s3Key: key,
                    s3Url: url,
                    localDeviceId: deviceId,
                    originalFileName: file.originalFilename,
                    fileSize: file.size,
                    mimeType: file.contentType,
                    type: type,
                    status: MediaFile.UploadStatus.COMPLETED
            )

            metadata.save(flush: true)
            uploaded << metadata
        }

        return uploaded
    }

    List<MediaFile> getMediaForResult(Long resultId) {
        return MediaFile.findAllByResultEntry(ResultEntry.get(resultId))
    }
}
