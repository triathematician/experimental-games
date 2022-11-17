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

import asher.greek.assets.Assets
import asher.greek.components.PlayerInfo
import asher.greek.gfx.DefenderGraphic
import asher.greek.gfx.GameComponentNone
import asher.greek.gfx.GraphicsFactory.createPaletteGraphics
import asher.greek.util.Point2
import asher.greek.util.rectangle
import javafx.geometry.Bounds
import javafx.scene.Group
import javafx.scene.paint.Color.LIGHTCYAN
import tornadofx.onChange
import tornadofx.onLeftClick

class DefenderPalette(val controller: GameController, _bounds: Bounds) : Group() {

    val SPACER = 14.0
    val SPACER2 = SPACER + 13.0

    var allTools: List<DefenderGraphic> = Assets.defenderTypes.values.mapIndexed { i, defender ->
        val pos = Point2(_bounds.minX + SPACER2 + 2 * SPACER2 * i, _bounds.centerY)
        defender.copy(position = pos).createPaletteGraphics(SPACER).also {
            it.initToolSelection()
        }
    }

    init {
        children += rectangle(_bounds, LIGHTCYAN)
        children.addAll(allTools)

        controller._selected.onChange { gc ->
            allTools.forEach { it.isSelected = it == gc && it.isAvailableToPurchase }
        }
    }

    private fun DefenderGraphic.initToolSelection() {
        onLeftClick {
            if (isSelected || !isAvailableToPurchase)
                controller.selectNone()
            else
                controller.selected = this
        }
    }

    fun update(player: PlayerInfo) {
        children.mapNotNull { it as? DefenderGraphic }.forEach {
            it.isAvailableToPurchase = it.defender.cost <= player.funds
            if (it == controller.selected && !it.isAvailableToPurchase)
                controller.selectNone()
        }
    }

}
