package asher.greek.components

import asher.greek.gfx.GraphicsFactory
import javafx.geometry.Bounds
import java.awt.Polygon

/** A level, comprising a scenario and a set of waves. */
class Level(_bounds: Bounds = GraphicsFactory.LEVEL_BOUNDS) {
    var name = ""
    var bounds = _bounds
    var path = AttackPath()
    var defenderArea = listOf<Polygon>()

    /** Player starting funds for level. */
    var startingFunds: Int = 0
    /** Player starting lives for level. */
    var startingLives: Int = 0

    var waves = listOf<LevelWave>()
}