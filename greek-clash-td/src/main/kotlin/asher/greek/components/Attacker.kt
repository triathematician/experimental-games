package asher.greek.components

import com.fasterxml.jackson.annotation.JsonCreator
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Shape
import java.util.*

/** Characteristics of an attacker. */
data class Attacker(
        var name: String,
        var type: String,
        var speed: Double,
        var hitPoints: Int,
        var color: Color,
        var uid: UUID = UUID.randomUUID()) {

    /** Position of attacker on path. */
    var position = PathPosition()
    /** Current speed multiplier of the attacker. */
    var speedMultiplier = 1.0
    /** Current health of attacker. */
    var health = hitPoints.toDouble()

    /** Tracks time at which speed multiplier will be reset. */
    private var resetSpeedMultiplierTime = 0

    val shape: Shape
        get() = Circle(position.point!!.x, position.point!!.y, 10.0)

    fun advance(time: Int) {
        if (time > resetSpeedMultiplierTime) speedMultiplier = 1.0
        position.advance(speed * speedMultiplier)
    }

    fun slowDown(multiplier: Double, time: Int) {
        speedMultiplier = multiplier
        resetSpeedMultiplierTime = time + 100
    }
}