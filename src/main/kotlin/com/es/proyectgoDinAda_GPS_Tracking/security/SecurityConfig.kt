package com.es.proyectgoDinAda_GPS_Tracking.security

import com.es.proyectgoDinAda_GPS_Tracking.exceptions.scurityConfigCustomExceptions.CustomAccessDeniedHandler
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.config.Customizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Autowired
    private lateinit var rsaKeys: RSAKeysProperties


    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        // Le paso los contorladores concretos para las excepciones de este config, ya que mi manejador de excepociones de el resto de la app no es compatible tal y como esta. Las excepciones personalizadas si, pero el manejador no.
        customAccessDeniedHandler: CustomAccessDeniedHandler,
    ): SecurityFilterChain {

        return http
            .csrf { csrf -> csrf.disable() } // Cross-Site Forgery
            .authorizeHttpRequests { auth ->
                auth
                    /* PUBLICOS */
                    .requestMatchers(HttpMethod.POST,"/usuarios/register").permitAll()
                    .requestMatchers(HttpMethod.POST,"/usuarios/login").permitAll() // Permitir hacer el login a todos

                    /* PRIVADOS USUARIOS*/
                    .requestMatchers(HttpMethod.GET,"/usuarios/").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET,"/usuarios/{id}").authenticated()
                    .requestMatchers(HttpMethod.PUT,"/usuarios/{id}").authenticated()
                    .requestMatchers(HttpMethod.DELETE,"/usuarios/{id}").authenticated()

                    /* PRIVADOS RUTAS*/
                    .requestMatchers(HttpMethod.GET,"/rutas/").authenticated()
                    .requestMatchers(HttpMethod.GET,"/rutas/{id}").authenticated()
                    .requestMatchers(HttpMethod.POST,"/rutas/").authenticated()
                    .requestMatchers(HttpMethod.PUT,"/rutas/{id}").authenticated()
                    .requestMatchers(HttpMethod.DELETE,"/rutas/{id}").authenticated()

                    /* PRIVADOS PUNTOS GPS */
                    .requestMatchers(HttpMethod.GET,"/puntos_gps/{rutaId}").authenticated()
                    .requestMatchers(HttpMethod.POST,"/puntos_gps/{rutaId}").authenticated()
                    .requestMatchers(HttpMethod.PUT,"/puntos_gps/{puntoId}").authenticated()
                    .requestMatchers(HttpMethod.DELETE,"/puntos_gps/{puntoId}").authenticated()

                    //.requestMatchers("/secretos/ficha1").hasAuthority("ADMIN") // El hasrole por defecto tiene que estar autenticated
                    //.requestMatchers(HttpMethod.DELETE, "/rutas_protegidas/eliminar/{nombre}").authenticated()

            }


            .exceptionHandling { exceptions ->
                // Excepciones
                exceptions
                    // AccessDeniedHandler para ForbiddenException
                    .accessDeniedHandler(customAccessDeniedHandler)

                    // AuthenticationEntryPoint para UnauthorizedException
                //.authenticationEntryPoint(customAuthenticationEntryPoint)
            }


            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt ( Customizer.withDefaults() ) // Establece que el contrl de autenticacion se hagapor jwt, en vez de una atenticacion basica
            }



            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No mantiene estado en la sesion, t_odo el rato depende del token JWT
            }

            .httpBasic(Customizer.withDefaults())
            .build()
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }



    /**
     * Inicializar un objeto de tipo AuthenticationManager (para el login de usuario controller)
     */
    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    /**
     * Metodo para codificar un JWT
     */
    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk: JWK = RSAKey.Builder(rsaKeys.publicKey).privateKey(rsaKeys.privateKey).build()
        val jwks: JWKSource<SecurityContext> = ImmutableJWKSet(JWKSet(jwk))
        return NimbusJwtEncoder(jwks)
    }


    /**
     * Metodo para decodificar un JWT
     */
    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey).build()
    }
}