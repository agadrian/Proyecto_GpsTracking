package com.es.proyectgoDinAda_GPS_Tracking.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class TokenService {

    @Autowired
    private lateinit var jwtEncoder: JwtEncoder

    /**
     * Usamos el autentication que obtenemos de hacer un login correcto para generar el token
     */
    fun generateToken(authentication: Authentication): String {

        val roles: String = authentication
            .authorities
            .joinToString(" ") {it.authority} // Roles del usuario

        val payload: JwtClaimsSet = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(Instant.now())
            .expiresAt(Date().toInstant().plus(Duration.of(1, ChronoUnit.HOURS))) // Expira en 1 hora
            .subject(authentication.name)
            .claim("roles", roles)
            .build()

        // Usa el jwtEncoder que esta configurado con la clave RSA privada para firmar el token
        return jwtEncoder.encode(JwtEncoderParameters.from(payload)).tokenValue
    }



}