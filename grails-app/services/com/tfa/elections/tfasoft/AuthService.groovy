package com.tfa.elections.tfasoft

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import javax.crypto.spec.SecretKeySpec
import java.security.Key
import java.util.Date

class AuthService {

    private static final String SECRET_KEY = "TFASOFT_SECRET_2025"
    private static final long EXPIRATION_MILLIS = 1000L * 60 * 60 * 24 // 24 hours

    def grailsApplication

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder()

    String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword)
    }

    boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }

    String generateToken(User user) {
        Key key = new SecretKeySpec(SECRET_KEY.bytes, SignatureAlgorithm.HS256.JcaName)

        return Jwts.builder()
                .setSubject(user.username)
                .setIssuer("tfasoft-auth")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MILLIS))
                .claim("email", user.email)
                .claim("role", user.role?.name())
                .claim("fairId", user.fairProfile?.id)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact()
    }

    String getUsernameFromToken(String token) {
        Key key = new SecretKeySpec(SECRET_KEY.bytes, SignatureAlgorithm.HS256.JcaName)
        def claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody()
        return claims.subject
    }
}