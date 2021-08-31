package com.example.Dtp

import com.example.Dtp.model.CSV
import com.example.Dtp.model.Registrator
import com.example.Dtp.repo.RegistatorRepository
import com.opencsv.bean.CsvToBeanBuilder
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.springframework.web.multipart.MultipartFile
import java.io.*

class CsvLogic {
    fun getOutputStream(repository: RegistatorRepository, csvSize: Int): ByteArrayInputStream {
        val csvHeader = arrayOf("id", "code", "locationX", "locationY")
        val csvBody: MutableList<String> = mutableListOf()
        repository.findAll().map {
            val string = "${it.id},${it.code},${it.location.x},${it.location.y}"
            if (csvBody.size < csvSize)
                csvBody.add(string)
        }
        return ByteArrayOutputStream()
            .use { out ->
                CSVPrinter(
                    PrintWriter(out),
                    CSVFormat.DEFAULT.withHeader(*csvHeader).withQuote(null)
                )
                    .use { csvPrinter ->
                        csvBody.forEach { record ->
                            csvPrinter.printRecord(record)
                        }
                        csvPrinter.flush()
                        ByteArrayInputStream(out.toByteArray())
                    }
            }
    }
    fun setCsvToDataBase(file: MultipartFile, repository: RegistatorRepository) {
        BufferedReader(InputStreamReader(file.inputStream)).use { reader ->
            val regFromCsv: List<Registrator> = CsvToBeanBuilder<CSV>(reader)
                .withType(CSV::class.java)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse()
                .map {
                    Registrator(
                        code = it.code,
                        location = GeometryFactory().createPoint(Coordinate(it.locationX, it.locationY))
                    )
                }
            repository.saveAll(regFromCsv)
            println("size ${regFromCsv.count()}")
        }
    }
}