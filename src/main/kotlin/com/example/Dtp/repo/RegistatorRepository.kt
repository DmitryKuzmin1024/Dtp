package com.example.Dtp.repo

import com.example.Dtp.model.Registrator
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RegistatorRepository : CrudRepository<Registrator, Long> {
    @Query(
        value = "select * from registrator r " +
                "where r.id " +
                "in (select t.registrator_id from transit t " +
                "where t.plate_num = :plate_num)",
        nativeQuery = true
    )
    fun findRegistratorsByPlateNum(plate_num: String): List<Registrator>

    @Query(
        value = "select * from registrator r " +
                "where r.id in " +
                "(select t.registrator_id from transit t " +
                "where t.plate_num = :plate_num " +
                "and t.registered_date >= :dateFrom " +
                "and t.registered_date <= :dateTo) ",
        nativeQuery = true
    )
    fun findRegistratorsByNumBetweenDates(
        plate_num: String,
        dateFrom: Date,
        dateTo: Date
    ): List<Registrator>

    @Query(
        value = "select * from registrator r " +
                "where r.id in " +
                "(select t.registrator_id from transit t " +
                "where t.plate_num = :plate_num " +
                "and t.registered_date >= :dateFrom " +
                "and t.registered_date <= :dateTo) " +
                "and ST_DWithin(r.location, " +
                "ST_SetSRID(ST_MakePoint(:longitude, :latitude), 0), " +
                ":radius)",
        nativeQuery = true
    )
    fun findRegistratorsByNumBetweenDatesInRadius(
        plate_num: String,
        dateFrom: Date,
        dateTo: Date,
        longitude: Double,
        latitude : Double,
        radius: Double
    ): List<Registrator>
}