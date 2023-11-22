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
package asher.greek.components

import asher.greek.assets.Assets
import tornadofx.booleanBinding
import tornadofx.getProperty
import tornadofx.property
import tornadofx.stringBinding

/** Manages the state of the overall game, including current level, player assets, etc. */
class GameState {
    val levels = Assets.levelConfig.createLevels()

    var playerFunds by property(0)
    var playerLives by property(0)

    var levelIndex by property(0)
    private var waveIndex by property(0)

    var isWaveStarted by property(false)
    var isWavePaused by property(false)
    var isWaveOver by property(false)
    var isPassedWave by property(false)

    var isTestMode by property(false)

    val playerInfo
        get() = PlayerInfo(playerFunds, playerLives)
    val curLevel
        get() = levels[levelIndex]
    val curWave
        get() = curLevel.waves[waveIndex]

    //region PROPERTIES

    val _playerFunds = getProperty(GameState::playerFunds)
    val _playerLives = getProperty(GameState::playerLives)

    val _levelIndex = getProperty(GameState::levelIndex)
    val _waveIndex = getProperty(GameState::waveIndex)

    val _waveStarted = getProperty(GameState::isWaveStarted)
    val _wavePaused = getProperty(GameState::isWavePaused)
    val _waveOver = getProperty(GameState::isWaveOver)
    private val _passedWave = getProperty(GameState::isPassedWave)

    val _testMode = getProperty(GameState::isTestMode)

    //endregion

    //region DERIVED PROPERTIES

    val levelActive = _waveStarted.booleanBinding(_wavePaused, _waveOver) {
        it!! && !isWavePaused && !isWaveOver
    }

    val hasPreviousLevel = booleanBinding(_levelIndex) { get() > 0 }
    val hasNextLevel = booleanBinding(_levelIndex) { get() < levels.size - 1 }

    val hasPreviousWave = booleanBinding(_waveIndex) { get() > 0 }
    val hasNextWave = booleanBinding(_waveIndex, _levelIndex) { get() < curLevel.waves.size - 1 }

    val canProceedToPreviousWave = hasPreviousWave.booleanBinding(_testMode) {
        it!! && isTestMode
    }
    val canProceedToNextWave = hasNextWave.booleanBinding(_passedWave, _testMode) {
        it!! && (isTestMode || isPassedWave)
    }
    val canProceedToPreviousLevel = hasPreviousLevel.booleanBinding(_testMode) {
        it!! && isTestMode
    }
    val canProceedToNextLevel = hasNextLevel.booleanBinding(_passedWave, hasNextWave, _testMode) {
        it!! && (isTestMode || (isPassedWave && !hasNextWave.get()))
    }

    //endregion

    init {
        initLevel()
    }

    //region WAVE/LEVEL CHANGE

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
        waveIndex = 0
        playerFunds = curLevel.startingFunds
        playerLives = curLevel.startingLives
    }

    //endregion
}
