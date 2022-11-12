package asher.greek.components

import asher.greek.util.Point2
import asher.greek.util.point
import javafx.scene.paint.Color
import java.util.*

/**
 * Characteristics of a defender.
 * Classes might include: NORMAL, RANGED, AREA
 */
data class Defender(
        val name: String,
        val type: String,
        val fireRate: Int,
        val range: Int,
        val attackPower: Int,
        val bulletSpeed: Int = 10,
        val slowPower: Double? = null,
        val areaAttack: Boolean = false,
        val cost: Int,
        val color: Color,
        val uid: UUID = UUID.randomUUID(),
        var position: Point2 = Point2()) {

    var speedMultiplier = 1.0
    var timeSinceLastFire = 0

    fun at(x: Number, y: Number) = copy(position = point(x, y), uid = UUID.randomUUID())
}