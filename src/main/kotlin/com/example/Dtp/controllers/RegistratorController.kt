package com.example.Dtp.controllers

import com.example.Dtp.model.CSV
import com.example.Dtp.repo.RegistatorRepository
import com.example.Dtp.model.Registrator
import com.opencsv.bean.CsvToBeanBuilder
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.*


@RestController
class RegistratorController(
    private val registratorRepository: RegistatorRepository,
    @Value("\${file.upload-dir}")
    private val fileDirectory: String
) {
    // 1. Генерация CSV файла с указанным количеством регистраторов
    // расположенных в рандомных точках
    @GetMapping("/exportCSV")
    fun exportCSV(
        @RequestParam("csv_size") csvSize: Int
    ): ResponseEntity<InputStreamResource> {
        val csvHeader = arrayOf("id", "code", "location")
        val csvBody: MutableList<Registrator> = mutableListOf()
        registratorRepository.findAll().map {
            if (csvBody.size < csvSize)
                csvBody.add(it)
        }
        val byteArrayOutputStream =
            ByteArrayOutputStream()
                .use { out ->
                    CSVPrinter(
                        PrintWriter(out),
                        CSVFormat.DEFAULT.withHeader(*csvHeader)
                    )
                        .use { csvPrinter ->
                            csvBody.forEach { record ->
                                csvPrinter.printRecord(record)
                            }
                            csvPrinter.flush()
                            ByteArrayInputStream(out.toByteArray())
                        }
                }
        val fileInputStream = InputStreamResource(byteArrayOutputStream)
        val csvFileName = "csv.csv"
        val headers = HttpHeaders()
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${csvFileName}")
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv")

        return ResponseEntity(
            fileInputStream,
            headers,
            HttpStatus.OK
        )
    }

    // 2. Импорт CSV файла с регистраторами для сохранения их в БД
    @PostMapping("/upload")
    fun uploadCSVFile(@RequestParam("CSV") file: MultipartFile, model: Model): String? {
        BufferedReader(InputStreamReader(file.inputStream)).use { reader ->
            val regFromCsv: List<Registrator> = CsvToBeanBuilder<CSV>(reader)
                .withType(CSV::class.java)
                .withSeparator(',')
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse()
                .map {
                    Registrator(
                        it.id,
                        it.code,
                        it.location
                    )
                }
            registratorRepository.saveAll(regFromCsv)
        }
        return "file-upload-status"
    }

    @PostMapping("/newRegistrator")
    fun addNewRegistrator(
    ): String {
        val gf = GeometryFactory()
        val point = gf.createPoint(Coordinate(2.2345678, 3.3456789))
        registratorRepository.save(
            Registrator(
                id = registratorRepository.count() + 1,
                code = "111",
                location = point
            )
        )
        return "Transit added"
    }

    @GetMapping("/findAll")
    fun findAll(): MutableIterable<Registrator> {
        registratorRepository.findAll().map {
            println("${it.id} ${it.location}")
        }
        return registratorRepository.findAll()
    }
}