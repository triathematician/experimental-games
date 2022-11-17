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

import asher.greek.gfx.DefenderGraphic
import asher.greek.util.circle
import asher.greek.util.point
import asher.greek.util.recenter
import javafx.scene.Group
import javafx.scene.paint.Color.BLACK
import javafx.scene.shape.Shape

/** Renders defender being placed on top of canvas. */
class DefenderDraggerTool(
    val controller: GameController,
    val validPlacement: (Shape) -> Boolean
) : Group() {

    var pos = point(100, 100)
        set(value) {
            field = value
            noSelectionGraphic.recenter(value.x, value.y)
            previewGraphic?.let {
                it.defender.position = point(value.x, value.y)
                val newGraphic = DefenderGraphic(it.defender.copy(position = pos), it.size, isForPreview = true)
                previewGraphic = newGraphic

                // must calculate intersection after setting parent
                newGraphic.isValidPlacement = validPlacement(newGraphic.defenderShape)
            }
        }

    private val noSelectionGraphic = circle(pos, 2.0).apply {
        fill = null
        stroke = BLACK
    }

    /** Rendered at mouse to show where it will be placed. */
    var previewGraphic: DefenderGraphic? = null
        private set(value) {
            field = value
            children.setAll(value ?: noSelectionGraphic)
        }

    init {
        isMouseTransparent = true
        children.add(noSelectionGraphic)
        controller._selected.addListener { _, _, _ -> updateTool() }
    }

    fun mouseEntered() {
        updateTool()
    }

    fun mouseExited() {
        previewGraphic = null
    }

    private fun updateTool() {
        val v = controller._selected.value
        previewGraphic = if (v is DefenderGraphic && v.isOnPalette)
            DefenderGraphic(v.defender.at(pos.x, pos.y), v.size, isForPreview = true)
        else
            null
    }
}
