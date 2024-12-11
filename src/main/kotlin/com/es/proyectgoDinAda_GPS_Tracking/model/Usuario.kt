package com.es.proyectgoDinAda_GPS_Tracking.model

import jakarta.persistence.*

@Entity
@Table(name = "usuarios")
data class Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, length = 40)
    var email: String? = null,

    @Column(nullable = false, length = 40)
    var password: String? = null,

    @Column(nullable = false, length = 40)
    var username: String? = null,

    var roles: String? = null, // e.j., "ROLE_USER,ROLE_ADMIN"

    @OneToMany(mappedBy = "usuario" ,cascade = [(CascadeType.ALL)], orphanRemoval = true)
    var rutas: MutableList<Ruta> = mutableListOf(),
)
