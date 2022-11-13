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
import javafx.beans.value.ObservableValue
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.paint.Color.BLACK
import javafx.scene.shape.Shape

/** Renders defender being placed on top of canvas. */
class DefenderDraggerTool(
    val selection: ObservableValue<DefenderGraphic?>,
    val validPlacement: (Shape) -> Boolean
) : Group() {

    var pos = point(100, 100)
        set(value) {
            field = value
            noSelectionGraphic.recenter(value.x, value.y)
            defenderGraphic?.let {
                it.defender.position = point(value.x, value.y)
                val newGraphic = DefenderGraphic(it.defender.copy(position = pos), it.size, tool = true)
                defenderGraphic = newGraphic

                // must calculate intersection after setting parent
                newGraphic.isValid = validPlacement(newGraphic.defenderShape)
            }
        }

    val noSelectionGraphic = circle(pos, 2.0).apply {
        fill = null
        stroke = BLACK
    }
    var defenderGraphic: DefenderGraphic? = null
        set(value) {
            field = value
            children.setAll(value ?: noSelectionGraphic)
        }

    init {
        isMouseTransparent = true
        children.add(noSelectionGraphic)
        selection.addListener { _, _, _ -> updateTool() }
    }

    fun updateTool() {
        val v = selection.value
        defenderGraphic = v?.let { DefenderGraphic(v.defender.at(pos.x, pos.y), v.size, tool = true) }
    }
}
