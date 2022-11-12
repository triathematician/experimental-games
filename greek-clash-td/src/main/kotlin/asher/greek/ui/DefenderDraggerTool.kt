package asher.greek.ui

import asher.greek.gfx.DefenderGraphic
import asher.greek.util.point
import asher.greek.util.recenter
import asher.greek.util.squareByCenter
import javafx.beans.value.ObservableValue
import javafx.scene.Group
import javafx.scene.paint.Color

/** Renders defender being placed on top of canvas. */
class DefenderDraggerTool(val selection: ObservableValue<DefenderGraphic?>): Group() {

    var pos = point(100, 100)
        set(value) {
            field = value
            pointGfx.recenter(value.x, value.y)
            defGfx?.let {
                it.defender.position = point(value.x, value.y)
                this@DefenderDraggerTool.children.setAll(
                    DefenderGraphic(it.defender.copy(position = pos), it.size, tool = true)
                )
            }
        }
    val pointGfx = squareByCenter(pos, 5.0, Color.BLACK)
    var defGfx: DefenderGraphic? = null
        set(value) {
            field = value
            children.setAll(value ?: pointGfx)
        }

    init {
        isMouseTransparent = true
        children.add(pointGfx)
        selection.addListener { _, _, _ -> updateTool() }
    }

    fun updateTool() {
        val v = selection.value
        defGfx = v?.let { DefenderGraphic(v.defender.at(pos.x, pos.y), v.size, tool = true) }
    }
}