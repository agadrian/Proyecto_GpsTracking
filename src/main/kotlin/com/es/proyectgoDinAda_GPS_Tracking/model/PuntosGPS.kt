package com.es.proyectgoDinAda_GPS_Tracking.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "puntos_gps")
data class PuntosGPS(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "ruta_id", nullable = false)
    var ruta: Ruta? = null,

    @Column(nullable = false)
    var latitud: Double? = null,

    @Column(nullable = false)
    var longitud: Double? = null,

    @Column(nullable = false)
    var fechaHora: LocalDateTime? = null // Necesario ¿?

)
