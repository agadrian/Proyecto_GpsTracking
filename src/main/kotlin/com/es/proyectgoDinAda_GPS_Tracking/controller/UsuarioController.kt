package com.es.proyectgoDinAda_GPS_Tracking.controller

import com.es.proyectgoDinAda_GPS_Tracking.exceptions.BadRequestException
import com.es.proyectgoDinAda_GPS_Tracking.exceptions.ForbiddenException
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

// TODO: Mirar que retorna cada cosa y si hay que cambiarlo o no

    /**
     * Insertar un usuario
     */
    @PostMapping("/register")
    fun register(
        @RequestBody newUser: Usuario
    ): ResponseEntity<Usuario?>{

        usuarioService.registerUsuario(newUser)

        // Al cliente le retornamos con la contraseña nula para una mayor seguridad
        return ResponseEntity(newUser.copy(password = null), HttpStatus.CREATED)
    }


    /**
     * Metodo para hacer el login
     */
    @PostMapping("/login")
    fun login(@RequestBody usuario: Usuario): ResponseEntity<Any> {

        val authentication: Authentication
        try {
            /* UsernamePasswordAuthenticationToken delega la accion de autenticar al usuario a un AuthenticationProvider (DaoAuthenticacionProvider por defecto) el cual para cargar los detalles del usuario usa la funcion que tenemos enm el service de loadByUsername que es la implementacion de UserDatailsService. Por tanto, al usar el UsernamePasswordAuthenticationToken, se encarga de to_do lo explicado previamente, y por ultimo, automaticamente comprueba e intenta validar la contraseña que obtiene cifrada de la base de datos medainte el passwordEncoder, para encondear esta contraseña que se le pasa, y la compara para ver si ahce match con la que obtiene de la base de datos. Si no coincioden, se lanza una excepcion que estamos recogiendo en el catch.*/
            authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(usuario.username, usuario.password))
        }catch (e: AuthenticationException) {

            return ResponseEntity(mapOf("mensaje" to "Credenciales incorrectas!"),HttpStatus.UNAUTHORIZED)
        }


        // Si los datos introducidos son validados y correctos, significa que ya estamos bien autenticados.
        // Pasamos a generar el token usando esta autenticacion

        val token = tokenService.generateToken(authentication)

        // Devolvemos el token al cliente
        return ResponseEntity(mapOf("token" to token), HttpStatus.CREATED)
    }



    @GetMapping("/")
    fun getAllUsers(
        authentication: Authentication
    ): ResponseEntity<List<Usuario>> {

        val users = usuarioService.getAllUsers()

        return ResponseEntity(users, HttpStatus.OK)
    }


    @GetMapping("/{id}")
    fun getUserByid(
        @PathVariable id: String,
        authentication: Authentication
    ): ResponseEntity<Usuario> {

        checkId(id)

        val user = usuarioService.getById(id, authentication)

        return ResponseEntity(user.copy(password = null), HttpStatus.OK)
    }


    @PutMapping("/{id}")
    fun updateUserById(
        @PathVariable id: String,
        @RequestBody newUser: Usuario,
        authentication: Authentication
    ): ResponseEntity<Usuario> {

        checkId(id)

        val userUpdated = usuarioService.updateById(id, newUser, authentication)

        return ResponseEntity(userUpdated, HttpStatus.OK)
    }


    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: String,
        authentication: Authentication
    ){
        checkId(id)

        usuarioService.deleteById(id, authentication)

    }


    /**
     * Realizar comprobaciones basicas sobre la ID, para no repetir codigo
     */
    fun checkId(id: String) {
        if (id.isBlank()) throw BadRequestException("La ID no puede estar vacía")

        if (id.toLongOrNull() == null) throw BadRequestException("El ID debe ser un número válido")

        if (id.toLong() <= 0) throw BadRequestException("La ID introducida no es válida")
    }





}