package asher.greek.assets

import asher.greek.components.AttackPath
import asher.greek.components.Level

class LevelConfig {
    var name = ""
    var path = listOf<Array<Int>>()
    var startingFunds = 0
    var startingLives = 20
    var waves = listOf<WaveConfig>()

    fun createLevel() = Level().also {
        it.name = name
        it.path = AttackPath(path.map { it.toPoint2() })
        it.startingFunds = startingFunds
        it.startingLives = startingLives
        it.waves = waves.mapIndexed { i, w -> w.createWave(it, i + 1) }
    }
}