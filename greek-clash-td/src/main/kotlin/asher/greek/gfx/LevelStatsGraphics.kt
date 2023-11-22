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

import asher.greek.components.Defender
import asher.greek.components.PlayerInfo
import asher.greek.components.WaveState
import asher.greek.ui.GameController
import asher.greek.util.righttext
import asher.greek.util.text
import javafx.beans.property.Property
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Bounds
import javafx.scene.Group
import javafx.scene.text.Font
import tornadofx.stringBinding

/** Stats for current level progress. */
class LevelStatsGraphics(controller: GameController, levelBounds: Bounds): Group() {

    private val lives = controller.game._playerLives.stringBinding { "Lives: $it" }
    private val funds = controller.game._playerFunds.stringBinding { "Funds: $it" }
    private val clock = controller.clockText
    private val note = controller.waveTerminationText
    private val note2 = controller._selected.stringBinding {
        when (it) {
            is DefenderGraphic -> "Defender: ${it.defender.stats()}"
            is GameComponentNone -> ""
            else -> it.toString()
        }
    }

    private val font = Font("Verdana", 18.0)
    private val font2 = Font("Verdana", 10.0)
    private val font3 = Font("Verdana", 8.0)

    init {
        text(lives, levelBounds.minX, levelBounds.minY - 10, font)
        righttext(funds, levelBounds.maxX, -10, width = levelBounds.width / 2, font)
        text(clock, levelBounds.minX, levelBounds.maxY + 15, font2)
        righttext(note, levelBounds.maxX, levelBounds.maxY + 15, width = levelBounds.width / 2, font2)
        text(note2, levelBounds.minY, levelBounds.maxY + 30, font3)
    }

    private fun Defender.stats() =
        "$name ($type) " +
        "rg/atk/rt/blt: $range/${if (attackPower == 0) slowPower else attackPower}/$fireRate/$bulletSpeed " +
        "$: $cost/$sellPrice " +
        "upg: ${if (upgrades.isNotEmpty()) "yes" else "max"}"
}
