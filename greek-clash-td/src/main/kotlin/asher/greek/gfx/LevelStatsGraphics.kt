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
package asher.greek.gfx

import asher.greek.components.PlayerInfo
import asher.greek.components.WaveState
import asher.greek.util.righttext
import asher.greek.util.text
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Bounds
import javafx.scene.Group
import javafx.scene.text.Font

/** Stats for current level progress. */
class LevelStatsGraphics(levelBounds: Bounds): Group() {

    val lives = SimpleStringProperty("Health: 0")
    val funds = SimpleStringProperty("Funds: 0")
    val clock = SimpleStringProperty("Clock: 0")
    val note = SimpleStringProperty("")

    val font = Font("Verdana", 18.0)
    val font2 = Font("Verdana", 10.0)

    init {
        text(lives, levelBounds.minX, levelBounds.minY - 10, font)
        righttext(funds, levelBounds.maxX, -10, width = levelBounds.width / 2, font)
        text(clock, levelBounds.minX, levelBounds.maxY + 15, font2)
        righttext(note, levelBounds.maxX, levelBounds.maxY + 15, width = levelBounds.width / 2, font2)
    }

    fun update(wave: WaveState, playerInfo: PlayerInfo) {
        lives.value = "Lives: ${playerInfo.lives}"
        funds.value = "Funds: ${playerInfo.funds}"
        clock.value = "Level: ${wave.levelWave.level.name} wave ${wave.levelWave.num} Clock: ${wave.clock}"
        val wonWave = wave.attackers.isEmpty() && wave.clock > wave.levelWave.lastSpawnTime
        val lostWave = playerInfo.lives == 0
        note.value = if (wonWave) "You successfully defended this wave!" else if (lostWave) "You lost all your lives!" else ""
    }
}
