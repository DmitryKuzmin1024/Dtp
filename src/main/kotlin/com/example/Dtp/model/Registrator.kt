package com.example.Dtp.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.locationtech.jts.geom.Point
import javax.persistence.*

@Entity
@Table(name = "registrator")
data class Registrator(
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    val id: Long,
    @Column(name="code")
    val code: String,
    @Column(name = "location")
    @JsonIgnore
    val location: Point
)