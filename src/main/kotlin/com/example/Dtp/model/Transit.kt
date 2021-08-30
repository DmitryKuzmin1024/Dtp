package com.example.Dtp.model

import com.example.Dtp.PostgreSQLEnumType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "transit")
@TypeDef(
    name = "pgsql_enum",
    typeClass = PostgreSQLEnumType::class
    )
data class Transit(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    val id: Long,
    @Column(name="plate_num")
    val plateNum: String,
    @Type( type = "pgsql_enum" )
    @Enumerated(EnumType.STRING)
    @Column(name="vehicle_type")
    val vehicleType: VehicleType,
    @Column(name="registrator_id")
    val registratorId: Long,
    @Column(name="registered_date")
    val registeredDate: Date

) {
    enum class VehicleType {
        SEDAN, TRUCK
    }
}