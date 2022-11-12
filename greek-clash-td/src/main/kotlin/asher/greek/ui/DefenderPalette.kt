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
import tornadofx.observable
import tornadofx.onChange
import tornadofx.property

class DefenderPalette(_bounds: Bounds) : Group() {

    val SPACER = 15.0
    val SPACER2 = SPACER + 15.0

    var allTools: List<DefenderGraphic> = Assets.defenderTypes.values.mapIndexed { i, defender ->
        val pos = Point2(_bounds.minX + SPACER2 + 2 * SPACER2 * i, _bounds.centerY)
        defender.copy(position = pos).createPaletteGraphics(SPACER).also {
            it.initToolSelection()
        }
    }
    var selectedTool by property<DefenderGraphic?>(null)
    var _selectedTool = getProperty(DefenderPalette::selectedTool).apply {
        onChange {
            allTools.forEach { it.selected = it == value && it.available }
        }
    }

    init {
        children += rectangle(_bounds, LIGHTCYAN)
        children.addAll(allTools)
    }

    private fun DefenderGraphic.initToolSelection() {
        setOnMouseClicked {
            selectedTool = if (selected || !available) null else this
        }
    }

    fun update(player: PlayerInfo) {
        children.mapNotNull { it as? DefenderGraphic }.forEach {
            it.available = it.defender.cost <= player.funds
        }
        selectedTool?.let {
            if (it.defender.cost > player.funds)
                selectedTool = null
        }
    }

}