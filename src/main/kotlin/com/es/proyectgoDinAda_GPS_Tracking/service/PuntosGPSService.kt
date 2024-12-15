package com.es.proyectgoDinAda_GPS_Tracking.service

import com.es.proyectgoDinAda_GPS_Tracking.exceptions.BadRequestException
import com.es.proyectgoDinAda_GPS_Tracking.exceptions.ForbiddenException
import com.es.proyectgoDinAda_GPS_Tracking.exceptions.NotFoundException
import com.es.proyectgoDinAda_GPS_Tracking.exceptions.UnauthorizedException
import com.es.proyectgoDinAda_GPS_Tracking.model.PuntosGPS
import com.es.proyectgoDinAda_GPS_Tracking.model.Usuario
import com.es.proyectgoDinAda_GPS_Tracking.repository.PuntosGPSRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class PuntosGPSService {

    private lateinit var usuarioService: UsuarioService
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
        puntoId: String,
        newPuntoGPS: PuntosGPS,
        authentication: Authentication
    ): PuntosGPS {
        // TODO

        // Verificar acceso permitido
        checkIfSelfOrAdmin(puntoId, authentication)

        val puntoActual = findByIdOrThrow(puntoId)


        // Actualizar los campos que quiera
        puntoActual.latitud = newPuntoGPS.latitud ?: puntoActual.latitud
        puntoActual.longitud = newPuntoGPS.longitud ?: puntoActual.longitud
        puntoActual.fechaHora = newPuntoGPS.fechaHora ?: puntoActual.fechaHora

        return puntosGPSRepository.save(puntoActual)
    }


    // Eliminar un punto GPS por ID
    fun deletePuntoGPS(
        puntoId: String,
        authentication: Authentication
    ) {
        checkIfSelfOrAdmin(puntoId, authentication)

        val punto = puntosGPSRepository.findById(puntoId.toLongOrNull() ?: throw BadRequestException("Formato de id introducida no valido")).orElseThrow { NotFoundException("Punto GPS con ID $puntoId no encontrado") }

        puntosGPSRepository.delete(punto)
    }


    private fun findByIdOrThrow(id: String): PuntosGPS {
        return puntosGPSRepository.findByIdOrNull(id.toLong()) ?: throw NotFoundException("Punto gps con ID $id no encontrado")
    }

    fun checkIfSelfOrAdmin(idPuntoGPS: String, authentication: Authentication){

        // Obtengo el puntogps
        val puntoGPS = findByIdOrThrow(idPuntoGPS)

        // Obtengo la ruta asociada al punto gps
        val ruta = puntoGPS.ruta ?: throw NotFoundException("El punto GPS no tiene una ruta asociada")

        // Obtengo el usuario de la autentication
        val user = usuarioService.findByUsername(authentication.name).orElseThrow { UnauthorizedException("Usuario no autorizado para esta operacion")}


        // Verificar si quien lo intenta es el propio usuario o un admin
        if (ruta.usuario?.id != user.id && !user.roles?.contains("ADMIN")!!) throw ForbiddenException("No tienes permisos para acceder a este recurso")
    }


}