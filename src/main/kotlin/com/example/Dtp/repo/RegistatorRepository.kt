package com.example.Dtp.repo

import com.example.Dtp.model.Registrator
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RegistatorRepository: CrudRepository<Registrator, Long> {
    fun findByCode(code: String): MutableIterable<Registrator>
}