package com.tfa.elections.tfasoft


import grails.rest.*
import grails.converters.*

import grails.rest.RestfulController
import org.springframework.http.HttpStatus

class AuthController {

    static responseFormats = ['json']

    def authService

    /**
     * Register a new user + FAIR profile
     */
    def register() {
        def json = request.JSON

        if (!json.username || !json.email || !json.password || !json.deviceId || !json.pollingStationCode) {
            respond([error: 'Missing required fields.'], status: HttpStatus.BAD_REQUEST)
            return
        }

        def user = new User(
                username: json.username,
                email: json.email,
                passwordHash: authService.encodePassword(json.password)
        )

        if (!user.validate()) {
            respond user.errors, status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        user.save(flush: true)

        def fair = new FAIRProfile(
                deviceId: json.deviceId,
                phoneNumber: json.phoneNumber,
                pollingStationCode: json.pollingStationCode,
                registeredAt: new Date(),
                user: user
        )

        if (!fair.validate()) {
            user.delete(flush: true)
            respond fair.errors, status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        fair.save(flush: true)
        respond([user: user.username, fairProfile: fair.pollingStationCode], status: HttpStatus.CREATED)
    }

    /**
     * Basic login (stub for JWT/token-based auth)
     */
    def login() {
        def json = request.JSON
        def user = User.findByUsername(json.username)

        if (user && authService.verifyPassword(json.password, user.passwordHash)) {
            respond([token: authService.generateToken(user)], status: HttpStatus.OK)
        } else {
            respond([error: 'Invalid credentials.'], status: HttpStatus.UNAUTHORIZED)
        }
    }
}
