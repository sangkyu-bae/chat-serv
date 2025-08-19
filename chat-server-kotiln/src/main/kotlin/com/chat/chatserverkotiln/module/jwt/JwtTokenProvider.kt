package com.chat.chatserverkotiln.module.jwt
import io.jsonwebtoken.*
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.SignatureException
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret-key}")
    private val secretKeyProp: String
) {
    private val log = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    private lateinit var secret: String

    @PostConstruct
    fun init() {
        secret = Base64.getEncoder().encodeToString(secretKeyProp.toByteArray())
    }

    fun validateJwtToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token)
            true
        } catch (e: SignatureException) {
            log.error("Invalid JWT signature: {}", e.message); false
        } catch (e: MalformedJwtException) {
            log.error("Invalid JWT token: {}", e.message); false
        } catch (e: ExpiredJwtException) {
            log.error("JWT token is expired: {}", e.message); false
        } catch (e: UnsupportedJwtException) {
            log.error("JWT token is unsupported: {}", e.message); false
        } catch (e: IllegalArgumentException) {
            log.error("JWT claims string is empty: {}", e.message); false
        }
    }

    fun getClaimsFromJwtToken(token: String): Claims? {
        return try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }

}