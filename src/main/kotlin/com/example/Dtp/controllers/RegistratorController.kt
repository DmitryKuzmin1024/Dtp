package com.example.Dtp.controllers

import com.example.Dtp.CsvLogic
import com.example.Dtp.repo.RegistatorRepository
import com.example.Dtp.model.Registrator
import com.example.Dtp.model.Transit
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class RegistratorController(
    private val registratorRepository: RegistatorRepository,
) {
    private val csvLogic = CsvLogic()

    // 1. Генерация CSV файла с указанным количеством регистраторов
    // расположенных в рандомных точках
    @GetMapping("/exportCSV")
    fun exportCSV(
        @RequestParam("csv_size") csvSize: Int
    ): ResponseEntity<InputStreamResource> {
        val byteArrayOutputStream = csvLogic.getOutputStream(registratorRepository, csvSize)
        val csvFileName = "csv.csv"
        val headers = HttpHeaders()
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${csvFileName}")
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv")
        return ResponseEntity(
            InputStreamResource(byteArrayOutputStream),
            headers,
            HttpStatus.OK
        )
    }

    // 2. Импорт CSV файла с регистраторами для сохранения их в БД
    @PostMapping("/upload")
    fun uploadCSVFile(@RequestParam("CSV") file: MultipartFile): String {
        csvLogic.setCsvToDataBase(file, registratorRepository)
        return "CSV uploaded"
    }

    @PostMapping("/newRegistrator")
    fun addNewRegistrator(
        @RequestParam("code") code: String,
        @RequestParam("locationX") locationX: Double,
        @RequestParam("locationY") locationY: Double
    ): String {
        val point = GeometryFactory()
            .createPoint(Coordinate(locationX, locationY))
        registratorRepository.save(
            Registrator(
                code = code,
                location = point
            )
        )
        return "Registrator added"
    }

    @GetMapping("/findAll")
    fun findAll(): MutableIterable<Registrator> {
        registratorRepository.findAll().map {
            println("${it.id} ${it.code} ${it.location}")
        }
        return registratorRepository.findAll()
    }

}