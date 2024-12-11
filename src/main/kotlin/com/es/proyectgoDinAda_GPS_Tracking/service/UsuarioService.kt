package com.es.proyectgoDinAda_GPS_Tracking.service

import com.es.proyectgoDinAda_GPS_Tracking.exceptions.AlreadyExistsException
import com.es.proyectgoDinAda_GPS_Tracking.exceptions.BadRequestException
import com.es.proyectgoDinAda_GPS_Tracking.exceptions.NotFoundException
import com.es.proyectgoDinAda_GPS_Tracking.model.Usuario
import com.es.proyectgoDinAda_GPS_Tracking.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsuarioService: UserDetailsService {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository


    fun registerUsuario(usuario: Usuario): Usuario? {

        // Comprobamos que el usuario (y email) no existe en la base de datos

        usuario.username?.let { usuarioRepository.findByUsername(it).ifPresent {throw AlreadyExistsException("El usuario ${usuario.username} ya existe")} }

        usuario.email?.let { usuarioRepository.findByEmail(it).ifPresent {throw AlreadyExistsException("El usuario con email ${usuario.email} ya existe")} }


        // Creamos la instancia de Usuario

        usuario.password = passwordEncoder.encode(usuario.password)

        return usuarioRepository.save(usuario)


        /*
         La password del newUsuario debe estar hasheada, así que usamos el passwordEncoder que tenemos definido.
         ¿De dónde viene ese passwordEncoder?
         El objeto passwordEncoder está definido al principio de esta clase.
         */
        // Devolvemos el Usuario insertado en la BDD
    }

    /**
     * Implementar la funcion de la interfaz
     */
    override fun loadUserByUsername(username: String?): UserDetails {
        val usuario: Usuario = usuarioRepository.findByUsername(username!!).orElseThrow {NotFoundException("El usuario no ha sido encontrado")}


        return User.builder()
            .username(usuario.username)
            .password(usuario.password)
            .roles(usuario.roles)
            .build()
    }


    fun getAllUsers(): List<Usuario> {
        return usuarioRepository.findAll()
    }

    fun getById(id: String): Usuario {
        val usuario: Usuario = usuarioRepository.findByIdOrNull(id.toLong()) ?: throw NotFoundException("Usuario con ID $id no encontrado")

        return usuario
    }

    fun updateById(id: String, newUser: Usuario): Usuario{
        val usuario = usuarioRepository.findByIdOrNull(id.toLong()) ?: throw NotFoundException("Usuario con id $id no ha sido encontrado")

        checkEmptyFields(newUser)
        checkNewName(usuario.username, newUser.username)
        checkNewEmail(usuario.email, newUser.email)


        // TODO: Hacer un check de estas opciones y otro de las nuevas que se ponen, para comprobar que no existan ya en la base de datos, en cuyo caso lanzar excepcion
        usuario.username = newUser.username ?: usuario.username
        usuario.password = newUser.password ?: usuario.password
        usuario.roles = newUser.roles ?: usuario.roles
        usuario.email = newUser.email ?: usuario.email

        return usuarioRepository.save(usuario)
    }


    fun deleteById(id: String) {
        val usuario = usuarioRepository.findByIdOrNull(id.toLong()) ?: throw NotFoundException("Usuario con id $id no encontrado")

        usuarioRepository.delete(usuario)
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

    fun checkEmptyFields(newUser: Usuario){
        if (newUser.email.isNullOrBlank()) throw BadRequestException("El email no puede estar vacío")
        if (newUser.username.isNullOrBlank()) throw BadRequestException("El nombre de usuario no puede estar vacío")
        if (newUser.password.isNullOrBlank()) throw BadRequestException("La contraseña no puede estar vacía")
    }
}