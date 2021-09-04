package com.example.Dtp.controllers

import com.example.Dtp.repo.RegistatorRepository
import com.example.Dtp.model.Registrator
import com.example.Dtp.model.Transit
import com.example.Dtp.repo.TransitRepository
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.*

@RestController
class TransitController(
    private val transitRepository: TransitRepository,
    private val registratorRepository: RegistatorRepository
) {
    // 1. Получить все регистраторы под которыми был зафиксирован проезд транспортного средства
    // с указанным номером
    @GetMapping("/findRegistratorsByPlateNum")
    fun findRegistratorsByPlateNum(
        @RequestParam("plate_num") plate_num: String
    ): List<Registrator> {
        return registratorRepository.findRegistratorsByPlateNum(plate_num)
    }

    // 2. Получить все проезды с типом ТС Truck,
    // по переданному идентификатору регистратора
    @GetMapping("/findTransitsTruckByRegistratorID")
    fun findTransitsTruckByRegistratorID(
        @RequestParam("id") id: Long
    ): List<Transit> {
        return transitRepository.findTransitsTruckByRegistratorID(id)
    }

    // 3. Зарегистрировать проезд с указанным номер под указанным регистратором
    @PostMapping("/setTransit")
    fun setTransit(
        @RequestParam("plate_num") plate_num: String,
        @RequestParam("registrator_id") registrator_id: Long
    ): String {
        val registeredDate = java.sql.Date.valueOf(LocalDate.now())
        transitRepository.setTransit(plate_num, registrator_id, registeredDate)
        return "Transit added"
    }

    // 4. Получить все ГРЗ зафиксированные указанным регистратором
    // в период времени (дата от - дата до)
    @GetMapping("/findTransitsByRegistratorIdBetweenDates")
    fun findTransitsByRegistratorIdBetweenDates(
        @RequestParam("registrator_id") registrator_id: Long,
        @RequestParam("registered_date_from") @DateTimeFormat(pattern = "yyyy-MM-dd") dateFrom: Date,
        @RequestParam("registered_date_to") @DateTimeFormat(pattern = "yyyy-MM-dd") dateTo: Date
    ): List<Transit> {
        return transitRepository.findTransitsByRegistratorIdBetweenDates(registrator_id, dateFrom, dateTo)
    }

    // 5. Получить все регистраторы под которыми был зафиксирован проезд транспортного средства
    // с указанным номером за период времени  (дата от - дата до)
    @GetMapping("/findRegistratorsByNumBetweenDates")
    fun findRegistratorsByNumBetweenDates(
        @RequestParam("plate_num") plate_num: String,
        @RequestParam("registered_date_from") @DateTimeFormat(pattern = "yyyy-MM-dd") dateFrom: Date,
        @RequestParam("registered_date_to") @DateTimeFormat(pattern = "yyyy-MM-dd") dateTo: Date
    ): List<Registrator> {
        return registratorRepository.findRegistratorsByNumBetweenDates(plate_num, dateFrom, dateTo)
    }

    // 6. Получить все регистраторы под которым был зафиксирован проезд транспортного средства
    // с указанным номером в указанный период времени
    // в указанной области (передается координата точки и радиус поиска от нее)
    @GetMapping("/findRegistratorsByNumBetweenDatesInRadius")
    fun findRegistratorsByNumBetweenDatesInRadius(
        @RequestParam("plate_num") plate_num: String,
        @RequestParam("registered_date_from") @DateTimeFormat(pattern = "yyyy-MM-dd") dateFrom: Date,
        @RequestParam("registered_date_to") @DateTimeFormat(pattern = "yyyy-MM-dd") dateTo: Date,
        @RequestParam("coordinate") coordinateString: String,
        @RequestParam("radius") radius: Double
    ): List<Registrator> {
        val longitude = coordinateString.split(",")[0].toDouble()
        val latitude = coordinateString.split(",")[1].toDouble()
        return registratorRepository.findRegistratorsByNumBetweenDatesInRadius(plate_num, dateFrom, dateTo, longitude, latitude, radius)
    }
}