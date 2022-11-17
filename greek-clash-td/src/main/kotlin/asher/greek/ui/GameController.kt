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
package asher.greek.ui

import asher.greek.components.GameState
import asher.greek.gfx.DefenderGraphic
import asher.greek.gfx.GameComponent
import asher.greek.gfx.GameComponentNone
import tornadofx.booleanBinding
import tornadofx.getProperty
import tornadofx.property

/** Manages changes to game state, add/remove defenders, etc. */
class GameController(val game: GameState, val mouse: GameMouse = GameMouse()) {

    var selected: GameComponent by property(GameComponentNone)
    var _selected = getProperty(GameController::selected).apply {
        addListener { _, old, _ -> old.isSelected = false }
    }

    var canUpgradeSelected = booleanBinding(_selected) {
        get().let {
            it is DefenderGraphic && it.isInPlay && it.defender.isUpgradeable && it.defender.cost <= game.player.funds
        }
    }

    var canSellSelected = booleanBinding(_selected) {
        get().let {it is DefenderGraphic && it.isInPlay }
    }

    fun selectNone() {
        selected = GameComponentNone
    }

}
