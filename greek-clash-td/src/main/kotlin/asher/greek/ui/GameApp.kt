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
package asher.greek.ui

import asher.greek.components.GameState
import javafx.scene.Scene
import tornadofx.*

class GameView(val game: GameState = GameState()) : View() {

    val controller = GameController(game)
    val levelGraphics = LevelPane(game, controller)

    override val root = borderpane {
        top = toolbar {
            button("Start Wave") {
                disableWhen { game._waveStarted }
                action { levelGraphics.startWaveTimer() }
            }
            button("Pause Wave") {
                enableWhen { game.levelActive }
                action { levelGraphics.pauseWaveTimer() }
            }
            button("Continue Wave") {
                enableWhen { game._wavePaused }
                action { levelGraphics.unpauseWaveTimer() }
            }
            button("Reset Wave") {
                visibleWhen { game._testMode }
                managedWhen { game._testMode }
                disableWhen { game.levelActive }
                action { levelGraphics.initWave(keepDefenders = true) }
            }
            separator {  }
            button("Previous Wave") {
                visibleWhen { game._testMode }
                managedWhen { game._testMode }
                enableWhen { game.canProceedToPreviousWave }
                action { previousWave() }
            }
            button("Next Wave") {
                action { nextWave() }
                enableWhen { game.canProceedToNextWave }
            }
            button("Previous Level") {
                visibleWhen { game._testMode }
                managedWhen { game._testMode }
                enableWhen { game.canProceedToPreviousLevel }
                action { previousLevel() }
            }
            button("Next Level") {
                enableWhen { game.canProceedToNextLevel }
                action { nextLevel() }
            }
            button("Restart Level") {
                action { restartLevel() }
            }
            separator { }
            button("Upgrade") {
                enableWhen { controller.canUpgradeSelected }
                action { levelGraphics.upgradeSelected() }
            }
            button("Sell") {
                enableWhen { controller.canSellSelected }
                action { levelGraphics.sellSelected() }
            }
            separator { }
            text(game.waveText)
            separator { }
            checkbox("Testing", game._testMode)
        }
        center = levelGraphics
    }

    //region change levels/waves

    fun previousWave() {
        levelGraphics.pauseWaveTimer()
        game.previousWave()
        levelGraphics.initWave(keepDefenders = true)
    }
    fun nextWave() {
        levelGraphics.pauseWaveTimer()
        game.nextWave()
        levelGraphics.initWave(keepDefenders = true)
    }

    fun previousLevel() {
        levelGraphics.pauseWaveTimer()
        game.previousLevel()
        levelGraphics.initWave(keepDefenders = false)
    }
    fun nextLevel() {
        levelGraphics.pauseWaveTimer()
        game.nextLevel()
        levelGraphics.initWave(keepDefenders = false)
    }
    fun restartLevel() {
        levelGraphics.pauseWaveTimer()
        game.initLevel()
        levelGraphics.initWave(keepDefenders = false)
    }

    //endregion
}

class GameApp : App() {
    override val primaryView = GameView::class

    override fun createPrimaryScene(view: UIComponent) = Scene(view.root, 1000.0, 1000.0)
}

fun main(args: Array<String>) = launch<GameApp>(args)
