package com.example.Dtp.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.locationtech.jts.geom.Point
import javax.persistence.*

@Entity
@Table(name = "registrator")
data class Registrator(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "code")
    val code: String,
    @Column(name = "location")
    @JsonIgnore
    val location: Point
) {
    init {
        require(validateCode(code)) { "code format: XXX-XXX" }
    }

    private fun validateCode(code: String) =
        code.matches(Regex("(\\d{3})[-](\\d{3})"))

}