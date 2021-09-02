package com.example.Dtp.controllers

import com.example.Dtp.repo.RegistatorRepository
import com.example.Dtp.model.Registrator
import com.example.Dtp.model.Transit
import com.example.Dtp.repo.TransitRepository
import org.geolatte.geom.Circle
import org.locationtech.jts.geom.Coordinate
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.*
import kotlin.math.pow

@RestController
class TransitController(
    private val transitRepository: TransitRepository,
    private val registratorRepository: RegistatorRepository
) {
    // 1. Получить все регистраторы под которыми был зафиксирован проезд транспортного средства
    // с указанным номером
    @GetMapping("/registratorsByByPlateNum")
    fun findRegistratorsByPlateNum(
        @RequestParam("plate_num") plate_num: String,
    ): MutableList<Registrator> {
        val regList: MutableList<Registrator> = mutableListOf()
        transitRepository.findByPlateNum(plate_num).map {
            if (registratorRepository.findById(it.registratorId).get() !in regList)
                regList.add(registratorRepository.findById(it.registratorId).get())
        }
        return regList
    }

    // 2. Получить все проезды с типом ТС Truck,
    // по переданному идентификатору регистратора
    @GetMapping("/transitTruckById")
    fun findTransitTruckById(
        @RequestParam("registrator_id") registrator_id: Long
    ): List<Transit> {
        return transitRepository.findByRegistratorId(registrator_id).filter {
            it.vehicleType == Transit.VehicleType.TRUCK
        }
    }

    // 3. Зарегистрировать проезд с указанным номер под указанным регистратором
    @PostMapping("/newTransit")
    fun addNewTransit(
        @RequestParam("plate_num") plate_num: String,
        @RequestParam("registrator_id") registrator_id: Long
    ): String {
        transitRepository.save(
            Transit(
                plateNum = plate_num,
                vehicleType = Transit.VehicleType.TRUCK,
                registratorId = registrator_id,
                registeredDate = java.sql.Date.valueOf(LocalDate.now())
            )
        )
        return "Transit added"
    }

    // 4. Получить все ГРЗ зафиксированные указанным регистратором
    // в период времени (дата от - дата до)
    @GetMapping("/TransitsByRegistratorDate")
    fun findTransitsByRegistratorAndDate(
        @RequestParam("registrator_id") registrator_id: Long,
        @RequestParam("registered_date_from") @DateTimeFormat(pattern = "yyyy-MM-dd") dateFrom: Date,
        @RequestParam("registered_date_to") @DateTimeFormat(pattern = "yyyy-MM-dd") dateTo: Date
    ): List<Transit> {
        return transitRepository.findByRegistratorId(registrator_id).filter {
            it.registeredDate in dateFrom..dateTo
        }
    }

    // 5. Получить все регистраторы под которыми был зафиксирован проезд транспортного средства
    // с указанным номером за период времени  (дата от - дата до)
    @GetMapping("/registratorsByNumDate")
    fun findRegistratorsByNumAndDate(
        @RequestParam("plate_num") plate_num: String,
        @RequestParam("registered_date_from") @DateTimeFormat(pattern = "yyyy-MM-dd") dateFrom: Date,
        @RequestParam("registered_date_to") @DateTimeFormat(pattern = "yyyy-MM-dd") dateTo: Date
    ): MutableList<Registrator> {
        val regList: MutableList<Registrator> = mutableListOf()
        transitRepository.findByPlateNum(plate_num).filter {
            it.registeredDate in dateFrom..dateTo
        }.map {
            if (registratorRepository.findById(it.registratorId).get() !in regList)
                regList.add(registratorRepository.findById(it.registratorId).get())
        }
        return regList
    }

    // 6. Получить все регистраторы под которым был зафиксирован проезд транспортного средства
    // с указанным номером в указанный период времени
    // в указанной области (передается координата точки и радиус поиска от нее)
    @GetMapping("/registratorsByNumDateGeo")
    fun findRegistratorsByNumDateGeo(
        @RequestParam("plate_num") plate_num: String,
        @RequestParam("registered_date_from") @DateTimeFormat(pattern = "yyyy-MM-dd") dateFrom: Date,
        @RequestParam("registered_date_to") @DateTimeFormat(pattern = "yyyy-MM-dd") dateTo: Date,
        @RequestParam("coordinate") coordinateString: String,
        @RequestParam("radius") radius: Double
    ): MutableList<Registrator> {
        println(coordinateString)
        val regList: MutableList<Registrator> = mutableListOf()
        val circle = Circle(
            Coordinate(
                coordinateString.split(",")[0].toDouble(),
                coordinateString.split(",")[1].toDouble()
            ), radius
        )
        transitRepository.findByPlateNum(plate_num).filter { transit ->
            transit.registeredDate in dateFrom..dateTo
        }.map { reg ->
            if (registratorRepository.findById(reg.registratorId).get() !in regList)
                regList.add(registratorRepository.findById(reg.registratorId).filter {
                    findPointsInRadius(it, circle)
                }.get())
            print(registratorRepository.findById(reg.registratorId).get().location)
        }
        return regList
    }

    fun findPointsInRadius (registrator: Registrator, circle : Circle): Boolean {
       return ((registrator.location.x - circle.center.x).pow(2.0) + (registrator.location.y - circle.center.y).pow(2.0)) < circle.radius.pow(2.0)
    }
}