package com.es.proyectgoDinAda_GPS_Tracking.repository

import com.es.proyectgoDinAda_GPS_Tracking.model.Ruta
import com.es.proyectgoDinAda_GPS_Tracking.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RutaRepository: JpaRepository<Ruta, Long> {
    fun findByUsuario(usuario: Usuario): List<Ruta>
    fun findByNombre(nombre: String): Optional<Ruta>
}