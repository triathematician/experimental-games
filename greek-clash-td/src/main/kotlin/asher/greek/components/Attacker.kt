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
