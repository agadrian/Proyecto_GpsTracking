package com.es.proyectgoDinAda_GPS_Tracking.service

import com.es.proyectgoDinAda_GPS_Tracking.exceptions.AlreadyExistsException
import com.es.proyectgoDinAda_GPS_Tracking.exceptions.NotFoundException
import com.es.proyectgoDinAda_GPS_Tracking.model.Usuario
import com.es.proyectgoDinAda_GPS_Tracking.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
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
}