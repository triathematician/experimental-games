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

import asher.greek.assets.Assets.attacker
import asher.greek.util.point

fun main() {
    WaveStateTest.testWave()
}

object WaveStateTest {

    fun testLevel() = Level().apply {
        path = AttackPath(listOf(point(0, 0),
                point(100, 100),
                point(100, 0),
                point(200, 100)))
    }

    fun testLevelWave() = LevelWave(testLevel(), 1).apply {
        order = AttackOrder(0 to attacker("Skeleton"),
                100 to attacker("Skeleton"),
                200 to attacker("Harpy"))
    }

    fun testWave() {
        val g = GameState()
        with(WaveState(g, testLevelWave())) {
            setup(listOf())
            start {}
            finishWave()
        }
    }

}
