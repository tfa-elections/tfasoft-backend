package tfasoft.backend

class UrlMappings {

    static mappings = {
        delete "/$controller/$id(.$format)?"(action:"delete")
        get "/$controller(.$format)?"(action:"index")
        get "/$controller/$id(.$format)?"(action:"show")
        post "/$controller(.$format)?"(action:"save")
        put "/$controller/$id(.$format)?"(action:"update")
        patch "/$controller/$id(.$format)?"(action:"patch")

        "/"(controller: 'application', action:'index')
        "500"(view: '/error')
        "404"(view: '/notFound')

        // API v1 base routes
        group "/api/v1", {

            // Auth endpoints
            post "/auth/login"(controller: 'auth', action: 'login')
            post "/auth/register"(controller: 'auth', action: 'register')

            // Result endpoints
            post "/result/submit"(controller: 'result', action: 'submit')
            get "/result/consensus"(controller: 'result', action: 'consensus')
            get "/result/submissionStatus"(controller: 'result', action: 'submissionStatus')

            // Media endpoints
            post "/media/upload"(controller: 'media', action: 'upload')
            post "/media/registerPendingUpload"(controller: 'media', action: 'registerPendingUpload')
            post "/media/completePendingUpload"(controller: 'media', action: 'completePendingUpload')
            get "/media/listByResult"(controller: 'media', action: 'listByResult')
            get "/media/getStatusByLocalMediaId"(controller: 'media', action: 'getStatusByLocalMediaId')

            // Polling Station Hierarchical Navigation
            get "/polling/regions"(controller: 'pollingStation', action: 'listRegions')
            get "/polling/divisions"(controller: 'pollingStation', action: 'listDivisions')
            get "/polling/subdivisions"(controller: 'pollingStation', action: 'listSubdivisions')
            get "/polling/centers"(controller: 'pollingStation', action: 'listCenters')
            get "/polling/stations"(controller: 'pollingStation', action: 'listStations')
            get "/polling/byCode"(controller: 'pollingStation', action: 'getByCode')

            // Aggregation endpoints
            get "/aggregation/summary"(controller: 'resultAggregation', action: 'summary')
            get "/aggregation/others"(controller: 'resultAggregation', action: 'others')

            // Review and Alerts endpoints
            post "/review/submitSuspicion"(controller: 'resultReview', action: 'submitSuspicion')
            post "/review/markAsRejectionCandidate"(controller: 'resultReview', action: 'markAsRejectionCandidate')
            post "/review/markAsRemovable"(controller: 'resultReview', action: 'markAsRemovable')

            // Audit access
            get "/audit"(controller: 'resultAudit', action: 'index')

            // Consensus status endpoints
            get "/consensus/agreement"(controller: 'consensus', action: 'agreement')
            get "/consensus/submissions"(controller: 'consensus', action: 'submissions')
        }
    }
}
