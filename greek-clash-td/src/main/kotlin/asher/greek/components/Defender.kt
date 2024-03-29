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
    var name: String = "",
    val type: String? = null,
    val fireRate: Int,
    val range: Int,
    val attackPower: Int,
    val bulletSpeed: Int = 10,
    val slowPower: Double? = null,
    val areaAttack: Boolean = false,
    val cost: Int,
    val color: Color? = null,
    val uid: UUID = UUID.randomUUID(),
    var position: Point2 = Point2(),
    var upgrades: List<Defender> = listOf(),
    val sellPrice: Int = cost / 2
) {

    var speedMultiplier = 1.0
    var timeSinceLastFire = 0

    fun at(x: Number, y: Number) = copy(position = point(x, y), uid = UUID.randomUUID())

    val isUpgradeable
        get() = upgrades.isNotEmpty()

    fun upgrade(): Defender? {
        if (!isUpgradeable) return null
        return upgrades.first().let {
            it.copy(
                name = it.name.ifBlank { name.increment() },
                type = type,
                color = color,
                position = Point2(position.x, position.y),
                upgrades = upgrades.drop(1),
                sellPrice = sellPrice + it.cost / 2
            )
        }
    }

    companion object {

        private fun String.increment(): String {
            val suffix = substringAfterLast(" ")
            val newSuffix = when (suffix) {
                "I" -> "II"
                "II" -> "III"
                "III" -> "IV"
                "IV" -> "V"
                "V" -> "VI"
                "VI" -> "VII"
                "VII" -> "VIII"
                "VIII" -> "IX"
                "IX" -> "X"
                else -> return "$this II"
            }
            return "${substringBeforeLast(" ")} $newSuffix"
        }

    }

}
