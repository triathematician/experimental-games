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
    val player = PlayerInfo()

    private var levelIndex by property(0)
    private var waveIndex by property(0)

    var isWaveStarted by property(false)
    var isWavePaused by property(false)
    var isWaveOver by property(false)
    var isPassedWave by property(false)

    var isTestMode by property(false)

    val curLevel
        get() = levels[levelIndex]
    val curWave
        get() = curLevel.waves[waveIndex]

    private val _levelIndex = getProperty(GameState::levelIndex)
    private val _waveIndex = getProperty(GameState::waveIndex)

    val _waveStarted = getProperty(GameState::isWaveStarted)
    val _wavePaused = getProperty(GameState::isWavePaused)
    val _waveOver = getProperty(GameState::isWaveOver)
    val _passedWave = getProperty(GameState::isPassedWave)

    val _testMode = getProperty(GameState::isTestMode)

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

    val levelActive = booleanBinding(_waveStarted, _wavePaused, _waveOver) { get() && !isWavePaused && !isWaveOver }

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
        waveIndex = 0
        player.funds = curLevel.startingFunds
        player.lives = curLevel.startingLives
    }

    //endregion
}
