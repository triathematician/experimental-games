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
package asher.greek.assets

import asher.greek.components.AttackPath
import asher.greek.components.Level

class LevelConfig {
    var name = ""
    var path = listOf<Array<Int>>()
    var startingFunds = 0
    var startingLives = 20
    var waves = listOf<WaveConfig>()

    fun createLevel() = Level().also {
        it.name = name
        it.path = AttackPath(path.map { it.toPoint2() })
        it.startingFunds = startingFunds
        it.startingLives = startingLives
        it.waves = waves.mapIndexed { i, w -> w.createWave(it, i + 1) }
    }
}
