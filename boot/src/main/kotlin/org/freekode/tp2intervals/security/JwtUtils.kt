package org.freekode.tp2intervals.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtils {

    @Value("\${jwt.secret:12345678901234567890123456789012}") // 32 characters default
    private lateinit var jwtSecret: String

    @Value("\${jwt.expirationMs:86400000}") // 1 day default
    private var jwtExpirationMs: Int = 86400000

    private fun getSigningKey(): SecretKey {
        return Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }

    fun generateJwtToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as CustomUserDetails
        return Jwts.builder()
            .subject((userPrincipal.username))
            .claim("userId", userPrincipal.id)
            .issuedAt(Date())
            .expiration(Date((Date()).time + jwtExpirationMs))
            .signWith(getSigningKey())
            .compact()
    }

    fun getUserNameFromJwtToken(token: String): String {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
            .subject
    }

    fun validateJwtToken(authToken: String): Boolean {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken)
            return true
        } catch (e: Exception) {
            // Log exceptions
        }
        return false
    }
}
