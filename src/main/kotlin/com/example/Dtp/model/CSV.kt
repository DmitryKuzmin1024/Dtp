package com.example.Dtp.model

import com.example.Dtp.NoArgConstructor
import com.opencsv.bean.CsvBindByName

@NoArgConstructor
data class CSV(
    @CsvBindByName(column = "code")
    val code: String,
    @CsvBindByName(column = "locationX")
    val locationX: Double,
    @CsvBindByName(column = "locationY")
    val locationY: Double
)