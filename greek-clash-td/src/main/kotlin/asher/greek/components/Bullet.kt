package asher.greek.components

import asher.greek.util.circle
import asher.greek.util.copy
import asher.greek.util.pointOnSegment
import javafx.scene.shape.Line
import javafx.scene.shape.Path
import javafx.scene.shape.Shape

/** When the bullets fly, this tracks the source and target. */
class Bullet(
        val source: Defender,
        val target: Attacker,
        val damage: Int,
        val radius: Int? = null,
        val tracking: Boolean = false) {

    val spawnPoint = source.position.copy()
    val targetPoint = target.position.point!!.copy()
    val speed = source.bulletSpeed
    val areaAttack = radius != null

    val hitList = mutableSetOf<Attacker>()

    var time = 0
    var lastPosition = spawnPoint
    var position = spawnPoint

    val distanceFromSpawn
        get() = spawnPoint.distance(position)
    val shape
        get() = when {
            !areaAttack -> Line(position.x, position.y, lastPosition.x, lastPosition.y)
            else -> circle(spawnPoint, distanceFromSpawn)
        }

    /** Move a bullet forward. */
    fun advance() {
        time++
        lastPosition = position
        position = if (tracking && !areaAttack) pointOnSegment(position, target.position.point!!, speed.toDouble())
        else pointOnSegment(spawnPoint, targetPoint, time * speed.toDouble())
    }

    fun hit(a: Attacker) : Boolean {
        if (a in hitList) return false
        val intersects = (Shape.intersect(a.shape, shape) as Path).elements.isNotEmpty()
        if (intersects) hitList += a
        return intersects
    }

    fun attackComplete() = (areaAttack && distanceFromSpawn >= source.range) || distanceFromSpawn >= 2*source.range
}

