package asher.greek.components

import asher.greek.util.Point2
import asher.greek.util.Polyline
import asher.greek.util.length
import asher.greek.util.pointAt

/** Path that attackers follow. */
class AttackPath(var path: Polyline = listOf()) {

    constructor(vararg pathPoint: Point2) : this(listOf(*pathPoint))

    val spawnPoint: Point2
        get() = path.first()
    val defensePoint: Point2
        get() = path.last()

    /** Gets total path distance. */
    val length: Double
        get() = path.length

    fun pointAt(pos: Double) = path.pointAt(pos)
}

/** Dynamic position along a path. Position is cumulative distance from spawn point. */
class PathPosition(var path: AttackPath? = null, var pos: Double = 0.0) {
    val point: Point2?
        get() = path?.pointAt(pos)

    fun advance(speed: Double) {
        pos += speed
    }
}