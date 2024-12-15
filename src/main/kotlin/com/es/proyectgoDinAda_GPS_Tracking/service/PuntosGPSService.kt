package com.es.proyectgoDinAda_GPS_Tracking.service

import com.es.proyectgoDinAda_GPS_Tracking.exceptions.NotFoundException
import com.es.proyectgoDinAda_GPS_Tracking.model.PuntosGPS
import com.es.proyectgoDinAda_GPS_Tracking.repository.PuntosGPSRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class PuntosGPSService {

    private lateinit var puntosGPSRepository: PuntosGPSRepository
    private lateinit var rutaService: RutaService


    fun createPuntoGPS(
        rutaId: String,
        puntoGPS: PuntosGPS,
        authentication: Authentication
    ): PuntosGPS {

        // Verifica que la ruta exista
        val ruta = rutaService.getRouteById(rutaId, authentication)
        puntoGPS.ruta = ruta

        // Guarda el nuevo punto GPS
        return puntosGPSRepository.save(puntoGPS)
    }

    // Obtener all puntos gps asociados a la ruta
    fun getPuntosByRutaId(
        rutaId: String,
        authentication: Authentication
    ): List<PuntosGPS> {

        val ruta = rutaService.getRouteById(rutaId, authentication)

        // Retorna los puntos GPS asociados a la ruta
        return puntosGPSRepository.findByRutaId(ruta.id!!)
    }


    // Actualizar un punto GPS
    fun updatePuntoGPS(
        puntoId: Long,
        newPuntoGPS: PuntosGPS,
        authentication: Authentication
    ): PuntosGPS {
        // TODO
        val puntoActual = puntosGPSRepository.findById(puntoId)
            .orElseThrow { NotFoundException("Punto GPS con ID $puntoId no encontrado") }

        // Actualizar los campos que quiera
        puntoActual.latitud = newPuntoGPS.latitud ?: puntoActual.latitud
        puntoActual.longitud = newPuntoGPS.longitud ?: puntoActual.longitud
        puntoActual.fechaHora = newPuntoGPS.fechaHora ?: puntoActual.fechaHora

        return puntosGPSRepository.save(puntoActual)
    }


    // Eliminar un punto GPS por ID
    fun deletePuntoGPS(
        puntoId: Long,
        authentication: Authentication
    ) {
        val punto = puntosGPSRepository.findById(puntoId)
            .orElseThrow { NotFoundException("Punto GPS con ID $puntoId no encontrado") }

        puntosGPSRepository.delete(punto)
    }



}