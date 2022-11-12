package asher.greek.gfx

import asher.greek.components.Level
import asher.greek.ui.DefenderDraggerTool
import asher.greek.util.point
import asher.greek.util.rectangle
import javafx.scene.Group

/** Background for a level/wave. Supports mouse interaction for adding defenders. */
class LevelBackgroundGraphics(level: Level, toolDragger: DefenderDraggerTool): Group() {
    init {
        children.add(rectangle(level.bounds, GraphicsFactory.LEVEL_COLOR))

        setOnMouseMoved { toolDragger.pos = point(it.x, it.y) }
        setOnMouseEntered { toolDragger.updateTool() }
        setOnMouseExited { toolDragger.defGfx = null }
    }
}