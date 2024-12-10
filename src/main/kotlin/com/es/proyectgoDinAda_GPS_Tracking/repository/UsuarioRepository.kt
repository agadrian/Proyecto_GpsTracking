package com.es.proyectgoDinAda_GPS_Tracking.repository

import com.es.proyectgoDinAda_GPS_Tracking.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UsuarioRepository: JpaRepository<Usuario, Long> {
    fun findByUsername(username: String): Optional<Usuario>
    fun findByEmail(email: String): Optional<Usuario>
}