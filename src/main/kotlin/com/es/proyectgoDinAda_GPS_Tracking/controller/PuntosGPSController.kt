package com.es.proyectgoDinAda_GPS_Tracking.controller

import com.es.proyectgoDinAda_GPS_Tracking.exceptions.BadRequestException
import com.es.proyectgoDinAda_GPS_Tracking.model.PuntosGPS
import com.es.proyectgoDinAda_GPS_Tracking.service.PuntosGPSService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/puntos_gps")
class PuntosGPSController {

    private lateinit var puntosGPSService: PuntosGPSService

    // Registrar un nuevo punto GPS
    @PostMapping("/{rutaId}")
    fun createPuntoGPS(
        @PathVariable rutaId: String,
        @RequestBody puntoGPS: PuntosGPS,
        authentication: Authentication
    ): ResponseEntity<PuntosGPS> {

        checkId(rutaId)

        val nuevoPuntoGPS = puntosGPSService.createPuntoGPS(rutaId, puntoGPS, authentication)

        return ResponseEntity(nuevoPuntoGPS, HttpStatus.CREATED)
    }

    // Listar todos los puntos GPS de una ruta a partir de su id
    @GetMapping("/{rutaId}")
    fun getPuntosByRutaId(
        @PathVariable rutaId: String,
        authentication: Authentication
    ): ResponseEntity<List<PuntosGPS>> {

        checkId(rutaId)

        val puntosGPS = puntosGPSService.getPuntosByRutaId(rutaId, authentication)

        return ResponseEntity(puntosGPS, HttpStatus.OK)
    }


    // Actualizar un punto GPS por id
    @PutMapping("/{puntoId}")
    fun updatePuntoGPS(
        // TODO POR AQUI
        @PathVariable puntoId: Long,
        @RequestBody updatedPuntoGPS: PuntosGPS,
        authentication: Authentication
    ): ResponseEntity<PuntosGPS> {

        val puntoActualizado = puntosGPSService.updatePuntoGPS(puntoId, updatedPuntoGPS, authentication)

        return ResponseEntity(puntoActualizado, HttpStatus.OK)
    }


    // Eliminar un punto GPS por ID
    @DeleteMapping("/{puntoId}")
    fun deletePuntoGPS(
        @PathVariable puntoId: Long,
        authentication: Authentication

    ) {
        puntosGPSService.deletePuntoGPS(puntoId, authentication)
    }



    private fun checkId(id: String) {
        if (id.isBlank()) throw BadRequestException("La ID no puede estar vacía")
        if (id.toLongOrNull() == null) throw BadRequestException("El ID debe ser un número válido")
        if (id.toLong() <= 0) throw BadRequestException("La ID introducida no es válida")
    }
}