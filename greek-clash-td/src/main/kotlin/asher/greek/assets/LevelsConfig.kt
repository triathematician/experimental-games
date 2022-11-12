package asher.greek.assets

class LevelsConfig {
    var levels = listOf<LevelConfig>()

    fun createLevels() = levels.map { it.createLevel() }
}