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
import asher.greek.gfx.GraphicsFactory.createPaletteGraphics
import asher.greek.util.Point2
import asher.greek.util.rectangle
import javafx.geometry.Bounds
import javafx.scene.Group
import javafx.scene.paint.Color.LIGHTCYAN
import tornadofx.getProperty
import tornadofx.onChange
import tornadofx.property

class DefenderPalette(_bounds: Bounds) : Group() {

    val SPACER = 14.0
    val SPACER2 = SPACER + 13.0

    var allTools: List<DefenderGraphic> = Assets.defenderTypes.values.mapIndexed { i, defender ->
        val pos = Point2(_bounds.minX + SPACER2 + 2 * SPACER2 * i, _bounds.centerY)
        defender.copy(position = pos).createPaletteGraphics(SPACER).also {
            it.initToolSelection()
        }
    }
    var selectedTool by property<DefenderGraphic?>(null)
    var _selectedTool = getProperty(DefenderPalette::selectedTool).apply {
        onChange {
            allTools.forEach { it.isSelected = it == value && it.isAvailableToPurchase }
        }
    }

    init {
        children += rectangle(_bounds, LIGHTCYAN)
        children.addAll(allTools)
    }

    private fun DefenderGraphic.initToolSelection() {
        setOnMouseClicked {
            selectedTool = if (isSelected || !isAvailableToPurchase) null else this
        }
    }

    fun update(player: PlayerInfo) {
        children.mapNotNull { it as? DefenderGraphic }.forEach {
            it.isAvailableToPurchase = it.defender.cost <= player.funds
        }
        selectedTool?.let {
            if (it.defender.cost > player.funds)
                selectedTool = null
        }
    }

}
