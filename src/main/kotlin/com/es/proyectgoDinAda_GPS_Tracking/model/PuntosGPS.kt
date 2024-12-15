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

    @Column(nullable = false, length = 20)
    var latitud: Double? = null,

    @Column(nullable = false, length = 20)
    var longitud: Double? = null,

    @Column(nullable = false, length = 15)
    var fechaHora: LocalDateTime = LocalDateTime.now(),

//    @Column(nullable = true)
//    var altitud: Double? = null,
//
//    @Column(nullable = true)
//    var velocidad: Float? = null



)
