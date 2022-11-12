package asher.greek.gfx

import asher.greek.components.PlayerInfo
import asher.greek.components.WaveState
import asher.greek.util.righttext
import asher.greek.util.text
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Bounds
import javafx.scene.Group
import javafx.scene.text.Font

/** Stats for current level progress. */
class LevelStatsGraphics(levelBounds: Bounds): Group() {

    val lives = SimpleStringProperty("Health: 0")
    val funds = SimpleStringProperty("Funds: 0")
    val clock = SimpleStringProperty("Clock: 0")
    val note = SimpleStringProperty("")

    val font = Font("Verdana", 18.0)
    val font2 = Font("Verdana", 10.0)

    init {
        text(lives, levelBounds.minX, levelBounds.minY - 10, font)
        righttext(funds, levelBounds.maxX, -10, width = levelBounds.width / 2, font)
        text(clock, levelBounds.minX, levelBounds.maxY + 15, font2)
        righttext(note, levelBounds.maxX, levelBounds.maxY + 15, width = levelBounds.width / 2, font2)
    }

    fun update(wave: WaveState, playerInfo: PlayerInfo) {
        lives.value = "Lives: ${playerInfo.lives}"
        funds.value = "Funds: ${playerInfo.funds}"
        clock.value = "Level: ${wave.levelWave.level.name} wave ${wave.levelWave.num} Clock: ${wave.clock}"
        val wonWave = wave.attackers.isEmpty() && wave.clock > wave.levelWave.lastSpawnTime
        val lostWave = playerInfo.lives == 0
        note.value = if (wonWave) "You successfully defended this wave!" else if (lostWave) "You lost all your lives!" else ""
    }
}