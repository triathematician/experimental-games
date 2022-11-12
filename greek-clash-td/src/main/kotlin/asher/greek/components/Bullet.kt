/*-
 * #%L
 * greek-clash-td
 * --
 * Copyright (C) 2020 - 2022 Elisha Peterson and Asher Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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

