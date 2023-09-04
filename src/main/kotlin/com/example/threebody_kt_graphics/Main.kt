package com.example.threebody_kt_graphics

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import kotlin.math.*

import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.stage.Stage
import javafx.util.Duration

class Threebody(private val dt: Float, private val num_steps: Int) {
    /* Initial Conditions and constants */
    private var m1 = 5.972e24    // mass of Earth (index 1) in kg
    private var m2 = 7.3477e22   // mass of Moon (index 2) in kg
    private var m3 = 1000.0        // mass of spacecraft (index 3) in kg

    private val G = 6.67430e-11

    /* Initial positions (m) and velocities (m/s) in (x, y, z), (vx, vy, vz) */
    private val init_pos_1 = arrayOf(0.0, 0.0, 0.0)
    private val init_pos_2 = arrayOf(384400000.0, 0.0, 0.0)
    private val init_pos_3 = arrayOf(6371000.0, 0.0, 0.0)

    private val init_vel_1 = arrayOf(0.0, 0.0, 0.0)
    private val init_vel_2 = arrayOf(0.0, 1022.0, 0.0)
    private val init_vel_3 = arrayOf(0.0, 7700.0, 0.0)

    private var x1 = init_pos_1[0]
    private var y1 = init_pos_1[1]
    private var z1 = init_pos_1[2]

    private var x2 = init_pos_2[0]
    private var y2 = init_pos_2[1]
    private var z2 = init_pos_2[2]

    private var x3 = init_pos_3[0]
    private var y3 = init_pos_3[1]
    private var z3 = init_pos_3[2]

    private var vx1 = init_vel_1[0]
    private var vy1 = init_vel_1[1]
    private var vz1 = init_vel_1[2]

    private var vx2 = init_vel_2[0]
    private var vy2 = init_vel_2[1]
    private var vz2 = init_vel_2[2]

    private var vx3 = init_vel_3[0]
    private var vy3 = init_vel_3[1]
    private var vz3 = init_vel_3[2]

    private var all_pos_1 = mutableListOf<Array<Double>>()
    private var all_pos_2 = mutableListOf<Array<Double>>()
    private var all_pos_3 = mutableListOf<Array<Double>>()

    private var current_step = 0

    init {
        all_pos_1.add(init_pos_1)
        all_pos_2.add(init_pos_2)
        all_pos_3.add(init_pos_3)
    }
    fun update_pos_vel(): Unit {
        current_step += 1
        val r12 = sqrt(Math.pow((x2 - x1), 2.0) + Math.pow((y2 - y1), 2.0)  + Math.pow((z2 - z1), 2.0))
        val r13 = sqrt(Math.pow((x3 - x1), 2.0) + Math.pow((y3 - y1), 2.0) + Math.pow((z3 - z1), 2.0))
        val r23 = sqrt(Math.pow((x3 - x2), 2.0) + Math.pow((y3 - y2), 2.0) + Math.pow((z3 - z2), 2.0))

        val F12 = G * (m1 * m2) / Math.pow(r12, 3.0)
        val F13 = G * (m1 * m3) / Math.pow(r13, 3.0)
        val F23 = G * (m2 * m3) / Math.pow(r23, 3.0)


        /* Update velocities */
        vx1 += dt * (F12 * (x2 - x1) + F13 * (x3 - x1)) / m1
        vy1 += dt * (F12 * (y2 - y1) + F13 * (y3 - y1)) / m1
        vz1 += dt * (F12 * (z2 - z1) + F13 * (z3 - z1)) / m1

        vx2 += dt * (-F12 * (x2 - x1) + F23 * (x3 - x2)) / m2
        vy2 += dt * (-F12 * (y2 - y1) + F23 * (y3 - y2)) / m2
        vz2 += dt * (-F12 * (z2 - z1) + F23 * (z3 - z2)) / m2

        vx3 += dt * (-F13 * (x3 - x1) - F23 * (x3 - x2)) / m3
        vy3 += dt * (-F13 * (y3 - y1) - F23 * (y3 - y2)) / m3
        vz3 += dt * (-F13 * (z3 - z1) - F23 * (z3 - z2)) / m3

        /* Update positions */
        x1 += vx1 * dt
        y1 += vy1 * dt
        z1 += vz1 * dt

        x2 += vx2 * dt
        y2 += vy2 * dt
        z2 += vz2 * dt

        x3 += vx3 * dt
        y3 += vy3 * dt
        z3 += vz3 * dt

        val new_pos_1 = arrayOf(x1, y1, z1)
        all_pos_1.add(new_pos_1)
        val new_pos_2 = arrayOf(x2, y2, z2)
        all_pos_2.add(new_pos_2)
        val new_pos_3 = arrayOf(x3, y3, z3)
        all_pos_3.add(new_pos_3)
    }

    fun update_mass(add_m1: Double = 0.0,
                    add_m2: Double = 0.0,
                    add_m3: Double = 0.0): Unit {
        if (current_step > round(num_steps/3.0)) {
            m1 += add_m1
            m2 += add_m2
            m3 += add_m3
        }
    }

    fun run_sim(): Unit {
        for (step in 1..num_steps) {
            update_pos_vel()
            update_mass()
        }
    }
    fun return_positions_1(): MutableList<Array<Double>> {
        return all_pos_1
    }

    fun return_positions_2(): MutableList<Array<Double>> {
        return all_pos_2
    }

    fun return_positions_3(): MutableList<Array<Double>> {
        return all_pos_3
    }
}

class FigureDrawer() : Application() {
    override fun start(primaryStage: Stage) {
        val root = Group()
        val scene = Scene(root, 400.0, 400.0)

        val coordinates = listOf(
            Pair(200.0, 200.0),
            Pair(250.0, 150.0),
            Pair(350.0, 200.0),
            Pair(370.0, 200.0),
            Pair(390.0, 200.0),
            Pair(410.0, 200.0)
        )

        val timeline = Timeline()

        for ((index, coordinate) in coordinates.withIndex()) {
            val x = coordinate.first
            val y = coordinate.second

            // Define the action of each KeyFrame to add a circle at the specific coordinates.
            val keyFrame = KeyFrame(Duration.millis((index + 1) * 500.0), {
                val point_earth = Circle(x, y, 10.0, Color.BLUE)
                root.children.add(point_earth)
            })

            timeline.keyFrames.add(keyFrame)
        }

        primaryStage.title = "Figure"
        primaryStage.scene = scene
        primaryStage.show()
        timeline.play()
    }
}

fun main() {
    val dt = 360f // in s
    val num_steps = 10
    val threebody = Threebody(dt, num_steps-1)
    threebody.run_sim()
    val all_pos_1 = threebody.return_positions_1()
    val all_pos_2 = threebody.return_positions_2()
    val all_pos_3 = threebody.return_positions_3()

    val all_pos_1_2d = all_pos_1.map { it.copyOfRange(0, 2) }.toMutableList()
    val all_pos_2_2d = all_pos_2.map { it.copyOfRange(0, 2) }.toMutableList()
    val all_pos_3_2d = all_pos_3.map { it.copyOfRange(0, 2) }.toMutableList()

    println("all_pos_1")
    for (row in all_pos_1) {
        println("-------")
        for (element in row) {
            println(element)
        }
    }
    println("all_pos_2")
    for (row in all_pos_2) {
        println("-------")
        for (element in row) {
            println(element)
        }
    }
    println("all_pos_3")
    for (row in all_pos_3) {
        println("-------")
        for (element in row) {
            println(element)
        }
    }
    Application.launch(FigureDrawer::class.java)
}

