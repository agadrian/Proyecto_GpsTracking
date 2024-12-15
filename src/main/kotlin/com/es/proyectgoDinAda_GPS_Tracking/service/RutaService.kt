package com.es.proyectgoDinAda_GPS_Tracking.service

import com.es.proyectgoDinAda_GPS_Tracking.exceptions.AlreadyExistsException
import com.es.proyectgoDinAda_GPS_Tracking.exceptions.BadRequestException
import com.es.proyectgoDinAda_GPS_Tracking.exceptions.ForbiddenException
import com.es.proyectgoDinAda_GPS_Tracking.exceptions.NotFoundException
import com.es.proyectgoDinAda_GPS_Tracking.model.Ruta
import com.es.proyectgoDinAda_GPS_Tracking.repository.RutaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class RutaService {

    @Autowired
    private lateinit var rutaRepository: RutaRepository

    @Autowired
    private lateinit var usuarioService: UsuarioService


    fun createRoute(
        ruta: Ruta,
        authentication: Authentication
    ): Ruta {

        if (ruta.nombre.isNullOrBlank()) {
            throw BadRequestException("El nombre de la ruta no puede estar vacío")
        }

        // Comprobar que no exista ese nombre ya
        val rutaExistente = rutaRepository.findByNombre(ruta.nombre!!)
        if (rutaExistente.isPresent) {
            throw AlreadyExistsException("Ya existe una ruta con el nombre ${ruta.nombre}")
        }

        val usuario = usuarioService.findByUsername(authentication.name).orElseThrow {NotFoundException("Error - Usuario actual no encontrado")}


        ruta.usuario = usuario

        return rutaRepository.save(ruta)
    }

    /**
     * Obtiene todas las rutas del usuario concreto
     */
    fun getAllRoutes(authentication: Authentication): List<Ruta> {
        val usuario = usuarioService.findByUsername(authentication.name).orElseThrow {NotFoundException("Error - Usuario actual no encontrado")}

        return rutaRepository.findByUsuario(usuario)
    }


    fun getRouteById(
        id: String,
        authentication: Authentication
    ): Ruta {

        val usuario = usuarioService.findByUsername(authentication.name).orElseThrow {NotFoundException("Error - Usuario actual no encontrado")}

        val ruta = rutaRepository.findById(id.toLong()).orElseThrow { NotFoundException("Ruta con ID $id no encontrada") }

        // Verificar si la ruta pertenece al usuario autenticado
        if (ruta.usuario?.id != usuario.id) {
            throw ForbiddenException("No tienes acceso a esta ruta")
        }

        return ruta
    }


    fun updateRouteById(
        id: String,
        nuevaRuta: Ruta,
        authentication: Authentication
    ): Ruta {
        val rutaActual = getRouteById(id, authentication)

        if (nuevaRuta.nombre.isNullOrBlank()) throw BadRequestException("El nombre de la ruta no puede estar vacío")


        val rutaExistente = rutaRepository.findByNombre(nuevaRuta.nombre!!)

        // Si ya existe el nombre de la ruta, y no es ella misma
        if(rutaExistente.isPresent && rutaExistente.get().id != id.toLong()) throw AlreadyExistsException("Ya existe una ruta con el nombre ${nuevaRuta.nombre}")


        // Actualizar el nombre, ya que es lo uynico que quiero que se pueda modificar
        rutaActual.nombre = nuevaRuta.nombre ?: rutaActual.nombre


        return rutaRepository.save(rutaActual)
    }

    fun deleteRouteById(
        id: String,
        authentication: Authentication
    ) {
        val rutaActual = getRouteById(id, authentication)

        rutaRepository.delete(rutaActual)

    }

    //TODO
    private fun validarPropietarioRuta(ruta: Ruta, authentication: Authentication) {
        val roles = authentication.authorities.map { it.authority }
        val isSelf =  ruta.usuario?.username == authentication.name
        val isAdmin = roles.contains("ROLE_ADMIN")

        if (!isSelf && !isAdmin) throw ForbiddenException("No tienes acceso a este recurso")
    }






}