package com.example.threebody_kt_graphics
fun main() {
    val coordinates = mutableListOf(
        arrayOf(50.0, 200.0),
        arrayOf(250.0, 150.0),
        arrayOf(350.0, 200.0),
        arrayOf(370.0, 200.0),
        arrayOf(390.0, 200.0),
        arrayOf(410.0, 200.0)
    )

    var minValue = Double.MAX_VALUE

    for (coordinate in coordinates) {
        if (coordinate[0] < minValue) {
            minValue = coordinate[0]
        }
    }

    println("Minimum value for the first column: $minValue")
}
