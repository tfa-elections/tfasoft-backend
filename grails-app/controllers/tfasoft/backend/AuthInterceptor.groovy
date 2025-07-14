package tfasoft.backend


import grails.artefact.Interceptor
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureException
import io.jsonwebtoken.ExpiredJwtException
import javax.crypto.spec.SecretKeySpec
import java.security.Key
import org.springframework.http.HttpStatus

class AuthInterceptor implements Interceptor {

    private static final String SECRET_KEY = "TFASOFT_SECRET_2025"

    AuthInterceptor() {
        matchAll()
                .excludes(controller: 'auth') // Allow registration and login without token
    }

    boolean before() {
        def authHeader = request.getHeader("Authorization")
        if (!authHeader || !authHeader.startsWith("Bearer ")) {
            render(status: HttpStatus.UNAUTHORIZED.value(), text: 'Missing or invalid Authorization header')
            return false
        }

        def token = authHeader.substring(7) // Remove "Bearer " prefix
        try {
            Key key = new SecretKeySpec(SECRET_KEY.bytes, "HmacSHA256")
            def claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody()
            request.setAttribute("authenticatedUsername", claims.subject)
            request.setAttribute("authenticatedEmail", claims.email)
            request.setAttribute("userRole", claims.role)
            request.setAttribute("fairId", claims.fairId)
            return true
        } catch (ExpiredJwtException | SignatureException | IllegalArgumentException e) {
            render(status: HttpStatus.UNAUTHORIZED.value(), text: 'Invalid or expired token')
            return false
        }
    }

    boolean after() { true }

    void afterView() {}
}
