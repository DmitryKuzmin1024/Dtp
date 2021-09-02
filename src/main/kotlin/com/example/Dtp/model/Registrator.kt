package com.example.Dtp.model

import com.example.Dtp.CustomJson
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.locationtech.jts.geom.Point
import javax.persistence.*

@Entity
@JsonSerialize(using = CustomJson::class)
@Table(name = "registrator")
data class Registrator(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "code")
    val code: String,
    @Column(name = "location")
    val location: Point
) {
    init {
        require(validateCode(code)) { "code format: XXX-XXX" }
    }

    private fun validateCode(code: String) =
        code.matches(Regex("(\\d{3})[-](\\d{3})"))

}