package com.example.threebody_kt_graphics

fun main() {
    val lst = listOf(
        listOf(1, 2, 3),
        listOf(11, 22, 33),
        listOf(111, 222, 333),
        listOf(1111, 2222, 3333)
    )

    val lst2 = lst.map { it.subList(0, 2) }
    for (row in lst2) {
        println("-------")
        for (element in row) {
            println(element)
        }
    }
}