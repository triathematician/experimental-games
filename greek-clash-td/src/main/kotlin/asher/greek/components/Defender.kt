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
