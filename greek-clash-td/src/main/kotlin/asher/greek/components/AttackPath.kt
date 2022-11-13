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
import asher.greek.util.Polyline
import asher.greek.util.length
import asher.greek.util.pointAt

/** Path that attackers follow. */
class AttackPath(var path: Polyline = listOf(), var pathWidth: Double = 5.0) {

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
