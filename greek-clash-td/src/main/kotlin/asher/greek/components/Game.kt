package asher.greek.components

import asher.greek.assets.Assets
import tornadofx.booleanBinding
import tornadofx.getProperty
import tornadofx.property
import tornadofx.stringBinding

/** Manages the state of the overall game, including current level, player assets, etc. */
class Game {
    val levels = Assets.levelConfig.createLevels()
    val player = PlayerInfo()

    private var levelIndex by property(0)
    private var waveIndex by property(0)
    var isPassedWave by property(false)
    var isLevelStarted by property(false)
    var isLevelPaused by property(false)
    var isTestMode by property(false)

    val curLevel
        get() = levels[levelIndex]
    val curWave
        get() = curLevel.waves[waveIndex]

    private var _levelIndex = getProperty(Game::levelIndex)
    private var _waveIndex = getProperty(Game::waveIndex)
    private var _passedWave = getProperty(Game::isPassedWave)

    var _levelStarted = getProperty(Game::isLevelStarted)
    var _levelPaused = getProperty(Game::isLevelPaused)
    var _testMode = getProperty(Game::isTestMode)

    val hasPreviousLevel = booleanBinding(_levelIndex) { get() > 0 }
    val hasNextLevel = booleanBinding(_levelIndex) { get() < levels.size - 1 }

    val hasPreviousWave = booleanBinding(_waveIndex) { get() > 0 }
    val hasNextWave = booleanBinding(_waveIndex, _levelIndex) { get() < curLevel.waves.size - 1 }

    val canProceedToPreviousWave = booleanBinding(hasPreviousWave, _testMode) {
        get() && isTestMode
    }
    val canProceedToNextWave = booleanBinding(hasNextWave, _passedWave, _testMode) {
        get() && (isTestMode || isPassedWave)
    }
    val canProceedToPreviousLevel = booleanBinding(hasPreviousLevel, _testMode) {
        get() && isTestMode
    }
    val canProceedToNextLevel = booleanBinding(hasNextLevel, _passedWave, hasNextWave, _testMode) {
        get() && (isTestMode || (isPassedWave && !hasNextWave.get()))
    }

    val waveText = stringBinding(_waveIndex, _levelIndex) {
        "Level ${levelIndex + 1}-${curLevel.name}, Wave ${get() + 1}"
    }

    val levelActive = booleanBinding(_levelStarted, _levelPaused) { get() && !isLevelPaused }

    init {
        initLevel()
    }

    //region MUTATORS

    fun previousWave() {
        require(hasPreviousWave.get())
        waveIndex--
    }

    fun nextWave() {
        require(hasNextWave.get())
        waveIndex++
    }

    fun previousLevel() {
        require(hasPreviousLevel.get())
        waveIndex = 0
        levelIndex--
        initLevel()
    }

    fun nextLevel() {
        require(hasNextLevel.get())
        waveIndex = 0
        levelIndex++
        initLevel()
    }

    internal fun initLevel() {
        player.funds = curLevel.startingFunds
        player.lives = curLevel.startingLives
    }

    //endregion
}