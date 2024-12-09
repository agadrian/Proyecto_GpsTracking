package com.es.proyectgoDinAda_GPS_Tracking.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "rutas")
data class Ruta(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    var usuario: Usuario? = null,

    @OneToMany(mappedBy = "ruta", cascade = [(CascadeType.ALL)], orphanRemoval = true)
    var puntos: List<PuntosGPS>? = null,

    @Column(nullable = false)
    var nombre: String? = null,

    @Column(nullable = false)
    var fechaInicio: LocalDateTime? = null,

    @Column(nullable = false)
    var fechaFin: LocalDateTime? = null,

    var distancia: Double? = null, // ¿?
)
