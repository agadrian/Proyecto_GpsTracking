package com.es.proyectgoDinAda_GPS_Tracking.repository

import com.es.proyectgoDinAda_GPS_Tracking.model.PuntosGPS
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PuntosGPSRepository: JpaRepository<PuntosGPS, Long> {

    // Obtener todas las columnas de la tabla puntosgps con la ID de la ruta
    @Query("SELECT p FROM PuntosGPS p WHERE p.ruta.id = :rutaId")
    fun findByRutaId(@Param("rutaId") rutaId: Long): List<PuntosGPS>
}