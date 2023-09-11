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
            update_mass(add_m2 = 10e20)
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

    companion object {
        lateinit var coordinates_earth: List<Array<Double>>
        lateinit var coordinates_moon: List<Array<Double>>
        var frameDuration = 1000.0
    }
    override fun start(primaryStage: Stage) {

        // gets minimum and maximum coordinates for earth
        var coordinates_earth_x_min = Double.MAX_VALUE
        var coordinates_earth_x_max = Double.MIN_VALUE
        var coordinates_earth_y_min = Double.MAX_VALUE
        var coordinates_earth_y_max = Double.MIN_VALUE


        for (coordinate in coordinates_earth) {
            if (coordinate[0] < coordinates_earth_x_min) {
                coordinates_earth_x_min = coordinate[0]
            }
            if (coordinate[0] > coordinates_earth_x_max) {
                coordinates_earth_x_max = coordinate[0]
            }
            if (coordinate[1] < coordinates_earth_y_min) {
                coordinates_earth_y_min = coordinate[1]
            }
            if (coordinate[1] > coordinates_earth_y_max) {
                coordinates_earth_y_max = coordinate[1]
            }
        }
        println("Earth x: " + coordinates_earth_x_min.toString() + " " + coordinates_earth_x_max.toString())
        println("Earth y: " + coordinates_earth_y_min.toString() + " " + coordinates_earth_y_max.toString())

        // gets minimum and maximum coordinates for moon
        var coordinates_moon_x_min = Double.MAX_VALUE
        var coordinates_moon_x_max = Double.MIN_VALUE
        var coordinates_moon_y_min = Double.MAX_VALUE
        var coordinates_moon_y_max = Double.MIN_VALUE

        for (coordinate in coordinates_moon) {
            if (coordinate[0] < coordinates_moon_x_min) {
                coordinates_moon_x_min = coordinate[0]
            }
            if (coordinate[0] > coordinates_moon_x_max) {
                coordinates_moon_x_max = coordinate[0]
            }
            if (coordinate[1] < coordinates_moon_y_min) {
                coordinates_moon_y_min = coordinate[1]
            }
            if (coordinate[1] > coordinates_moon_y_max) {
                coordinates_moon_y_max = coordinate[1]
            }
        }
        println("Moon x: " + coordinates_moon_x_min.toString() + " " + coordinates_moon_x_max.toString())
        println("Moon y: " + coordinates_moon_y_min.toString() + " " + coordinates_moon_y_max.toString())

        // get window size in km
        val coordinates_x = listOf(coordinates_earth_x_min, coordinates_earth_x_max,
            coordinates_moon_x_min, coordinates_moon_x_max)
        val coordinates_y = listOf(coordinates_earth_y_min, coordinates_earth_y_max,
            coordinates_moon_y_min, coordinates_moon_y_max)
        val coordinates_x_min = coordinates_x.min()
        val coordinates_x_max = coordinates_x.max()
        val coordinates_y_min = coordinates_y.min()
        val coordinates_y_max = coordinates_y.max()
        println("All x: " + coordinates_x_min.toString() + " " + coordinates_x_max.toString())
        println("All y: " + coordinates_y_min.toString() + " " + coordinates_y_max.toString())

        val size_x = coordinates_x_max - coordinates_x_min
        val size_y = coordinates_y_max - coordinates_y_min
        println("Size x: " + size_x.toString())
        println("Size y: " + size_y.toString())

        // coordinate_transfer to window size
        val window_size_x = 600.0
        val window_size_y = 600.0
        val trans_x = size_x/window_size_x
        val trans_y = size_y/window_size_y
        println("trans x: " + trans_x.toString())
        println("trans y: " + trans_y.toString())

        val root = Group()
        val scene = Scene(root, window_size_x * 1.2, window_size_y * 1.2)

        val timeline = Timeline()

        // Clear the frame
        fun clearFrame() {
            root.children.clear()
        }
        // TODO: Coordination transformation is wrong lel
        for ((index, _) in coordinates_earth.withIndex()) {
            val coordinate_earth = coordinates_earth[index]
            val coordinate_moon = coordinates_moon[index]
            val x_earth = coordinate_earth[0] / trans_x - 1.2 * (coordinates_x_min / trans_x)
            val y_earth = coordinate_earth[1] / trans_y - 1.2 * (coordinates_y_min / trans_y)
            val x_moon= coordinate_moon[0] / trans_x - 1.2 * (coordinates_x_min/ trans_x)
            val y_moon = coordinate_moon[1] / trans_y - 1.2 * (coordinates_y_min/ trans_y)

            // Define the action of each KeyFrame to add a circle at the specific coordinates.
            val keyFrame = KeyFrame(Duration.millis((index + 1) * frameDuration), {
                val point_earth = Circle(x_earth, y_earth, 15.0, Color.BLUE)
                val point_moon= Circle(x_moon, y_moon, 3.0, Color.GRAY)
                root.children.add(point_earth)
                root.children.add(point_moon)
            })

            timeline.keyFrames.add(keyFrame)
        }

        primaryStage.title = "Figure"
        primaryStage.scene = scene
        primaryStage.show()
        timeline.play()
    }
}

fun print_matrix(matrix: List<Array<Double>>) {
    var maxLenNumber = 0
    val nbrOfRows = matrix.size
    val nbrOfColumns = matrix[0].size
    for (row in matrix) {
        for (element in row) {
            val formattedNumber = String.format("%.2f", element)
            val lengthFormattedNumber = formattedNumber.length
            if (lengthFormattedNumber > maxLenNumber) {
                maxLenNumber = lengthFormattedNumber
            }
        }
    }
    val divStr = "-".repeat(nbrOfColumns * (maxLenNumber+2) + nbrOfRows.toString().length + 3)

    println("\nMatrix:")
    for ((idrow, row) in matrix.withIndex()) {
        print("i_" + idrow + ":")
        for ((idx, element) in row.withIndex()) {
            val formattedNumber = String.format("%.2f", element)
            var filledElement = formattedNumber.padStart(maxLenNumber+1, ' ')
            filledElement = filledElement.padEnd(maxLenNumber+2, ' ')
            print(filledElement)
            if (idx < row.size - 1) {
                print('|')
            }

        }
        if (idrow < nbrOfRows-1) {
            print("\n" + divStr + "\n")
        }
    }
    print("\n\n")
}

fun main() {
    val dt = 3600f // in s
    val num_steps = 5000
    val frameDuration = 2.0
    val threebody = Threebody(dt, num_steps-1)
    threebody.run_sim()
    val all_pos_1 = threebody.return_positions_1()
    val all_pos_2 = threebody.return_positions_2()
    val all_pos_3 = threebody.return_positions_3()

    val all_pos_1_2d = all_pos_1.map { it.copyOfRange(0, 2) }.toList()
    val all_pos_2_2d = all_pos_2.map { it.copyOfRange(0, 2) }.toList()
    val all_pos_3_2d = all_pos_3.map { it.copyOfRange(0, 2) }.toList()

    //print_matrix(all_pos_1_2d)
    FigureDrawer.frameDuration = frameDuration
    FigureDrawer.coordinates_earth = all_pos_1_2d
    FigureDrawer.coordinates_moon = all_pos_2_2d
    Application.launch(FigureDrawer::class.java)
}
