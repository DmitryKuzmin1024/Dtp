package com.example.Dtp.repo

import com.example.Dtp.model.Transit
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.transaction.Transactional

@Repository
interface TransitRepository : CrudRepository<Transit, Long> {
    @Query(
        value = "select * from transit t " +
                "where t.registrator_id " +
                "in (select r.id from registrator r where r.id = :id) " +
                "and t.vehicle_type = 'TRUCK'",
        nativeQuery = true
    )
    fun findTransitsTruckByRegistratorID(id: Long): List<Transit>

    @Transactional
    @Modifying
    @Query(
        value = "insert into transit (plate_num, vehicle_type, registrator_id, registered_date) " +
                "values (:plateNum, 'TRUCK', :registratorId, :registeredDate)",
        nativeQuery = true
    )
    fun setTransit(
        plateNum: String,
        registratorId: Long,
        registeredDate: Date
    )

    @Query(
        value = "select * from transit t " +
                "where t.registrator_id " +
                "in (select r.id from registrator r where r.id = :id) " +
                "and t.registered_date >= :dateFrom " +
                "and t.registered_date <= :dateTo",
        nativeQuery = true
    )
    fun findTransitsByRegistratorIdBetweenDates(
        id: Long,
        dateFrom: Date,
        dateTo: Date
    ): List<Transit>

//
//    fun findByPlateNum(plateNum: String): MutableIterable<Transit>
//    fun findByRegistratorId(registratorId: Long): MutableIterable<Transit>
}