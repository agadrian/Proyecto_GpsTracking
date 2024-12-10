package com.es.proyectgoDinAda_GPS_Tracking.controller

import com.es.proyectgoDinAda_GPS_Tracking.exceptions.BadRequestException
import com.es.proyectgoDinAda_GPS_Tracking.model.Usuario
import com.es.proyectgoDinAda_GPS_Tracking.security.TokenService
import com.es.proyectgoDinAda_GPS_Tracking.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.naming.AuthenticationException

@RestController
@RequestMapping("/usuarios")
class UsuarioController {


    @Autowired
    private lateinit var usuarioService: UsuarioService

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var tokenService: TokenService


    /**
     * Insertar un usuario
     */
    @PostMapping("/register")
    fun register(
        @RequestBody newUser: Usuario
    ): ResponseEntity<Usuario?> {

        if (newUser.username.isNullOrBlank()) throw BadRequestException("El nombre de usuario es obligatorio")

        if (newUser.password.isNullOrBlank()) throw BadRequestException("La contraseña es obligatoria")

        if (newUser.email.isNullOrBlank()) throw BadRequestException("El email es obligatorio")


        usuarioService.registerUsuario(newUser)

        // Al cliente le retornamos con la contraseña nula para una mayor seguridad
        return ResponseEntity(newUser.copy(password = null), HttpStatus.CREATED)
    }

    /**
     * Metodo para hacer el login
     */
    @PostMapping("/login")
    fun login(@RequestBody usuario: Usuario): ResponseEntity<Any>? {
        val authentication: Authentication
        try {
            authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(usuario.username, usuario.password))
        }catch (e: AuthenticationException) {
            return ResponseEntity(mapOf("mensaje" to "Credenciales incorrectas!"),HttpStatus.UNAUTHORIZED)
        }

        // Si pasamos la autenticacion, significa que ya estamos bien autenticados.
        // Pasamos a generar el token

        var token = ""
        token = tokenService.generateToken(authentication)


        return ResponseEntity(mapOf("token" to token), HttpStatus.CREATED)

    }


    // Saludar user
    @GetMapping("/usuario_autenticado")
    fun saludarUserAutenticado(authentication: Authentication): String {
        return "Hola ${authentication.name}"
    }


    @GetMapping("/")
    fun getAllUsers(): ResponseEntity<List<Usuario>> {
        val users = usuarioService.getAllUsers()
        return ResponseEntity(users, HttpStatus.OK)
    }
}