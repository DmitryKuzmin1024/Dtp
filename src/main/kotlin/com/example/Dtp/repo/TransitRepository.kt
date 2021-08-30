package com.example.Dtp.repo

import com.example.Dtp.model.Transit
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TransitRepository : CrudRepository<Transit, Long> {
    fun findByPlateNum(plateNum: String): MutableIterable<Transit>
    fun findByRegistratorId(registratorId: Long): MutableIterable<Transit>
}