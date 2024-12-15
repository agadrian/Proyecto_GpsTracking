package com.es.proyectgoDinAda_GPS_Tracking.service

import com.es.proyectgoDinAda_GPS_Tracking.exceptions.AlreadyExistsException
import com.es.proyectgoDinAda_GPS_Tracking.exceptions.BadRequestException
import com.es.proyectgoDinAda_GPS_Tracking.exceptions.ForbiddenException
import com.es.proyectgoDinAda_GPS_Tracking.exceptions.NotFoundException
import com.es.proyectgoDinAda_GPS_Tracking.model.Usuario
import com.es.proyectgoDinAda_GPS_Tracking.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UsuarioService: UserDetailsService {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository


    fun registerUsuario(
        usuario: Usuario
    ): Usuario? {

        checkEmptyFields(usuario)
        checkPassword(usuario.password!!)

        // Comprobamos que el usuario (y email) no existe en la base de datos
        // Comprobamos que el usuario (y email) no existe en la base de datos
        usuario.username?.let { usuarioRepository.findByUsername(it).ifPresent {throw AlreadyExistsException("El usuario ${usuario.username} ya existe")} }

        usuario.email?.let { usuarioRepository.findByEmail(it).ifPresent {throw AlreadyExistsException("El usuario con email ${usuario.email} ya existe")} }


        //Codificamos la contraseña, guarda el usuario en la BBDD y lo retorna

        usuario.password = passwordEncoder.encode(usuario.password)

        return usuarioRepository.save(usuario)

    }

    /**
     * Implementar la funcion de la interfaz. Esta funcion es usada por el autenticationmanager,  que us
     */
    override fun loadUserByUsername(username: String?): UserDetails {
        val usuario: Usuario = findByUsername(username!!).orElseThrow {NotFoundException("El usuario no ha sido encontrado")}

        return User.builder()
            .username(usuario.username)
            .password(usuario.password)
            .roles(usuario.roles)
            .build()
    }


    fun getAllUsers(): List<Usuario> {
        return usuarioRepository.findAll()
    }


    fun getById(
        id: String,
        authentication: Authentication
    ): Usuario {

        val usuario = findByIdOrThrow(id)

        checkIfSelfOrAdmin(usuario, authentication)

        return usuario
    }


    fun updateById(
        id: String,
        newUser: Usuario,
        authentication: Authentication
    ): Usuario{

        val usuario = findByIdOrThrow(id)

        checkIfSelfOrAdmin(usuario, authentication)
        validateNewUserData(newUser, usuario)


        usuario.username = newUser.username ?: usuario.username
        usuario.password = passwordEncoder.encode(newUser.password) ?: usuario.password
        usuario.roles = newUser.roles ?: usuario.roles
        usuario.email = newUser.email ?: usuario.email

        return usuarioRepository.save(usuario)
    }


    fun deleteById(
        id: String,
        authentication: Authentication
    ) {
        val usuario = findByIdOrThrow(id)

        checkIfSelfOrAdmin(usuario, authentication)

        usuarioRepository.delete(usuario)
    }



    /* VALIDACIONES Y COMPROBACIONES */

    fun validateNewUserData(
        newUser: Usuario,
        usuario: Usuario
    ){
        checkEmptyFields(newUser)
        checkNewName(usuario.username, newUser.username)
        checkNewEmail(usuario.email, newUser.email)
        // En este punto la password nunca sera nula ya que se com prueba antes
        checkPassword(newUser.password!!)
    }

    fun checkNewName(usuarioName: String?, newUserName: String?){
        if (newUserName != null && usuarioName != newUserName ){
            if (usuarioRepository.findByUsername(newUserName).isPresent) throw AlreadyExistsException("El nombre de usuario ya existe, use otro")
        }
    }


    fun checkNewEmail(email: String?, newEmail: String?){
        if (newEmail != null && email != newEmail){
            if (usuarioRepository.findByEmail(newEmail).isPresent) throw AlreadyExistsException("El email ya esta registrado en otra cuenta, por favor, use otro")
        }
    }

    fun checkPassword(newPassword: String){
        if (newPassword.length < 8) throw BadRequestException("La contraseña debe tener mas de 8 caracteres")
    }

    fun checkEmptyFields(newUser: Usuario){
        if (newUser.email.isNullOrBlank()) throw BadRequestException("El email no puede estar vacío")
        if (newUser.username.isNullOrBlank()) throw BadRequestException("El nombre de usuario no puede estar vacío")
        if (newUser.password.isNullOrBlank()) throw BadRequestException("La contraseña no puede estar vacía")
    }


    fun checkIfSelfOrAdmin(user: Usuario, authentication: Authentication){
        val roles = authentication.authorities.map { it.authority }

        if (authentication.name != user.username && !roles.contains("ROLE_ADMIN")){
            println("NOPE")
            throw ForbiddenException("No tienes acceso a este recurso")
        }
    }

    fun findByIdOrThrow(id: String): Usuario{
        return usuarioRepository.findByIdOrNull(id.toLong()) ?: throw NotFoundException("Usuario con ID $id no encontrado")
    }

    fun findByUsername(username: String): Optional<Usuario> {
        return usuarioRepository.findByUsername(username)
    }
}