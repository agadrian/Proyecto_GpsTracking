package com.es.proyectgoDinAda_GPS_Tracking.controller

import com.es.proyectgoDinAda_GPS_Tracking.exceptions.BadRequestException
import com.es.proyectgoDinAda_GPS_Tracking.exceptions.ForbiddenException
import com.es.proyectgoDinAda_GPS_Tracking.exceptions.NotFoundException
import com.es.proyectgoDinAda_GPS_Tracking.model.Ruta
import com.es.proyectgoDinAda_GPS_Tracking.repository.UsuarioRepository
import com.es.proyectgoDinAda_GPS_Tracking.service.RutaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.util.RouteMatcher
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rutas")
class RutaController {


    @Autowired
    private lateinit var rutaService: RutaService


    /**
     * Registrar en la bbdd una nueva ruta
     */
    @PostMapping("/")
    fun registerNewRoute(
        @RequestBody ruta: Ruta,
        authentication: Authentication
    ): ResponseEntity<Ruta> {

        if (ruta.nombre.isNullOrBlank()) {
            throw BadRequestException("El nombre de la ruta no puede estar vacío")
        }

        val nuevaRuta = rutaService.createRoute(ruta, authentication)
        return ResponseEntity(nuevaRuta, HttpStatus.CREATED)
    }


    /**
     * Obtener listado de todas las rutas
     */
    @GetMapping("/")
    fun getAllRoutes(
        authentication: Authentication
    ): ResponseEntity<List<Ruta>> {
        val allRutas = rutaService.getAllRoutes(authentication)
        return ResponseEntity(allRutas, HttpStatus.OK)
    }


    /**
     * Obtener ruta por id
     */
    @GetMapping("/{id}")
    fun getRouteById(
        @PathVariable id: String,
        authentication: Authentication
    ): ResponseEntity<Ruta> {

        checkId(id)

        val newRuta = rutaService.getRouteById(id, authentication)

        return ResponseEntity(newRuta, HttpStatus.OK)
    }

    /**
     * Actualizar ruta por id
     */
    @PutMapping("/{id}")
    fun updateRoute(
        @PathVariable id: String,
        @RequestBody newRuta: Ruta,
        authentication: Authentication
    ): ResponseEntity<Ruta> {

        val ruta = rutaService.updateRouteById(id, newRuta, authentication)
        return ResponseEntity(ruta, HttpStatus.OK)

    }


    /**
     * Borrar ruta por id
     */
    @DeleteMapping("/{id}")
    fun deleteRouteById(
        @PathVariable id: String,
        authentication: Authentication
    ){
        checkId(id)

        rutaService.deleteRouteById(id, authentication)
    }


    fun checkId(id: String) {
        if (id.isBlank()) throw BadRequestException("La ID no puede estar vacía")
        if (id.toLongOrNull() == null) throw BadRequestException("El ID debe ser un número válido")
        if (id.toLong() <= 0) throw BadRequestException("La ID introducida no es válida")
    }
}