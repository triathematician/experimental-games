package asher.greek.gfx

import asher.greek.components.Defender
import asher.greek.util.circle
import asher.greek.util.squareByCenter
import asher.greek.util.text
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.shape.Shape
import tornadofx.singleAssign

/** Renders defender on main view, and in palette. */
class DefenderGraphic(val defender: Defender, val size: Double, palette: Boolean = false, tool: Boolean = false) : Group() {
    var selectionShape by singleAssign<Shape>()
    var selected: Boolean
        get() = selectionShape.isVisible
        set(value) {
            if (value) assert(available)
            selectionShape.isVisible = value
        }
    var available: Boolean = false
        set(value) {
            field = value
            opacity = if (value) 1.0 else 0.3
            if (!value)
                selected = false
        }

    init {
        with (defender) {
            selectionShape = squareByCenter(position, size + 5, Color.YELLOW).apply {
                isVisible = false
            }
            children.add(selectionShape)
            val shape = squareByCenter(position, size, color)
            children.add(shape)
            children.add(text(position, name.beforeSpace(), size / 2).also {
                it.alignAbove(shape)
                it.isMouseTransparent = true
            })
            children.add(text(position, name.afterSpace(), size / 2).also {
                it.alignAbove2(shape)
                it.isMouseTransparent = true
            })

            when {
                palette -> {
                    children.add(text(position, "$type \$$cost", size / 2).also {
                        it.alignBelow(shape)
                    })
                    children.add(text(position, "D: $attackPower, R: $range", size / 2).also {
                        it.alignBelow2(shape)
                    })
                }
                tool -> {
                    children.add(circle(position, range.toDouble(), null).apply {
                        stroke = color
                    })
                }
                else -> {}
            }
        }
    }

}