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
import asher.greek.util.circle
import asher.greek.util.squareByCenter
import asher.greek.util.text
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.paint.Color.RED
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import tornadofx.singleAssign

/** Renders defender on main view, and in palette. */
class DefenderGraphic(
    val defender: Defender,
    val size: Double,
    val isOnPalette: Boolean = false,
    val isForPreview: Boolean = false
) : Group(), GameComponent {

    var defenderShape by singleAssign<Rectangle>()
    var selectionShape by singleAssign<Shape>()
    var rangeShape by singleAssign<Shape>()
    val textGroup = Group()

    override var isSelected: Boolean
        get() = selectionShape.isVisible
        set(value) {
            if (value) assert(isAvailableToPurchase)
            selectionShape.isVisible = value
            rangeShape.isVisible = value && !isOnPalette
        }
    var isAvailableToPurchase: Boolean = false
        set(value) {
            field = value
            opacity = if (value) 1.0 else 0.3
            if (!value)
                isSelected = false
        }
    var isValidPlacement: Boolean = false
        set(value) {
            field = value
            selectionShape.isVisible = !value
            selectionShape.fill = RED
        }
    val isInPlay: Boolean
        get() = !isOnPalette && !isForPreview

    init {
        with (defender) {
            selectionShape = squareByCenter(position, size + 5, Color.YELLOW).apply {
                isVisible = false
            }
            defenderShape = squareByCenter(position, size, color)
            rangeShape = circle(position, range.toDouble(), fill = null, stroke = color)
            rangeShape.isVisible = isForPreview
            updateTextGroup()

            children.add(selectionShape)
            children.add(defenderShape)
            children.add(textGroup)
            children.add(rangeShape)
        }
    }

    private fun updateTextGroup() = with(textGroup) {
        children.removeAll()
        with (defender) {
            children.add(text(position, name.beforeSpace(), size / 2).also {
                it.alignAbove(defenderShape)
                it.isMouseTransparent = true
            })
            children.add(text(position, name.afterSpace(), size / 2).also {
                it.alignAbove2(defenderShape)
                it.isMouseTransparent = true
            })
            if (isOnPalette) {
                children.add(text(position, "$type \$$cost", size / 2).also {
                    it.alignBelow(defenderShape)
                })
                children.add(text(position, "D: $attackPower, R: $range", size / 2).also {
                    it.alignBelow2(defenderShape)
                })
            }
        }
    }

}
